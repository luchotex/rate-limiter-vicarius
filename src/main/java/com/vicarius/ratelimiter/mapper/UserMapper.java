package com.vicarius.ratelimiter.mapper;

import com.vicarius.ratelimiter.dto.UserDto;
import com.vicarius.ratelimiter.dto.UserQuotaDto;
import com.vicarius.ratelimiter.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;

@Mapper(componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface UserMapper {

    UserDto toUserDto(User user);

    UserQuotaDto toUserQuotaDto(User user);

    User toUser(UserDto userDto);
}
