package org.example.camel.stub;

import org.mobicents.protocols.sctp.ManagementImpl;
import org.restcomm.protocols.ss7.m3ua.ExchangeType;
import org.restcomm.protocols.ss7.m3ua.Functionality;
import org.restcomm.protocols.ss7.m3ua.IPSPType;
import org.restcomm.protocols.ss7.m3ua.impl.M3UAManagementImpl;
import org.restcomm.protocols.ss7.m3ua.parameter.ParameterFactory;
import org.restcomm.protocols.ss7.m3ua.parameter.RoutingContext;
import org.restcomm.protocols.ss7.m3ua.parameter.TrafficModeType;
import org.restcomm.protocols.ss7.m3ua.impl.parameter.ParameterFactoryImpl;

public class M3uaServer {

    private static final String MANAGEMENT_NAME = "CamelStubM3ua";
    private static final String AS_NAME = "stubAS";
    private static final String ASP_NAME = "stubASP";

    private static final long ROUTING_CONTEXT = 100L;

    private final M3UAManagementImpl m3uaManagement;
    private final String associationName;

    public M3uaServer(ManagementImpl sctpManagement,
                      String associationName) {

        this.associationName = associationName;

        m3uaManagement = new M3UAManagementImpl(
                MANAGEMENT_NAME,
                "CamelStub",
                null
        );

        m3uaManagement.setTransportManagement(sctpManagement);
    }

    public void start() throws Exception {

        m3uaManagement.start();

        ParameterFactory factory = new ParameterFactoryImpl();

        RoutingContext routingContext =
                factory.createRoutingContext(
                        new long[]{ROUTING_CONTEXT}
                );

        TrafficModeType trafficMode =
                factory.createTrafficModeType(
                        TrafficModeType.Loadshare
                );

        // Создаём Application Server
        boolean asExists = false;

        for (var as : m3uaManagement.getAppServers()) {
            if (AS_NAME.equals(as.getName())) {
                asExists = true;
                break;
            }
        }

        if (!asExists) {
            m3uaManagement.createAs(
                    AS_NAME,
                    Functionality.SGW,
                    ExchangeType.SE,
                    null,
                    routingContext,
                    trafficMode,
                    1,
                    null
            );
        }

        // Создаём ASP Factory
        boolean aspExists = false;

        for (var asp : m3uaManagement.getAspfactories()) {
            if (ASP_NAME.equals(asp.getName())) {
                aspExists = true;
                break;
            }
        }

        if (!aspExists) {
            m3uaManagement.createAspFactory(
                    ASP_NAME,
                    associationName
            );

            m3uaManagement.assignAspToAs(
                    AS_NAME,
                    ASP_NAME
            );
        }

        System.out.println("M3UA server configured");
    }

    public void startAsp() throws Exception {

        m3uaManagement.startAsp(ASP_NAME);

        System.out.println("M3UA server ASP started");
    }

    public M3UAManagementImpl getManagement() {
        return m3uaManagement;
    }

    public void stop() throws Exception {
        m3uaManagement.stop();
    }
}
