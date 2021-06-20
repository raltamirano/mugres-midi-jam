package mugres.apps.midijam;

import mugres.core.utils.IPCUtils;

/**
 * MUGRES TCP/IP MIDI Jam client bootstrapper
 */
public class JamClient {
    public static void main(final String[] args) {
        Common.setup();
        IPCUtils.runMUGRESTCPIPClient(args, MessageProcessor.of(), Common::configureInputTransformer);
    }
}

