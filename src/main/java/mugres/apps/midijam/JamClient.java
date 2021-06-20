package mugres.apps.midijam;

import mugres.core.common.Instrument;
import mugres.core.utils.IPCUtils;
import mugres.ipc.protocol.messages.SetPartyMessage;
import mugres.ipc.tcpip.MUGRESTCPIPClient;

import java.util.function.Consumer;

/**
 * MUGRES TCP/IP MIDI Jam client bootstrapper
 */
public class JamClient {
    public static void main(final String[] args) {
        Common.setup();
        IPCUtils.runMUGRESTCPIPClient(args, ClientMessageProcessor.of(), onClientConnected(args));
    }

    private static Consumer<MUGRESTCPIPClient> onClientConnected(final String[] args) {
        return client -> {
            ClientState.getInstance().setClient(client);
            Common.configureInputTransformer(client);
            try {
                client.sendToServer(SetPartyMessage.of(getInstrument(args)));
            } catch (final Throwable ignore) {}
        };
    }

    private static Instrument getInstrument(final String[] args) {
        try {
            final String instrument = args[2];
            try {
                final int id = Integer.parseInt(instrument);
                return Instrument.of(id);
            } catch (final Throwable ignore) {
                return Instrument.of(instrument);
            }
        } catch (final Throwable ignore) {
            return Instrument.Acoustic_Grand_Piano;
        }
    }
}

