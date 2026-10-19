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

        // Проверяем существование SCTP-сервера
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

        // Проверяем существование серверной association
        if (sctpManagement.getAssociation(ASSOCIATION_NAME) == null) {
            sctpManagement.addServerAssociation(
                    REMOTE_IP,
                    REMOTE_PORT,
                    SERVER_NAME,
                    ASSOCIATION_NAME,
                    IpChannelType.SCTP
            );
        }

        // Запускаем сервер, если он ещё не запущен
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