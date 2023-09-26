package com.gameloft.profilematcher.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.text.SimpleDateFormat;

@Configuration
public class JsonSerializationConfiguration {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .setSerializationInclusion(JsonInclude.Include.ALWAYS)
                .configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true)
                .setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss'Z'"));
    }

}
