 package bookings.domain;

 import com.google.protobuf.Empty;
 import io.cloudstate.javasupport.EntityId;
 import io.cloudstate.javasupport.eventsourced.CommandContext;
 import io.cloudstate.javasupport.eventsourced.CommandHandler;
 import io.cloudstate.javasupport.eventsourced.EventHandler;
 import io.cloudstate.javasupport.eventsourced.EventSourcedEntity;

 import bookings.*;
 import bookings.domain.*;

 /**
  * A hotel reservation domain entity.
  */
 @EventSourcedEntity
 public class HotelReservationEntity {

     private String reservationId;

     /**
      * The hotel being reserved.
      */
     private String hotel;

     /**
      * The room being reserved.
      */
     private String roomNumber;

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
     public HotelReservationEntity(@EntityId String reservationId) {
         this.reservationId = reservationId;
     }

     /**
      * Put this entity in the reserved state and emit event.
      */
     @CommandHandler
     public Empty reserveHotelHandler(Bookings.ReserveHotelCommand cmd, CommandContext ctx) {
         if (reserved) {
             ctx.fail("Hotel room already reserved");
         } else if (cancelled) {
             ctx.fail("Cancelled hotel room cannot be reserved again.");
         }

         ctx.emit(Domain.HotelReserved.newBuilder()
                 .setReservationId(cmd.getReservationId())
                 .setHotel(cmd.getHotel())
                 .setRoomNumber(cmd.getRoomNumber()).build());

         return Empty.getDefaultInstance();
     }

     /**
      * Handle reserved event previously emitted.
      */
     @EventHandler
     public void hotelReservedHandler(Domain.HotelReserved hotelReserved) {
         reserved = true;
         hotel = hotelReserved.getHotel();
         roomNumber = hotelReserved.getRoomNumber();
     }

     /**
      * Put this entity in the cancelled state and emit event.
      */
     @CommandHandler
     public Empty cancelHotelHandler(Bookings.CancelHotelReservationCommand cmd, CommandContext ctx) {
         if (!reserved) {
             ctx.fail("Hotel room must be reserved before it can be cancelled.");
         } else if (cancelled) {
             ctx.fail("Cancelled hotel room cannot be cancelled again.");
         }
         ctx.emit(Domain.HotelCancelled.newBuilder()
                 .setReservationId(cmd.getReservationId()).build());

         return Empty.getDefaultInstance();
     }

     /**
      * Handle cancelled event previously emitted.
      */
     @EventHandler
     public void hotelCancelledHandler(Domain.HotelCancelled hotelCancelled) {
         cancelled = true;
     }
 }