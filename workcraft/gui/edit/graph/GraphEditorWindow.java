package org.workcraft.gui.edit.graph;


import org.workcraft.dom.visual.VisualAbstractGraphModel;
import org.workcraft.gui.edit.EditorWindow;

@SuppressWarnings("serial")
public class GraphEditorWindow extends EditorWindow {
	protected GraphEditorPane editorPane;

	public GraphEditorWindow(String title, VisualAbstractGraphModel document) {
		super(title);
		editorPane = new GraphEditorPane(document);
		this.setContentPane(editorPane);
	}

}