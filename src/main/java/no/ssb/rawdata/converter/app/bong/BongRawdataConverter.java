package no.ssb.rawdata.converter.app.bong;

import lombok.extern.slf4j.Slf4j;
import no.ssb.avro.convert.csv.CsvToRecords;
import no.ssb.rawdata.api.RawdataMessage;
import no.ssb.rawdata.converter.core.AbstractRawdataConverter;
import no.ssb.rawdata.converter.core.ConversionResult;
import no.ssb.rawdata.converter.core.schema.AggregateSchemaBuilder;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.GenericRecordBuilder;

import java.util.ArrayList;
import java.util.List;

import static no.ssb.rawdata.converter.app.bong.CounterKeys.TOTAL_BONG_LINE_COUNT;
import static no.ssb.rawdata.converter.core.util.RawdataMessageUtil.posAndIdOf;

@Slf4j
public class BongRawdataConverter extends AbstractRawdataConverter {

    private final Schema aggregateSchema;
    private final Schema metadataSchema;
    private final Schema bongSchema;
    private final Schema bongItemSchema;

    private static final String RAWDATA_ITEM_NAME_BONG = "entry";
    private static final String FIELDNAME_METADATA = "metadata";
    private static final String FIELDNAME_BONG = "bong";
    private static final String FIELDNAME_BONG_ITEMS = "BONG_ITEMS";
    private final BongRawdataConverterConfig converterConfig;

    public BongRawdataConverter(BongRawdataConverterConfig converterConfig) {
        this.converterConfig = converterConfig;
        this.metadataSchema = readAvroSchema("schema/message-metadata.avsc");
        this.bongSchema = readAvroSchema(converterConfig.getSchemaFileBong());
        this.bongItemSchema = bongSchema.getField(FIELDNAME_BONG_ITEMS).schema().getElementType();
        aggregateSchema = new AggregateSchemaBuilder("no.ssb.dapla.kilde.bong.rawdata")
          .schema(FIELDNAME_METADATA, metadataSchema)
          .schema(FIELDNAME_BONG, bongSchema)
          .build();
    }

    @Override
    public Schema targetAvroSchema() {
        return aggregateSchema;
    }

    @Override
    public boolean isConvertible(RawdataMessage rawdataMessage) {
        return true;
    }

    @Override
    public ConversionResult convert(RawdataMessage rawdataMessage) {
        ConversionResult.ConversionResultBuilder resultBuilder = ConversionResult.builder(new GenericRecordBuilder(aggregateSchema));

        // Add metadata about the message
        addMetadata(rawdataMessage, resultBuilder);

        // Convert bong data
        if (rawdataMessage.keys().contains(RAWDATA_ITEM_NAME_BONG)) {
            convertBongData(rawdataMessage, resultBuilder);
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

    void convertBongData(RawdataMessage rawdataMessage, ConversionResult.ConversionResultBuilder resultBuilder) {
        byte[] data = rawdataMessage.get(RAWDATA_ITEM_NAME_BONG);
        try (CsvToRecords csvToRecords = new CsvToRecords(data, bongItemSchema)) {
            List<GenericRecord> bongItems = new ArrayList<>();
            csvToRecords.forEach(bongItems::add);

            GenericRecord bongRecord = new GenericRecordBuilder(bongSchema).set(FIELDNAME_BONG_ITEMS, bongItems).build();
            resultBuilder.withRecord(FIELDNAME_BONG, bongRecord);
        } catch (Exception e) {
            throw new BongRawdataConverterException("Error converting bong data at " + posAndIdOf(rawdataMessage), e);
        }
    }

    static class BongRawdataConverterException extends RuntimeException {
        BongRawdataConverterException(String message, Throwable cause) {
            super(message, cause);
        }
    }

}
