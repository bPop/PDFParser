package proba;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

public class ReadContent {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		PDDocument document = PDDocument.
		load("/home/igor/Desktop/python/pdfs/qiun.pdf");
		PDPage page = (PDPage) document.getDocumentCatalog().getAllPages().get(4);
		
		//System.out.println("----------------");
		
		//System.out.println(page.);
		//System.out.println(page.getTrimBox().getWidth());
		System.out.println(page.getContents().getInputStreamAsString());
	}
}
