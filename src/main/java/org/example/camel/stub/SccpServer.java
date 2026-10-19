
package org.example.camel.stub;

import org.restcomm.protocols.ss7.m3ua.impl.M3UAManagementImpl;
import org.restcomm.protocols.ss7.sccp.impl.SccpStackImpl;

import org.restcomm.protocols.ss7.sccp.Mtp3ServiceAccessPoint;
import org.restcomm.protocols.ss7.sccp.Mtp3Destination;
import java.util.Objects;

public class SccpServer {

    private static final String STACK_NAME = "CamelStubSccp";
    private static final String LOCAL_GT_DIGITS = "";

    private static final int MTP3_ID = 1;
    private static final int SAP_ID = 1;
    private static final int DESTINATION_ID = 1;

    private static final int LOCAL_POINT_CODE = 2;
    private static final int REMOTE_POINT_CODE = 1;

    private static final int REMOTE_SSN = 8;
    private static final int NETWORK_INDICATOR = 2;

    private static final int REMOTE_SPC_ID = 1;
    private static final int REMOTE_SSN_ID = 1;

    private static final int SLS_MIN = 0;
    private static final int SLS_MAX = 255;
    private static final int SLS_MASK = 255;

    private final SccpStackImpl sccpStack;

    public SccpServer(M3UAManagementImpl m3uaManagement) {

        sccpStack = new SccpStackImpl(STACK_NAME, null);


        // Подключаем M3UA как транспорт SCCP
        sccpStack.setMtp3UserPart(MTP3_ID, m3uaManagement);
    }

    public void start() throws Exception {

        System.out.println("Starting SCCP server...");

        sccpStack.start();

        configureSap();
        configureDestination();
        configureRemoteSpc();
        configureRemoteSsn();

        System.out.println("SCCP listener registered for SSN=8");
        System.out.println("SCCP server configured");

    }

    private void configureRemoteSpc() throws Exception {

        var resource = sccpStack.getSccpResource();

        var remoteSpc = resource.getRemoteSpc(REMOTE_SPC_ID);

        if (remoteSpc == null) {

            resource.addRemoteSpc(
                    REMOTE_SPC_ID,
                    REMOTE_POINT_CODE,
                    0,
                    0
            );

            System.out.println("Remote SPC created");
            return;
        }

        boolean matches =
                remoteSpc.getRemoteSpc() == REMOTE_POINT_CODE
                        && remoteSpc.getRemoteSpcFlag() == 0
                        && remoteSpc.getMask() == 0;

        if (!matches) {

            resource.modifyRemoteSpc(
                    REMOTE_SPC_ID,
                    REMOTE_POINT_CODE,
                    0,
                    0
            );

            System.out.println("Remote SPC updated");

        } else {
            System.out.println("Remote SPC already configured");
        }
    }

    private void configureRemoteSsn() throws Exception {

        var resource = sccpStack.getSccpResource();

        var remoteSsn = resource.getRemoteSsn(REMOTE_SSN_ID);

        if (remoteSsn == null) {

            resource.addRemoteSsn(
                    REMOTE_SSN_ID,
                    REMOTE_POINT_CODE,
                    REMOTE_SSN,
                    0,
                    false
            );

            System.out.println("Remote SSN created");
            return;
        }

        boolean matches =
                remoteSsn.getRemoteSpc() == REMOTE_POINT_CODE
                        && remoteSsn.getRemoteSsn() == REMOTE_SSN
                        && remoteSsn.getRemoteSsnFlag() == 0
                        && !remoteSsn.getMarkProhibitedWhenSpcResuming();

        if (!matches) {

            resource.modifyRemoteSsn(
                    REMOTE_SSN_ID,
                    REMOTE_POINT_CODE,
                    REMOTE_SSN,
                    0,
                    false
            );

            System.out.println("Remote SSN updated");

        } else {
            System.out.println("Remote SSN already configured");
        }
    }

    private void configureSap() throws Exception {

        var router = sccpStack.getRouter();

        Mtp3ServiceAccessPoint sap =
                router.getMtp3ServiceAccessPoint(SAP_ID);

        if (sap == null) {

            router.addMtp3ServiceAccessPoint(
                    SAP_ID,
                    MTP3_ID,
                    LOCAL_POINT_CODE,
                    NETWORK_INDICATOR,
                    0,
                    LOCAL_GT_DIGITS
            );

            System.out.println("SCCP SAP created");
            return;
        }

        boolean matches =
                sap.getMtp3Id() == MTP3_ID
                        && sap.getOpc() == LOCAL_POINT_CODE
                        && sap.getNi() == NETWORK_INDICATOR
                        && sap.getNetworkId() == 0
                        && Objects.equals(sap.getLocalGtDigits(), LOCAL_GT_DIGITS );

        if (!matches) {

            router.modifyMtp3ServiceAccessPoint(
                    SAP_ID,
                    MTP3_ID,
                    LOCAL_POINT_CODE,
                    NETWORK_INDICATOR,
                    0,
                    LOCAL_GT_DIGITS
            );

            System.out.println("SCCP SAP updated");

        } else {
            System.out.println("SCCP SAP already configured");
        }
    }

    private void configureDestination() throws Exception {

        var router = sccpStack.getRouter();

        Mtp3ServiceAccessPoint sap =
                router.getMtp3ServiceAccessPoint(SAP_ID);

        if (sap == null) {
            throw new IllegalStateException("SCCP SAP not found");
        }

        Mtp3Destination destination =
                sap.getMtp3Destination(DESTINATION_ID);

        if (destination == null) {

            router.addMtp3Destination(
                    SAP_ID,
                    DESTINATION_ID,
                    REMOTE_POINT_CODE,
                    REMOTE_POINT_CODE,
                    SLS_MIN,
                    SLS_MAX,
                    SLS_MASK
            );

            System.out.println("MTP3 Destination created");
            return;
        }

        boolean matches =
                destination.getFirstDpc() == REMOTE_POINT_CODE
                        && destination.getLastDpc() == REMOTE_POINT_CODE
                        && destination.getFirstSls() == SLS_MIN
                        && destination.getLastSls() == SLS_MAX
                        && destination.getSlsMask() == SLS_MASK;

        if (!matches) {

            router.modifyMtp3Destination(
                    SAP_ID,
                    DESTINATION_ID,
                    REMOTE_POINT_CODE,
                    REMOTE_POINT_CODE,
                    SLS_MIN,
                    SLS_MAX,
                    SLS_MASK
            );

            System.out.println("MTP3 Destination updated");

        } else {
            System.out.println("MTP3 Destination already configured");
        }
    }

    public SccpStackImpl getStack() {
        return sccpStack;
    }

    public void stop() {
        sccpStack.stop();
    }
}
