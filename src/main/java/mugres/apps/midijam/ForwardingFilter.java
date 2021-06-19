package mugres.apps.midijam;

import mugres.core.common.Context;
import mugres.core.common.Signals;
import mugres.core.filter.Filter;
import mugres.ipc.protocol.messages.SignalsMessage;
import mugres.ipc.tcpip.MUGRESTCPIPNode;

import java.util.Map;

import static mugres.apps.midijam.Common.NODE;

public class ForwardingFilter extends Filter {
    public ForwardingFilter(final Map<String, Object> arguments) {
        super(arguments);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    protected boolean internalCanHandle(final Context context, final Signals signals) {
        return true;
    }

    @Override
    protected Signals internalHandle(final Context context, final Signals signals) {
        forwardSignals(context, signals);
        return signals;
    }

    private void forwardSignals(final Context context, final Signals signals) {
        try {
            getNode(context).broadcast(new SignalsMessage(signals));
        } catch (final Throwable t) {
            System.out.println("Error forwarding signals: " + t);
        }
    }

    private MUGRESTCPIPNode getNode(final Context context) {
        return context.get(NODE);
    }

    public static final String NAME = "MIDI Jam Forwarding";
}
