package mugres.apps.midijam.gui;

import mugres.core.common.Instrument;
import mugres.core.common.Pitch;

import java.util.HashSet;
import java.util.Set;

public class ClientMainController {
    private final ClientMainModel model;
    private final ClientMainView view;
    private final Set<Pitch> actives = new HashSet<>();

    public ClientMainController(final ClientMainModel model, final ClientMainView view) {
        if (model == null)
            throw new IllegalArgumentException("model");
        if (view == null)
            throw new IllegalArgumentException("view");

        this.view = view;
        this.model = model;

        view.setController(this);
        updateConnectionStatus();
    }

    public void connect() {
        try {
            model.connect();
        } catch (final Throwable t) {
            view.notifyError("Can't connect", t);
        } finally {
            updateConnectionStatus();
        }
    }

    public void disconnect() {
        try {
            model.disconnect();
        } catch (final Throwable t) {
            view.notifyError("Error while disconnecting", t);
        } finally {
            updateConnectionStatus();
        }
    }

    public void start() {
        view.setVisible(true);
        updateConnectionData();
    }

    public void exit() {
        view.setVisible(false);
        view.dispose();
    }

    public synchronized void noteOn(final Pitch pitch, final int velocity) {
        if (actives.contains(pitch)) return;

        model.noteOn(pitch, velocity);
        actives.add(pitch);
    }

    public synchronized void noteOff(final Pitch pitch) {
        if (!actives.contains(pitch)) return;

        model.noteOff(pitch);
        actives.remove(pitch);
    }

    public void updateHost(final String value) {
        model.setHost(value);
        updateConnectionData();
        updateConnectionStatus();
    }

    public void updatePort(final int value) {
        model.setPort(value);
        updateConnectionData();
        updateConnectionStatus();
    }

    public void updateInstrument(final Instrument value) {
        model.setInstrument(value);
        updateOptions();
    }

    private void updateConnectionData() {
        view.updateConnectionData(model.getHost(), model.getPort());
    }

    private void updateConnectionStatus() {
        view.updateConnectionStatus(model.canConnect(), model.isConnected());
    }

    private void updateOptions() {
        view.updateOptions(model.getInstrument());
    }
}
