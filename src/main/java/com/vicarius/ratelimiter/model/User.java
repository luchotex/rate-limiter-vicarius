package com.vicarius.ratelimiter.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class User {
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
    @Getter
    @Setter
    private LocalDateTime lastLoginUtc;
}
