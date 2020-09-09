package no.ssb.rawdata.converter.app.bong;

import no.ssb.rawdata.converter.test.message.RawdataMessageFixtures;
import no.ssb.rawdata.converter.test.message.RawdataMessages;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static no.ssb.rawdata.converter.test.RawdataPublisher.filesystemConfig;
import static no.ssb.rawdata.converter.test.RawdataPublisher.postgresConfig;
import static no.ssb.rawdata.converter.test.RawdataPublisher.publishRawdataMessages;

public class BongRawdataPublisherTest {

    static RawdataMessageFixtures fixtures;

    @BeforeAll
    static void loadFixtures() {
        fixtures = RawdataMessageFixtures.init("some-shop");
    }

    @Test
    @Disabled
    void publishRawdataMessagesToLocalPostgres() {
        String topic = "some-shop";
        RawdataMessages messages = fixtures.rawdataMessages(topic);
        publishRawdataMessages(messages, postgresConfig(topic));
    }

    @Test
    @Disabled
    void publishRawdataMessagesToLocalAvroFile() {
        String topic = "some-shop";
        RawdataMessages messages = fixtures.rawdataMessages(topic);
        publishRawdataMessages(messages, filesystemConfig(topic, "../localenv/rawdatastore")
          .cleanupBefore(true)
          .cleanupAfter(true)
        );
    }

}
