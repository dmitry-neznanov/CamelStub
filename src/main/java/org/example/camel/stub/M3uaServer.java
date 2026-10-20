package org.example.camel.stub;

import org.mobicents.protocols.sctp.ManagementImpl;
import org.restcomm.protocols.ss7.m3ua.ExchangeType;
import org.restcomm.protocols.ss7.m3ua.Functionality;
import org.restcomm.protocols.ss7.m3ua.impl.M3UAManagementImpl;
import org.restcomm.protocols.ss7.m3ua.parameter.ParameterFactory;
import org.restcomm.protocols.ss7.m3ua.parameter.RoutingContext;
import org.restcomm.protocols.ss7.m3ua.parameter.TrafficModeType;
import org.restcomm.protocols.ss7.m3ua.impl.parameter.ParameterFactoryImpl;

public class M3uaServer {

    private static final String MANAGEMENT_NAME = "CamelStubM3ua";
    private static final String MANAGEMENT_PATH =  "CamelStub";
    private static final String AS_NAME = "stubAS";
    private static final String ASP_NAME = "stubASP";

    private static final long ROUTING_CONTEXT = 100L;

    private static final int LOCAL_PC = 2;
    private static final int REMOTE_PC = 1;
    private static final int SCCP_SI = 3;


    private final M3UAManagementImpl m3uaManagement;
    private final String associationName;

    public M3uaServer(ManagementImpl sctpManagement,
                      String associationName) {

        this.associationName = associationName;

        m3uaManagement = new M3UAManagementImpl(
                MANAGEMENT_NAME,
                MANAGEMENT_PATH,
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

        configureRoute();

        System.out.println("M3UA routes:");

        m3uaManagement.getRoute().forEach((key, route) -> {
            System.out.println("Key: " + key);
            System.out.println("Route: " + route);
        });
    }

    public void startAsp() throws Exception {

        for (var asp : m3uaManagement.getAspfactories()) {

            if (ASP_NAME.equals(asp.getName())) {

                if (asp.getStatus()) {
                    System.out.println("M3UA ASP already started");
                    return;
                }

                m3uaManagement.startAsp(ASP_NAME);

                System.out.println("M3UA server ASP started");
                return;
            }
        }

        throw new IllegalStateException(
                "M3UA ASP not found: " + ASP_NAME
        );
    }

    public M3UAManagementImpl getManagement() {
        return m3uaManagement;
    }

    public void stop() throws Exception {
        m3uaManagement.stop();
    }

    private void configureRoute() throws Exception {

        if (m3uaManagement.getRoute().isEmpty()) {

            m3uaManagement.addRoute(
                    REMOTE_PC,
                    LOCAL_PC,
                    SCCP_SI,
                    AS_NAME,
                    TrafficModeType.Loadshare
            );

            System.out.println("M3UA route created");

        } else {
            System.out.println("M3UA routes already configured");
        }
    }
}
