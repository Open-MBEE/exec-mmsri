package org.openmbee.mms.mmsri.config;

import static org.zalando.logbook.BodyFilter.merge;
import static org.zalando.logbook.BodyFilters.defaultValue;
import static org.zalando.logbook.json.JsonBodyFilters.replaceJsonStringProperty;

import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.logbook.BodyFilter;

@Configuration
public class LogBookConfig {

    @Bean
    public BodyFilter bodyFilter() {
        return merge(
                defaultValue(),
                replaceJsonStringProperty(
                        Stream.of("password","token").collect(Collectors.toSet()),
                        "XXX"));
    }
}
