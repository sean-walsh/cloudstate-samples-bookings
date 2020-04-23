 package bookings.domain;

 import com.google.protobuf.Empty;
 import io.cloudstate.javasupport.EntityId;
 import io.cloudstate.javasupport.eventsourced.CommandContext;
 import io.cloudstate.javasupport.eventsourced.CommandHandler;
 import io.cloudstate.javasupport.eventsourced.EventHandler;
 import io.cloudstate.javasupport.eventsourced.EventSourcedEntity;

 import carservice.*;
 import cardomain.*;

 /**
  * A car reservation domain entity.
  */
 @EventSourcedEntity
 public class CarReservationEntity {

     private String reservationId;

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
     public Empty reserveCarHandler(Carservice.ReserveCarCommand cmd, CommandContext ctx) {
         if (reserved) {
             ctx.fail("Car room already reserved");
         } else if (cancelled) {
             ctx.fail("Cancelled car cannot be reserved again.");
         }

         ctx.emit(Cardomain.CarReserved.newBuilder()
                 .setReservationId(cmd.getReservationId())
                 .setCompany(cmd.getCompany())
                 .setCarType(cmd.getCarType()).build());

         return Empty.getDefaultInstance();
     }

     /**
      * Handle reserved event previously emitted.
      */
     @EventHandler
     public void carReservedHandler(Cardomain.CarReserved carReserved) {
         reserved = true;
         company = carReserved.getCompany();
         carType = carReserved.getCarType();
     }

     /**
      * Put this entity in the cancelled state and emit event.
      */
     @CommandHandler
     public Empty cancelCarHandler(Carservice.CancelCarReservationCommand cmd, CommandContext ctx) {
         if (!reserved) {
             ctx.fail("Car must be reserved before it can be cancelled.");
         } else if (cancelled) {
             ctx.fail("Cancelled car cannot be cancelled again.");
         }
         ctx.emit(Cardomain.CarCancelled.newBuilder()
                 .setReservationId(cmd.getReservationId()).build());

         return Empty.getDefaultInstance();
     }

     /**
      * Handle cancelled event previously emitted.
      */
     @EventHandler
     public void carCancelledHandler(Cardomain.CarCancelled carCancelled) {
         cancelled = true;
     }
 }