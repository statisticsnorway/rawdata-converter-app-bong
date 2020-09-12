package no.ssb.rawdata.converter.app.bong;

import io.micronaut.test.annotation.MicronautTest;
import no.ssb.rawdata.converter.core.RawdataConsumerFactory;
import no.ssb.rawdata.converter.core.job.ConverterJobScheduler;
import org.junit.jupiter.api.Disabled;

import javax.inject.Inject;
import java.nio.file.Path;

@Disabled
@MicronautTest(environments = {"local-filesystem"})
public class BongConverterJobIntegrationTestBase {

    @Inject
    protected ConverterJobScheduler jobScheduler;

    @Inject
    protected RawdataConsumerFactory rawdataConsumerFactory;

    protected static String uriStringOf(String relativePath) {
        return Path.of("../rawdata-converter-project/localenv/datastore").toUri().toString();
    }

}
