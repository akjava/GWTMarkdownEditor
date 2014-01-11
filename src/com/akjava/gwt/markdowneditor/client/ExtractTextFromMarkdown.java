package com.akjava.gwt.markdowneditor.client;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.lib.common.utils.CSVUtils;
import com.google.common.base.Joiner;

public class ExtractTextFromMarkdown {

	
	
	

	public ExtractResult extract(String markdown){
		String[] lines=CSVUtils.splitLinesWithGuava(markdown).toArray(new String[0]);
		
		ExtractResult result=new ExtractResult();
		result.setMarkdownTemplateMap(new LinkedHashMap<String, String>());
		
		Analyzer analyzer=new Analyzer();
		List<String> passStrings=new ArrayList<String>();
		for(int i=0;i<lines.length;i++){
			String line=lines[i];
			String next=null;
			if(i<lines.length-1){
				next=lines[i+1];
			}
			
			
			
			if(line.isEmpty()){
				passStrings.add("");
				continue;
			}
			
			
			analyzer.setLineAt(i);
			
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
			
			String newLine="";
			for(int j=0;j<line.length();j++){
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
						
						newLine+=line.substring(analyzer.textStart,cotinued+1);
						
					}else{
						//in italic ignore
						System.out.println("italic:"+line.substring(analyzer.textStart, j+1));
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
						newLine+=line.substring(analyzer.textStart,cotinued+1);
						j=cotinued;
						analyzer.bold=false;
						}else{
							System.out.println("bold:"+line.substring(analyzer.textStart, j+1));
						}
					}else{
						System.out.println("bold:"+line.substring(analyzer.textStart, j+1));
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
						if(line.charAt(k)!='*'){
							break;
						}
						cotinued=k;
					}
					int length=cotinued-j+1;
					if(length==1){
						analyzer.italic=true;
						System.out.println("italic:"+line.substring(analyzer.textStart, cotinued+1));
					}else{
						analyzer.bold=true;
						System.out.println("bold:"+line.substring(analyzer.textStart, cotinued+1));
					}
					j=cotinued;//skip here
				}else{
					//safe text
					analyzer.text+=ch;
					System.out.println("safe-text:"+analyzer.text);
					}
				}
			}
			
			
			if(analyzer.italic){
				newLine+=line.substring(analyzer.textStart);
				passStrings.add(newLine);
			}else if(analyzer.bold){
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
					String converted="";
					result.addTemplate(key, value);
					converted="${"+key+"}";
					
					newLine+=converted;
				}
				
				
				passStrings.add(newLine);
			}
			
			
			
			
		}
		
		result.setExtractedMarkdown(Joiner.on("\n").join(passStrings));
		
		return result;
	}
	
	private class Analyzer{
		private String text="";
		boolean italic;
		boolean bold;
		boolean strike;
		int valueIndex=1;
		int lineAt;
		int textStart;
		int textEnd;
		boolean incode;
		boolean intag;//not support nested tag do search simply..
		private ExtractResult extractedResult;
		public void setLineAt(int at){
			lineAt=at;
			textStart=0;
			textEnd=0;
			bold=false;
			italic=false;
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
	
	public static class ExtractResult{
		
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
		public String getExtractedMarkdown() {
			return extractedMarkdown;
		}
	}
}
