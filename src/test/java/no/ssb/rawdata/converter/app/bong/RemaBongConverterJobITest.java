package no.ssb.rawdata.converter.app.bong;

import no.ssb.dapla.dataset.api.Type;
import no.ssb.rawdata.api.RawdataConsumer;
import no.ssb.rawdata.converter.core.RawdataConverter;
import no.ssb.rawdata.converter.core.RawdataConverterConfig;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * This is an integration test that requires some additional test data that
 * is not available by default in this repo.
 *
 * Ask a friend to get hold of some rema test rawdata :-)
 */
public class RemaBongConverterJobITest extends BongConverterJobIntegrationTestBase {

    @Test
    @Disabled
    public void testConverterJobExecutor() {
        RawdataConverterConfig config = new RawdataConverterConfig();
        config.setActiveByDefault(true);
        config.setDryrun(false);
        config.setDebug(true);
//        config.setAllMessagesDumpPath("./rema-receipts");
        config.setTopic("bong-rema-target-test");
        config.setStorageType("local");
        config.setStorageRoot(uriStringOf("../localenv/datastore"));
        config.setStoragePath("/kilde/rema/bong/2018-10/raadata/test");
        config.setStorageVersion(1598553650000L);
        config.setInitialPosition("LAST");
        config.setWindowMaxRecords(1000);
        config.setSchemaMetricsEnabled(true);
        config.setDatasetType(Type.BOUNDED);

        BongRawdataConverterConfig bongRawdataConverterConfig = new BongRawdataConverterConfig();
        bongRawdataConverterConfig.setSchemaFileBong("schema/rema-bong.avsc");
        bongRawdataConverterConfig.setSource("rema");
        RawdataConverter converter = new BongRawdataConverter(bongRawdataConverterConfig);

        RawdataConsumer rawdataConsumer = rawdataConsumerFactory.rawdataConsumer(config);
        jobScheduler.start(config, converter, rawdataConsumer);
    }

}
