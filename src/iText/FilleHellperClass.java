package iText;

import com.itextpdf.text.pdf.PdfReader;

public class FilleHellperClass {

	public int numberPages(String filePath) throws Exception{
		PdfReader reader = new PdfReader(filePath);
		return reader.getNumberOfPages();
	}
}
