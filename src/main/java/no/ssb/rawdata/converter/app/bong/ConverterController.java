package no.ssb.rawdata.converter.app.bong;


import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.Async;
import io.micronaut.scheduling.annotation.ExecuteOn;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.ssb.rawdata.api.RawdataConsumer;
import no.ssb.rawdata.converter.core.job.ConverterJobScheduler;
import no.ssb.rawdata.converter.core.RawdataConsumerFactory;
import no.ssb.rawdata.converter.core.RawdataConverter;
import no.ssb.rawdata.converter.core.RawdataConverterConfig;

import javax.ws.rs.core.MediaType;

@Controller("/converter/start")
@Slf4j
@RequiredArgsConstructor
public class ConverterController {

    private final RawdataConsumerFactory rawdataConsumerFactory;
    private final ConverterJobScheduler jobScheduler;

    @Async
    @ExecuteOn(TaskExecutors.IO)
    @Post(consumes = MediaType.APPLICATION_JSON)
    public void startConverter(ConverterJob job) {
        log.info("Starting converter...\n{}", job);
        RawdataConverter rawdataConverter = newConverter(job.bongRawdataConverterConfig);
        RawdataConsumer rawdataConsumer = rawdataConsumerFactory.rawdataConsumer(job.rawdataConverterConfig);
        jobScheduler.start(job.rawdataConverterConfig, rawdataConverter, rawdataConsumer);
    }

    private RawdataConverter newConverter(BongRawdataConverterConfig bongRawdataConverterConfig) {
        RawdataConverter converter = new BongRawdataConverter(bongRawdataConverterConfig);
        return converter;
    }

    @Data
    public static class ConverterJob {
        private RawdataConverterConfig rawdataConverterConfig;
        private BongRawdataConverterConfig bongRawdataConverterConfig;
    }
}
