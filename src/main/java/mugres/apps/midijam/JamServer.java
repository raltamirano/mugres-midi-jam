package mugres.apps.midijam;

import mugres.core.utils.IPCUtils;

/**
 * MUGRES TCP/IP MIDI Jam server bootstrapper
 */
public class JamServer {
    public static void main(final String[] args) {
        Common.setup();
        IPCUtils.runMUGRESTCPIPServer("MIDI Jam Server", args,
                ServerMessageProcessor.of(),
                ServerState.getInstance()::setServer);
    }
}

