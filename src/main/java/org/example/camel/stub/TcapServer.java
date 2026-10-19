package org.example.camel.stub;

import org.restcomm.protocols.ss7.sccp.SccpProvider;
import org.restcomm.protocols.ss7.tcap.TCAPStackImpl;

public class TcapServer {

    private static final String STACK_NAME = "CamelStubTcap";
    private static final int LOCAL_SSN = 8;

    private final TCAPStackImpl tcapStack;

    public TcapServer(SccpProvider sccpProvider) {

        tcapStack = new TCAPStackImpl(
                STACK_NAME,
                sccpProvider,
                LOCAL_SSN
        );
    }

    public void start() throws Exception {

        System.out.println("Starting TCAP server...");

        tcapStack.start();

        System.out.println("TCAP server started");
    }

    public void stop() {

        tcapStack.stop();

        System.out.println("TCAP server stopped");
    }

    public TCAPStackImpl getStack() {
        return tcapStack;
    }
}
