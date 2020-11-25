 package wirelessmesh.domain;

 import com.google.protobuf.Empty;
 import io.cloudstate.javasupport.EntityId;
 import io.cloudstate.javasupport.eventsourced.CommandContext;
 import io.cloudstate.javasupport.eventsourced.CommandHandler;
 import io.cloudstate.javasupport.eventsourced.EventHandler;
 import io.cloudstate.javasupport.eventsourced.EventSourcedEntity;

 import domain.Devicedomain.*;
 import service.Wirelessmeshservice.*;

 /**
  * A device domain entity.
  */
 @EventSourcedEntity
 public class DeviceEntity {

     /**
      * Has the customer activated this newly acquired device.
      */
     private boolean activated = false;

     private String deviceId;

     /**
      * Unique customer id associated with the device.
      */
     private String customerId;

     /**
      * The room in which this device is located.
      */
     private String room = "";

     /**
      * Whether the device nightlight is on or off.
      */
     private boolean nightlightOn = false;

     /**
      * Constructor.
      * @param deviceId The entity id will be the deviceId.
      */
     public DeviceEntity(@EntityId String deviceId) {
         this.deviceId = deviceId;
     }

     /**
      * Activate this device and emit event.
      */
     @CommandHandler
     public Empty activateDevice(ActivateDeviceCommand cmd, CommandContext ctx) {
         if (activated) {
             ctx.fail("Device already activated");
         }

         ctx.emit(DeviceActivated.newBuilder()
                 .setDeviceId(cmd.getDeviceId())
                 .setCustomerId(cmd.getCustomerId()).build());

         return Empty.getDefaultInstance();
     }

     /**
      * Assign a room for this device and emit event.
      */
     @CommandHandler
     public Empty assignRoom(AssignRoomCommand cmd, CommandContext ctx) {
         ctx.emit(RoomAssigned.newBuilder()
                 .setDeviceId(cmd.getDeviceId())
                 .setRoom(cmd.getRoom()).build());

         return Empty.getDefaultInstance();
     }

     /**
      * Set the nightlight to the opposite of on or off and emit event.
      */
     @CommandHandler
     public Empty toggleNightlight(ToggleNightlightCommand cmd, CommandContext ctx) {
         ctx.emit(NightlightToggled.newBuilder()
              .setDeviceId(cmd.getDeviceId())
              .setNightlightOn(!nightlightOn).build());

         return Empty.getDefaultInstance();
     }

     /**
      * Update state on successful persistence of DeviceActivated event.
      */
     @EventHandler
     public void deviceActivated(DeviceActivated deviceActivated) {
         activated = true;
         customerId = deviceActivated.getCustomerId();
     }

     /**
      * Update state on successful persistence of RoomAssigned event.
      */
     @EventHandler
     public void roomAssigned(RoomAssigned roomAssigned) {
         room = roomAssigned.getRoom();
     }

     /**
      * Update state on successful persistence of NightlightToggled event.
      * @param nightlightToggled
      */
     @EventHandler
     public void nightlightToggled(NightlightToggled nightlightToggled) {
         nightlightOn = nightlightToggled.getNightlightOn();
     }

     /**
      * Get the current state of this device.
      */
     @CommandHandler
     public Device getDevice(GetDeviceCommand cmd, CommandContext ctx) {
         return Device.newBuilder()
                 .setDeviceId(deviceId)
                 .setActivated(activated)
                 .setCustomerId(customerId)
                 .setRoom(room)
                 .setNightlightOn(nightlightOn)
                 .build();
     }
 }