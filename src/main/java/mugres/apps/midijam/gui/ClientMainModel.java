package mugres.apps.midijam.gui;

import mugres.MUGRES;
import mugres.apps.midijam.ClientMessageProcessor;
import mugres.apps.midijam.ClientState;
import mugres.apps.midijam.Common;
import mugres.core.common.Instrument;
import mugres.core.common.Pitch;
import mugres.core.common.Played;
import mugres.core.common.Signal;
import mugres.ipc.protocol.messages.SetPartyMessage;
import mugres.ipc.tcpip.MUGRESTCPIPClient;
import mugres.ipc.tcpip.MUGRESTCPIPServer;

import java.io.IOException;
import java.util.UUID;

public class ClientMainModel {
    private String host = "localhost";
    private int port = MUGRESTCPIPServer.DEFAULT_PORT;
    private Instrument instrument = Instrument.Acoustic_Grand_Piano;
    private MUGRESTCPIPClient client;

    public synchronized boolean isConnected() {
        return client == null ? false : client.isConnected();
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Instrument getInstrument() {
        return instrument;
    }

    public void setInstrument(Instrument instrument) {
        this.instrument = instrument;
    }

    public boolean canConnect() {
        return host != null && !host.trim().isEmpty() && port > 0 && port < 65536;
    }

    public synchronized void connect() throws IOException {
        if (isConnected())
            throw new IllegalStateException("Already connected!");

        client = MUGRESTCPIPClient.of(host, port);
        client.setListener(ClientMessageProcessor.of());
        client.connect();

        Common.configureInputTransformer(client);
        ClientState.getInstance().setClient(client);
        try {
            client.sendToServer(SetPartyMessage.of(instrument));
        } catch (final Throwable ignore) {}

    }

    public synchronized void disconnect() throws IOException {
        if (!isConnected())
            throw new IllegalStateException("Not connected!");

        ClientState.getInstance().setClient(null);
        Common.removeInputTransformer();

        client.disconnect();
        client = null;
    }

    public void noteOn(final Pitch pitch, final int velocity) {
        try {
            MUGRES.input().send(Signal.of(UUID.randomUUID(), System.currentTimeMillis(),
                    DEFAULT_CHANNEL, Played.of(pitch, velocity), true));
        } catch (final Throwable t) {
            System.out.println("NoteOn error: " + t);
        }
    }

    public void noteOff(final Pitch pitch) {
        try {
            MUGRES.input().send(Signal.of(UUID.randomUUID(), System.currentTimeMillis(),
                    DEFAULT_CHANNEL, Played.of(pitch, 0), false));
        } catch (final Throwable t) {
            System.out.println("NoteOff error: " + t);
        }
    }

    private static final int DEFAULT_CHANNEL = 0;
}

