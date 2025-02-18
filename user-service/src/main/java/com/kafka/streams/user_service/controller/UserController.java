package com.kafka.streams.user_service.controller;

import com.kafka.avro.User;
import com.kafka.streams.user_service.producer.UserProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path="/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserProducer userProducer;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String sendUser(@RequestBody User user) {
        userProducer.sendUser(user);
        return "User sent successfully";
    }
}
