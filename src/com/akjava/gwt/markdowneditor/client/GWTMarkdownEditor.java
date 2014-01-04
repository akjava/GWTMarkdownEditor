package com.akjava.gwt.markdowneditor.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class GWTMarkdownEditor implements EntryPoint {
	public void onModuleLoad() {
		VerticalPanel root=new VerticalPanel();
		RootPanel.get().add(root);
		root.add(new MarkdownEditor());
	}
}
