package com.kafka.streams.user_service.streams;

import com.kafka.avro.User;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserStreamsProcessor {

    @Bean
    public KStream<String, User> readStream(@Qualifier("userKStreamsBuilder") StreamsBuilder streamsBuilder, Serde<User> userSerde) {
        KStream<String, User> kStream = streamsBuilder.stream("user-topic",
                Consumed.with(Serdes.String(), userSerde));

        kStream.mapValues(user -> new User(user.getId(),
                                            user.getName().toString().toUpperCase(),
                                            user.getEmail(),
                                            user.getPassword()))
                .to("output-user-topic");

        return kStream;
    }
}
