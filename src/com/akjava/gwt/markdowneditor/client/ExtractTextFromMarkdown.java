package com.akjava.gwt.markdowneditor.client;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.akjava.lib.common.utils.CSVUtils;
import com.google.common.base.Joiner;

public class ExtractTextFromMarkdown {

	
	
	

	public ExtractResult extract(String markdown){
		String[] lines=CSVUtils.splitLines(markdown);
		
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
			analyzer.setLineAt(i);
			
			//ignore
			if(MarkdownPredicates.getStartWithTitleLinePredicate().apply(line)){
				passStrings.add(line);
				continue;
			}
			
			if(next!=null && MarkdownPredicates.getStartWithTitle1OrTitle2Predicate().apply(next)){
				passStrings.add(line);
				passStrings.add(next);
				i++;
				continue;
			}
			
			String key=analyzer.getValueKey();
			analyzer.incrementIndex();
			
			String value=line;
			String converted="";
			result.addTemplate(key, value);
			converted="${"+key+"}";
			
			
			
			passStrings.add(converted);
		}
		
		result.setExtractedMarkdown(Joiner.on("\n").join(passStrings));
		
		return result;
	}
	
	private class Analyzer{
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
