/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package kafka.producer;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import java.util.Properties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import kafka.producer.data.Ride;

import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class JsonProducer {
    
    private static final String rides_topic = Topics.RIDES_TOPIC;
    private static Properties props = new Properties();

    public JsonProducer() {
        props = KafkaConfig.getProducerConfig();
    }

    public List<Ride> getRides() throws IOException {
        var ridesStream = this.getClass().getResource("/rides.csv");
        var reader = new CSVReader(new FileReader(ridesStream.getFile()));
        reader.skip(1);
        return reader.readAll().stream().map(arr -> new Ride(arr))
                .collect(Collectors.toList());

    }

    public void publishRides(List<Ride> rides) throws ExecutionException, InterruptedException {

        KafkaProducer<String, Ride> kafkaProducer = new KafkaProducer<String, Ride>(props);
        for(Ride ride: rides) {
            ride.tpep_pickup_datetime = LocalDateTime.now().minusMinutes(20);
            ride.tpep_dropoff_datetime = LocalDateTime.now();
            var record = kafkaProducer.send(
                new ProducerRecord<>(rides_topic, String.valueOf(ride.DOLocationID), ride), 
                (metadata, exception) -> {
                    if(exception != null) {
                        System.out.println(exception.getMessage());
                    }
                }
            );
            System.out.println(record.get().offset());
            System.out.println(ride.DOLocationID);
            Thread.sleep(500);
        }
        kafkaProducer.close();
    }

    public static void main(String[] args) throws IOException, CsvException, ExecutionException, InterruptedException  {
        var producer = new JsonProducer();
        var rides = producer.getRides();
        producer.publishRides(rides);
    }
}