package no.ssb.rawdata.converter.app.bong;

import lombok.extern.slf4j.Slf4j;
import no.ssb.avro.convert.csv.CsvToRecords;
import no.ssb.avro.convert.csv.InconsistentCsvDataException;
import no.ssb.avro.convert.json.Json;
import no.ssb.avro.convert.json.JsonSettings;
import no.ssb.avro.convert.json.ToGenericRecord;
import no.ssb.rawdata.api.RawdataMessage;
import no.ssb.rawdata.converter.core.AbstractRawdataConverter;
import no.ssb.rawdata.converter.core.ConversionResult;
import no.ssb.rawdata.converter.core.ValueInterceptorChain;
import no.ssb.rawdata.converter.core.schema.AggregateSchemaBuilder;
import no.ssb.rawdata.converter.core.util.RawdataMessageFacade;
import no.ssb.rawdata.converter.core.util.Xml;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.GenericRecordBuilder;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static no.ssb.rawdata.converter.app.bong.CounterKeys.TOTAL_BONG_LINES_COUNT;
import static no.ssb.rawdata.converter.core.util.RawdataMessageUtil.posAndIdOf;

@Slf4j
public class BongRawdataConverter extends AbstractRawdataConverter {

    enum BongFileFormat {
        CSV, XML;
    }

    private final Schema aggregateSchema;
    private final Schema metadataSchema;
    private final Schema bongSchema;
    private final BongRawdataConverterConfig converterConfig;
    private final ValueInterceptorChain valueInterceptorChain;

    private static final String SOURCE_REMA = "rema";
    private static final String RAWDATA_ITEMNAME_BONG = "entry";
    private static final String FIELDNAME_METADATA = "metadata";
    private static final String FIELDNAME_BONG = "bong";
    private static final String FIELDNAME_BONG_ITEMS = "BONG_ITEMS";

    public BongRawdataConverter(BongRawdataConverterConfig converterConfig) {
        this(converterConfig, new ValueInterceptorChain());
    }

    public BongRawdataConverter(BongRawdataConverterConfig converterConfig, ValueInterceptorChain valueInterceptorChain) {
        this.converterConfig = converterConfig;
        this.metadataSchema = readAvroSchema("schema/message-metadata.avsc");
        this.bongSchema = readAvroSchema(converterConfig.getSchemaFileBong());
        aggregateSchema = new AggregateSchemaBuilder("no.ssb.dapla.kilde.bong.rawdata")
          .schema(FIELDNAME_METADATA, metadataSchema)
          .schema(FIELDNAME_BONG, bongSchema)
          .build();
        this.valueInterceptorChain = valueInterceptorChain;

        if (! converterConfig.getCsvSettings().isEmpty()) {
            log.info("Overridden CSV parser settings:\n{}", Json.prettyFrom(converterConfig.getCsvSettings()));
        }
    }

    private boolean isSource(String source) {
        return source.equalsIgnoreCase(converterConfig.getSource());
    }

    private BongFileFormat bongFileFormat() {
        return isSource(SOURCE_REMA)
          ? BongFileFormat.XML
          : BongFileFormat.CSV;
    }

    @Override
    public Schema targetAvroSchema() {
        return aggregateSchema;
    }

    @Override
    public boolean isConvertible(RawdataMessage rawdataMessage) {
        if (! rawdataMessage.keys().contains(RAWDATA_ITEMNAME_BONG)) {
            log.warn("Encountered rawdata message without bong data (no item called '" + RAWDATA_ITEMNAME_BONG + "'). " +
              "Items in message: " + rawdataMessage.keys() + ". Skipping message " + posAndIdOf(rawdataMessage));
            return false;
        }

        if (isSource(SOURCE_REMA)) {
            String xml = new String(rawdataMessage.get(RAWDATA_ITEMNAME_BONG));

            if (xml.contains("ReceiptNumber")) {
                return true;
            }
            else {
                log.info("Skipping non-receipt REMA data at " + posAndIdOf(rawdataMessage));
                return false;
            }
        }

        return true;
    }

    @Override
    public ConversionResult convert(RawdataMessage rawdataMessage) {
        ConversionResult.ConversionResultBuilder resultBuilder = ConversionResult.builder(new GenericRecordBuilder(aggregateSchema));

        // Add metadata about the message
        addMetadata(rawdataMessage, resultBuilder);

        // Convert bong data
        if (bongFileFormat() == BongFileFormat.CSV) {
            convertCsvBongData(rawdataMessage, resultBuilder);
        }
        else {
            convertXmlBongData(rawdataMessage, resultBuilder);
        }

        return resultBuilder.build();
    }

    void addMetadata(RawdataMessage rawdataMessage, ConversionResult.ConversionResultBuilder resultBuilder) {
        GenericRecordBuilder builder = new GenericRecordBuilder(metadataSchema);
        builder.set("ulid", rawdataMessage.ulid().toString());
        builder.set("dcPosition", rawdataMessage.position());
        builder.set("dcTimestamp", rawdataMessage.timestamp());
        resultBuilder.withRecord(FIELDNAME_METADATA, builder.build());
    }

    void convertCsvBongData(RawdataMessage rawdataMessage, ConversionResult.ConversionResultBuilder resultBuilder) {
        byte[] data = rawdataMessage.get(RAWDATA_ITEMNAME_BONG);
        Schema bongItemSchema = bongSchema.getField(FIELDNAME_BONG_ITEMS).schema().getElementType();

        try (CsvToRecords records = new CsvToRecords(new ByteArrayInputStream(data), bongItemSchema, converterConfig.getCsvSettings())) {
            List<GenericRecord> bongItems = new ArrayList<>();
            records.forEach(bongItems::add);
            resultBuilder.appendCounter(TOTAL_BONG_LINES_COUNT, bongItems.size());
            GenericRecord bongRecord = new GenericRecordBuilder(bongSchema).set(FIELDNAME_BONG_ITEMS, bongItems).build();
            resultBuilder.withRecord(FIELDNAME_BONG, bongRecord);
        }
        catch (InconsistentCsvDataException e) {
            log.warn("Encountered inconsistent csv data at " + posAndIdOf(rawdataMessage) + ". The bong data could not be converted.", e);
            resultBuilder.addFailure(e);
        }
        catch (Exception e) {
            throw new BongRawdataConverterException("Error converting bong data at " + posAndIdOf(rawdataMessage), e);
        }
    }

    void convertXmlBongData(RawdataMessage rawdataMessage, ConversionResult.ConversionResultBuilder resultBuilder) {
        byte[] data = rawdataMessage.get(RAWDATA_ITEMNAME_BONG);
        try {
            String json = xmlToJson(data);
            JsonSettings jsonSettings = new JsonSettings().enforceCamelCasedKeys(false);
            GenericRecord genericRecord = ToGenericRecord.from(json, bongSchema, jsonSettings);
            resultBuilder.withRecord(FIELDNAME_BONG, genericRecord);
        }
        catch (Exception e) {
            RawdataMessageFacade.print(rawdataMessage);
            throw new BongRawdataConverterException("Error converting bong data at " + posAndIdOf(rawdataMessage), e);
        }
    }

    static String xmlToJson(byte[] data) {
        Map<String, Object> map = Xml.toGenericMap(new String(data));
        return Json.from(map);
    }

    static class BongRawdataConverterException extends RuntimeException {
        BongRawdataConverterException(String message, Throwable cause) {
            super(message, cause);
        }
    }

}
