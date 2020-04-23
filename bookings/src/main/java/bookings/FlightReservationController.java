package bookings;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import flightservice.*;

@RestController
public class FlightReservationController {

    @PostMapping(value = "/flight-reservations")
    public ResponseEntity<Object> reserveFlight(@RequestBody Flightservice.ReserveFlightCommand cmd) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8080).usePlaintext().build();

        FlightBookingServiceGrpc.FlightBookingServiceBlockingStub stub = FlightBookingServiceGrpc.newBlockingStub(channel);

        stub.reserveFlight(Flightservice.ReserveFlightCommand.newBuilder()
                .setReservationId(cmd.getReservationId())
                .setFlightNumber(cmd.getFlightNumber())
                .build());

        channel.shutdown();

        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @PostMapping(value = "/flight-cancellations")
    public ResponseEntity<Object> cancelFlightReservation(@RequestBody Flightservice.CancelFlightReservationCommand cmd) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8080).usePlaintext().build();

        FlightBookingServiceGrpc.FlightBookingServiceBlockingStub stub = FlightBookingServiceGrpc.newBlockingStub(channel);

        stub.cancelFlightReservation(Flightservice.CancelFlightReservationCommand.newBuilder()
                .setReservationId(cmd.getReservationId())
                .build());

        channel.shutdown();

        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @GetMapping("/flight-reservations/{reservationId}")
    public ResponseEntity<Flightservice.FlightReservation> getFlightReservation(@PathVariable("reservationId") String reservationId) {

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8080).usePlaintext().build();

        FlightBookingServiceGrpc.FlightBookingServiceBlockingStub stub = FlightBookingServiceGrpc.newBlockingStub(channel);

        Flightservice.FlightReservation reservation = stub.getFlightReservation(Flightservice.GetFlightReservationCommand.newBuilder()
                .setReservationId(reservationId)
                .build());

        channel.shutdown();

        return ResponseEntity.ok((Flightservice.FlightReservation)reservation);
    }
}