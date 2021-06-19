package mugres.apps.midijam;

import mugres.core.utils.IPCUtils;

/**
 * MUGRES TCP/IP MIDI Jam client bootstrapper
 */
public class JamClient {
    public static void main(final String[] args) {
        IPCUtils.runMUGRESTCPIPClient(args, MessageProcessor.of(), Common::setup);
    }
}

