package wirelessmesh;

import service.Wirelessmeshservice;
import wirelessmesh.domain.*;
import io.cloudstate.javasupport.CloudState;

import domain.*;

public class WirelessMeshMain {

    public static void main(String... args) {
        new CloudState()
                .registerEventSourcedEntity(
                        DeviceEntity.class,
                        Wirelessmeshservice.getDescriptor().findServiceByName("WirelessMeshService"),
                        Devicedomain.getDescriptor())
                .start();
    }
}
