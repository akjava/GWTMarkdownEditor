package com.akjava.gwt.markdowneditor.client;

import com.google.common.base.Ascii;
import com.google.common.base.Predicate;

public class MarkdownPredicates {

	public static TitleLinePredicate getTitleLinePredicate(){
		return TitleLinePredicate.INSTANCE;
	}
	public enum  TitleLinePredicate implements Predicate<String>{
		INSTANCE;
		@Override
		public boolean apply(String input) {
			if(input.isEmpty()||input.length()<2){
				return false;
			}
			int ch=input.charAt(0);
			if(ch!='-' && ch!='='){
				return false;
			}
			for(int i=1;i<input.length();i++){
				if(input.charAt(i)==Ascii.LF){
					continue;//usually end?
				}
				if(input.charAt(i)!=ch){
					return false;
				}
			}
			return true;
		}
		
	}
	public static StartWithTitleLinePredicate getStartWithTitleLinePredicate(){
		return StartWithTitleLinePredicate.INSTANCE;
	}
	public enum  StartWithTitleLinePredicate implements Predicate<String>{
		INSTANCE;
		@Override
		public boolean apply(String input) {
			return input.startsWith("#");
		}
		
	}
}
