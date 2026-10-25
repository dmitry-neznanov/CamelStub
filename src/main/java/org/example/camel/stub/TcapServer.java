package org.example.camel.stub;

import org.restcomm.protocols.ss7.sccp.SccpProvider;
import org.restcomm.protocols.ss7.tcap.TCAPStackImpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class TcapServer {

    private static final String STACK_NAME = "CamelStubTcap";
    private static final int LOCAL_SSN = 8;

    private final TCAPStackImpl tcapStack;
    private static final Path PERSIST_DIR = Path.of(
            System.getProperty("user.home"),
            "camel-sigtran",
            "stub"
    );

    public TcapServer(SccpProvider sccpProvider) throws IOException {

        tcapStack = new TCAPStackImpl(
                STACK_NAME,
                sccpProvider,
                LOCAL_SSN
        );
        Files.createDirectories(PERSIST_DIR);

        tcapStack.setPersistDir(PERSIST_DIR.toString());
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
