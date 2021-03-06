package org.workcraft.plugins.circuit;

import org.workcraft.annotations.VisualClass;
import org.workcraft.formula.BooleanFormula;
import org.workcraft.formula.utils.FormulaToString;
import org.workcraft.observation.PropertyChangedEvent;

@VisualClass(org.workcraft.plugins.circuit.VisualFunctionContact.class)
public class FunctionContact extends Contact {
    public static final String PROPERTY_SET_FUNCTION = "Set function";
    public static final String PROPERTY_RESET_FUNCTION = "Reset function";

    private BooleanFormula setFunction = null;
    private BooleanFormula resetFunction = null;

    public FunctionContact(IOType ioType) {
        super(ioType);
    }

    public FunctionContact() {
        super();
    }

    public BooleanFormula getSetFunction() {
        return setFunction;
    }

    public void setSetFunction(BooleanFormula value) {
        if (setFunction != value) {
            String setFunctionString = FormulaToString.toString(setFunction);
            String valueString = FormulaToString.toString(value);
            if (!setFunctionString.equals(valueString)) {
                setSetFunctionQuiet(value);
                sendNotification(new PropertyChangedEvent(this, PROPERTY_SET_FUNCTION));
            }
        }
    }

    public void setSetFunctionQuiet(BooleanFormula value) {
        setFunction = value;
    }

    public BooleanFormula getResetFunction() {
        return resetFunction;
    }

    public void setResetFunction(BooleanFormula value) {
        if (resetFunction != value) {
            String resetFunctionString = FormulaToString.toString(resetFunction);
            String valueString = FormulaToString.toString(value);
            if (!resetFunctionString.equals(valueString)) {
                setResetFunctionQuiet(value);
                sendNotification(new PropertyChangedEvent(this, PROPERTY_RESET_FUNCTION));
            }
        }
    }

    public void setResetFunctionQuiet(BooleanFormula value) {
        resetFunction = value;
    }

}
