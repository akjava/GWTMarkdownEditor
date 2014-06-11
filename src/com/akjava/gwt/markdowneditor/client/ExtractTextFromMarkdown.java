package com.akjava.gwt.markdowneditor.client;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.markdowneditor.client.MarkdownPredicates.StartWithTitle1OrTitle2Predicate;
import com.akjava.lib.common.utils.CSVUtils;
import com.akjava.lib.common.utils.StringUtils;
import com.akjava.lib.common.utils.log.GWTLogger;
import com.google.common.base.Joiner;

public class ExtractTextFromMarkdown {
private  GWTLogger logger;
	
	
	public GWTLogger getLogger() {
	return logger;
}

public void setLogger(GWTLogger logger) {
	this.logger = logger;
}

	private boolean isWhitespace(char ch){
		return ch==' ' || ch=='\t';
	}

	public static boolean debug;
	public ExtractedResult extract(String markdown){
		return extract(markdown,false);
	}
	public ExtractedResult extract(String markdown,boolean parseAll){
		String[] lines=CSVUtils.splitLinesWithGuava(markdown).toArray(new String[0]);
		
		ExtractedResult result=new ExtractedResult();
		result.setMarkdownTemplateMap(new LinkedHashMap<String, String>());
		
		Analyzer analyzer=new Analyzer();
		List<String> passStrings=new ArrayList<String>();
		for(int i=0;i<lines.length;i++){
			
			String line=lines[i];
			if(logger!=null){
				logger.log("extract:"+line);
			}
			String next=null;
			if(i<lines.length-1){
				next=lines[i+1];
			}
			
			
			
			if(line.isEmpty()){
				passStrings.add("");
				continue;
			}
			
			
			analyzer.setLineAt(i);
			
			if(analyzer.linecode){
				if(line.indexOf("```")!=-1){
					analyzer.linecode=false;
					passStrings.add(line);
					continue;
				}
				else{
					passStrings.add(line);
					continue;
				}
			}else{
				if(line.indexOf("```")!=-1){
					analyzer.linecode=true;
					passStrings.add(line);
					continue;
				}
			}
			
			String trimed=line.trim();
			if(line.startsWith(">")){
				//do it later
				//passStrings.add(line);
				//continue;
			}else{
				if(line.indexOf("<")!=-1 && line.indexOf(">")!=-1){
					passStrings.add(line);
					continue;
				}
			}
			
			if(trimed.startsWith("***")){
				passStrings.add(line);
				continue;
			}
			
			
			//remove possible tag
			
			
			//ignore title
			if(MarkdownPredicates.getStartWithTitleLinePredicate().apply(line)){
				passStrings.add(line);
				continue;
			}
			
			//TODO support table
			if(next!=null && MarkdownPredicates.getStartWithTitle1OrTitle2Predicate().apply(next)){
				passStrings.add(line);
				passStrings.add(next);
				i++;
				continue;
			}
			
			if(MarkdownPredicates.getTableLinePredicate().apply(trimed)){
				passStrings.add(line);
				continue;
			}
			
			if(debug)System.out.println("start-parse-character");
			
			String newLine="";
			for(int j=0;j<line.length();j++){
				//
				
				if(j==0){
				int match=StringUtils.countStartWith(line,'>');
				//LogUtils.log("match:"+match+","+line);
					if(match>0){
						
						j=match;
						newLine+=line.substring(0,match);
						
					}
				}
				
				//skip-list
				if(j==0 && trimed.startsWith("-")){
					int cotinued=0;
					//check until continue
					for(int k=j+1;k<trimed.length();k++){
						
						if(!isWhitespace(trimed.charAt(k))){//because GWT not supported yet,@see https://code.google.com/p/google-web-toolkit/issues/detail?id=1935
							break;
						}
						cotinued=k;
					}
					int length=cotinued;
					if(length>0){
					//whitespace - whitespace
					
					int trimstart=line.indexOf("-");
					j=trimstart+length+1;
					newLine+=line.substring(0,trimstart+length+1);
					}
				}
				
				char ch=line.charAt(j);
				if(analyzer.italic){
					if(ch=='*'){//close italic
						analyzer.italic=false;
						int cotinued=j;
						//check until continue
						for(int k=j+1;k<line.length();k++){
							
							if(line.charAt(k)!='*'){
								break;
							}
							cotinued=k;
						}
						//int length=cotinued-j+1;
						j=cotinued;//skip here
						
						
						String italicLine=line.substring(analyzer.textStart,cotinued+1);
						if(parseAll){
							String key=analyzer.getValueKey();
							analyzer.incrementIndex();
							
							String text=italicLine.replace("*", "");
							
							
							newLine+=addNewLine("*","*",key,text);
							result.addTemplate(key, fixSpaceText(text));
						}else{
							newLine+=italicLine;
						}
						
						
					}else{
						//in italic ignore
						if(debug)System.out.println("italic:"+line.substring(analyzer.textStart, j+1));
					}
				}else if(analyzer.bold){
					if(ch=='*'){
						int cotinued=j;
						for(int k=j+1;k<line.length();k++){
							
							if(line.charAt(k)!='*'){
								break;
							}
							cotinued=k;
						}
						int length=cotinued-j+1;
						if(length>1){
						String boldLine=line.substring(analyzer.textStart,cotinued+1);
						if(parseAll){
							String key=analyzer.getValueKey();
							analyzer.incrementIndex();
							
							String text=boldLine.replace("*", "");
							
							newLine+=addNewLine("**","**",key,text);
							result.addTemplate(key, fixSpaceText(text));
						}else{
							newLine+=boldLine;
						}
						
							
						
						j=cotinued;
						analyzer.bold=false;
						}else{
							if(debug)System.out.println("bold:"+line.substring(analyzer.textStart, j+1));
						}
					}else{
						if(debug)System.out.println("bold:"+line.substring(analyzer.textStart, j+1));
					}
				}else if(analyzer.strike){
					if(ch=='~'){
						int cotinued=j;
						for(int k=j+1;k<line.length();k++){
							
							if(line.charAt(k)!='~'){
								break;
							}
							cotinued=k;
						}
						int length=cotinued-j+1;
						if(length>1){
							String strikeText=line.substring(analyzer.textStart,cotinued+1);
							if(parseAll){
								String key=analyzer.getValueKey();
								analyzer.incrementIndex();
								
								String text=strikeText.replace("~", "");
								
								newLine+=addNewLine("~~","~~",key,text);
								result.addTemplate(key, fixSpaceText(text));
							}else{
								newLine+=strikeText;
							}
							
						
						j=cotinued;
						
						
						analyzer.strike=false;
						}else{
							if(debug)System.out.println("strike:"+line.substring(analyzer.textStart, j+1));
						}
					}else{
						if(debug)System.out.println("strike:"+line.substring(analyzer.textStart, j+1));
					}
				}else if(analyzer.textcode){
					if(ch=='`'){
						int cotinued=j;
						for(int k=j+1;k<line.length();k++){
							
							if(line.charAt(k)!='`'){
								break;
							}
							cotinued=k;
						}
						int length=cotinued-j+1;
						if(length>0){
							String codeText=line.substring(analyzer.textStart,cotinued+1);
							if(parseAll){
								String key=analyzer.getValueKey();
								analyzer.incrementIndex();
								
								String text=codeText.replace("`", "");
								
								newLine+=addNewLine("`","`",key,text);
								result.addTemplate(key, fixSpaceText(text));
							}else{
								newLine+=codeText;
							}
						
						j=cotinued;
						analyzer.textcode=false;
						}else{
							if(debug)System.out.println("textcode:"+line.substring(analyzer.textStart, j+1));
						}
					}else{
						if(debug)System.out.println("textcode:"+line.substring(analyzer.textStart, j+1));
					}
				}else{
				if(ch=='*'){
					//add safe text
					String safeText=analyzer.text;
					if(!safeText.isEmpty()){
						//do template
						String key=analyzer.getValueKey();
						analyzer.incrementIndex();
						
						String value=safeText;
						
						
						newLine+=addNewLine("","",key,value);
						result.addTemplate(key, fixSpaceText(value));
						
						
						analyzer.text="";
					}
					
					
					
					analyzer.textStart=j;
					int cotinued=j;
					//check until continue
					for(int k=j+1;k<line.length();k++){
						if(line.charAt(k)!='*'){
							break;
						}
						cotinued=k;
					}
					int length=cotinued-j+1;
					if(length==1){
						analyzer.italic=true;
						if(debug)System.out.println("italic:"+line.substring(analyzer.textStart, cotinued+1));
					}else{
						analyzer.bold=true;
						if(debug)System.out.println("bold:"+line.substring(analyzer.textStart, cotinued+1));
					}
					j=cotinued;//skip here
				}else if(ch=='~'){
					//add safe text
					String safeText=analyzer.text;
					if(!safeText.isEmpty()){
						//do template
						String key=analyzer.getValueKey();
						analyzer.incrementIndex();
						
						String value=safeText;
						String converted="";
						result.addTemplate(key, value);
						converted="${"+key+"}";
						
						newLine+=converted;
						analyzer.text="";
					}
					
					analyzer.textStart=j;
					int cotinued=j;
					//check until continue
					for(int k=j+1;k<line.length();k++){
						if(line.charAt(k)!='~'){
							break;
						}
						cotinued=k;
					}
					int length=cotinued-j+1;
					if(length>1){
						analyzer.strike=true;
						if(debug)System.out.println("strike:"+line.substring(analyzer.textStart, cotinued+1));
					}
					j=cotinued;//skip here
					
				}else if(ch=='`'){
					//add safe text
					String safeText=analyzer.text;
					if(!safeText.isEmpty()){
						//do template
						String key=analyzer.getValueKey();
						analyzer.incrementIndex();
						
						String value=safeText;
						String converted="";
						result.addTemplate(key, value);
						converted="${"+key+"}";
						
						newLine+=converted;
						analyzer.text="";
					}
					
					analyzer.textStart=j;
					int cotinued=j;
					//check until continue
					for(int k=j+1;k<line.length();k++){
						if(line.charAt(k)!='`'){
							break;
						}
						cotinued=k;
					}
					int length=cotinued-j+1;
					if(length>0){
						analyzer.textcode=true;
						if(debug)System.out.println("textcode:"+line.substring(analyzer.textStart, cotinued+1));
					}
					j=cotinued;//skip here
					
				}else if(ch=='|'){
					String safeText=analyzer.text;
					if(!safeText.isEmpty()){
						//do template
						String key=analyzer.getValueKey();
						analyzer.incrementIndex();
						
						String value=safeText;
						String converted="";
						result.addTemplate(key, value);
						converted="${"+key+"}";
						
						newLine+=converted;
						analyzer.text="";
					}
					newLine+='|';//just skip
				}else if(ch=='['){
					//LogUtils.log("["+line);
					boolean findLink=false;
					int connection=line.indexOf("](",j+1);
					if(connection!=-1){
						//int end=line.indexOf(")");//bug version to test catch error
						int end=line.indexOf(")",connection);
						if(end!=-1){//find link
							
							//make key
							String safeText=analyzer.text;
							//LogUtils.log("safe-text:"+safeText);
							if(!safeText.isEmpty()){
								//do template
								String key=analyzer.getValueKey();
								analyzer.incrementIndex();
								
								String value=safeText;
								String converted="";
								result.addTemplate(key, value);
								converted="${"+key+"}";
								
								newLine+=converted;
								analyzer.text="";
							}
							//skip links
							String linkText=line.substring(j,end+1);
							if(parseAll){
								String key=analyzer.getValueKey();
								analyzer.incrementIndex();
								
								int startLink=linkText.indexOf("[");
								int endLink=linkText.indexOf("](");
								
								String text=linkText.substring(startLink+1,endLink);
								
								
								
								
								newLine+=addNewLine(linkText.substring(0,startLink+1),linkText.substring(endLink),key,text);
								result.addTemplate(key, fixSpaceText(text));
								
							}else{
								newLine+=linkText;
							}
							
							j=end;//auto increment
							findLink=true;
						}
					}
					if(!findLink){
					analyzer.text+=ch;
					if(debug)System.out.println("safe-text:"+analyzer.text);
					}
				}else if(ch=='!'){
					boolean findLink=false;
					if(j<line.length()-1 && line.charAt(j+1)=='['){//TODO support alt
					int connection=line.indexOf("](",j+1);
					if(connection!=-1){
						int end=line.indexOf(")");
						if(end!=-1){//find link
							//make key
							String safeText=analyzer.text;
							if(!safeText.isEmpty()){
								//do template
								String key=analyzer.getValueKey();
								analyzer.incrementIndex();
								
								String value=safeText;
								String converted="";
								result.addTemplate(key, value);
								converted="${"+key+"}";
								
								newLine+=converted;
								analyzer.text="";
							}
							//skip links
							newLine+=line.substring(j,end+1);
							j=end;//auto increment
							findLink=true;
						}
					}
					}
					if(!findLink){
					analyzer.text+=ch;
					if(debug)System.out.println("safe-text:"+analyzer.text);
					}
				}
				else{
					//safe text
					analyzer.text+=ch;
					if(debug)System.out.println("safe-text:"+analyzer.text);
					}
				}
			}
			
			
			if(analyzer.italic){
				
				newLine+=line.substring(analyzer.textStart);
				passStrings.add(newLine);
			}else if(analyzer.bold){
				newLine+=line.substring(analyzer.textStart);
				passStrings.add(newLine);
			}else if(analyzer.strike){
				newLine+=line.substring(analyzer.textStart);
				passStrings.add(newLine);
			}else if(analyzer.textcode){
				newLine+=line.substring(analyzer.textStart);
				passStrings.add(newLine);
			}else{
				//add safe text
				String safeText=analyzer.text;
				if(!safeText.isEmpty()){
					//do template
					String key=analyzer.getValueKey();
					analyzer.incrementIndex();
					
					String value=safeText;
					/*
					String converted="";
					result.addTemplate(key, value);
					converted="${"+key+"}";
					
					newLine+=converted;
					*/
					
					newLine+=addNewLine("","",key,value);
					result.addTemplate(key, fixSpaceText(value));
				}
				
				
				passStrings.add(newLine);
			}
			
			
			
			
		}
		
		if(parseAll){
			for(int i=0;i<passStrings.size();i++){
				String line=passStrings.get(i);
				
				//prev line is title;
				if(i!=0 && MarkdownPredicates.getStartWithTitle1OrTitle2Predicate().apply(line)){
					String head=passStrings.get(i-1);
					String key=analyzer.getValueKey();
					passStrings.set(i-1, "${"+key+"}");
					result.addTemplate(key, head);
					analyzer.incrementIndex();
				}else if(MarkdownPredicates.getStartWithTitleLinePredicate().apply(line)){
					int index=line.lastIndexOf("#");
					if(index!=-1){
						String key=analyzer.getValueKey();
						passStrings.set(i,line.substring(0,index+1)+"${"+key+"}");
						result.addTemplate(key, line.substring(index+1));
						analyzer.incrementIndex();
					}
				}
			}
		}
		
		
		result.setExtractedMarkdown(Joiner.on("\n").join(passStrings));
		
		return result;
	}
	private String addNewLine(String before,String after,String keyName,String text){
		
		if(text.startsWith(" ")){
			before+=" ";
		}
		if(text.endsWith(" ")){
			after=" "+after;
		}
		//TODO accurate space
		
		return before+"${"+keyName+"}"+after;
	}
	
	private String fixSpaceText(String text){
		return text.trim();
	}
	
	private class Analyzer{
		private String text="";
		boolean italic;
		boolean bold;
		boolean strike;
		int valueIndex=1;
		//int lineAt;
		int textStart;
		//int textEnd;
		boolean textcode;
		boolean linecode;

		
		private ExtractedResult extractedResult;
		public void setLineAt(int at){
			//lineAt=at;
			textStart=0;
			//textEnd=0;
			bold=false;
			italic=false;
			strike=false;
			textcode=false;
			
			text="";
			
		}
		private int getValueIndex(){
			return valueIndex;
		}
		private String getValueKey(){
			return "v"+valueIndex;
		}
		private void incrementIndex(){
			valueIndex++;
		}
	}
	
	public static class ExtractedResult{
		
		private void addTemplate(String key,String value){
			markdownTemplateMap.put(key,value);
		}
		
		private Map<String,String> markdownTemplateMap;
		public Map<String, String> getMarkdownTemplateMap() {
			return markdownTemplateMap;
		}
		
		private String extractedMarkdown;
		public void setMarkdownTemplateMap(Map<String, String> markdownTemplateMap) {
			this.markdownTemplateMap = markdownTemplateMap;
		}
		public void setExtractedMarkdown(String extractedMarkdown) {
			this.extractedMarkdown = extractedMarkdown;
		}
		public String getExtractedMarkdownTemplateText() {
			return extractedMarkdown;
		}
	}
}
