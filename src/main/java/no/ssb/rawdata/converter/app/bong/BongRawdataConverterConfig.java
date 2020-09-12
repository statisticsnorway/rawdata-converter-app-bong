package no.ssb.rawdata.converter.app.bong;

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.annotation.Context;
import io.micronaut.core.convert.format.MapFormat;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties("rawdata.converter.app.bong")
@Data
public class BongRawdataConverterConfig {

    /**
     * The name of the bong avro schema to use
     */
    @NotBlank
    private String schemaFileBong;

    @NotBlank
    private String bongSource;

    /**
     * Optional csv parser settings overrides.
     * E.g. allowing to explicitly specify the delimiter character
     */
    @MapFormat(transformation = MapFormat.MapTransformation.FLAT)
    private Map<String, Object> csvSettings = new HashMap<>();

}
