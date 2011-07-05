package interfaces;

import org.apache.pdfbox.pdmodel.PDDocument;
import com.itextpdf.text.pdf.PdfReader;

public interface iParser {

	//parse the instr. in a page
	public void parsePage(PdfReader reader, PDDocument document, int n) throws Exception;
	//parse the instructions in a document
	public void parseDocument(String uri, boolean isXML, boolean isExcel, boolean isHTML, int pageFrom, int pageTo, float scaleX, float scaleY, float offsetX, float offsetY, int crossX, int crossY) throws Exception;
	//check if two cells are neighbours
	public boolean areNeighbours(iCell c1, iCell c2);
	//match the content to the cells
	public void match();
	//version with pdfbox
	public void matchPDFBOX(PDDocument document)throws Exception;
	//find tables as connected components
	//public List<Table> makeTables();
	//find neigh for each cell
	public int[][] findNeighbours();
	
	
}
