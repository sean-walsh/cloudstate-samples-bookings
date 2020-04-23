package bookings;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import carservice.*;

@RestController
public class CarReservationController {

    @PostMapping(value = "/car-reservations")
    public ResponseEntity<Object> reserveCar(@RequestBody Carservice.ReserveCarCommand cmd) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8080).usePlaintext().build();

        CarBookingServiceGrpc.CarBookingServiceBlockingStub stub = CarBookingServiceGrpc.newBlockingStub(channel);

        stub.reserveCar(Carservice.ReserveCarCommand.newBuilder()
                .setReservationId(cmd.getReservationId())
                .setCompany(cmd.getCompany())
                .setCarType(cmd.getCarType())
                .build());

        channel.shutdown();

        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @PostMapping(value = "/car-cancellations")
    public ResponseEntity<Object> cancelCarReservation(@RequestBody Carservice.CancelCarReservationCommand cmd) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8080).usePlaintext().build();

        CarBookingServiceGrpc.CarBookingServiceBlockingStub stub = CarBookingServiceGrpc.newBlockingStub(channel);

        stub.cancelCarReservation(Carservice.CancelCarReservationCommand.newBuilder()
                .setReservationId(cmd.getReservationId())
                .build());

        channel.shutdown();

        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @GetMapping("/car-reservations/{reservationId}")
    public ResponseEntity<Carservice.CarReservation> getCarReservation(@PathVariable("reservationId") String reservationId) {

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8080).usePlaintext().build();

        CarBookingServiceGrpc.CarBookingServiceBlockingStub stub = CarBookingServiceGrpc.newBlockingStub(channel);

        Carservice.CarReservation reservation = stub.getCarReservation(Carservice.GetCarReservationCommand.newBuilder()
                .setReservationId(reservationId)
                .build());

        channel.shutdown();

        return ResponseEntity.ok((Carservice.CarReservation)reservation);
    }
}