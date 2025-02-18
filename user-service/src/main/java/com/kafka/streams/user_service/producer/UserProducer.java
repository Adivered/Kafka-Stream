package com.kafka.streams.user_service.producer;

import com.kafka.avro.User;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserProducer {
    private final KafkaTemplate<String, User> kafkaTemplate;


    public void sendUser(User user) {
        kafkaTemplate.send("user-topic", user);
    }
}
