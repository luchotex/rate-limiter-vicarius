package com.vicarius.ratelimiter.dto;

import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

public class UserDto {
    @Getter
    @Setter
    @Id
    private String id;
    @Getter
    @Setter
    private String firstName;
    @Getter
    @Setter
    private String lastName;
}
