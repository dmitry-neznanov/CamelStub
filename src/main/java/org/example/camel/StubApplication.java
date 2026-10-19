package org.example.camel;

import org.example.camel.stub.M3uaServer;
import org.example.camel.stub.SctpServer;

public class StubApplication {

    public static void main(String[] args) throws Exception {

        SctpServer sctpServer = new SctpServer();

        sctpServer.start();

        M3uaServer m3uaServer = new M3uaServer(
                sctpServer.getManagement(),
                sctpServer.getAssociationName()
        );

        m3uaServer.start();
        m3uaServer.startAsp();

        System.out.println("CamelStub started");
    }
}