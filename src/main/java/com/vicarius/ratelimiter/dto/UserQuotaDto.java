package com.vicarius.ratelimiter.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;


@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper=true)
@NoArgsConstructor
@SuperBuilder
public class UserQuotaDto extends UserDto {

    @Getter
    @Setter
    private Integer quotaNumber;
}
