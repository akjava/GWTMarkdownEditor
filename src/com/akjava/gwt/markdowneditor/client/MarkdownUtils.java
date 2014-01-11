package com.akjava.gwt.markdowneditor.client;

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
}
