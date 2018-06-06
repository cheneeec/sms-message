package com.gongsj.app.controller;

import com.gongsj.app.entity.SmsMessageUser;
import com.gongsj.app.service.SmsMessageUserService;


import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;


@RestController
@AllArgsConstructor
@RequestMapping("/api/user")
public class SmsMessageUserController {

    private final SmsMessageUserService messageUserService;

    @GetMapping("/{id}")
    public SmsMessageUser get(@PathVariable String id) {
        return messageUserService.get(id);
    }

    @PutMapping("/{id}")
    public SmsMessageUser put(@PathVariable String id, @RequestBody SmsMessageUser messageUser) {
        validateId(id, messageUser);
        return messageUserService.save(messageUser);
    }

    @PostMapping("/{id}")
    public void post(@PathVariable String id, @RequestBody SmsMessageUser messageUser) {
        validateId(id, messageUser);
        messageUserService.create(messageUser);
    }


    @GetMapping
    public Page<SmsMessageUser> findByPage(Pageable pageable) {
        return messageUserService.findByPage(pageable);
    }


    private void validateId(String id, SmsMessageUser messageUser) {
        if (!id.equalsIgnoreCase(messageUser.getId())) {
            throw new IllegalArgumentException("Inconsistent id");
        }
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable String id) {
        messageUserService.remove(id);
    }

}
