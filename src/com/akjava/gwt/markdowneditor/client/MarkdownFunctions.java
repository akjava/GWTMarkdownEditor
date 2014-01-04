package com.akjava.gwt.markdowneditor.client;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicates;
import com.google.common.base.Strings;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

public class MarkdownFunctions {
	/*
	 * i use supplier  for avoid initial create ConvertTitles
	 */
	private static Supplier<List<ConvertTitle>> titleSupplier=Suppliers.memoize(new ConvertTitleSupplier());
	private static  class ConvertTitleSupplier implements Supplier<List<ConvertTitle>>{
		@Override
		public List<ConvertTitle> get() {
			List<ConvertTitle> titles=new ArrayList<ConvertTitle>();
			for(int i=0;i<=6;i++){
				titles.add(new ConvertTitle(i));
			}
			return titles;
		}
		
	}
	public static StripTitleStart getStripTitleStart(){
		return StripTitleStart.INSTANCE;
	}
	public enum  StripTitleStart implements Function<String,String>{
		INSTANCE;
		@Override
		public String apply(String input) {
			while(input.startsWith("#")){
				input=input.substring(1);
			}
			return input;
		}
	}
	
	
	public static ConvertTitle getConvertTitle(int level){
		Preconditions.checkArgument(level<=6,"level must be in 0-6");
		return titleSupplier.get().get(level);
	}
	
	public static class  ConvertTitle implements Function<String,String>{
		private int level;
		public ConvertTitle(int level){
			this.level=level;
		}
		@Override
		public String apply(String input) {
			String stripped=getStripTitleStart().apply(input);

			if(level==1){
				if(!stripped.endsWith("\n")){
					stripped+="\n";
				}
				return stripped+Strings.repeat("=", Math.max(4, stripped.length()-1))+"\n";
			}else if(level==2){
				if(!stripped.endsWith("\n")){
					stripped+="\n";
				}
				return stripped+Strings.repeat("-", Math.max(4, stripped.length()-1))+"\n";
			}else if(level>2){
				String header=Strings.repeat("#",level);
				return header+stripped;
			}else{
				return stripped;
			}
		}
	}
}
