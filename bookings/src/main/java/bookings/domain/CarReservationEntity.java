 package bookings.domain;

 import com.google.protobuf.Empty;
 import io.cloudstate.javasupport.EntityId;
 import io.cloudstate.javasupport.eventsourced.CommandContext;
 import io.cloudstate.javasupport.eventsourced.CommandHandler;
 import io.cloudstate.javasupport.eventsourced.EventHandler;
 import io.cloudstate.javasupport.eventsourced.EventSourcedEntity;

 import cardomain.Cardomain.*;
 import carservice.Carservice.*;

 /**
  * A car reservation domain entity.
  */
 @EventSourcedEntity
 public class CarReservationEntity {

     private String reservationId;

     /**
      * The user doing the reserving.
      */
     private String userId;

     /**
      * The car rental company being reserved.
      */
     private String company;

     /**
      * The car type being reserved.
      */
     private String carType;

     /**
      * This reservation has received the reserve command and is in the reserved state.
      */
     private Boolean reserved;

     /**
      * This reservation has received the cancel command and is in the cancellation state.
      */
     private Boolean cancelled;

     /**
      * Constructor.
      * @param reservationId The entity id will be the same as this.
      */
     public CarReservationEntity(@EntityId String reservationId) {
         this.reservationId = reservationId;
     }

     /**
      * Put this entity in the reserved state and emit event.
      */
     @CommandHandler
     public Empty reserveCarHandler(ReserveCarCommand cmd, CommandContext ctx) {
         if (reserved)
             ctx.fail("Car room already reserved");
         else if (cancelled)
             ctx.fail("Cancelled car cannot be reserved again.");

         ctx.emit(CarReserved.newBuilder()
                 .setReservationId(cmd.getReservationId())
                 .setUserId(cmd.getUserId())
                 .setCompany(cmd.getCompany())
                 .setCarType(cmd.getCarType()).build());

         return Empty.getDefaultInstance();
     }

     /**
      * Handle reserved event previously emitted.
      */
     @EventHandler
     public void carReservedHandler(CarReserved carReserved) {
         reserved = true;
         userId = carReserved.getUserId();
         company = carReserved.getCompany();
         carType = carReserved.getCarType();
     }

     /**
      * Put this entity in the cancelled state and emit event.
      */
     @CommandHandler
     public Empty cancelCarHandler(CancelCarReservationCommand cmd, CommandContext ctx) {
         if (!reserved)
             ctx.fail("Car must be reserved before it can be cancelled.");
         else if (cancelled)
             ctx.fail("Cancelled car cannot be cancelled again.");

         ctx.emit(CarCancelled.newBuilder()
                 .setReservationId(cmd.getReservationId()).build());

         return Empty.getDefaultInstance();
     }

     /**
      * Handle cancelled event previously emitted.
      */
     @EventHandler
     public void carCancelledHandler(CarCancelled carCancelled) {
         cancelled = true;
     }

     /**
      * Get the current state of this reservation.
      */
     @CommandHandler
     public CarReservation getReservation(GetCarReservationCommand cmd, CommandContext ctx) {
         if (!reserved)
             ctx.fail("Car must be reserved before it can be retrieved.");

             return CarReservation.newBuilder()
                     .setReservationId(reservationId)
                     .setUserId(userId)
                     .setCompany(company)
                     .setCarType(carType)
                     .build();
         }
 }