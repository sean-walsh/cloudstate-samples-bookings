package bookings;

import io.cloudstate.javasupport.*;

import bookings.domain.*;
import flightservice.*;
import flightdomain.*;
import hotelservice.*;
import hoteldomain.*;
import carservice.*;
import cardomain.*;

public final class Main {
    public static void main(String[] args) throws Exception {
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
