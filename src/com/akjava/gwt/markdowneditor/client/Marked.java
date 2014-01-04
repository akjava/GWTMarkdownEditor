package com.akjava.gwt.markdowneditor.client;

public class Marked {

	public static final native String marked(String text)/*-{
	return $wnd.marked(text);
	}-*/;
}
