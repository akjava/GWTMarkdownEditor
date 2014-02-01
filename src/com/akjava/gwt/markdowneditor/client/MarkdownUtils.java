package com.akjava.gwt.markdowneditor.client;

import java.util.List;
import java.util.Map;

import com.akjava.gwt.lib.client.GWTJSLogger;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.markdowneditor.client.ExtractTextFromMarkdown.ExtractedResult;
import com.akjava.lib.common.utils.TemplateUtils;
import com.akjava.lib.common.utils.log.GWTLogger;

public class MarkdownUtils {
private MarkdownUtils(){}
public static String createLink(String title,String url){
	if(title==null){
		title="";
	}
	return "["+title+"]"+"("+url+")";
}

public static String createItalic(String title){
	if(title==null){
		title="";
	}
	return "*"+title+"*";
}

public static String createImage(String title,String url){
	if(title==null){
		title="";
	}
	return "!["+title+"]"+"("+url+")";
}




public static String markdownToKeyAndUrlMarkdown(ExtractTextFromMarkdown extractTextFromMarkdown,List<KeyAndUrl> keyAndUrls,boolean eachKeyOnlyOnce,String markdownText){
	extractTextFromMarkdown.setLogger(new GWTJSLogger());
	ExtractedResult extractedResult=extractTextFromMarkdown.extract(markdownText);
	
			
	Map<String,String> map=extractedResult.getMarkdownTemplateMap();
	KeyAndUrlUtils.insertKeyAndUrl(map, keyAndUrls, eachKeyOnlyOnce);

	return TemplateUtils.createText(extractedResult.getExtractedMarkdownTemplateText(), map);
}


}
