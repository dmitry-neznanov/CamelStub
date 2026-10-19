package org.example.camel.stub;

import org.example.camel.listener.CapServerListener;
import org.restcomm.protocols.ss7.cap.CAPStackImpl;
import org.restcomm.protocols.ss7.cap.api.CAPProvider;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.CAPServiceCircuitSwitchedCall;
import org.restcomm.protocols.ss7.tcap.api.TCAPProvider;

public class CapServer {

    private final CAPStackImpl capStack;
    private final CapServerListener listener;

    public CapServer(TCAPProvider tcapProvider) {
        this.capStack = new CAPStackImpl(
                "CamelStubCap",
                tcapProvider
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
    }

    public void stop() {
        capStack.stop();
    }

    public CAPProvider getProvider() {
        return capStack.getCAPProvider();
    }
}