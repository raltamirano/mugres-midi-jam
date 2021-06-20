package mugres.apps.midijam;

import mugres.ipc.Envelope;
import mugres.ipc.Listener;
import mugres.ipc.protocol.Message;
import mugres.ipc.protocol.messages.SetPartyMessage;
import mugres.ipc.protocol.messages.SignalsMessage;

import static mugres.apps.midijam.Common.mugresOutput;

public class ServerMessageProcessor implements Listener {
    private final ClientMessageProcessor clientMessageProcessor;

    private ServerMessageProcessor(final ClientMessageProcessor clientMessageProcessor) {
        if (clientMessageProcessor == null)
            throw new IllegalArgumentException("clientMessageProcessor");

        this.clientMessageProcessor = clientMessageProcessor;
    }

    public static ServerMessageProcessor of(final ClientMessageProcessor clientMessageProcessor) {
        return new ServerMessageProcessor(clientMessageProcessor);
    }

    @Override
    public void onMessage(final Envelope<Message> message) {
        switch (message.payload().type()) {
            case SET_PARTY:
                final SetPartyMessage setPartyMessage = (SetPartyMessage) message.payload();
                ServerState.getInstance().updateParty(message.header().from(), setPartyMessage.instrument());
                break;
            default:
                // Delegate non server related messages
                clientMessageProcessor.onMessage(message);
                break;
        }
    }
}
