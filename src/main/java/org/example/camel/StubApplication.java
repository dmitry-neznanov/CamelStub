package org.example.camel;

import org.example.camel.stub.SctpServer;

public class StubApplication {

    public static void main(String[] args) throws Exception {
        SctpServer server = new SctpServer();
        server.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                server.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));

        new java.util.concurrent.CountDownLatch(1).await();
    }
}
