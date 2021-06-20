package mugres.apps.midijam;

import mugres.ipc.Envelope;
import mugres.ipc.Listener;
import mugres.ipc.protocol.Message;
import mugres.ipc.protocol.messages.SetPartyMessage;

public class ServerMessageProcessor implements Listener {
    private ServerMessageProcessor() {
    }

    public static ServerMessageProcessor of() {
        return new ServerMessageProcessor();
    }

    @Override
    public void onMessage(final Envelope<Message> message) {
        switch (message.payload().type()) {
            case SET_PARTY:
                final SetPartyMessage setPartyMessage = (SetPartyMessage) message.payload();
                ServerState.getInstance().updateParty(message.header().from(), setPartyMessage.instrument());
                break;
            default:
                System.out.println("Discarding message: " + message);
                break;
        }
    }
}
