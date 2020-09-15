package no.ssb.rawdata.converter.app.bong;

import no.ssb.rawdata.converter.core.ConversionResult;
import no.ssb.rawdata.converter.core.ValueInterceptorChain;
import no.ssb.rawdata.converter.core.util.RawdataMessageFacade;
import no.ssb.rawdata.converter.test.message.RawdataMessageFixtures;
import no.ssb.rawdata.converter.test.message.RawdataMessages;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class BongRawdataConverterTest {

    static RawdataMessageFixtures fixtures;

    @BeforeAll
    static void loadFixtures() {
        fixtures = RawdataMessageFixtures.init("some-shop");
    }

    @Test
    void shouldConvertRawdataMessages() {
        RawdataMessages messages = fixtures.rawdataMessages("some-shop");
        BongRawdataConverterConfig config = new BongRawdataConverterConfig();
        config.setSchemaFileBong("schema/ng-bong.avsc");
        BongRawdataConverter converter = new BongRawdataConverter(config);
        ConversionResult result = converter.convert(messages.index().get("position-3"));
        System.out.println(result.getGenericRecord().toString());
    }

    @Disabled
    @Test
    void remaBong_shouldConvertRawdataMessage() {
        String position = "20181002_153330920-110201-418572";
        String topic = "rema-test";
        RawdataMessages messages = fixtures.rawdataMessages(topic);

        BongRawdataConverterConfig config = new BongRawdataConverterConfig();
        config.setSchemaFileBong("schema/rema-bong.avsc");
        config.setSource("rema");

        BongRawdataConverter converter = new BongRawdataConverter(config);
        ConversionResult result = converter.convert(messages.index().get(position));
        System.out.println(result.getGenericRecord().toString());
    }

}
