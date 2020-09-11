package no.ssb.rawdata.converter.app.bong;

import io.micronaut.test.annotation.MicronautTest;
import no.ssb.dapla.dataset.api.Type;
import no.ssb.rawdata.api.RawdataConsumer;
import no.ssb.rawdata.converter.core.RawdataConsumerFactory;
import no.ssb.rawdata.converter.core.RawdataConverter;
import no.ssb.rawdata.converter.core.RawdataConverterConfig;
import no.ssb.rawdata.converter.core.job.ConverterJobScheduler;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

@MicronautTest(environments = {"local-filesystem"})
@Disabled
public class BongConverterJobExecutorTest {

    @Inject
    private ConverterJobScheduler jobScheduler;

    @Inject
    private RawdataConsumerFactory rawdataConsumerFactory;

    @Test
    @Disabled
    public void testConverterJobExecutor() {
        RawdataConverterConfig rawdataConverterConfig = new RawdataConverterConfig();
        rawdataConverterConfig.setActiveByDefault(true);
        rawdataConverterConfig.setDryrun(false);
        rawdataConverterConfig.setStorageType("local");
        rawdataConverterConfig.setStorageRoot("file:///Users/kenneth/dev/code/ssb/rawdata-converter-project/localenv/datastore");
        rawdataConverterConfig.setStoragePath("/kilde/some-shop/bong/2017-01/radata/v1");
        rawdataConverterConfig.setStorageVersion(1598553650000L);
        rawdataConverterConfig.setServiceAccountKeyFile("blah");
        rawdataConverterConfig.setTopic("some-shop");
        rawdataConverterConfig.setWindowMaxRecords(1000);
        rawdataConverterConfig.setInitialPosition("FIRST");
        rawdataConverterConfig.setSchemaMetricsEnabled(false);
        rawdataConverterConfig.setDatasetType(Type.BOUNDED);

        BongRawdataConverterConfig bongRawdataConverterConfig = new BongRawdataConverterConfig();
        bongRawdataConverterConfig.setSchemaFileBong("schema/ng-bong.avsc");
        bongRawdataConverterConfig.getCsvSettings().put("delimiters", "|");
        RawdataConverter converter = new BongRawdataConverter(bongRawdataConverterConfig);

        RawdataConsumer rawdataConsumer = rawdataConsumerFactory.rawdataConsumer(rawdataConverterConfig);
        jobScheduler.start(rawdataConverterConfig, converter, rawdataConsumer);
    }

    private static RawdataConverter newConverter(BongRawdataConverterConfig bongRawdataConverterConfig) {
        RawdataConverter converter = new BongRawdataConverter(bongRawdataConverterConfig);
        return converter;
    }

}
