package com.akjava.gwt.markdowneditor.client;

import com.akjava.gwt.lib.client.LogUtils;

/**
 * for replace
 * usually read form KeyAndUrlUtils.loadFromCsvText
 * 
 * csv format is simplly 
 * title,url
 * 
 * how to replace
 * but totally limited text can replace it.
 * @see MarkdownFunctions.MarkdownToKeyAndUrlMarkdown
 * 
 * @author aki
 *
 */
public class KeyAndUrl implements Comparable<KeyAndUrl>{
private boolean used;
private boolean replaceWithoutLink;

/*
 * the key-url is not completed or same path
 */
public boolean isReplaceWithoutLink() {
	return replaceWithoutLink;
}
public void setReplaceWithoutLink(boolean replaceWithoutLink) {
	this.replaceWithoutLink = replaceWithoutLink;
}
public boolean isUsed() {
	return used;
}
public void setUsed(boolean used) {
	this.used = used;
}
private String key;
public KeyAndUrl(String key, String url) {
	this.key=key;
	this.url=url;
}
public String getKey() {
	return key;
}
public void setKey(String key) {
	this.key = key;
}
public String getUrl() {
	return url;
}
public void setUrl(String url) {
	this.url = url;
}
private String url;

/**
 * default sort is reversed,for replace
 */
public int compareTo(KeyAndUrl o) {
    if(o instanceof KeyAndUrl){
        int result= ((KeyAndUrl)o).getKey().compareTo(this.getKey());
        if(result!=0){
        	return result;
        }
        return  ((KeyAndUrl)o).getUrl().compareTo(this.getUrl());
    }
    return 0;
}
@Override
public boolean equals(Object o) {
	if(o instanceof KeyAndUrl){
		return this.toString().equals(o.toString());
	}
   return false;
  }
@Override
public String toString() {
    return key+","+url;
  }
}
