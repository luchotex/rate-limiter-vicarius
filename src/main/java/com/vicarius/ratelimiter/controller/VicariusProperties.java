package com.vicarius.ratelimiter.controller;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "vicarius")
public class VicariusProperties {

    @Getter
    @Setter
    private List<FreedApi> freedApi;
}
