
package org.example.camel.stub;

import org.restcomm.protocols.ss7.tcap.api.TCAPProvider;
import org.restcomm.protocols.ss7.tcap.api.TCListener;
import org.restcomm.protocols.ss7.tcap.api.tc.dialog.Dialog;
import org.restcomm.protocols.ss7.tcap.api.tc.dialog.events.*;
import org.restcomm.protocols.ss7.tcap.asn.comp.Invoke;

public class TcapServerListener implements TCListener {


    private final TCAPProvider provider;

    public TcapServerListener(TCAPProvider provider) {
        this.provider = provider;
    }


    @Override
    public void onTCUni(TCUniIndication indication) {
        System.out.println("TCAP UNI received");
    }



    @Override
    public void onTCBegin(TCBeginIndication indication) {

        Dialog dialog = indication.getDialog();

        System.out.println(
                "TCAP BEGIN received. Dialog ID="
                        + dialog.getLocalDialogId()
        );

        System.out.println(
                "Remote Dialog ID=" + dialog.getRemoteDialogId()
        );

        System.out.println(
                "Dialog state=" + dialog.getState()
        );

        try {
            TCEndRequest endRequest =
                    provider.getDialogPrimitiveFactory().createEnd(dialog);

            // Обязательно устанавливаем тип завершения
            endRequest.setTermination(TerminationType.Basic);

            dialog.send(endRequest);

            System.out.println(
                    "TCAP END sent. Dialog ID="
                            + dialog.getLocalDialogId()
            );

        } catch (Exception e) {
            System.err.println("Failed to send TCAP END");
            e.printStackTrace();
        }
    }



    @Override
    public void onTCContinue(TCContinueIndication indication) {
        System.out.println("TCAP CONTINUE received");
    }

    @Override
    public void onTCEnd(TCEndIndication indication) {
        System.out.println("TCAP END received");
    }

    @Override
    public void onTCUserAbort(TCUserAbortIndication indication) {
        System.out.println("TCAP USER ABORT received");
    }

    @Override
    public void onTCPAbort(TCPAbortIndication indication) {
        System.out.println("TCAP PROVIDER ABORT received");
    }

    @Override
    public void onTCNotice(TCNoticeIndication indication) {
        System.out.println("TCAP NOTICE received");
    }

    @Override
    public void onDialogReleased(Dialog dialog) {
        System.out.println("TCAP dialog released");
    }

    @Override
    public void onInvokeTimeout(Invoke invoke) {
        System.out.println("TCAP invoke timeout");
    }

    @Override
    public void onDialogTimeout(Dialog dialog) {
        System.out.println("TCAP dialog timeout");
    }
}
