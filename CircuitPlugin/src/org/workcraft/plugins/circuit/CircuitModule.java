package org.workcraft.plugins.circuit;

import org.workcraft.Framework;
import org.workcraft.Initialiser;
import org.workcraft.Module;
import org.workcraft.PluginManager;
import org.workcraft.Tool;
import org.workcraft.dom.ModelDescriptor;
import org.workcraft.gui.propertyeditor.SettingsPage;
import org.workcraft.plugins.circuit.serialisation.FunctionDeserialiser;
import org.workcraft.plugins.circuit.serialisation.FunctionSerialiser;
import org.workcraft.plugins.circuit.tools.CheckCircuitTool;
import org.workcraft.plugins.circuit.tools.STGGeneratorTool;
import org.workcraft.serialisation.xml.XMLDeserialiser;
import org.workcraft.serialisation.xml.XMLSerialiser;


public class CircuitModule implements Module {

	@Override
	public String getDescription() {
		return "Gate-level circuit model";
	}

	@Override
	public void init(final Framework framework) {
		PluginManager pm = framework.getPluginManager();

		pm.registerClass(Tool.class, new Initialiser<Tool>() {
			@Override
			public Tool create() {
				return new STGGeneratorTool(framework);
			}
		});

		pm.registerClass(Tool.class, new Initialiser<Tool>() {
			@Override
			public Tool create() {
				return new CheckCircuitTool(framework);
			}
		});

		pm.registerClass(Tool.class, new Initialiser<Tool>() {
			@Override
			public Tool create() {
				return new CheckCircuitTool(framework) {
					@Override
					public String getDisplayName() {
						return "Check circuit only for conformation";
					}
					@Override
					public boolean checkDeadlock() {
						return false;
					}
					@Override
					public boolean checkHazard() {
						return false;
					}
				};
			}
		});

		pm.registerClass(Tool.class, new Initialiser<Tool>() {
			@Override
			public Tool create() {
				return new CheckCircuitTool(framework) {
					@Override
					public String getDisplayName() {
						return "Check circuit only for deadlocks";
					}
					@Override
					public boolean checkConformation() {
						return false;
					}
					@Override
					public boolean checkHazard() {
						return false;
					}
				};
			}
		});

		pm.registerClass(Tool.class, new Initialiser<Tool>() {
			@Override
			public Tool create() {
				return new CheckCircuitTool(framework) {
					@Override
					public String getDisplayName() {
						return "Check circuit only for hazards";
					}
					@Override
					public boolean checkConformation() {
						return false;
					}
					@Override
					public boolean checkDeadlock() {
						return false;
					}
				};
			}
		});

		pm.registerClass(ModelDescriptor.class, CircuitModelDescriptor.class);
		pm.registerClass(XMLSerialiser.class, FunctionSerialiser.class);
		pm.registerClass(XMLDeserialiser.class, FunctionDeserialiser.class);
		pm.registerClass(SettingsPage.class, CircuitSettings.class);
	}
}