package mugres.apps.midijam;

import mugres.core.utils.IPCUtils;

/**
 * MUGRES TCP/IP MIDI Jam server bootstrapper
 */
public class JamServer {
    public static void main(final String[] args) {
        IPCUtils.runMUGRESTCPIPServer("MIDI Jam Server", args, MessageProcessor.of(), Common::setup);
    }
}

