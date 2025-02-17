package com.kafka.streams.user_service.streams;

import com.kafka.avro.User;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserStreamsProcessor {

    public KStream<String, User> readStream(StreamsBuilder streamsBuilder) {
        KStream<String, User> kStream = streamsBuilder.stream("user-topic", )
    }
}
