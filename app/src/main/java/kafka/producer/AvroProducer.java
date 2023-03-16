package kafka.producer;

import schemaregistry.RideRecord;

import java.util.Properties;
import java.util.ArrayList;
import java.util.List;


public class AvroProducer {

    private static Properties props;

    public AvroProducer() {
        props = KafkaConfig.getAvroProducerConfig();
    }

    public void getRides() {

        List<RideRecord> rides = new ArrayList<RideRecord>();
        RideRecord ride = new RideRecord();

        ride.setVendorId(1);
        ride.setPassengerCount(22);
        ride.setTripDistance(7.2);

        rides.add(ride);

        System.out.println(rides.get(0));
    }

    public static void main(String[] args) {
        new AvroProducer().getRides();
    }
}
