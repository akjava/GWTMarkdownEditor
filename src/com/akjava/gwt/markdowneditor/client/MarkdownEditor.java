package com.akjava.gwt.markdowneditor.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.akjava.gwt.lib.client.GWTHTMLUtils;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.StorageControler;
import com.akjava.gwt.lib.client.StorageException;
import com.akjava.gwt.lib.client.TextSelection;
import com.akjava.gwt.lib.client.widget.TabInputableTextArea;
import com.akjava.lib.common.functions.StringFunctions;
import com.akjava.lib.common.predicates.StringPredicates;
import com.akjava.lib.common.tag.Tag;
import com.akjava.lib.common.utils.CSVUtils;
import com.akjava.lib.common.utils.ValuesUtils;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
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
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
public class MarkdownEditor extends SplitLayoutPanel {

	  public static final String KEY_SESSION="markdowneditor_session_value";//for session storage
	  public static final String KEY_LAST_SESSION_ID="markdowneditor_session_last_session_id";
	  
	//private static final String KEY_MARKDOWNEDITOR = "KEY_MARKDOWN_EDITOR";
	private TextArea textArea;
	
	public TextArea getTextArea() {
		return textArea;
	}
	

	private CheckBox autoConvertCheck;
	private HTML previewHTML;
	private TextArea htmlArea;
	public TextArea getHtmlArea() {
		return htmlArea;
	}

	private ListBox titleLevelBox;

	private StorageControler storageControler=new StorageControler(false);//use session
	private ListBox imageListBox;
	
	private Optional<String> syncHtmlKey=Optional.absent();
	private Optional<String> syncTextKey=Optional.absent();
	private TabLayoutPanel rightTabPanel;
	private VerticalPanel previewPanel;
	public void setSyncHtmlKey(Optional<String> syncHtmlKey) {
		this.syncHtmlKey = syncHtmlKey;
	}
	public void setSyncTextKey(Optional<String> syncTextKey) {
		this.syncTextKey = syncTextKey;
	}
	public MarkdownEditor(){
		this(false,"","");
	}
	
	public static final String MD_EDITOR_COPIED_TEXT="system_copied_text";
	public void copyText(String text) {
		try {
			storageControler.setValue(MD_EDITOR_COPIED_TEXT, text);
		} catch (StorageException e) {
			Window.alert(e.getMessage());
			e.printStackTrace();
		}	
	}
	public String getCopiedText(){
		return storageControler.getValue(MD_EDITOR_COPIED_TEXT,"");
	}
	/**
	 * 
	 * @param readOnly
	 * @param session_id
	 * @param defaultValue
	 * 
	 * HTML integration
	 * @see GWTMarkdownEditor
	 */
	public MarkdownEditor(boolean readOnly,String session_id,String defaultValue){
		createLeftPanels();
		createRightPanels();
		
		/**
		 * what is doing?
		 * try to keep value when browser back
		 * 
		 * session_id is usually get from html hidden-input.
		 * get KEY_LAST_SESSION_ID from storage.
		 * 
		 * if these are equals,it's same session and action ;get value from storage.
		 * last edited text always store in storage.
		 * 
		 * session_id is created when edit or add button clicked manually
		 * 
		 */
		 if(!session_id.isEmpty()){
	        	try {
	        		String lastSessionId = storageControler.getValue(KEY_LAST_SESSION_ID, "");
	        		GWT.log("gwtwiki:lastSessionId="+lastSessionId+",session_id="+session_id);
	        		if(!session_id.equals(lastSessionId)){
	        			//new situation
	        			GWT.log("gwtwiki:different session id,get initial value from PEOPERTY_DEFAULT_ID");
	        			//String data=ValueUtils.getFormValueById(PEOPERTY_DEFAULT_ID, "");
	    		        textArea.setText(defaultValue);
	    		        storageControler.setValue(KEY_SESSION,defaultValue);
	    		        storageControler.setValue(KEY_LAST_SESSION_ID, session_id);//mark used
	        		}else{
	        			GWT.log("gwtwiki:use last modified value");
	        			String lastModified=storageControler.getValue(KEY_SESSION, "");
	        			textArea.setText(lastModified);
	        		}
				} catch (StorageException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        	
	        }else{
	        	GWT.log("gwtwiki:no session id,get initial value from PEOPERTY_DEFAULT_ID");
	        	//String data=ValueUtils.getFormValueById(PEOPERTY_DEFAULT_ID, "");
		        textArea.setText(defaultValue);
	        }
		 
		 if(readOnly){
			 textArea.setReadOnly(readOnly);
		 }
		 doConvert();
	}
	
	
	public void setTotalHeightPX(int px){
	this.setHeight(px+"px");
	rightTabPanel.setHeight(px+"px");
	//	previewPanel.setHeight(px+"px");
	//	htmlArea.setHeight(px+"px");
	textArea.setHeight((px-80)+"px");
	}

	private void createRightPanels() {
		DockLayoutPanel rightPanel=new DockLayoutPanel(Unit.PX);
		rightPanel.setSize("100%", "100%");
		this.add(rightPanel);
		
		createOptionArea(rightPanel);

		rightTabPanel = new TabLayoutPanel(40,Unit.PX);
		rightPanel.add(rightTabPanel);
		rightTabPanel.setSize("100%","100%");
		createPreviewArea(rightTabPanel);
		createHtmlArea(rightTabPanel);
		rightTabPanel.selectTab(0);
	}
	
	public TabLayoutPanel getRightTabPanel(){
		return rightTabPanel;
	}

	private void createPreviewArea(TabLayoutPanel tab) {
		previewPanel = new VerticalPanel();//really need?
		previewPanel.setSize("100%","100%");
		ScrollPanel scroll=new ScrollPanel();
		scroll.setWidth("100%");
		previewPanel.add(scroll);
		scroll.setHeight("100%");
		previewHTML = new HTML();
		scroll.setWidget(previewHTML);
		tab.add(previewPanel,"Preview");
	}
	
	private void createHtmlArea(TabLayoutPanel tab) {
		VerticalPanel container=new VerticalPanel();
		container.setSize("100%", "100%");
		htmlArea = new TextArea();
		htmlArea.setWidth("95%");
		htmlArea.setHeight("100%");
		container.add(htmlArea);
		tab.add(container,"HTML");
	}

	private void createLeftPanels() {
		DockLayoutPanel leftPanel=new DockLayoutPanel(Unit.PX);
		
		leftPanel.setSize("100%", "100%");
		this.addWest(leftPanel,560);
		createToolbars(leftPanel);
		createTextAreas(leftPanel);
		
	}

	private void createOptionArea(DockLayoutPanel parent) {
		HorizontalPanel panel=new HorizontalPanel();
		parent.addNorth(panel,40);
		
		Button bt=new Button("Convert");
		bt.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				doConvert();
			}
		});
		panel.add(bt);
		
		autoConvertCheck = new CheckBox("auto");
		autoConvertCheck.setValue(true);
		autoConvertCheck.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if(autoConvertCheck.getValue()){
					onTextAreaUpdate();
				}
			}
		});
		panel.add(autoConvertCheck);
	}

	private void createTextAreas(Panel parent) {
		//VerticalPanel textAreaBase=new VerticalPanel();
		//textAreaBase.setSize("100%", "100%");
		//parent.add(textAreaBase);
		//textAreaBase.setBorderWidth(3);
		textArea = new TabInputableTextArea();
		parent.add(textArea);
		
		//textArea.setText(storageControler.getValue(KEY_MARKDOWNEDITOR, ""));
		
		textArea.setStylePrimaryName("textbg");
	  	textArea.setWidth("98%");
	    textArea.setHeight("98%");
	    textArea.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				
				if(event.getNativeKeyCode()==KeyCodes.KEY_ENTER){
					onTextAreaUpdate();
				}
				else if(event.isControlKeyDown()){//copy or paste
					onTextAreaUpdate();
				}
				else{
					onTextAreaUpdate();
				}
			}
		});
	    /*
	     * these called when focus out
	    textArea.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				LogUtils.log("value-changed");
			}
		});
	    textArea.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				LogUtils.log("changed");
			}
		});
		*/
	}

	private void createToolbars(DockLayoutPanel parent) {
		VerticalPanel panels=new VerticalPanel();
		//panels.setHeight("100px");
		
		parent.addNorth(panels,60);
		HorizontalPanel button1Panel=new HorizontalPanel();
		panels.add(button1Panel);
		
		HorizontalPanel button2Panel=new HorizontalPanel();
		panels.add(button2Panel);
		
		titleLevelBox = new ListBox();
		titleLevelBox.addItem("Clear");
		titleLevelBox.addItem("Head 1");
		titleLevelBox.addItem("Head 2");
		titleLevelBox.addItem("Head 3");
		titleLevelBox.addItem("Head 4");
		titleLevelBox.addItem("Head 5");
		titleLevelBox.addItem("Head 6");
		titleLevelBox.addItem("<HEAD>");
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
		titleLevelBox.setTitle("convert pointed line to title");
		titleLevelBox.setSelectedIndex(7);//default empty
		button2Panel.add(titleLevelBox);
		
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
		strikeBt.setTitle("insert strike");
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
		codeBt.setTitle("insert code");
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
		
		Button lineBt=new Button("HR",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				for(TextSelection selection:TextSelection.createTextSelection(textArea).asSet()){
					insertBetweenSelectionText(selection.getCurrentLine(),"********\n","");	
					onTextAreaUpdate();
				}
				}
		});
		lineBt.setTitle("Insert a horizontail Line");
		button1Panel.add(lineBt);
		
		Button LinkBt=new Button("URL",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				for(TextSelection selection:TextSelection.createTextSelection(textArea).asSet()){
					String selected=selection.getSelection();
					String url=Window.prompt("Link URL", "http://");
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
		
		Button alinkBt=new Button("Atag",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				for(TextSelection selection:TextSelection.createTextSelection(textArea).asSet()){
					String selected=selection.getSelection();
					String url=Window.prompt("Link URL", "http://");
					if(url==null){//cancel
						return;
					}
					String newText="<a href='"+url+"'>"+selected+"</a>";
					
					selection.replace(newText);
					onTextAreaUpdate();
				}
				}
		});
		alinkBt.setTitle("Insert a URL with alink");
		button1Panel.add(alinkBt);
		
		Button ImageBt=new Button("IMG",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				for(TextSelection selection:TextSelection.createTextSelection(textArea).asSet()){
					String selected=selection.getSelection();
					if(selected.endsWith(".jpg")||selected.endsWith(".png")){
						String newText="!["+""+"]"+"("+selected+")";
						
						selection.replace(newText);
						onTextAreaUpdate();
					}else{
					String url=Window.prompt("Image URL", "");
					if(url==null){//cancel
						return;
					}
					String newText="!["+selected+"]"+"("+url+")";
					
					selection.replace(newText);
					onTextAreaUpdate();
					}
				}
				}
		});
		ImageBt.setTitle("Insert a Image");
		button1Panel.add(ImageBt);
		
		Button ListBt=new Button("List",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				for(TextSelection selection:TextSelection.createTextSelection(textArea).asSet()){
					String selected=selection.getSelection();
					
					List<String> converted=FluentIterable
					.from(Arrays.asList(selected.split("\n")))
					.filter(StringPredicates.getNotEmpty())
					.transform(new StringFunctions.StringToPreFixAndSuffix("- ",""))
					.toList();
					
					selection.replace(Joiner.on("\n").join(converted));
					
					onTextAreaUpdate();
				}
				}
		});
		ListBt.setTitle("Convert to List");
		button1Panel.add(ListBt);
		
		Button numberListBt=new Button("Nist",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				for(TextSelection selection:TextSelection.createTextSelection(textArea).asSet()){
					String selected=selection.getSelection();
					
					List<String> converted=FluentIterable
					.from(Arrays.asList(selected.split("\n")))
					.filter(StringPredicates.getNotEmpty())
					.transform(new StringFunctions.StringToPreFixAndSuffixWithLineNumber(1,"${value}. ",""))
					.toList();
					
					selection.replace(Joiner.on("\n").join(converted));
					
					onTextAreaUpdate();
				}
				}
		});
		numberListBt.setTitle("Convert to Number List");
		button1Panel.add(numberListBt);
		
		Button tableBt=new Button("Tab2Table",new ClickHandler() {
			
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
		tableBt.setTitle("Convert Tab to table");
		button2Panel.add(tableBt);
		
		Button t2tableBt=new Button("Tab2Head",new ClickHandler() {
			
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
		t2tableBt.setTitle("Convert tab tree to Title");
		button2Panel.add(t2tableBt);
		
		Button tab2listBt=new Button("Tab2List",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				for(TextSelection selection:TextSelection.createTextSelection(textArea).asSet()){
					String selected=selection.getSelection();
					
					List<String> converted=FluentIterable
					.from(Arrays.asList(selected.split("\n")))
					
					.transform(new TabListFunction())
					.toList();
					
					selection.replace(Joiner.on("\n").join(converted));
					
					onTextAreaUpdate();
				}
				}
		});
		tab2listBt.setTitle("Convert tab tree to List");
		button2Panel.add(tab2listBt);
		
		imageListBox = new ListBox();
		imageListBox.addItem("");
		imageListBox.addItem("16");
		imageListBox.addItem("32");
		imageListBox.addItem("50");
		imageListBox.addItem("64");
		imageListBox.addItem("100");
		imageListBox.addItem("200");
		imageListBox.addItem("400");
		imageListBox.addItem("600");
		imageListBox.addItem("800");
		imageListBox.addItem("<img>");
		imageListBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
			
				int width=ValuesUtils.toInt(imageListBox.getItemText(imageListBox.getSelectedIndex()),0);
				
				imageSelected(width);
				
				
			}
		});
		imageListBox.setTitle("insert <img> tag with width");
		imageListBox.setSelectedIndex(imageListBox.getItemCount()-1);//default empty
		button2Panel.add(imageListBox);
		
		
		button2Panel.add(new Button("Undo", new ClickHandler() {//TODO better
			

				@Override
				public void onClick(ClickEvent event) {
					
					int undoIndex=historyIndex-1;
					if(undoIndex<0){
						return;
					}
						
					String text=textHistory.get(undoIndex);
					textArea.setText(text);
					lastHistory=text;
					historyIndex--;
					if(historyIndex<0){
						historyIndex=0;
					}
					onTextAreaUpdate();
				}
			}));
		button2Panel.add(new Button("Redo", new ClickHandler() {
			

				@Override
				public void onClick(ClickEvent event) {
					if(historyIndex<textHistory.size()){
						int redoIndex=historyIndex+1;
						if(redoIndex>=textHistory.size()){
							return;
						}
						String text=textHistory.get(redoIndex);
						textArea.setText(text);
						lastHistory=text;
						historyIndex++;
						if(historyIndex>=textHistory.size()){
							historyIndex=textHistory.size()-1;
						}
						onTextAreaUpdate();
					}
				}
			}));
		
		button2Panel.add(new Button("Paste", new ClickHandler() {
			

			@Override
			public void onClick(ClickEvent event) {
				for(TextSelection selection:TextSelection.createTextSelection(textArea).asSet()){
					String copiedText=getCopiedText();
					
					if(!Strings.isNullOrEmpty(copiedText)){
						insertBetweenSelectionText(selection,copiedText,"");	
						onTextAreaUpdate();
					}
				}
					
					
			}
		}));
		
	}
	


	public void clearHistory(){
		lastHistory=null;
		textHistory.clear();
		historyIndex=0;
	}
    public void addHistory(String text){
    	if(text.equals(lastHistory)){
    		return;
    	}
    	textHistory.add(text);
    	lastHistory=text;
    	//GWT.log("add history:"+text);
    	if(textHistory.size()>1000){
    		textHistory.remove(0);
    	}
    	historyIndex=textHistory.size()-1;
    }
	private List<String> textHistory=new ArrayList<String>();
	private int historyIndex;
	private String lastHistory;
	
	protected void imageSelected(int width) {
		String url=Window.prompt("Image URL", "");
		if(url==null){//cancel
			return;
		}
		
		Optional<TextSelection> selection= getTextSelection();
		for(TextSelection textSelection:selection.asSet()){
			Tag tag=new Tag("img").attr("src",url).attr("alt",textSelection.getSelection()).attr("width", ""+width).single();
			textSelection.replace(tag.toString());
		}
		onTextAreaUpdate();
		imageListBox.setSelectedIndex(imageListBox.getItemCount()-1);
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
	
	public static class TabListFunction implements Function<String,String>{
		@Override
		public String apply(String input) {
			if(input.isEmpty()){
				return "";
			}
			String text="";
			int level=0;//all text add level
			for(int i=0;i<input.length();i++){
				if(input.charAt(i)=='\t'){
					level++;
				}else{
					text=input.substring(i);
					break;
				}
			}
			//level=Math.min(level, 6);
			return Strings.repeat("\t", level)+"- "+text;
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
		addHistory(textArea.getText());//full backup
		if(autoConvertCheck.getValue()){
			doConvert();
		}else{
			
		}
		syncOutput();//sync storage or html/text for form
	}
	
	/**
	 * syncOutput is integrate for standard html web-apps
	 */
    public void syncOutput(){//TODO async
    	
    		//set values for html-form hidden
    		if(syncHtmlKey.isPresent()){
    			String html=htmlArea.getText();
    			GWTHTMLUtils.setValueAttributeById(syncHtmlKey.get(), html);
    		}
    		
        	String text=textArea.getText();
    		if(syncTextKey.isPresent()){
    			GWTHTMLUtils.setValueAttributeById(syncTextKey.get(), text);
    		}
    		
    		//store to session-storage for back-button
    		try {
    			storageControler.setValue(KEY_SESSION, textArea.getText());
    		} catch (StorageException e) {
    			e.printStackTrace();
    		}
    }

    public String getHtmlText(){
    	return htmlArea.getText();
    }
    public String getMarkdownText(){
    	return textArea.getText();
    }
    
    public String getHeader(){
    	return "";
    	}
    public String getFooter(){
    	return "";
    	}
	public void doConvert() {
		String text=textArea.getText();
		String html=Marked.marked(text);
		htmlArea.setText(html);
		previewHTML.setHTML(getHeader()+html+getFooter());	
	}
	public HTML getPreviewHTML() {
		return previewHTML;
	}
}
