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
	public static StartWithTitle1OrTitle2Predicate getStartWithTitle1OrTitle2Predicate(){
		return StartWithTitle1OrTitle2Predicate.INSTANCE;
	}
	
	public enum  StartWithTitle1OrTitle2Predicate implements Predicate<String>{
		INSTANCE;
		@Override
		public boolean apply(String input) {
			if(input.indexOf("|")!=-1){
				return false;
			}
			return input.startsWith("===") || input.startsWith("---") ;
		}
		
	}
	
	public static TableLinePredicate getTableLinePredicate(){
		return TableLinePredicate.INSTANCE;
	}
	
	public enum  TableLinePredicate implements Predicate<String>{
		INSTANCE;
		@Override
		public boolean apply(String input) {
			input=input.trim();
			boolean containA=false;
			boolean containB=false;
			
			for(int i=0;i<input.length();i++){
				char ch=input.charAt(i);
				if(ch=='|'){
					containA=true;
				}else if(ch=='-'){
					containB=true;
				}else{
					return false;
				}
			}
			
			return containA&&containB;
		}
		
	}
}
