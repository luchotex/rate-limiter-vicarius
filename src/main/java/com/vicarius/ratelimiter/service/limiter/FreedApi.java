package com.vicarius.ratelimiter.service.limiter;

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
