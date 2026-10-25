package org.example.camel.stub;

import org.mobicents.protocols.sctp.ManagementImpl;
import org.restcomm.protocols.ss7.m3ua.As;
import org.restcomm.protocols.ss7.m3ua.Asp;
import org.restcomm.protocols.ss7.m3ua.AspFactory;
import org.restcomm.protocols.ss7.m3ua.ExchangeType;
import org.restcomm.protocols.ss7.m3ua.Functionality;
import org.restcomm.protocols.ss7.m3ua.impl.M3UAManagementImpl;
import org.restcomm.protocols.ss7.m3ua.impl.parameter.ParameterFactoryImpl;
import org.restcomm.protocols.ss7.m3ua.parameter.ParameterFactory;
import org.restcomm.protocols.ss7.m3ua.parameter.RoutingContext;
import org.restcomm.protocols.ss7.m3ua.parameter.TrafficModeType;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;

public class M3uaServer {

    private static final String MANAGEMENT_NAME = "CamelStubM3ua";
    private static final String MANAGEMENT_PATH = "CamelStub";

    private static final String AS_NAME = "stubAS";
    private static final String ASP_NAME = "stubASP";

    private static final long ROUTING_CONTEXT = 100L;

    private static final int LOCAL_PC = 2;
    private static final int REMOTE_PC = 1;
    private static final int SCCP_SI = 3;

    private static final int MIN_ASP_ACTIVE = 1;

    private static final Functionality FUNCTIONALITY =
            Functionality.SGW;

    private static final ExchangeType EXCHANGE_TYPE =
            ExchangeType.SE;

    private static final int TRAFFIC_MODE =
            TrafficModeType.Loadshare;

    private static final Path PERSIST_DIR = Path.of(
            System.getProperty("user.home"),
            "camel-sigtran",
            "stub"
    );

    private final M3UAManagementImpl m3uaManagement;
    private final String associationName;

    public M3uaServer(
            ManagementImpl sctpManagement,
            String associationName
    ) {
        this.associationName =
                Objects.requireNonNull(associationName);

        m3uaManagement = new M3UAManagementImpl(
                MANAGEMENT_NAME,
                MANAGEMENT_PATH,
                null
        );

        m3uaManagement.setTransportManagement(
                Objects.requireNonNull(sctpManagement)
        );

        m3uaManagement.setPersistDir(
                PERSIST_DIR.toString()
        );
    }

    public void start() throws Exception {

        Files.createDirectories(PERSIST_DIR);

        m3uaManagement.start();

        configureAs();
        configureAsp();
        configureRoute();

        System.out.println(
                "M3UA config directory: " + PERSIST_DIR
        );

        System.out.println("M3UA routes:");

        m3uaManagement.getRoute().forEach((key, route) -> {
            System.out.println("Key: " + key);

            for (As as : route.getAsArray()) {
                if (as != null) {
                    System.out.println("AS: " + as.getName());
                }
            }
        });

        System.out.println("M3UA server configured");
    }

    private void configureAs() throws Exception {

        ParameterFactory factory = new ParameterFactoryImpl();

        RoutingContext routingContext =
                factory.createRoutingContext(
                        new long[]{ROUTING_CONTEXT}
                );

        TrafficModeType trafficMode =
                factory.createTrafficModeType(TRAFFIC_MODE);

        As existingAs = null;

        for (As as : m3uaManagement.getAppServers()) {
            if (AS_NAME.equals(as.getName())) {
                existingAs = as;
                break;
            }
        }

        if (existingAs == null) {

            m3uaManagement.createAs(
                    AS_NAME,
                    FUNCTIONALITY,
                    EXCHANGE_TYPE,
                    null,
                    routingContext,
                    trafficMode,
                    MIN_ASP_ACTIVE,
                    null
            );

            System.out.println("M3UA AS created: " + AS_NAME);
            return;
        }

        boolean matches =
                existingAs.getFunctionality() == FUNCTIONALITY
                        && existingAs.getExchangeType() == EXCHANGE_TYPE
                        && existingAs.getIpspType() == null
                        && existingAs.getMinAspActiveForLb() == MIN_ASP_ACTIVE
                        && existingAs.getNetworkAppearance() == null
                        && existingAs.getRoutingContext() != null
                        && Arrays.equals(
                        existingAs.getRoutingContext()
                                .getRoutingContexts(),
                        new long[]{ROUTING_CONTEXT}
                )
                        && existingAs.getTrafficModeType() != null
                        && existingAs.getTrafficModeType().getMode()
                        == TRAFFIC_MODE;

        if (!matches) {
            throw new IllegalStateException(
                    "Existing M3UA AS configuration mismatch: "
                            + AS_NAME
            );
        }

        System.out.println(
                "M3UA AS configuration matches: " + AS_NAME
        );
    }

    private void configureAsp() throws Exception {

        AspFactory existingFactory = null;

        for (AspFactory factory : m3uaManagement.getAspfactories()) {
            if (ASP_NAME.equals(factory.getName())) {
                existingFactory = factory;
                break;
            }
        }

        if (existingFactory == null) {

            m3uaManagement.createAspFactory(
                    ASP_NAME,
                    associationName
            );

            System.out.println(
                    "M3UA ASP Factory created: " + ASP_NAME
            );

        } else {

            if (existingFactory.getAssociation() == null) {
                throw new IllegalStateException(
                        "ASP Factory has no SCTP association: "
                                + ASP_NAME
                );
            }

            String actualAssociation =
                    existingFactory.getAssociation().getName();

            if (!associationName.equals(actualAssociation)) {
                throw new IllegalStateException(
                        "ASP association mismatch. Expected: "
                                + associationName
                                + ", actual: "
                                + actualAssociation
                );
            }

            System.out.println(
                    "M3UA ASP Factory already exists: " + ASP_NAME
            );
        }

        // Проверяем назначение ASP независимо от того,
        // была ли фабрика создана только что.
        As targetAs = null;

        for (As as : m3uaManagement.getAppServers()) {
            if (AS_NAME.equals(as.getName())) {
                targetAs = as;
                break;
            }
        }

        if (targetAs == null) {
            throw new IllegalStateException(
                    "M3UA AS not found: " + AS_NAME
            );
        }

        boolean assigned = false;

        for (Asp asp : targetAs.getAspList()) {
            if (asp.getAspFactory() != null
                    && ASP_NAME.equals(
                    asp.getAspFactory().getName()
            )) {
                assigned = true;
                break;
            }
        }

        if (!assigned) {

            m3uaManagement.assignAspToAs(
                    AS_NAME,
                    ASP_NAME
            );

            System.out.println(
                    "M3UA ASP assigned: "
                            + ASP_NAME + " -> " + AS_NAME
            );

        } else {
            System.out.println(
                    "M3UA ASP already assigned to AS"
            );
        }
    }

    private void configureRoute() throws Exception {

        String routeKey =
                REMOTE_PC + ":" + LOCAL_PC + ":" + SCCP_SI;

        var route = m3uaManagement.getRoute().get(routeKey);

        if (route != null) {

            for (As as : route.getAsArray()) {
                if (as != null && AS_NAME.equals(as.getName())) {
                    System.out.println(
                            "M3UA route already exists: " + routeKey
                    );
                    return;
                }
            }
        }

        m3uaManagement.addRoute(
                REMOTE_PC,
                LOCAL_PC,
                SCCP_SI,
                AS_NAME,
                TRAFFIC_MODE
        );

        System.out.println(
                "M3UA route configured: "
                        + routeKey + " -> " + AS_NAME
        );
    }

    public void startAsp() throws Exception {

        for (AspFactory asp : m3uaManagement.getAspfactories()) {

            if (ASP_NAME.equals(asp.getName())) {

                if (asp.getStatus()) {
                    System.out.println(
                            "M3UA ASP already started"
                    );
                    return;
                }

                m3uaManagement.startAsp(ASP_NAME);

                System.out.println(
                        "M3UA server ASP started"
                );
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
}
