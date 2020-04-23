package bookings;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import hotelservice.*;

@RestController
public class HotelReservationController {

    @PostMapping(value = "/hotel-reservations")
    public ResponseEntity<Object> reserveHotel(@RequestBody Hotelservice.ReserveHotelCommand cmd) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8080).usePlaintext().build();

        HotelBookingServiceGrpc.HotelBookingServiceBlockingStub stub = HotelBookingServiceGrpc.newBlockingStub(channel);

        stub.reserveHotel(Hotelservice.ReserveHotelCommand.newBuilder()
                .setReservationId(cmd.getReservationId())
                .setHotel(cmd.getHotel())
                .setRoomNumber(cmd.getRoomNumber())
                .build());

        channel.shutdown();

        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @PostMapping(value = "/hotel-cancellations")
    public ResponseEntity<Object> cancelHotelReservation(@RequestBody Hotelservice.CancelHotelReservationCommand cmd) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8080).usePlaintext().build();

        HotelBookingServiceGrpc.HotelBookingServiceBlockingStub stub = HotelBookingServiceGrpc.newBlockingStub(channel);

        stub.cancelHotelReservation(Hotelservice.CancelHotelReservationCommand.newBuilder()
                .setReservationId(cmd.getReservationId())
                .build());

        channel.shutdown();

        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @GetMapping("/hotel-reservations/{reservationId}")
    public ResponseEntity<Hotelservice.HotelReservation> getHotelReservation(@PathVariable("reservationId") String reservationId) {

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8080).usePlaintext().build();

        HotelBookingServiceGrpc.HotelBookingServiceBlockingStub stub = HotelBookingServiceGrpc.newBlockingStub(channel);

        Hotelservice.HotelReservation reservation = stub.getHotelReservation(Hotelservice.GetHotelReservationCommand.newBuilder()
                .setReservationId(reservationId)
                .build());

        channel.shutdown();

        return ResponseEntity.ok((Hotelservice.HotelReservation)reservation);
    }
}