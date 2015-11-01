package ru.jmessenger.application;

/**
 * Created by dima on 01.11.15.
 */

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.UpnpServiceImpl;
import org.fourthline.cling.model.message.header.STAllHeader;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.registry.Registry;
import org.fourthline.cling.registry.RegistryListener;
import org.fourthline.cling.support.igd.PortMappingListener;
import org.fourthline.cling.support.model.PortMapping;

/**
 * Runs a simple UPnP discovery procedure.
 */
public class TestUPNP {

    public static void main(String[] args) throws Exception {

        PortMapping desiredMapping =
                new PortMapping(
                        3128,
                        "192.168.1.20",
                        PortMapping.Protocol.TCP,
                        "My Port Mapping"
                );

        UpnpService upnpService =
                new UpnpServiceImpl(
                        new PortMappingListener(desiredMapping)
                );

        upnpService.getControlPoint().search();
        Thread.sleep(120000); //2min
        upnpService.shutdown(); //close port
        //without shutdown port will remain be open

    }
}