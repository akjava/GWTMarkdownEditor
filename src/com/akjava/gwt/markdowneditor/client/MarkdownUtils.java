package com.akjava.gwt.markdowneditor.client;

import java.util.List;
import java.util.Map;

import com.akjava.gwt.markdowneditor.client.ExtractTextFromMarkdown.ExtractedResult;
import com.akjava.lib.common.utils.TemplateUtils;

public class MarkdownUtils {
private MarkdownUtils(){}
public static String createLink(String title,String url){
	if(title==null){
		title="";
	}
	return "["+title+"]"+"("+url+")";
}

public static String createImage(String title,String url){
	if(title==null){
		title="";
	}
	return "!["+title+"]"+"("+url+")";
}

public static String markdownToKeyAndUrlMarkdown(ExtractTextFromMarkdown extractTextFromMarkdown,List<KeyAndUrl> keyAndUrls,boolean eachKeyOnlyOnce,String markdownText){
	ExtractedResult extractedResult=extractTextFromMarkdown.extract(markdownText);
	Map<String,String> map=extractedResult.getMarkdownTemplateMap();
	KeyAndUrlUtils.insertKeyAndUrl(map, keyAndUrls, eachKeyOnlyOnce);
	return TemplateUtils.createText(extractedResult.getExtractedMarkdownTemplateText(), map);
}


}
