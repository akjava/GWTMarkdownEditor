package com.akjava.gwt.markdowneditor.client;

import java.util.ArrayList;
import java.util.List;

import com.akjava.gwt.lib.client.datalist.RichTitle;
import com.akjava.gwt.lib.client.datalist.SimpleTextData;
import com.akjava.lib.common.functions.StringFunctions;
import com.akjava.lib.common.functions.StringFunctions.StringToPreFixAndSuffix;
import com.akjava.lib.common.utils.FileNames;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.FluentIterable;

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
		public String apply(String line) {
			while(line.startsWith("#")){
				line=line.substring(1);
			}
			return line;
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
	

	public static class SimpleTextDataToTitleLinkTextFunction implements Function<SimpleTextData,String>{
		private boolean fullPath;
		private String basePath="";
		private String linkExtension=".html";
		private FileNames fileNames=FileNames.asSlash();
/**
 * 
 * @param basePath set domain name or parent directory
 * @param fullPath 
 * @param linkSuffix usually ".html" but you can set it ""
 */
		public SimpleTextDataToTitleLinkTextFunction(String basePath,boolean fullPath,String linkSuffix){
			this.basePath=basePath;
			this.fullPath=fullPath;
			this.linkExtension=linkSuffix;
		}
		@Override
		public String apply(SimpleTextData input) {
			String path=null;
			if(fullPath){
				path=input.getName();
			}else{
				path=fileNames.getFileName(input.getName());
			}
			
			path=FileNames.getRemovedExtensionName(path);
			path=basePath+path+linkExtension;
			
			String line=StringFunctions.getFirstLineOnly().apply(input.getData());
			String title=getStripTitleStart().apply(line);
			
			return MarkdownUtils.createLink(title, path);
		}
		
	}
	
	/**
	 * usually used for keyword links
	 * 
	 * from RichTitle
	 * RPG/Role Playing Game(pronounce)
	 * to
	 * RPG\tURL
	 * Role Playing Game\tURL
	 * pronounce\tURL
	 * 
	 * @author aki
	 *
	 */
	public static class SimpleTextKeywordLinksFunction implements Function<SimpleTextData,List<String>>{
		private boolean fullPath;
		private String basePath="";
		private String linkExtension=".html";
		private FileNames fileNames=FileNames.asSlash();
		
/**
 * 
 * @param basePath set domain name or parent directory
 * @param fullPath 
 * @param linkSuffix usually ".html" but you can set it ""
 */
		

		public SimpleTextKeywordLinksFunction(String basePath,boolean fullPath,String linkSuffix){
			this.basePath=basePath;
			this.fullPath=fullPath;
			this.linkExtension=linkSuffix;
		}
		@Override
		public List<String> apply(SimpleTextData input) {
			String path=null;
			if(fullPath){
				path=input.getName();
			}else{
				path=fileNames.getFileName(input.getName());
			}
			
			path=FileNames.getRemovedExtensionName(path);
			path=basePath+path+linkExtension;
			
			String line=StringFunctions.getFirstLineOnly().apply(input.getData());
			String title=getStripTitleStart().apply(line);
			RichTitle richTitle=new RichTitle(title);
			
			Iterable<String> bothTitles=richTitle.getBothTitles();
			
			return FluentIterable.from(bothTitles).transform(new StringToPreFixAndSuffix("",","+path)).toList();
			
		}
		
	}
}
