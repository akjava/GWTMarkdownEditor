package markdown;



import junit.framework.TestCase;

import com.akjava.gwt.markdowneditor.client.ExtractTextFromMarkdown;
import com.akjava.gwt.markdowneditor.client.ExtractTextFromMarkdown.ExtractResult;
import com.akjava.lib.common.utils.IOUtils;
import com.google.common.base.Joiner;
import com.google.common.base.Joiner.MapJoiner;

public class ExtractTest extends TestCase {
private MapJoiner joiner=Joiner.on("\r\n").withKeyValueSeparator("=");
private ExtractTextFromMarkdown extractTextFromMarkdown=new ExtractTextFromMarkdown();
	public void testTitle(){
		doTest("test1");
	}
	public void testTitleTemplate(){
		doTestTemplate("test1");
	}


	public void testBold(){
		doTest("test2");
	}
	public void testBoldTemplate(){
		doTestTemplate("test2");
	}
	
	private void doTest(String key) {
		String markdown=IOUtils.readResourceAsUTF8Text("markdown/resources/"+key+".md");
		String correct=IOUtils.readResourceAsUTF8Text("markdown/resources/"+key+".properties");
		
		
		ExtractResult result=extractTextFromMarkdown.extract(markdown);
		
		String extracted=joiner.join(result.getMarkdownTemplateMap());
		assertEquals(correct, extracted);
	}
	
	private void doTestTemplate(String key) {
		String markdown=IOUtils.readResourceAsUTF8Text("markdown/resources/"+key+".md");
		String correct=IOUtils.readResourceAsUTF8Text("markdown/resources/"+key+".txt");
		
		
		ExtractResult result=extractTextFromMarkdown.extract(markdown);
		
		
		assertEquals(correct, result.getExtractedMarkdown().replace("\n", "\r\n"));
	}
}
