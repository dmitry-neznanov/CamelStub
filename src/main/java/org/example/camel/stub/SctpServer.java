package org.example.camel.stub;

import org.mobicents.protocols.api.IpChannelType;
import org.mobicents.protocols.sctp.ManagementImpl;

import java.io.IOException;

public class SctpServer {

    private static final String MANAGEMENT_NAME = "CamelStubSctp";
    private static final String SERVER_NAME = "camel-sctp-server";
    private static final String ASSOCIATION_NAME = "stubAssociation";

    private static final String LOCAL_IP = "127.0.0.1";
    private static final int LOCAL_PORT = 2905;

    private static final String REMOTE_IP = "127.0.0.1";
    private static final int REMOTE_PORT = 2906;

    private final ManagementImpl sctpManagement;

    public SctpServer() throws IOException {
        sctpManagement = new ManagementImpl(MANAGEMENT_NAME);
    }

    public void start() throws Exception {

        sctpManagement.start();

        sctpManagement.addServer(
                SERVER_NAME,
                LOCAL_IP,
                LOCAL_PORT
        );

        sctpManagement.addServerAssociation(
                REMOTE_IP,
                REMOTE_PORT,
                SERVER_NAME,
                ASSOCIATION_NAME,
                IpChannelType.SCTP
        );

        sctpManagement.getAssociation(ASSOCIATION_NAME)
                .setAssociationListener(new SctpAssociationListener());

        sctpManagement.startServer(SERVER_NAME);

        System.out.println(
                "SCTP server started on " + LOCAL_IP + ":" + LOCAL_PORT
        );
    }

    public void stop() throws Exception {

        sctpManagement.stop();

        System.out.println("SCTP server stopped");
    }
}