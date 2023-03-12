package kafka.producer;

import java.util.Properties;

public class JsonLocationCounters {

    private static Properties props = new Properties();

    public JsonLocationCounters() {
        props = KafkaConfig.getStreamingConfig();
    }

    
}
