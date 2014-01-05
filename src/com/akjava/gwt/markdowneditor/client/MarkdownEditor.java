package com.akjava.gwt.markdowneditor.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.StorageControler;
import com.akjava.gwt.lib.client.StorageException;
import com.akjava.gwt.lib.client.TextSelection;
import com.akjava.gwt.lib.client.widget.TabInputableTextArea;
import com.akjava.lib.common.functions.HtmlFunctions.StringToPreFixAndSuffix;
import com.akjava.lib.common.predicates.StringPredicates;
import com.akjava.lib.common.utils.CSVUtils;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
public class MarkdownEditor extends HorizontalPanel {

	private static final String KEY_MARKDOWNEDITOR = "KEY_MARKDOWN_EDITOR";
	private TextArea textArea;
	private CheckBox autoConvertCheck;
	private HTML previewHTML;
	private TextArea htmlArea;
	private ListBox titleLevelBox;

	private StorageControler storageControler=new StorageControler(false);
	
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
		textArea = new TabInputableTextArea();
		parent.add(textArea);
		
		textArea.setText(storageControler.getValue(KEY_MARKDOWNEDITOR, ""));
		
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
				
				if(index>6){
					index=0;
				}
				titleSelected(index);
				
				
			}
		});
		titleLevelBox.setSelectedIndex(7);//default empty
		button1Panel.add(titleLevelBox);
		
		Button boldBt=new Button("Bold",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				for(TextSelection selection:TextSelection.createTextSelection(textArea).asSet()){
					insertBetweenSelectionText(selection,"**","**");	
					onTextAreaUpdate();
					}
				}
		});
		button1Panel.add(boldBt);
		Button ItalicBt=new Button("Italic",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				for(TextSelection selection:TextSelection.createTextSelection(textArea).asSet()){
					insertBetweenSelectionText(selection,"*","*");	
					onTextAreaUpdate();
					}
				}
		});
		button1Panel.add(ItalicBt);
		
		Button strikeBt=new Button("Strike",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				for(TextSelection selection:TextSelection.createTextSelection(textArea).asSet()){
					insertBetweenSelectionText(selection,"~~","~~");	
					onTextAreaUpdate();
					}
				}
		});
		button1Panel.add(strikeBt);
		
		Button codeBt=new Button("Code",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				for(TextSelection selection:TextSelection.createTextSelection(textArea).asSet()){
					if(selection.containLineBreak()){
						insertBetweenSelectionText(selection,"```\n","\n```");
					}else{
						insertBetweenSelectionText(selection,"`","`");	
					}
					onTextAreaUpdate();
				}
				}
		});
		button1Panel.add(codeBt);
		
		Button blockBt=new Button("Block",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				for(TextSelection selection:TextSelection.createTextSelection(textArea).asSet()){
					insertBetweenSelectionText(selection.getCurrentLine(),">","");	
					onTextAreaUpdate();
				}
				}
		});
		blockBt.setTitle("Add a Blockquote");
		button1Panel.add(blockBt);
		
		Button lineBt=new Button("Line",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				for(TextSelection selection:TextSelection.createTextSelection(textArea).asSet()){
					insertBetweenSelectionText(selection.getCurrentLine(),"********\n","");	
					onTextAreaUpdate();
				}
				}
		});
		lineBt.setTitle("Insert a Line");
		button1Panel.add(lineBt);
		
		Button LinkBt=new Button("URL",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				for(TextSelection selection:TextSelection.createTextSelection(textArea).asSet()){
					String selected=selection.getSelection();
					String url=Window.prompt("put url", "http://");
					if(url==null){//cancel
						return;
					}
					String newText="["+selected+"]"+"("+url+")";
					
					selection.replace(newText);
					onTextAreaUpdate();
				}
				}
		});
		LinkBt.setTitle("Insert a URL");
		button1Panel.add(LinkBt);
		
		Button ListBt=new Button("List",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				for(TextSelection selection:TextSelection.createTextSelection(textArea).asSet()){
					String selected=selection.getSelection();
					
					List<String> converted=FluentIterable
					.from(Arrays.asList(selected.split("\n")))
					.filter(StringPredicates.getNotEmpty())
					.transform(new StringToPreFixAndSuffix("- ",""))
					.toList();
					
					selection.replace(Joiner.on("\n").join(converted));
					
					onTextAreaUpdate();
				}
				}
		});
		ListBt.setTitle("Convert to List");
		button1Panel.add(ListBt);
		
		Button tableBt=new Button("Table",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				for(TextSelection selection:TextSelection.createTextSelection(textArea).asSet()){
					String selected=selection.getSelection();
					List<List<String>> csvs=CSVUtils.csvToListList(selected, true, false);
					
					Joiner tableJoiner=Joiner.on("|");
					List<String> converted=new ArrayList<String>();
					for(int i=0;i<csvs.size();i++){
						List<String> csv=csvs.get(i);
							converted.add(tableJoiner.join(csv));
						if(i==0){//header need line
							List<String> lines=new ArrayList<String>();
							for(int j=0;j<csv.size();j++){
								lines.add("---");
							}
							converted.add(tableJoiner.join(lines));
						}
					}
				
					
					selection.replace(Joiner.on("\n").join(converted));
					
					onTextAreaUpdate();
				}
				}
		});
		tableBt.setTitle("Convert to table");
		button1Panel.add(tableBt);
		
		Button t2tableBt=new Button("Tab2Title",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				for(TextSelection selection:TextSelection.createTextSelection(textArea).asSet()){
					String selected=selection.getSelection();
					
					List<String> converted=FluentIterable
							.from(Arrays.asList(selected.split("\n")))
							
							.transform(new TabTitleFunction())
							.toList();
							
							selection.replace(Joiner.on("\n").join(converted));
					
					onTextAreaUpdate();
				}
				}
		});
		tableBt.setTitle("Convert to tab tree to title");
		button1Panel.add(t2tableBt);
		
	}
	
	public static class TabTitleFunction implements Function<String,String>{
		@Override
		public String apply(String input) {
			if(input.isEmpty()){
				return "";
			}
			String text="";
			int level=1;//all text add level
			for(int i=0;i<input.length();i++){
				if(input.charAt(i)=='\t'){
					level++;
				}else{
					text=input.substring(i);
					break;
				}
			}
			level=Math.min(level, 6);
			return Strings.repeat("#", level)+text;
		}
	}
	
	private void debug(String text){
		for(int i=0;i<text.length();i++){
			LogUtils.log(i+":"+text.charAt(i)+","+((int)text.charAt(i)));
		}
	}
	
	private static void insertBetweenSelectionText(TextSelection selection,String header,String footer){
    	String newText=header+selection.getSelection()+footer;
		selection.replace(newText);
    	
		TextArea target=selection.getTargetTextArea();
		target.setCursorPos(selection.getStart()+(header+selection.getSelection()).length());
		target.setFocus(true);
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
				}else{
					LogUtils.log("no next-line");
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
		
		try {
			storageControler.setValue(KEY_MARKDOWNEDITOR,text);
		} catch (StorageException e) {
			LogUtils.log(e.getMessage());
		}
	}
}
