package proba;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;

public class CreateRawPDF {

	/**
	 * @param args
	 */
	public static String[] commands1={

		"31.2 600.64 11.04 4.08 re",

		"W n",
		"",
		"",
		"",
		""
		};
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		PDDocument document = new PDDocument();
		PDPage page = new PDPage();
		PDRectangle mediaBox = new PDRectangle(592, 842);
		//PDRectangle mediaBox = new PDRectangle(1366, 1870);
		page.setMediaBox(mediaBox);
		document.addPage(page);
		
		//PDFont font = PDTrueTypeFont.loadTTF(document, "/home/igor/.fonts/arial.ttf");
		PDPageContentStream contentStream = new PDPageContentStream(document, page);
		
		for(int i=0;i<commands1.length;i++)
			contentStream.appendRawCommands(commands1[i]);
		contentStream.beginText();
		//for(int i=0;i<commands2.length;i++) contentStream.appendRawCommands(commands2[i]);
		//contentStream.appendRawCommands("13 771 Td\n");
		//contentStream.setFont(font, 12);		
		//contentStream.appendRawCommands("(IGOR)Tj\n");
		//contentStream.appendRawCommands("50 0 Td\n");
		//contentStream.appendRawCommands("(Simevski)Tj\n");
		contentStream.endText();
		contentStream.close();
		//System.out.println(page.getMediaBox().getHeight());
		document.save("/home/igor/Desktop/python/asd.pdf");
		document.close();
		
	}

}
