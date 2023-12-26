package com.vicarius.ratelimiter.mapper;

import com.vicarius.ratelimiter.dto.UserDto;
import com.vicarius.ratelimiter.dto.UserQuotaDto;
import com.vicarius.ratelimiter.model.User;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toUserDto(User user);

    @InheritConfiguration
    UserQuotaDto toUserQuotaDto(User user);

    User toUser(UserDto userDto);
}
