package org.workcraft.plugins.circuit;

import java.util.ArrayList;
import java.util.Collection;

import org.workcraft.annotations.VisualClass;
import org.workcraft.dom.math.MathGroup;
import org.workcraft.gui.propertyeditor.NamePropertyDescriptor;
import org.workcraft.observation.PropertyChangedEvent;
import org.workcraft.util.Hierarchy;

@VisualClass(org.workcraft.plugins.circuit.VisualCircuitComponent.class)
public class CircuitComponent extends MathGroup {

    public static final String PROPERTY_MODULE = "Module";
    public static final String PROPERTY_IS_ENVIRONMENT = "Treat as environment";
    public static final String PROPERTY_PATH_BREAKER = "Path breaker";

    private String name = "";
    private String module = "";
    private boolean isEnvironment = false;
    private boolean pathBreaker = false;

    public void setName(String name) {
        this.name = name;
        sendNotification(new PropertyChangedEvent(this, NamePropertyDescriptor.PROPERTY_NAME));
    }

    public String getName() {
        return name;
    }

    public void setModule(String module) {
        this.module = module;
        sendNotification(new PropertyChangedEvent(this, PROPERTY_MODULE));
    }

    public String getModule() {
        return module;
    }

    public boolean isMapped() {
        return (module != null) && !module.isEmpty();
    }

    public void setIsEnvironment(boolean value) {
        this.isEnvironment = value;
        sendNotification(new PropertyChangedEvent(this, PROPERTY_IS_ENVIRONMENT));
    }

    public boolean getIsEnvironment() {
        return isEnvironment;
    }

    public boolean getPathBreaker() {
        return pathBreaker;
    }

    public void setPathBreaker(boolean value) {
        if (this.pathBreaker != value) {
            this.pathBreaker = value;
            sendNotification(new PropertyChangedEvent(this, PROPERTY_PATH_BREAKER));
        }
    }

    public Collection<Contact> getContacts() {
        return Hierarchy.filterNodesByType(getChildren(), Contact.class);
    }

    public Collection<Contact> getInputs() {
        ArrayList<Contact> result = new ArrayList<>();
        for (Contact contact: getContacts()) {
            if (contact.isInput()) {
                result.add(contact);
            }
        }
        return result;
    }

    public Collection<Contact> getOutputs() {
        ArrayList<Contact> result = new ArrayList<>();
        for (Contact contact: getContacts()) {
            if (contact.isOutput()) {
                result.add(contact);
            }
        }
        return result;
    }

    public Contact getFirstInput() {
        Contact result = null;
        for (Contact contact: getContacts()) {
            if (contact.isInput()) {
                result = contact;
                break;
            }
        }
        return result;
    }

    public Contact getFirstOutput() {
        Contact result = null;
        for (Contact contact: getContacts()) {
            if (contact.isOutput()) {
                result = contact;
                break;
            }
        }
        return result;
    }

    public boolean isSingleInputSingleOutput() {
        return (getContacts().size() == 2) && (getFirstInput() != null) && (getFirstOutput() != null);
    }

}
