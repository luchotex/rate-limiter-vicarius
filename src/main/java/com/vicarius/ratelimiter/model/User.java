package com.vicarius.ratelimiter.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class User {
    @Getter
    @Setter
    @Id
    @GeneratedValue(generator = "uuid-hibernate-generator")
    @GenericGenerator(name = "uuid-hibernate-generator", strategy = "org.hibernate.id.UUIDGenerator")
    @Type(type = "uuid-char")
    @Column(name = "id", nullable = false, columnDefinition = "uuid")
    private UUID id;
    @Getter
    @Setter
    private String firstName;
    @Getter
    @Setter
    private String lastName;
    @Getter
    @Setter
    private LocalDateTime lastLoginUtc;
    @Getter
    @Setter
    private boolean disabled = Boolean.FALSE;

    @Getter
    @Setter
    private Integer quotaNumber;
}
