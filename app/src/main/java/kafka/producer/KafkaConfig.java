package kafka.producer;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.streams.StreamsConfig;
import java.util.Properties;

public class KafkaConfig {
 
    private static Properties props = new Properties();
    private static final String bootstrapServer = "pkc-3w22w.us-central1.gcp.confluent.cloud:9092";
    private static String jaasConfigTemplate = "org.apache.kafka.common.security.plain.PlainLoginModule required username='%s' password='%s';";

    public static Properties getProducerConfig() {

        final String jaasConfig = String.format(jaasConfigTemplate, Secrets.KAFKA_CLUSTER_KEY, Secrets.KAFKA_CLUSTER_SECRET);
        
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        props.put("security.protocol", "SASL_SSL");
        props.put("sasl.jaas.config", jaasConfig);
        props.put("sasl.mechanism", "PLAIN");
        props.put("client.dns.lookup", "use_all_dns_ips");
        props.put("session.timeout.ms", "45000");
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "io.confluent.kafka.serializers.KafkaJsonSerializer");

        return props;
    }
}
