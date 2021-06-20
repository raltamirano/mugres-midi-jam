package mugres.apps.midijam.gui;

import mugres.core.common.Note;
import mugres.core.common.Pitch;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.PAGE_END;
import static mugres.core.common.Note.BASE_OCTAVE;

public class ClientMainView extends JFrame implements ActionListener, ChangeListener, DocumentListener, KeyEventDispatcher {
    private boolean connected;
    private ClientMainController controller;
    private JTextField hostTextField;
    private JSpinner portSpinner;
    final JButton connectButton;
    final JButton disconnectButton;
    final JButton exitButton;

    public ClientMainView() {
        final KeyboardFocusManager keyboardFocusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        keyboardFocusManager.addKeyEventDispatcher(this);

        setTitle(TITLE);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(500, 100));
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(screenSize.width/2-this.getSize().width/2, screenSize.height/2-this.getSize().height/2);

        // Connection
        final JPanel connectionPanel = new JPanel();
        getContentPane().add(connectionPanel, CENTER);

        final JLabel hostLabel = new JLabel("Host: ");
        connectionPanel.add(hostLabel);
        hostTextField = new JTextField();
        hostTextField.setColumns(20);
        hostTextField.getDocument().addDocumentListener(this);
        connectionPanel.add(hostTextField);
        final JLabel portLabel = new JLabel("Port: ");
        connectionPanel.add(portLabel);
        portSpinner = new JSpinner(new SpinnerNumberModel
                (1, 1, 65535, 1));
        portSpinner.setEditor(new JSpinner.NumberEditor(portSpinner,"#"));
        portSpinner.addChangeListener(this);
        connectionPanel.add(portSpinner);


        // Buttons
        final JPanel buttonsPanel = new JPanel();
        getContentPane().add(buttonsPanel, PAGE_END);

        connectButton = new JButton("Connect");
        connectButton.setActionCommand(CONNECT);
        connectButton.addActionListener(this);
        buttonsPanel.add(connectButton);
        disconnectButton = new JButton("Disconnect");
        disconnectButton.setActionCommand(DISCONNECT);
        disconnectButton.addActionListener(this);
        buttonsPanel.add(disconnectButton);
        exitButton = new JButton("Exit");
        exitButton.setActionCommand(EXIT);
        exitButton.addActionListener(this);
        buttonsPanel.add(exitButton);
    }

    public void setController(final ClientMainController controller) {
        if (controller == null)
            throw new IllegalArgumentException("controller");

        this.controller = controller;
    }

    public void updateConnectionData(final String host, final int port) {
        try {
            SwingUtilities.invokeAndWait(() -> {
                hostTextField.setText(host);
                portSpinner.setValue(port);
            });
        } catch (final Throwable ignore) {
        }
    }

    public void updateConnectionStatus(final boolean canConnect, final boolean isConnected) {
        connected = isConnected;
        connectButton.setEnabled(canConnect && !isConnected);
        disconnectButton.setEnabled(isConnected);
        hostTextField.setEnabled(!isConnected);
        portSpinner.setEnabled(!isConnected);
    }

    public void notifyError(final String friendlyText, final Throwable error) {
        error.printStackTrace();
        JOptionPane.showMessageDialog(this, friendlyText, "Error", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        switch(e.getActionCommand()) {
            case CONNECT:
                controller.connect();
                break;
            case DISCONNECT:
                controller.disconnect();
                break;
            case EXIT:
                controller.exit();
                break;
        }
    }

    @Override
    public void stateChanged(final ChangeEvent e) {
        onPortChanged((int)portSpinner.getValue());
    }

    @Override
    public void insertUpdate(final DocumentEvent e) {
        onHostChanged(hostTextField.getText());
    }

    @Override
    public void removeUpdate(final DocumentEvent e) {
        onHostChanged(hostTextField.getText());
    }

    @Override
    public void changedUpdate(final DocumentEvent e) {
        onHostChanged(hostTextField.getText());
    }

    @Override
    public boolean dispatchKeyEvent(final KeyEvent e) {
        if (!connected || (e.getID() != KeyEvent.KEY_PRESSED && e.getID() != KeyEvent.KEY_RELEASED)) return false;

        final Pitch pitch = getPitchFromKey(e);
        if (pitch == null) return false;

        if (e.getID() == KeyEvent.KEY_PRESSED)
            controller.noteOn(pitch, e.isShiftDown() ? HARD : e.isAltDown() ? SOFT : NORMAL);
        else
            controller.noteOff(pitch);

        return true;
    }

    private Pitch getPitchFromKey(final KeyEvent e) {
        return PITCH_MAP.get(Character.toLowerCase(e.getKeyChar()));
    }

    private void onHostChanged(final String value) {
        controller.updateHost(value);
    }

    private void onPortChanged(final int value) {
        controller.updatePort(value);
    }

    public static final String TITLE = "MUGRES MIDI Jam Client";
    private static final String CONNECT = "connect";
    private static final String DISCONNECT = "disconnect";
    private static final String EXIT = "exit";
    private static final int HARD = 127;
    private static final int SOFT = 50;
    private static final int NORMAL = 90;
    private static final Map<Character, Pitch> PITCH_MAP = new HashMap<>();

    static {
        PITCH_MAP.put('z', Pitch.of(Note.C, BASE_OCTAVE-1));
        PITCH_MAP.put('s', Pitch.of(Note.CS, BASE_OCTAVE-1));
        PITCH_MAP.put('x', Pitch.of(Note.D, BASE_OCTAVE-1));
        PITCH_MAP.put('d', Pitch.of(Note.DS, BASE_OCTAVE-1));
        PITCH_MAP.put('c', Pitch.of(Note.E, BASE_OCTAVE-1));
        PITCH_MAP.put('v', Pitch.of(Note.F, BASE_OCTAVE-1));
        PITCH_MAP.put('g', Pitch.of(Note.FS, BASE_OCTAVE-1));
        PITCH_MAP.put('b', Pitch.of(Note.G, BASE_OCTAVE-1));
        PITCH_MAP.put('h', Pitch.of(Note.GS, BASE_OCTAVE-1));
        PITCH_MAP.put('n', Pitch.of(Note.A, BASE_OCTAVE-1));
        PITCH_MAP.put('j', Pitch.of(Note.AS, BASE_OCTAVE-1));
        PITCH_MAP.put('m', Pitch.of(Note.B, BASE_OCTAVE-1));

        PITCH_MAP.put('q', Pitch.of(Note.C, BASE_OCTAVE));
        PITCH_MAP.put('2', Pitch.of(Note.CS, BASE_OCTAVE));
        PITCH_MAP.put('w', Pitch.of(Note.D, BASE_OCTAVE));
        PITCH_MAP.put('3', Pitch.of(Note.DS, BASE_OCTAVE));
        PITCH_MAP.put('e', Pitch.of(Note.E, BASE_OCTAVE));
        PITCH_MAP.put('r', Pitch.of(Note.F, BASE_OCTAVE));
        PITCH_MAP.put('5', Pitch.of(Note.FS, BASE_OCTAVE));
        PITCH_MAP.put('t', Pitch.of(Note.G, BASE_OCTAVE));
        PITCH_MAP.put('6', Pitch.of(Note.GS, BASE_OCTAVE));
        PITCH_MAP.put('y', Pitch.of(Note.A, BASE_OCTAVE));
        PITCH_MAP.put('7', Pitch.of(Note.AS, BASE_OCTAVE));
        PITCH_MAP.put('u', Pitch.of(Note.B, BASE_OCTAVE));

        PITCH_MAP.put('i', Pitch.of(Note.C, BASE_OCTAVE+1));
        PITCH_MAP.put('9', Pitch.of(Note.CS, BASE_OCTAVE+1));
        PITCH_MAP.put('o', Pitch.of(Note.D, BASE_OCTAVE+1));
        PITCH_MAP.put('0', Pitch.of(Note.DS, BASE_OCTAVE+1));
        PITCH_MAP.put('p', Pitch.of(Note.E, BASE_OCTAVE+1));
    }
}
