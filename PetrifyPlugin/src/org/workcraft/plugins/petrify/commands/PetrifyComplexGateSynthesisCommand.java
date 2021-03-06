package org.workcraft.plugins.petrify.commands;

public class PetrifyComplexGateSynthesisCommand extends PetrifyAbstractSynthesisCommand {

    @Override
    public String[] getSynthesisParameter() {
        String[] result = new String[1];
        result[0] = "-cg";
        return result;
    }

    @Override
    public String getDisplayName() {
        return "Complex gate [Petrify]";
    }

    @Override
    public Position getPosition() {
        return null;
    }

}
