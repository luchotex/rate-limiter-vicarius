package com.vicarius.ratelimiter.dto;


import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

public class UserDto {
    @Getter
    @Setter
    private UUID id;
    @Getter
    @Setter
    private String firstName;
    @Getter
    @Setter
    private String lastName;
    @Getter
    @Setter
    private Boolean disabled;
}
