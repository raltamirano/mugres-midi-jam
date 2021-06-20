package mugres.apps.midijam;

import mugres.ipc.Envelope;
import mugres.ipc.Listener;
import mugres.ipc.protocol.Message;
import mugres.ipc.protocol.messages.SignalsMessage;

import static mugres.apps.midijam.Common.mugresOutput;

public class MessageProcessor implements Listener {
    public static MessageProcessor of() {
        return new MessageProcessor();
    }

    @Override
    public void onMessage(final Envelope<Message> message) {
        switch (message.payload().type()) {
            case SIGNALS:
                final SignalsMessage signalsMessage = (SignalsMessage) message.payload();
                signalsMessage.signals().forEach(mugresOutput()::send);
                break;
            default:
                System.out.println("Discarding message: " + message);
                break;
        }
    }
}
