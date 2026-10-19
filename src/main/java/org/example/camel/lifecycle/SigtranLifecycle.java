package org.example.camel.lifecycle;

import org.example.camel.stub.*;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

@Component
public class SigtranLifecycle implements SmartLifecycle {

    private SctpServer sctpServer;
    private M3uaServer m3uaServer;
    private SccpServer sccpServer;
    private TcapServer tcapServer;
    private CapServer capServer;

    private volatile boolean running = false;

    @Override
    public synchronized void start() {

        if (running) {
            return;
        }

        try {
            // 1. SCTP
            sctpServer = new SctpServer();
            sctpServer.start();

            // 2. M3UA
            m3uaServer = new M3uaServer(
                    sctpServer.getManagement(),
                    sctpServer.getAssociationName()
            );
            m3uaServer.start();

            // 3. SCCP
            sccpServer = new SccpServer(
                    m3uaServer.getManagement()
            );
            sccpServer.start();

            // 4. TCAP
            tcapServer = new TcapServer(
                    sccpServer.getStack().getSccpProvider()
            );
            tcapServer.start();

            // CAP
            capServer = new CapServer(
                    tcapServer.getStack().getProvider()
            );

            capServer.start();

            // 5. Запускаем M3UA ASP
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

        // Останавливаем компоненты в обратном порядке

        try {
            if (capServer != null) {
                capServer.stop();
                capServer = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            if (tcapServer != null) {
                tcapServer.stop();
                tcapServer = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (sccpServer != null) {
                sccpServer.stop();
                sccpServer = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (m3uaServer != null) {
                m3uaServer.stop();
                m3uaServer = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (sctpServer != null) {
                sctpServer.stop();
                sctpServer = null;
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