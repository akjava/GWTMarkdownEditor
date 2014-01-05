GWTMarkdownEditor
=================

this is markdown editor with GWT

Inside
------
right now it's using **marked.js** inside.

>https://github.com/chjj/marked

but you can change easily,native code just call marked

```java
package com.akjava.gwt.markdowneditor.client;

public class Marked {
	public static final native String marked(String text)/*-{
	return $wnd.marked(text);
	}-*/;
}
```


Goal
-------
add short-cut or ui to help write markdown more easily(I usually forget how to write it down)

release as widget(i hope you can use it in your gwt-app)

License
-------
Apache 2.0

Functions
----
only support title control by ListBox

Tested
------
| OS| Browser| Version|Test Date|
| ------------- |:-------------:| -----:|-----:|
| Windows7-64bit      | Chrome| 31 |3 Jan 2013|

TODO
----
store last modified
add code,link,image
support csv2table
bold
line
