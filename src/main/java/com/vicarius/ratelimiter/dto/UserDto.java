package com.vicarius.ratelimiter.dto;


import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@AllArgsConstructor
@SuperBuilder
@Data
@NoArgsConstructor
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
