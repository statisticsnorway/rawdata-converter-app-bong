package no.ssb.rawdata.converter.app.bong;

import com.google.common.base.Strings;
import io.micronaut.context.annotation.ConfigurationProperties;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@ConfigurationProperties("rawdata.converter.app.bong")
@Data
public class BongRawdataConverterConfig {

    @NotEmpty
    private String schemaFileBong;

    public String toDebugString() {
        return debugItem("schema bong", schemaFileBong);
    }

    private String debugItem(String label, Object value) {
        return Strings.padEnd(label, 24, '.') + " " + value + "\n";
    }

}
