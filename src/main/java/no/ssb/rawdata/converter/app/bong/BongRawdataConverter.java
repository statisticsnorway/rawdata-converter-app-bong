package no.ssb.rawdata.converter.app.bong;

import lombok.extern.slf4j.Slf4j;
import no.ssb.rawdata.api.RawdataMessage;
import no.ssb.rawdata.converter.core.AbstractRawdataConverter;
import no.ssb.rawdata.converter.core.AggregateSchemaBuilder;
import no.ssb.rawdata.converter.core.ConversionResult;
import no.ssb.rawdata.converter.core.Metadata;
import no.ssb.rawdata.converter.core.RawdataMessageFacade;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecordBuilder;

import javax.inject.Singleton;

@Singleton
@Slf4j
public class BongRawdataConverter extends AbstractRawdataConverter {

    private final Schema aggregateSchema;
    private static final String ELEMENT_NAME_METADATA = "metadata";

    public BongRawdataConverter() {
        aggregateSchema = new AggregateSchemaBuilder("no.ssb.dapla.kilde.bong.rawdata")
          .schema(ELEMENT_NAME_METADATA, Metadata.SCHEMA)
          .build();
    }

    @Override
    public Schema targetAvroSchema() {
        return aggregateSchema;
    }

    @Override
    public boolean isConvertible(RawdataMessage msg) {
        return true;
    }

    @Override
    public ConversionResult convert(RawdataMessage rawdataMessage) {
        log.info("Convert bong rawdata message {}", rawdataMessage);
        ConversionResult.ConversionResultBuilder resultBuilder = new ConversionResult.ConversionResultBuilder(new GenericRecordBuilder(aggregateSchema));

        RawdataMessageFacade.print(rawdataMessage);

        return resultBuilder.build();
    }
}
