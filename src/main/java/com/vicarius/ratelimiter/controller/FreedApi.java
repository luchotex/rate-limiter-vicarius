package com.vicarius.ratelimiter.controller;

import lombok.Getter;
import lombok.Setter;

public class FreedApi {
    @Getter
    @Setter
    private String url;
    @Getter
    @Setter
    private String method;
}
