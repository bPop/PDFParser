package proba;

import java.awt.geom.Rectangle2D;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.util.PDFTextStripperByArea;

public class Main {
	public static double x,y,w,h;
	public static double maxy=841.8898;
	public static final String DEFAULT_ENCODING =
    	null;
        //"ISO-8859-1";
        //"ISO-8859-6"; //arabic
        //"US-ASCII";
        //"UTF-8";
        //"UTF-16";
        //"UTF-16BE";
        //"UTF-16LE";
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		String encoding = DEFAULT_ENCODING;
        //!!! Впиши патека до pdf датотеката и излезната датотека
        String pdfFile = "/home/igor/Desktop/python/asd.pdf";
        String textFile = "/home/igor/Desktop/python/result/res.txt";
        PDPage page =null;        
        Writer output = null; //за излезната датотека
        PDDocument document = null;
        
        try
        {
        	document = PDDocument.load( pdfFile );
            if( encoding != null )
            {
            	output = new OutputStreamWriter(
                            new FileOutputStream( textFile ), encoding );
            }
            else
            {
            //use default encoding
            	output = new OutputStreamWriter(
                            new FileOutputStream(textFile));
            }
            
            PDFTextStripperByArea stripper = new PDFTextStripperByArea();
            stripper.setSortByPosition(true);
            
            //овде се поставува страната во документот (методот get)
            //почетна страна е нулта, а не еден
            page = (PDPage) document.getDocumentCatalog().getAllPages().get(0);
            
            Rectangle2D rect = new Rectangle2D.Double();
            
            //координатниот почеток е горе лево
            //овде се поставува правоаголникот што екстрактира текст
            //во даден регион.
            //setRect(x,y,w,h)
            
            maxy=page.findMediaBox().getHeight();
            x=2.89;
            y=maxy-734.8;
            w=40;
            h=2;
            
            rect.setRect(x, y, w, h);            
            
            //додавање регион во stripper
            stripper.addRegion("1", rect);	

            //ова е наредбата за екстракција
            stripper.extractRegions(page);
            String localResult1 = stripper.getTextForRegion("1");
            
            
            System.out.println(page.findMediaBox().getHeight());
            
            
            System.out.println("---------\n"+localResult1+"\n-----------");
        }
        finally
        {
        	if( output != null )
            {
        		output.close();
            }
            if( document != null )
            {
            	document.close();
            }
        }
        //System.out.println(page.PAGE_SIZE_A4);
        //PDPageContentStream contentStream = new PDPageContentStream(document, page, true, false);
        //System.out.println(x+" "+y+" "+w+" "+h+" "+"re S\n");
        
        //contentStream.appendRawCommands("1 w\n"+x+" "+y+" "+w+" "+h+" "+"re\n");
        //contentStream.closeAndStroke();
	}

}
