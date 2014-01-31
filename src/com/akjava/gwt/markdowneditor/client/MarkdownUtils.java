package com.akjava.gwt.markdowneditor.client;

import java.util.List;
import java.util.Map;

import com.akjava.gwt.lib.client.LogUtils;
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
	LogUtils.log("ready-extract");
	ExtractedResult extractedResult=extractTextFromMarkdown.extract(markdownText);
	LogUtils.log("before-extract");
	Map<String,String> map=extractedResult.getMarkdownTemplateMap();
	LogUtils.log("done-extract");
	KeyAndUrlUtils.insertKeyAndUrl(map, keyAndUrls, eachKeyOnlyOnce);
	LogUtils.log("inserted");
	return TemplateUtils.createText(extractedResult.getExtractedMarkdownTemplateText(), map);
}


}
