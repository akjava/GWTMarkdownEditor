package com.akjava.gwt.markdowneditor.client;

import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.TextSelection;
import com.google.common.base.Optional;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
public class MarkdownEditor extends HorizontalPanel {

	private TextArea textArea;
	private CheckBox autoConvertCheck;
	private HTML previewHTML;
	private TextArea htmlArea;
	private ListBox titleLevelBox;

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
		autoConvertCheck.setValue(true);
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
		//textArea.setText("line1 abc\r\nline2 efg\ntest3");
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
		
		titleLevelBox = new ListBox();
		titleLevelBox.addItem("Clear");
		titleLevelBox.addItem("Title 1");
		titleLevelBox.addItem("Title 2");
		titleLevelBox.addItem("Title 3");
		titleLevelBox.addItem("Title 4");
		titleLevelBox.addItem("Title 5");
		titleLevelBox.addItem("Title 6");
		titleLevelBox.addItem("");
		titleLevelBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				int index=titleLevelBox.getSelectedIndex();
				LogUtils.log("selection-changed:"+index);
				if(index>6){
					index=0;
				}
				titleSelected(index);
				
				
			}
		});
		titleLevelBox.setSelectedIndex(7);//default empty
		button1Panel.add(titleLevelBox);
		
	}
	private void debug(String text){
		for(int i=0;i<text.length();i++){
			LogUtils.log(i+":"+text.charAt(i)+","+((int)text.charAt(i)));
		}
	}
	
	private void titleSelected(int level){
		Optional<TextSelection> selection= getTextSelection();
		
		for(TextSelection textSelection:selection.asSet()){
			LogUtils.log("select:"+textSelection.getSelection()+","+textSelection.getStart()+","+textSelection.getEnd());
			TextSelection tmp1=textSelection.getCurrentLine();
			LogUtils.log("current:"+tmp1.getSelection()+","+tmp1.getStart()+","+tmp1.getEnd());
			TextSelection lineSelection=textSelection.getCurrentLine();
			boolean startWithTitle=MarkdownPredicates.getStartWithTitleLinePredicate().apply(lineSelection.getSelection());
			if(startWithTitle){
				//can ignore next line
				String newLine=MarkdownFunctions.getConvertTitle(level).apply(lineSelection.getSelection());
				lineSelection.replace(newLine);
			}else{
				boolean nextLineIsTitle=false;
				Optional<TextSelection> nextLine=lineSelection.getNextLine();
				if(nextLine.isPresent()){
					TextSelection tmp2=nextLine.get();
					LogUtils.log("next:"+tmp2.getSelection()+","+tmp2.getStart()+","+tmp2.getEnd());
					TextSelection nextLineSelection=nextLine.get();
					nextLineIsTitle=MarkdownPredicates.getTitleLinePredicate().apply(nextLineSelection.getSelection());
				}
				String newLine=MarkdownFunctions.getConvertTitle(level).apply(lineSelection.getSelection());
				if(nextLineIsTitle){
					TextSelection bothSelection=new TextSelection(lineSelection.getStart(), nextLine.get().getEnd(), textArea);
					bothSelection.replace(newLine);
				}else{
					lineSelection.replace(newLine);
				}
			}
		}
		onTextAreaUpdate();
		titleLevelBox.setSelectedIndex(7);
	}
	
	public Optional<TextSelection> getTextSelection(){
		return TextSelection.createTextSelection(textArea);
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
