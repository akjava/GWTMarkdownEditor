package markdown;

import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import com.akjava.gwt.markdowneditor.client.ExtractTextFromMarkdown;
import com.akjava.gwt.markdowneditor.client.ExtractTextFromMarkdown.ExtractedResult;
import com.akjava.gwt.markdowneditor.client.KeyAndUrl;
import com.akjava.gwt.markdowneditor.client.KeyAndUrlUtils;
import com.akjava.lib.common.utils.CSVUtils;
import com.akjava.lib.common.utils.IOUtils;
import com.akjava.lib.common.utils.TemplateUtils;

public class KeywordTest extends TestCase{
	private ExtractTextFromMarkdown extractTextFromMarkdown=new ExtractTextFromMarkdown();
	private List<KeyAndUrl> keyAndUrls;

	@Override
	public void setUp(){
		String keywords=IOUtils.readResourceAsUTF8Text("markdown/resources2/keyword.csv");
		keyAndUrls = KeyAndUrlUtils.loadFromCsvText(keywords);
	}
	
	private void doTest(String key,boolean eachKeyOnlyOnce) {
		String markdown=IOUtils.readResourceAsUTF8Text("markdown/resources2/"+key+".md");
		
		String correct=IOUtils.readResourceAsUTF8Text("markdown/resources2/"+key+".txt");
		correct=CSVUtils.toNLineSeparator(correct);
		
		ExtractedResult extractedResult=extractTextFromMarkdown.extract(markdown);
		Map<String,String> map=extractedResult.getMarkdownTemplateMap();
		KeyAndUrlUtils.insertKeyword(map, keyAndUrls, eachKeyOnlyOnce);
		
		//System.out.println(extractedResult.getExtractedMarkdownTemplateText());
		
		String resultText=TemplateUtils.createText(extractedResult.getExtractedMarkdownTemplateText(), map);
		
		assertEquals(correct, resultText);
	}
	
	public void test1eachKeyOnlyOnce(){
		doTest("test1",true);
	}
	public void test1false(){
		doTest("test1b",false);
	}
}
