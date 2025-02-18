package com.kafka.streams.user_service.config;

import com.kafka.avro.User;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.StreamsConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.kafka.core.CleanupConfig;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;

import static io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig.AUTO_REGISTER_SCHEMAS;
import static io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.ALLOW_AUTO_CREATE_TOPICS_CONFIG;
import static org.apache.kafka.streams.StreamsConfig.APPLICATION_ID_CONFIG;

@Configuration
public class KafkaStreamsConfig {
    @Value("${spring.kafka.schema-registry-url}")
    private String schemaRegistryUrl;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.auto.register.schemas}")
    private String autoRegisterSchemas;

    @Value("${spring.kafka.allow.auto.create.topics}")
    private String allowAutoCreateTopics;

    private ProducerFactory<String, User> producerFactory() {
        final HashMap<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        return new DefaultKafkaProducerFactory<>(props);
    }

    private HashMap<String, Object> getStringObjectHashMap() {
        final HashMap<String, Object> props = new HashMap<>();
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, SpecificAvroSerde.class);
        props.put(SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl);
        props.put(AUTO_REGISTER_SCHEMAS, autoRegisterSchemas);
        props.put(ALLOW_AUTO_CREATE_TOPICS_CONFIG, allowAutoCreateTopics);
        return props;
    }

    @Bean
    KafkaTemplate<String, User> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    public KafkaStreamsConfiguration getKafkaStreamsConfiguration(String applicationId) {
        final HashMap<String, Object> props = getStringObjectHashMap();
        props.put(APPLICATION_ID_CONFIG, applicationId);
        return new KafkaStreamsConfiguration(props);
    }

    public StreamsBuilderFactoryBean getStreamsBuilderFactoryBean(KafkaStreamsConfiguration kafkaStreamsConfiguration) {
        final var streamsBuilderFactoryBean = new StreamsBuilderFactoryBean(kafkaStreamsConfiguration);
        streamsBuilderFactoryBean.setCleanupConfig((new CleanupConfig(false, false)));
        // streamsBuilderFactoryBean.setStreamsUncaughtExceptionHandler(new );
        return streamsBuilderFactoryBean;
    }

    @Bean
    public KafkaStreamsConfiguration userStreamsConfig(@Value("${spring.kafka.streams.application.id}") String applicationId) {
        return getKafkaStreamsConfiguration(applicationId);
    }

    @Bean
    public StreamsBuilderFactoryBean userKStreamsBuilder(KafkaStreamsConfiguration userStreamsConfig) {
        return getStreamsBuilderFactoryBean(userStreamsConfig);
    }

    @Bean
    public Serde<User> userSerde(){
        final HashMap<String, Object> serdeConfig = new HashMap<>();
        serdeConfig.put(AUTO_REGISTER_SCHEMAS, autoRegisterSchemas);
        serdeConfig.put(SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl);

        final var specificAvroSerde = new SpecificAvroSerde<User>();
        specificAvroSerde.configure(serdeConfig, false);
        return specificAvroSerde;
    }
}
