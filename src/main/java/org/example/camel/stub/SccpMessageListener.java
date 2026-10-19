package org.example.camel.stub;

import org.restcomm.protocols.ss7.sccp.*;
import org.restcomm.protocols.ss7.sccp.message.*;
import org.restcomm.protocols.ss7.sccp.parameter.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class SccpMessageListener implements SccpListener {

    private final SccpProvider provider;

    public SccpMessageListener(SccpProvider provider) {
        this.provider = provider;
    }

    @Override
    public void onMessage(SccpDataMessage message) {

        String request = new String(
                message.getData(),
                StandardCharsets.UTF_8
        );

        System.out.println("SCCP request received: " + request);

        try {
            sendResponse(message);
        } catch (IOException e) {
            System.err.println("Failed to send SCCP response");
            e.printStackTrace();
        }
    }

    private void sendResponse(SccpDataMessage request) throws IOException {

        // Адрес отправителя запроса становится получателем ответа
        var calledParty = request.getCallingPartyAddress();

        // Адрес получателя запроса становится отправителем ответа
        var callingParty = request.getCalledPartyAddress();

        byte[] payload = "Hello from CamelStub"
                .getBytes(StandardCharsets.UTF_8);

        var response = provider.getMessageFactory()
                .createDataMessageClass0(
                        calledParty,
                        callingParty,
                        payload,
                        0,
                        false,
                        null,
                        null
                );

        provider.send(response);

        System.out.println("SCCP response sent");
    }

    @Override
    public void onNotice(SccpNoticeMessage message) {
        System.out.println("SCCP notice received: " + message);
    }

    @Override
    public void onCoordResponse(int dpc, int ssn) {
    }

    @Override
    public void onState(int dpc, int ssn, boolean inService, int multiplicityIndicator) {
    }

    @Override
    public void onPcState(
            int dpc,
            SignallingPointStatus status,
            Integer restrictedImportanceLevel,
            RemoteSccpStatus remoteSccpStatus) {
    }

    @Override
    public void onNetworkIdState(int networkId, NetworkIdState state) {
    }

    @Override
    public void onConnectIndication(
            SccpConnection connection,
            SccpAddress calledParty,
            SccpAddress callingParty,
            ProtocolClass protocolClass,
            Credit credit,
            byte[] data,
            Importance importance) throws Exception {
    }

    @Override
    public void onConnectConfirm(SccpConnection connection, byte[] data) {
    }

    @Override
    public void onDisconnectIndication(
            SccpConnection connection,
            ReleaseCause cause,
            byte[] data) {
    }

    @Override
    public void onDisconnectIndication(
            SccpConnection connection,
            RefusalCause cause,
            byte[] data) {
    }

    @Override
    public void onDisconnectIndication(
            SccpConnection connection,
            ErrorCause cause) {
    }

    @Override
    public void onResetIndication(
            SccpConnection connection,
            ResetCause cause) {
    }

    @Override
    public void onResetConfirm(SccpConnection connection) {
    }

    @Override
    public void onData(SccpConnection connection, byte[] data) {
    }

    @Override
    public void onDisconnectConfirm(SccpConnection connection) {
    }
}
