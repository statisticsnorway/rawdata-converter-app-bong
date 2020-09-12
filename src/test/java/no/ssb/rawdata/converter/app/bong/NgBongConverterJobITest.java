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
 * Ask a friend to get hold of some ng test rawdata :-)
 */
public class NgBongConverterJobITest extends BongConverterJobIntegrationTestBase {

    @Test
    @Disabled
    public void testConverterJobExecutor() {
        RawdataConverterConfig rawdataConverterConfig = new RawdataConverterConfig();
        rawdataConverterConfig.setActiveByDefault(true);
        rawdataConverterConfig.setDryrun(false);
        rawdataConverterConfig.setTopic("bong-ng-test");
        rawdataConverterConfig.setStorageType("local");
        rawdataConverterConfig.setStorageRoot(uriStringOf("../localenv/datastore"));
        rawdataConverterConfig.setStoragePath("/kilde/ng/bong/2018-10/raadata/test");
        rawdataConverterConfig.setStorageVersion(1598553650000L);
        rawdataConverterConfig.setInitialPosition("LAST");
        rawdataConverterConfig.setWindowMaxRecords(1000);
        rawdataConverterConfig.setSchemaMetricsEnabled(false);
        rawdataConverterConfig.setDatasetType(Type.BOUNDED);

        BongRawdataConverterConfig bongRawdataConverterConfig = new BongRawdataConverterConfig();
        bongRawdataConverterConfig.setSchemaFileBong("schema/ng-bong.avsc");
        bongRawdataConverterConfig.getCsvSettings().put("delimiters", "|");
        RawdataConverter converter = new BongRawdataConverter(bongRawdataConverterConfig);

        RawdataConsumer rawdataConsumer = rawdataConsumerFactory.rawdataConsumer(rawdataConverterConfig);
        jobScheduler.start(rawdataConverterConfig, converter, rawdataConsumer);
    }

}
