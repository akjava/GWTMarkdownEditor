package com.akjava.gwt.markdowneditor.client;

import com.akjava.gwt.lib.client.GWTHTMLUtils;
import com.akjava.gwt.lib.client.LogUtils;
import com.google.common.base.Optional;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Window;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class GWTMarkdownEditor implements EntryPoint {
	  public static final String PEOPERTY_READ_ONLY="gwtmarkdowneditorreadonly";
	  public static final String PEOPERTY_SESSION_ID="gwtmarkdowneditorsessionid";
	  
	  public static final String PEOPERTY_DEFAULT_ID="gwtmarkdowneditordefaultid";
	  public static final String PEOPERTY_OUTPUT_TEXT="gwtmarkdowneditoroutputtext";
	  public static final String PEOPERTY_OUTPUT_HTML="gwtmarkdowneditoroutputhtml";
	  
	public void onModuleLoad() {
		if(GWTHTMLUtils.isExistPanel("gwtmarkdowneditorcontainer")){
			MarkdownEditor editor=createMarkdownEditortByHtml();
			GWTHTMLUtils.getPanelIfExist("gwtmarkdowneditorcontainer").add(editor);
			//trying fix scroll bugs,seems not bad
			editor.getTextArea().setFocus(true); //if true scroll problem will fixed,but window scroll to there.
			Window.scrollTo(0, -1000);
			editor.getTextArea().setFocus(false);
		}
		
	}
	
	public static MarkdownEditor createMarkdownEditortByHtml(){
		  boolean readOnly=GWTHTMLUtils.isExistPanel(PEOPERTY_READ_ONLY);
	      
	      String defaultValue=GWTHTMLUtils.getInputValueById(PEOPERTY_DEFAULT_ID, "");
	      String session_id=GWTHTMLUtils.getInputValueById(PEOPERTY_SESSION_ID,"");
	      
	      MarkdownEditor editor= new MarkdownEditor(readOnly, session_id, defaultValue);
	      
	     
	      editor.setSyncTextKey(Optional.of(PEOPERTY_OUTPUT_TEXT));
	      editor.setSyncHtmlKey(Optional.of(PEOPERTY_OUTPUT_HTML));
	      
	      return editor;
	}
}
