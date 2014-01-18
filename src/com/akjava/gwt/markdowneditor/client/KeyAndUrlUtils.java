package com.akjava.gwt.markdowneditor.client;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.akjava.lib.common.predicates.StringPredicates;
import com.akjava.lib.common.utils.CSVUtils;
import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.Collections2;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;

public class KeyAndUrlUtils {
	private KeyAndUrlUtils(){}
	public static List<KeyAndUrl> loadFromCsvText(String text){
		List<KeyAndUrl> lists=FluentIterable.from(CSVUtils.splitLinesWithGuava(text))
				.filter(StringPredicates.getNotEmpty())
				.transform(getTextToKeyAndUrlFunction()).toList();
			
		return Lists.newArrayList(lists);//for sort
	}
	
	public static TextToKeyAndUrlFunction getTextToKeyAndUrlFunction(){
		return TextToKeyAndUrlFunction.INSTANCE;
	}
	public enum  TextToKeyAndUrlFunction implements Function<String,KeyAndUrl>{
		INSTANCE;
		private  Splitter splitter=Splitter.on(",").limit(2);
		@Override
		public KeyAndUrl apply(String input) {
			List<String> values=Lists.newArrayList(splitter.split(input));
			String key=values.get(0);//must be
			String url=values.size()>1?values.get(1):"#";
			return new KeyAndUrl(key,url);
		}
		
	}
	public static final boolean EachKeyOnlyOnce=true;
	/**
	 * this function do sort
	 * @param extractedValues
	 * @param keywords
	 * @param eachKeyOnlyOnce
	 */
	public static void insertKeyAndUrl(Map<String,String> extractedValues,List<KeyAndUrl> keywords,boolean eachKeyOnlyOnce){
		//reset used
		for(KeyAndUrl key:keywords){
			if(key.isUsed()){
				key.setUsed(false);
			}
		}
		//use keywords directory to cut down time.
		Collections.sort(keywords);
		
		
		for(String keyName:extractedValues.keySet()){
			String value=extractedValues.get(keyName);
			String newValue=replaceTextToKeyAndUrl(value,keywords,eachKeyOnlyOnce);
			extractedValues.put(keyName, newValue);
		}
	}
	/**
	 * this is totally replace,do extract first
	 * and sort before by yourself ,usually called insertKeyAndUrl so not sort
	 * Collections.sort(keywords);
	 * 
	 * @param text
	 * @param keywords must be sorted
	 * @return
	 */
	public static String replaceTextToKeyAndUrl(String text,List<KeyAndUrl> keywords,boolean eachKeyOnlyOnce){
		StringBuilder sbuilder=new StringBuilder();
		String remainText=text;
        String notMatchText="";
        while(remainText.length()>0){
            boolean match=false;
            FOR:for(int j=0;j<keywords.size();j++){
            	KeyAndUrl keyword=keywords.get(j);
                if(remainText.startsWith(keyword.getKey())){
                   //System.out.println("mutch:"+keyword);
                    if(notMatchText.length()>0){
                        sbuilder.append(notMatchText);
                    }
                  
                    if(eachKeyOnlyOnce && keyword.isUsed()){
                    	 sbuilder.append(keyword.getKey());//add as plain
                    }else{
                    	 sbuilder.append(MarkdownUtils.createLink(keyword.getKey(), keyword.getUrl()));
                    	 keyword.setUsed(true);
                    }
                    notMatchText="";
                    remainText=remainText.substring(keyword.getKey().length());
                    match=true;
                    break FOR;
                }
            }
            //check each character
            if(match==false){
                notMatchText+=remainText.charAt(0);
                remainText=remainText.substring(1);
            }
        }
        //finally add remain.
        if(notMatchText.length()>0){
        	sbuilder.append(notMatchText);
        }
		
        return sbuilder.toString();
	}
}
