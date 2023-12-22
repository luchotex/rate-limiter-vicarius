package com.vicarius.ratelimiter.controller;

import com.vicarius.ratelimiter.dto.UserDto;
import com.vicarius.ratelimiter.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/user")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping(path = "/{id}")
    public ResponseEntity<UserDto> getById(@PathVariable UUID id) {
        final UserDto userDto = userService.getById(id);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @PostMapping(path = "/")
    public ResponseEntity<UserDto> save(@RequestBody UserDto user) {
        final UserDto userDto = userService.save(user);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<UserDto> update(@PathVariable UUID id, @RequestBody UserDto user) {
        final UserDto userDto = userService.update(id, user);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<UserDto> delete(@PathVariable UUID id) {
        final UserDto userDto = userService.delete(id);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }
}
