 package bookings.domain;

 import com.google.protobuf.Empty;
 import io.cloudstate.javasupport.EntityId;
 import io.cloudstate.javasupport.eventsourced.*;
 import io.cloudstate.javasupport.eventsourced.EventSourcedEntity;

 import flightdomain.Flightdomain.*;
 import flightservice.Flightservice.*;

 /**
  * A flight reservation domain entity.
  */
 @EventSourcedEntity
 public class FlightReservationEntity {

     private String reservationId;

     /**
      * The user doing the reserving.
      */
     private String userId;

     /**
      * The flight number being reserved.
      */
     private String flightNumber;

     /**
      * This reservation has received the reserve command and is in the reserved state.
      */
     private Boolean reserved = false;

     /**
      * This reservation has received the cancel command and is in the cancellation state.
      */
     private Boolean cancelled = false;

     /**
      * Constructor.
      * @param reservationId The entity id will be the same as this.
      */
     public FlightReservationEntity(@EntityId String reservationId) {
         this.reservationId = reservationId;
     }

     /**
      * Put this entity in the reserved state and emit event.
      */
     @CommandHandler
     public Empty reserveFlight(ReserveFlightCommand cmd, CommandContext ctx) {
         if (reserved)
             ctx.fail("Flight already reserved");
         else if (cancelled)
             ctx.fail("Cancelled flight cannot be reserved again.");

         ctx.emit(FlightReserved.newBuilder()
                 .setReservationId(cmd.getReservationId())
                 .setUserId(cmd.getUserId())
                 .setFlightNumber(cmd.getFlightNumber()).build());

         return Empty.getDefaultInstance();
     }

     /**
      * Handle reserved event previously emitted.
      */
     @EventHandler
     public void flightReserved(FlightReserved flightReserved) {
         reserved = true;
         userId = flightReserved.getUserId();
         flightNumber = flightReserved.getFlightNumber();
     }

     /**
      * Put this entity in the cancelled state and emit event.
      */
     @CommandHandler
     public Empty cancelFlightReservation(CancelFlightReservationCommand cmd, CommandContext ctx) {
         if (!reserved)
             ctx.fail("Flight must be reserved before it can be cancelled.");
         else if (cancelled)
             ctx.fail("Cancelled flight cannot be cancelled again.");

         ctx.emit(FlightCancelled.newBuilder()
                 .setReservationId(cmd.getReservationId()).build());

         return Empty.getDefaultInstance();
     }

     /**
      * Handle cancelled event previously emitted.
      */
     @EventHandler
     public void flightCancelled(FlightCancelled flightCancelled) {
         cancelled = true;
     }

     /**
      * Get the current state of this reservation.
      */
     @CommandHandler
     public FlightReservation getFlightReservation(GetFlightReservationCommand cmd, CommandContext ctx) {
         if (!reserved)
             ctx.fail("Flight must be reserved before it can be retrieved.");

         return FlightReservation.newBuilder()
                 .setReservationId(reservationId)
                 .setUserId(userId)
                 .setFlightNumber(flightNumber)
                 .setCancelled(cancelled)
                 .build();
     }
 }