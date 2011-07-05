package proba;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.*;

public class CreatePDF {

	/**
	 * @param args
	 */
	
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		PDDocument document = new PDDocument();
		PDPage page = new PDPage();
		document.addPage(page);
		PDFont font = PDTrueTypeFont.loadTTF(document, "/home/igor/.fonts/arial.ttf");
		PDPageContentStream contentStream = new PDPageContentStream(document, page);		
		contentStream.beginText();
		contentStream.setFont( font, 12 );
		
		
		contentStream.moveTextPositionByAmount(0, page.getMediaBox().getHeight()-1);
		contentStream.drawString("LLL");
		//contentStream.moveTextPositionByAmount(0,10);
		//contentStream.drawString("Simevski");
		
		contentStream.endText();
		contentStream.close();
		document.save("/home/igor/Desktop/python/asd.pdf");
		document.close();
		
	}

}
