GWTMarkdownEditor
=================

this is markdown editor with GWT

###Inside
right now it's using **marked.js** inside.

>https://github.com/chjj/marked

but you can  use another markup script easily,native code just call marked.change this line.

```java
package com.akjava.gwt.markdowneditor.client;

public class Marked {
	public static final native String marked(String text)/*-{
	return $wnd.marked(text);
	}-*/;
}
```


###Goal
add short-cut or ui to help write markdown more easily(I usually forget how to write it down)

release as widget(i hope you can use it in your gwt-app)

###License

Apache 2.0

###Functions
**Key**
- Title(choosed by pull-down menu or tab-tree)
- Bold/Italic/Strike(select text and click button)
- Code(select text and click button)
- Blockquote(click button)
- Line(click button)
- URL(select and click button and set url)
- Image(select and click button and set url)
- List(selection lines and click)
- Table(selection tabbed-csv-lines and click)

**Others**
- store last converted in local-storage

###Tested
| OS| Browser| Version|Test Date|
| ------------- |:-------------:| -----:|-----:|
| Windows7-64bit      | Chrome| 31 |5 Jan 2013|

###TODO
support Youtube
********
###Credits
<!-- Place this code where you want the badge to render. -->
<a href="//plus.google.com/103021856782435660635?prsrc=3"
   rel="publisher" target="_top" style="text-decoration:none;display:inline-block;color:#333;text-align:center; font:13px/16px arial,sans-serif;white-space:nowrap;">
<span style="display:inline-block;font-weight:bold;vertical-align:top;margin-right:5px; margin-top:0px;">Aki Miyazaki</span><span style="display:inline-block;vertical-align:top;margin-right:13px; margin-top:0px;">on</span>
<img src="http://ssl.gstatic.com/images/icons/gplus-16.png" alt="Google+" style="border:0;width:16px;height:16px;"/>
</a>