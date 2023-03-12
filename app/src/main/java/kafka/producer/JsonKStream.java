package kafka.producer;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Produced;
import kafka.producer.customserdes.CustomSerdes;
import kafka.producer.data.Ride;

import java.util.Properties;

public class JsonKStream {

    private static Properties props = new Properties();
    private static String inputRidesTopic;
    private static String outputRidesCountersTopic;

    public JsonKStream() {
        props = KafkaConfig.getStreamingConfig();
        inputRidesTopic = Topics.RIDES_TOPIC;
        outputRidesCountersTopic = Topics.RIDES_COUNTERS_TOPIC;
    }

    public Topology createTopology() {
        StreamsBuilder streamsBuilder = new StreamsBuilder();
        var ridesStream = streamsBuilder.stream(inputRidesTopic, Consumed.with(Serdes.String(), CustomSerdes.getSerde(Ride.class)));
        var puLocationCount = ridesStream.groupByKey().count().toStream();

        puLocationCount.to(outputRidesCountersTopic, Produced.with(Serdes.String(), Serdes.Long()));
        return streamsBuilder.build();
    }

    public void countPLocation() throws InterruptedException {
        var topology = createTopology();
        var kStreams = new KafkaStreams(topology, props);
        kStreams.start();
        while (kStreams.state() != KafkaStreams.State.RUNNING) {
            System.out.println(kStreams.state());
            Thread.sleep(1000);
        }
        System.out.println(kStreams.state());
        Runtime.getRuntime().addShutdownHook(new Thread(kStreams::close));
    }

    public static void main(String[] args) throws InterruptedException {
        var object = new JsonKStream();
        object.countPLocation();
    }
    
}
