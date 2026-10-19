package org.example.camel.lifecycle;

import org.example.camel.stub.M3uaServer;
import org.example.camel.stub.SccpServer;
import org.example.camel.stub.SctpServer;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

@Component
public class SigtranLifecycle implements SmartLifecycle {

    private SctpServer sctpServer;
    private M3uaServer m3uaServer;
    private SccpServer sccpServer;

    private volatile boolean running = false;

    @Override
    public synchronized void start() {

        if (running) {
            return;
        }

        try {
            sctpServer = new SctpServer();
            sctpServer.start();

            m3uaServer = new M3uaServer(
                    sctpServer.getManagement(),
                    sctpServer.getAssociationName()
            );
            m3uaServer.start();

            sccpServer = new SccpServer(
                    m3uaServer.getManagement()
            );
            sccpServer.start();

            m3uaServer.startAsp();

            running = true;

            System.out.println("CamelStub SIGTRAN stack started");

        } catch (Exception e) {
            stop();
            throw new IllegalStateException(
                    "Failed to start SIGTRAN stack", e
            );
        }
    }

    @Override
    public synchronized void stop() {

        running = false;

        // Останавливаем в обратном порядке
        try {
            if (sccpServer != null) {
                sccpServer.stop();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (m3uaServer != null) {
                m3uaServer.stop();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (sctpServer != null) {
                sctpServer.stop();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public int getPhase() {
        return 0;
    }
}
