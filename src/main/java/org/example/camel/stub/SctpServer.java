package org.example.camel.stub;

import org.mobicents.protocols.api.IpChannelType;
import org.mobicents.protocols.sctp.ManagementImpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class SctpServer {

    private static final String MANAGEMENT_NAME = "CamelStubSctp";
    private static final String SERVER_NAME = "camel-sctp-server";
    private static final String ASSOCIATION_NAME = "stubAssociation";

    private static final String LOCAL_IP = "127.0.0.1";
    private static final int LOCAL_PORT = 2905;

    private static final String REMOTE_IP = "127.0.0.1";
    private static final int REMOTE_PORT = 2906;

    private final ManagementImpl sctpManagement;

    private static final Path PERSIST_DIR = Path.of(
            System.getProperty("user.home"),
            "camel-sigtran",
            "stub"
    );

    public SctpServer() throws IOException {
        Files.createDirectories(PERSIST_DIR);

        sctpManagement = new ManagementImpl(MANAGEMENT_NAME);
        sctpManagement.setPersistDir(
                PERSIST_DIR.toString()
        );
    }

    public void start() throws Exception {

        sctpManagement.start();

        boolean serverExists = false;

        for (var server : sctpManagement.getServers()) {
            if (SERVER_NAME.equals(server.getName())) {
                serverExists = true;
                break;
            }
        }

        if (!serverExists) {
            sctpManagement.addServer(
                    SERVER_NAME,
                    LOCAL_IP,
                    LOCAL_PORT
            );
        }

        boolean associationExists = false;

        for (var association : sctpManagement.getAssociations().values()) {
            if (ASSOCIATION_NAME.equals(association.getName())) {
                associationExists = true;
                break;
            }
        }

        if (!associationExists) {
            sctpManagement.addServerAssociation(
                    REMOTE_IP,
                    REMOTE_PORT,
                    SERVER_NAME,
                    ASSOCIATION_NAME,
                    IpChannelType.SCTP
            );
        }

        var association = sctpManagement.getAssociation(ASSOCIATION_NAME);

        System.out.println(
                "SCTP association started: " + association.isStarted()
        );

        for (var server : sctpManagement.getServers()) {
            if (SERVER_NAME.equals(server.getName())) {

                if (!server.isStarted()) {
                    sctpManagement.startServer(SERVER_NAME);
                    System.out.println("SCTP server started");
                } else {
                    System.out.println("SCTP server already started");
                }

                break;
            }
        }

        System.out.println(
                "SCTP server listening on " + LOCAL_IP + ":" + LOCAL_PORT
        );

        System.out.println(
                "SCTP association started: " +
                        sctpManagement.getAssociation(ASSOCIATION_NAME).isStarted()
        );
    }

    public void stop() throws Exception {

        sctpManagement.stop();

        System.out.println("SCTP server stopped");
    }

    public ManagementImpl getManagement() {
        return sctpManagement;
    }

    public String getAssociationName() {
        return ASSOCIATION_NAME;
    }

}