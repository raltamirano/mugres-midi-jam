package mugres.apps.midijam;

import mugres.MUGRES;
import mugres.ipc.Envelope;
import mugres.ipc.Listener;
import mugres.ipc.protocol.Message;
import mugres.ipc.protocol.messages.PartyListMessage;
import mugres.ipc.protocol.messages.SignalsMessage;

public class ClientMessageProcessor implements Listener {
    private ClientMessageProcessor() {}

    public static ClientMessageProcessor of() {
        return new ClientMessageProcessor();
    }

    @Override
    public void onMessage(final Envelope<Message> message) {
        switch (message.payload().type()) {
            case SIGNALS:
                final SignalsMessage signalsMessage = (SignalsMessage) message.payload();
                final int partyChannel = ClientState.getInstance().getPartyChannel(message.header().from());
                signalsMessage.signals().forEach(s -> MUGRES.output().send(s.modifiedChannel(partyChannel)));
                break;
            case PARTY_LIST:
                final PartyListMessage partyListMessage = (PartyListMessage) message.payload();
                ClientState.getInstance().updatePartyList(partyListMessage.partyList());
                break;
            default:
                System.out.println("Discarding message: " + message);
                break;
        }
    }
}
