
package org.example.camel.stub;

import org.example.camel.listener.CapServerListener;
import org.restcomm.protocols.ss7.cap.CAPStackImpl;
import org.restcomm.protocols.ss7.cap.api.CAPProvider;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.CAPServiceCircuitSwitchedCall;
import org.restcomm.protocols.ss7.tcap.api.TCAPProvider;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class CapServer {

    private static final String STACK_NAME = "CamelStubCap";

    private static final Path CONFIG_DIR = Path.of(
            System.getProperty("user.home"),
            "camel-sigtran",
            "stub"
    );

    private final CAPStackImpl capStack;
    private final CapServerListener listener;

    public CapServer(TCAPProvider tcapProvider) throws IOException {

        Objects.requireNonNull(tcapProvider);

        Files.createDirectories(CONFIG_DIR);

        this.capStack = new CAPStackImpl(
                STACK_NAME,
                tcapProvider
        );

        capStack.setPersistDir(
                CONFIG_DIR.toString()
        );

        this.listener = new CapServerListener();
    }

    public void start() throws Exception {

        capStack.start();

        CAPProvider provider = capStack.getCAPProvider();

        CAPServiceCircuitSwitchedCall service =
                provider.getCAPServiceCircuitSwitchedCall();

        service.addCAPServiceListener(listener);
        provider.addCAPDialogListener(listener);

        service.acivate();

        System.out.println("CAP server started");
        System.out.println(
                "CAP config directory: " + CONFIG_DIR
        );
    }

    public void stop() {
        capStack.stop();
        System.out.println("CAP server stopped");
    }

    public CAPProvider getProvider() {
        return capStack.getCAPProvider();
    }
}
