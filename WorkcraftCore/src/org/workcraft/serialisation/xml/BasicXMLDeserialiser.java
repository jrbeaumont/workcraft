package org.workcraft.serialisation.xml;

import org.w3c.dom.Element;
import org.workcraft.exceptions.DeserialisationException;

public interface BasicXMLDeserialiser extends XMLDeserialiser {
    Object deserialise(Element element) throws DeserialisationException;
}
