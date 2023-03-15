package kafka.producer;

import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.streams.StreamsConfig;
import java.util.Properties;

public class KafkaConfig {
 
    private static Properties props = new Properties();
    private static final String bootstrapServer = "pkc-3w22w.us-central1.gcp.confluent.cloud:9092";
    private static final String schemaRegistryEndpoint = "https://psrc-35wr2.us-central1.gcp.confluent.cloud";
    private static String jaasConfig = String.format(
        "org.apache.kafka.common.security.plain.PlainLoginModule required username='%s' password='%s';",
        Secrets.KAFKA_CLUSTER_KEY, 
        Secrets.KAFKA_CLUSTER_SECRET
    );
  

    private static Properties getBasicConfigs() {

        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        props.put("security.protocol", "SASL_SSL");
        props.put("sasl.jaas.config", jaasConfig);
        props.put("sasl.mechanism", "PLAIN");
        props.put("client.dns.lookup", "use_all_dns_ips");
        props.put("session.timeout.ms", "45000");

        return props;
    }


    public static Properties getProducerConfig() {
        
        props = getBasicConfigs();

        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "io.confluent.kafka.serializers.KafkaJsonSerializer");

        return props;
    }


    public static Properties getAvroProducerConfig() {

        props = getBasicConfigs();

        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class.getName());
        props.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryEndpoint);
        props.put("basic.auth.credentials.source", "USER_INFO");
        props.put("basic.auth.user.info", Secrets.SCHEMA_REGISTRY_KEY+":"+Secrets.SCHEMA_REGISTRY_SECRET);

        return props;
    }

    
    public static Properties getStreamingConfig(String applicationID) {

        props = getBasicConfigs();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, applicationID);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);

        return props;
    }
}
