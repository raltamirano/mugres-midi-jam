package mugres.apps.midijam;

import mugres.apps.midijam.gui.ClientMainController;
import mugres.apps.midijam.gui.ClientMainModel;
import mugres.apps.midijam.gui.ClientMainView;

/**
 * MUGRES TCP/IP MIDI Jam client GUI bootstrapper
 */
public class JamClientGUI {
    public static void main(final String[] args) {
        Common.setup();

        final ClientMainModel model = new ClientMainModel();
        final ClientMainView view = new ClientMainView();
        final ClientMainController controller = new ClientMainController(model, view);
        controller.start();
    }
}

