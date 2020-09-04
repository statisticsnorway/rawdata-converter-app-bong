package no.ssb.rawdata.converter.app.bong;

import no.ssb.rawdata.converter.core.util.RawdataMessageFacade;
import no.ssb.rawdata.converter.test.message.RawdataMessageFixtures;
import no.ssb.rawdata.converter.test.message.RawdataMessages;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class RawdataMessageFixturesTest {

    static RawdataMessageFixtures fixtures;

    @BeforeAll
    static void loadFixtures() {
        fixtures = RawdataMessageFixtures.init("some-shop");
    }

    @Test
    void testStuff() {
        RawdataMessages messages = fixtures.rawdataMessages("some-shop");
        RawdataMessageFacade.print(messages.index().get("position-1"));
    }

}
