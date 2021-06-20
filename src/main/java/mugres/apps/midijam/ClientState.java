package mugres.apps.midijam;

import mugres.core.common.InstrumentChange;
import mugres.core.common.Party;
import mugres.ipc.tcpip.MUGRESTCPIPClient;
import mugres.ipc.tcpip.MUGRESTCPIPServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientState {
    private static final ClientState INSTANCE = new ClientState();
    private MUGRESTCPIPClient client = null;
    private final Map<String, Party> partyList = new HashMap<>();
    private final List<String> partyNamesList = new ArrayList<>();

    private ClientState() {}

    public static ClientState getInstance() {
        return INSTANCE;
    }

    public MUGRESTCPIPClient getClient() {
        return client;
    }

    public void setClient(final MUGRESTCPIPClient client) {
        this.client = client;
    }

    public void updatePartyList(final List<Party> newList) {
        synchronized (partyList) {
            partyList.clear();
            partyNamesList.clear();
            for(int i=0; i<newList.size(); i++) {
                final Party party = newList.get(i);
                partyList.put(party.getName(), party);
                partyNamesList.add(party.getName());
                Common.mugresOutput().send(InstrumentChange.of(i, party.getInstrument()));
            }
        }
    }

    public int getPartyChannel(final String partyName) {
        synchronized (partyList) {
            return partyNamesList.indexOf(partyName);
        }
    }
}
