package com.vicarius.ratelimiter.controller;

import com.vicarius.ratelimiter.dto.UserDto;
import com.vicarius.ratelimiter.dto.UserQuotaDto;
import com.vicarius.ratelimiter.service.CustomMapDynamicAutowireService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final CustomMapDynamicAutowireService customMapDynamicAutowireService;

    @GetMapping(path = "/{id}")
    public ResponseEntity<UserDto> getById(@PathVariable UUID id) {
        final UserDto userDto = customMapDynamicAutowireService.getById(id);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @PostMapping(path = "/")
    public ResponseEntity<UserDto> save(@RequestBody UserDto user) {
        final UserDto userDto = customMapDynamicAutowireService.save(user);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<UserDto> update(@PathVariable UUID id, @RequestBody UserDto user) {
        final UserDto userDto = customMapDynamicAutowireService.update(id, user);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<UserDto> delete(@PathVariable UUID id) {
        final UserDto userDto = customMapDynamicAutowireService.delete(id);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }
    @GetMapping(path = "/{id}/quota")
    public ResponseEntity<UserQuotaDto> consumeQuota(@PathVariable UUID id) {
        final UserQuotaDto userQuotaDto = customMapDynamicAutowireService.consumeQuota(id);
        return new ResponseEntity<>(userQuotaDto, HttpStatus.OK);
    }


    @GetMapping(path = "/quotas")
    public ResponseEntity<List<UserQuotaDto>> getUsersQuota() {
        final List<UserQuotaDto> userQuotaDtoList = customMapDynamicAutowireService.getUsersQuota();
        return new ResponseEntity<>(userQuotaDtoList, HttpStatus.OK);
    }
}
