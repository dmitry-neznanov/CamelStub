
package org.example.camel.stub;

import org.restcomm.protocols.ss7.m3ua.impl.M3UAManagementImpl;
import org.restcomm.protocols.ss7.sccp.impl.SccpStackImpl;

public class SccpServer {

    private static final String STACK_NAME = "CamelStubSccp";
    private static final String SAP_NAME = "stubSAP";

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

        sccpStack.start();

        // В учебном стенде сбрасываем сохранённую
        // конфигурацию SCCP и создаём её заново.
        sccpStack.removeAllResourses();

        // Удалённый сигнальный узел — CamelGenerator
        sccpStack.getSccpResource().addRemoteSpc(
                REMOTE_SPC_ID,
                REMOTE_POINT_CODE,
                0,
                0
        );

        // Удалённая подсистема на CamelGenerator
        sccpStack.getSccpResource().addRemoteSsn(
                REMOTE_SSN_ID,
                REMOTE_POINT_CODE,
                REMOTE_SSN,
                0,
                false
        );

        // Локальный MTP3 Service Access Point
        sccpStack.getRouter().addMtp3ServiceAccessPoint(
                SAP_ID,
                MTP3_ID,
                LOCAL_POINT_CODE,
                NETWORK_INDICATOR,
                0,
                SAP_NAME
        );

        // Маршрут к CamelGenerator (Point Code 1)
        sccpStack.getRouter().addMtp3Destination(
                SAP_ID,
                DESTINATION_ID,
                REMOTE_POINT_CODE,
                REMOTE_POINT_CODE,
                SLS_MIN,
                SLS_MAX,
                SLS_MASK
        );

        System.out.println("SCCP server configured");
    }

    public SccpStackImpl getStack() {
        return sccpStack;
    }

    public void stop() {
        sccpStack.stop();
    }
}
