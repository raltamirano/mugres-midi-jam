package mugres.apps.midijam;

import mugres.core.MUGRES;
import mugres.core.common.Context;
import mugres.core.common.io.Input;
import mugres.core.common.io.Output;
import mugres.core.filter.Filter;
import mugres.core.filter.builtin.system.Monitor;
import mugres.core.live.processor.transformer.Transformer;
import mugres.ipc.tcpip.MUGRESTCPIPNode;

import static java.util.Collections.emptyMap;

public class Common {
    private static Input mugresInput;
    private static Output mugresOutput;
    private static Transformer transformer;

    private Common() {}

    public static void setup(final MUGRESTCPIPNode node) {
        mugresInput = Input.midiInput(MUGRES.getMidiInputPort());
        mugresOutput = Output.midiOutput(MUGRES.getMidiOutputPort());

        registerForwardingFilter();
        configureInputTransformer(node);
    }

    public static Input mugresInput() {
        return mugresInput;
    }

    public static Output mugresOutput() {
        return mugresOutput;
    }

    private static void registerForwardingFilter() {
        Filter.register(ForwardingFilter.NAME, ForwardingFilter.class);
    }

    private static void configureInputTransformer(final MUGRESTCPIPNode node) {
        final mugres.core.live.processor.transformer.config.Configuration config =
                new mugres.core.live.processor.transformer.config.Configuration();

        config.appendFilter(Monitor.NAME, emptyMap());
        config.appendFilter(ForwardingFilter.NAME, emptyMap());

        final Context playContext = Context.ComposableContext.of(Context.createBasicContext());
        playContext.put(NODE, node);

        transformer = new Transformer(playContext,
                mugresInput,
                mugresOutput,
                config);
        transformer.start();
    }

    public static final String NODE = "node";
}
