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
- Title(choosed by pull-down menu)
- Bold/Italic/Strike(select text and click button)
- Code(select text and click button)
- Blockquote(click button)
- Line(click button)
- URL(select and click button and set url)
- List(selection lines and click)
- Table(selection tabbed-csv-lines and click)
**Others**
store last converted in local-storage
###Tested
| OS| Browser| Version|Test Date|
| ------------- |:-------------:| -----:|-----:|
| Windows7-64bit      | Chrome| 31 |5 Jan 2013|
********
###TODO

support image,csv2table,csv2list,csv2titles
