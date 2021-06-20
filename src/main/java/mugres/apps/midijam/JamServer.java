package mugres.apps.midijam;

import mugres.core.common.Instrument;
import mugres.core.utils.IPCUtils;

/**
 * MUGRES TCP/IP MIDI Jam server bootstrapper
 */
public class JamServer {
    public static void main(final String[] args) {
        Common.setup();
        IPCUtils.runMUGRESTCPIPServer("MIDI Jam Server", args,
                ServerMessageProcessor.of(ClientMessageProcessor.of()),
                server -> {
                    ServerState.getInstance().setServer(server);
                    Common.configureInputTransformer(server);
                });
    }

    private static Instrument getInstrument(final String[] args) {
        try {
            final String instrument = args[1];
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

