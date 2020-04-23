package bookings;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.cloudstate.javasupport.*;

import bookings.domain.*;
import flightservice.*;
import flightdomain.*;
import hotelservice.*;
import hoteldomain.*;
import carservice.*;
import cardomain.*;

@SpringBootApplication
public class BookingSpringApplication {
    public static void main(String[] args) throws java.lang.InterruptedException, java.util.concurrent.ExecutionException {
        SpringApplication.run(BookingSpringApplication.class, args);

        new CloudState()
                .registerEventSourcedEntity(
                        FlightReservationEntity.class,
                        Flightservice.getDescriptor().findServiceByName("FlightBookingService"),
                        Flightdomain.getDescriptor())
                .registerEventSourcedEntity(
                        HotelReservationEntity.class,
                        Hotelservice.getDescriptor().findServiceByName("HotelBookingService"),
                        Hoteldomain.getDescriptor())
                .registerEventSourcedEntity(
                        CarReservationEntity.class,
                        Carservice.getDescriptor().findServiceByName("CarBookingService"),
                        Cardomain.getDescriptor())
                .start()
                .toCompletableFuture()
                .get();
    }
}
