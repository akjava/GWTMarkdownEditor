package com.akjava.gwt.markdowneditor.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

public class MarkdownEditor extends HorizontalPanel {

	private TextArea textArea;
	private CheckBox autoConvertCheck;
	private HTML previewHTML;
	private TextArea htmlArea;

	public MarkdownEditor(){
		createLeftPanels();
		createRightPanels();
	}

	private void createRightPanels() {
		VerticalPanel rightPanel=new VerticalPanel();
		this.add(rightPanel);

		TabPanel tab=new TabPanel();
		rightPanel.add(tab);
		tab.setSize("560px","700px");
		createPreviewArea(tab);
		createHtmlArea(tab);
		tab.selectTab(0);
	}

	private void createPreviewArea(TabPanel tab) {
		VerticalPanel panel=new VerticalPanel();
		panel.setHeight("700px");
		previewHTML = new HTML();
		panel.add(previewHTML);
		tab.add(panel,"Preview");
	}
	
	private void createHtmlArea(TabPanel tab) {
		htmlArea = new TextArea();
		htmlArea.setWidth("560px");
		htmlArea.setHeight("700px");
		tab.add(htmlArea,"HTML");
	}

	private void createLeftPanels() {
		VerticalPanel leftPanel=new VerticalPanel();
		this.add(leftPanel);
		createToolbars(leftPanel);
		createTextAreas(leftPanel);
		createOptionArea(leftPanel);
	}

	private void createOptionArea(VerticalPanel parent) {
		HorizontalPanel panel=new HorizontalPanel();
		parent.add(panel);
		autoConvertCheck = new CheckBox("auto");
		panel.add(autoConvertCheck);
		Button bt=new Button("Convert");
		bt.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				doConvert();
			}
		});
		panel.add(bt);
	}

	private void createTextAreas(VerticalPanel parent) {
		textArea = new TextArea();
		parent.add(textArea);
		textArea.setStylePrimaryName("textbg");
	  	textArea.setWidth("560px");
	    textArea.setHeight("700px");
	    textArea.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				
				if(event.getNativeKeyCode()==KeyCodes.KEY_ENTER){
				doConvert();
				}
				else if(event.isControlKeyDown()){//copy or paste
					doConvert();
				}
				else{
					doConvert();
				}
			}
		});
	}

	private void createToolbars(VerticalPanel parent) {
		VerticalPanel panels=new VerticalPanel();
		parent.add(panels);
		HorizontalPanel button1Panel=new HorizontalPanel();
		panels.add(button1Panel);
	}
	
	public void onTextAreaUpdate(){
		if(autoConvertCheck.getValue()){
			doConvert();
		}else{
			
		}
	}

	private void doConvert() {
		String text=textArea.getText();
		String html=Marked.marked(text);
		htmlArea.setText(html);
		previewHTML.setHTML(html);
	}
}
