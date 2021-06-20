package mugres.apps.midijam;

import mugres.core.common.Instrument;
import mugres.core.common.Party;
import mugres.ipc.protocol.messages.PartyListMessage;
import mugres.ipc.tcpip.MUGRESTCPIPServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ServerState {
    private static final ServerState INSTANCE = new ServerState();
    private MUGRESTCPIPServer server = null;
    private final Map<String, Party> partyList = new HashMap<>();

    private ServerState() {}

    public static ServerState getInstance() {
        return INSTANCE;
    }

    public MUGRESTCPIPServer getServer() {
        return server;
    }

    public void setServer(final MUGRESTCPIPServer server) {
        this.server = server;
    }

    public void updateParty(final String name, final Instrument instrument) {
        final Party party = new Party(name, instrument, 0);
        synchronized (partyList) {
            partyList.put(name, party);
            // Update server's client state
            ClientState.getInstance().updatePartyList(new ArrayList<>(partyList.values()));
        }
        broadcastPartyList();
    }

    private void broadcastPartyList() {
        try {
            PartyListMessage partyListMessage = null;
            synchronized (partyList) {
                partyListMessage = PartyListMessage.of(new ArrayList<>(partyList.values()));
            }
            server.broadcast(partyListMessage);
        } catch (final Throwable t) {
            System.out.println("Error broadcasting new party list: " + t);
        }
    }
}
