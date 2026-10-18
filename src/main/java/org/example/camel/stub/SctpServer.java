package org.example.camel.stub;


import org.mobicents.protocols.api.IpChannelType;
import org.mobicents.protocols.sctp.ManagementImpl;

public class SctpServer {

    private static final String SERVER_NAME = "camel-sctp-server";
    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 2905;

    private final ManagementImpl sctpManagement;

    public SctpServer() throws Exception {
        sctpManagement = new ManagementImpl("CamelStubSctp");
    }

    public void start() throws Exception {

        // Инициализация SCTP Management
        sctpManagement.start();

        // Создание SCTP-сервера
        sctpManagement.addServer(
                SERVER_NAME,
                SERVER_IP,
                SERVER_PORT
        );

        sctpManagement.addServerAssociation(
                "127.0.0.1",
                2906,
                SERVER_NAME,
                "stubAssociation",
                IpChannelType.SCTP
        );

        // Запуск SCTP-сервера
        sctpManagement.startServer(SERVER_NAME);

        System.out.println(
                "SCTP server started on " +
                        SERVER_IP + ":" + SERVER_PORT
        );
    }

    public void stop() throws Exception {
        sctpManagement.stop();
    }
}
