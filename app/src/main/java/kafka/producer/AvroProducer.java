package kafka.producer;

import schemaregistry.RideRecord;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import com.opencsv.CSVReader;
import java.io.FileReader;

import java.util.stream.Collectors;
import java.util.Properties;
import java.util.List;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import com.opencsv.exceptions.CsvException;


public class AvroProducer {

    private static Properties props;
    private static final String ridesFilePath = "/rides.csv";

    public AvroProducer() {
        props = KafkaConfig.getAvroProducerConfig();
    }

    public List<RideRecord> getRides() throws IOException, CsvException {

        var ridesResource = this.getClass().getResource(ridesFilePath);
        var ridesReader = new CSVReader(new FileReader(ridesResource.getFile()));
        ridesReader.skip(1);

        return ridesReader
                .readAll()
                .stream()
                .map(
                    row -> RideRecord
                            .newBuilder()    
                            .setVendorId(Integer.parseInt(row[0]))
                            .setPassengerCount(Integer.parseInt(row[3]))   
                            .setTripDistance(Double.parseDouble(row[4])) 
                            .setRateCodeId(Integer.parseInt(row[5]))
                            .build()
                ).collect(Collectors.toList());
    }

    public void publishRides(List<RideRecord> rides) throws ExecutionException, InterruptedException {
        
        KafkaProducer<String, RideRecord> kafkaProducer = new KafkaProducer<>(props);

        for (RideRecord ride : rides) 
        {
            var record = kafkaProducer.send(
                    new ProducerRecord<>(
                            "rides_avro", 
                            String.valueOf(ride.getVendorId()),
                            ride
                        ), 
                    (metadata, exception) -> {
                        if (exception != null) {
                            System.out.println(exception.getMessage());
                        }
                    }
            );
            System.out.println(record.get().offset());
            Thread.sleep(500);
        }

        kafkaProducer.close();
    }

    public static void main(String[] args) throws IOException, CsvException, ExecutionException, InterruptedException {
        var producer = new AvroProducer();
        var rideRecords = producer.getRides();
        producer.publishRides(rideRecords);
    }

}
