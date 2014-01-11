package markdown;



import junit.framework.TestCase;

import com.akjava.gwt.markdowneditor.client.ExtractTextFromMarkdown;
import com.akjava.gwt.markdowneditor.client.ExtractTextFromMarkdown.ExtractedResult;
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
	
	public void testStrike(){
		doTest("test3");
	}
	public void testCodeStrike(){
		doTestTemplate("test3");
	}
	
	public void testCode(){
		doTest("test4");
	}
	public void testCodeTemplate(){
		doTestTemplate("test4");
	}
	
	public void testTextCode(){
		doTest("test5");
	}
	public void testTextCodeTemplate(){
		doTestTemplate("test5");
	}
	
	public void testBlock(){
		doTest("test6");
	}
	public void testBlockTemplate(){
		doTestTemplate("test6");
	}
	
	public void testList(){
		doTest("test7");
	}
	public void testListTemplate(){
		doTestTemplate("test7");
	}
	
	public void testTable(){
		doTest("test8");
	}
	public void testTableTemplate(){
		doTestTemplate("test8");
	}
	
	public void testLink(){
		doTest("test9");
	}
	public void testLinkTemplate(){
		doTestTemplate("test9");
	}
	
	public void testImage(){
		doTest("test10");
	}
	public void testImageTemplate(){
		doTestTemplate("test10");
	}
	
	public void testMix1(){
		doTest("test11");
	}
	public void testMix1Template(){
		doTestTemplate("test11");
	}
	
	public void testMix2(){
		doTest("test12");
	}
	public void testMix2Template(){
		doTestTemplate("test12");
	}
	
	private void doTest(String key) {
		String markdown=IOUtils.readResourceAsUTF8Text("markdown/resources/"+key+".md");
		String correct=IOUtils.readResourceAsUTF8Text("markdown/resources/"+key+".properties");
		
		
		ExtractedResult result=extractTextFromMarkdown.extract(markdown);
		
		String extracted=joiner.join(result.getMarkdownTemplateMap());
		assertEquals(correct, extracted);
	}
	
	private void doTestTemplate(String key) {
		String markdown=IOUtils.readResourceAsUTF8Text("markdown/resources/"+key+".md");
		String correct=IOUtils.readResourceAsUTF8Text("markdown/resources/"+key+".txt");
		
		
		ExtractedResult result=extractTextFromMarkdown.extract(markdown);
		
		
		assertEquals(correct, result.getExtractedMarkdownTemplateText().replace("\n", "\r\n"));
	}
}
