package iText;

import interfaces.iCell;
import interfaces.iParser;

import java.awt.Font;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.util.PDFTextStripperByArea;
import com.itextpdf.text.pdf.PRTokeniser;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;

public class TableParser implements iParser {
	List<Cell> list = new LinkedList<Cell>();
	List<Content> contents = new LinkedList<Content>();
	List<Line> lines = new LinkedList<Line>();
	public static float SCALETHX = 250;
	public static float SCALETHY = 150;
	public static int THRESKRATKIX = 1; // prag za otstranuvanje kratki po x
	public static int THRESKRATKIY = 1; // prag za otstranuvanje kratki po y
	public static float MANOFFX = 0; // offset za ekstrakcija po x
	public static float MANOFFY = 0; // offset za text extrakt po y
	public static int STRANICAOD = 1; // pocetna stranica
	public static int STRANICADO = -1; // krajna stranica 0 za samo STRANICAOD,
	// -1 za broj na stranici
	public static int PRIKAZISTR = 6; // stranica za pdf prikaz na skelet na
	// tabela

	public static float THRESLX = 2; // mal prag za sosedstvo po x
	public static float THRESLY = 2; // mal prag za sosedstvo po y
	public static float THRESHX = 400; // golem prag za sosedstvo po x
	public static float THRESHY = 50; // golem prag za sosedstvo po y
	public static float EPSILON = 1; // kelii blisku edna do druga
	public static float GRAYLEVEL = 0.4f; // pdf gray level
	public static final byte HORIZ = 0; // orientacija na liniite
	public static final byte VERT = 1;
	public static final byte TOCKA = 2;
	public static final byte ODREDI = 3; // odredi orientacija
	public static float PAGEWIDTH = 592f; // sirocina na stranica
	public static float PAGEHEIGHT = 842f; // dolzina na stranica
	public static float PGOFFX;
	public static float PGOFFY;
	public static float size;
	public static int granica;
	public static int stranica;
	public static int sheet = 0;
	public static PDDocument document;
	public static PDPage page;
	public static List<String> commands = new LinkedList<String>();
	int brStr;
	double maxy; // visocina na stranicata
	int neighbours[][];
	float[] lenOffset;
	public static int cntHor = 0, cntVert = 0;
	static String g_uri = "";
	static List<Table> tables;
	Line[] hor_linii;
	Line[] vert_linii;
	List<Line> hor_line = new LinkedList<Line>();
	List<Line> vert_line = new LinkedList<Line>();
	static List<Line> vl;
	static List<Line> hl;
	PrintWriter out;

	PdfReaderContentParser parser;
	TextExtractionStrategy strategy;
	PdfDictionary resources;

	public void initialize() {
		document = null;
		page = null;
		commands = new LinkedList<String>();
		hor_line = new LinkedList<Line>();
		vert_line = new LinkedList<Line>();
	}

	@Override
	public void parseDocument(String uri, boolean isXML, boolean isExcel,
			boolean isHTML, int pageFrom, int pageTo, float scaleX,
			float scaleY, float offsetX, float offsetY, int crossX, int crossY)
			throws Exception {
		System.out.println("Parsing started");
		g_uri = uri;
		String base = g_uri.substring(0, g_uri.indexOf(".") + 1);
		// base = "/home/igor/Desktop/python/xml.";

		STRANICAOD = pageFrom;
		STRANICADO = pageTo;
		SCALETHX = scaleX;
		SCALETHY = scaleY;
		MANOFFX = offsetX;
		MANOFFY = offsetY;
		THRESKRATKIX = crossX;
		THRESKRATKIY = crossX;
		String url = base + "xml";
		File xml = new File(url);
		if (isXML) {
			out = new PrintWriter(xml);
			out.println("<Document>");
		}
		PdfReader reader = new PdfReader(uri);
		document = PDDocument.load(uri);
		base = g_uri.substring(0, g_uri.indexOf(".") + 1);
		// base = "/home/igor/Desktop/python/excel.";
		url = base + "xls";
		AddInExcel excel = new AddInExcel(url);
		brStr = reader.getNumberOfPages();
		int odStr = 1, doStr = 1;
		if (STRANICADO == 0) {
			if (STRANICAOD > 0 && STRANICAOD <= brStr) {
				odStr = STRANICAOD;
				doStr = STRANICAOD;
			}
		} else if (STRANICADO == -1) {
			if (STRANICAOD > 0 && STRANICAOD <= brStr) {
				odStr = STRANICAOD;
			}
			doStr = brStr;
		} else {
			if (STRANICAOD > 0 && STRANICAOD <= brStr)
				odStr = STRANICAOD;
			else
				odStr = 1;
			if (STRANICADO >= STRANICAOD && STRANICADO <= brStr)
				doStr = STRANICADO;
			else
				doStr = brStr;
		}
		for (int i = odStr; i <= doStr; i++) {
			stranica = i - 1;
			page = (PDPage) document.getDocumentCatalog().getAllPages().get(
					stranica);
			if (page.getCropBox() != null) {
				PAGEHEIGHT = page.getCropBox().getHeight();
				PAGEWIDTH = page.getCropBox().getWidth();
				size = page.getMediaBox().getHeight();
				PGOFFY = size - page.getCropBox().getUpperRightY() + MANOFFY;
				PGOFFX = page.getCropBox().getLowerLeftX() + MANOFFX;
			} else {
				PAGEHEIGHT = page.getMediaBox().getHeight();
				PAGEWIDTH = page.getMediaBox().getWidth();
				size = PAGEHEIGHT;
				PGOFFX = 0;
				PGOFFY = 0;
			}
			float min = Math.min(PAGEHEIGHT, PAGEWIDTH);
			THRESLX = min / SCALETHX;
			THRESLY = min / SCALETHY;
			THRESHX = PAGEWIDTH - THRESLX;
			THRESHY = PAGEHEIGHT - THRESLY;
			tables = new LinkedList<Table>();

			try {
				list = new LinkedList<Cell>();
				cntHor = 0;
				cntVert = 0;
				parsePage(reader, document, i);
				// extracting to Excel
				if (isExcel)
					excel.add(tables);
				// extracting to XML
				if (isXML) {
					// docToXML(tables);
					XMLCreator xmlC = new XMLCreator(out, tables, i);
					xmlC.analyze();
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
			sheet++;
		}
		if (isXML) {
			out.append("</Document>\n");
			out.flush();
			out.close();
			System.out.println("XML done!");
		}
		try {
			if (isExcel) {
				excel.closeXcl();
				System.out.println("Excel done");
			}
		} catch (Exception e) {
			System.out.println("Excel exception");
			e.printStackTrace();
		} finally {
			// 1
			document.close();
		}

		// for(Cell c:
		// list){if(c.getContent()!=null)System.out.println(c.getContent().getText());}

	}

	@Override
	public void parsePage(PdfReader reader, PDDocument document, int n)
			throws Exception {
		System.out.println("Parsing page number " + n);
		// String content = new String(reader.getPageContent(n));
		// System.out.println(reader.getPageRotation(n));
		parser = new PdfReaderContentParser(reader);
		resources = reader.getPageN(n).getAsDict(PdfName.RESOURCES);
		byte[] streamBytes = reader.getPageContent(n);
		PRTokeniser tokenizer = new PRTokeniser(streamBytes);

		float x_pos = 0;
		float y_pos = 0;
		float leading = 0;
		Font font = null;
		float fontSize = 0;
		String dataType = null;
		String posledenToken;
		List<String> pomosna = new LinkedList<String>();
		LinkedList<String> rabotna = new LinkedList<String>();
		List<String> moveto = new LinkedList<String>();
		boolean opFlag, grayFlag = false;

		// анализатор на pdf содржински тек
		while (tokenizer.nextToken()) {
			opFlag = false;
			while (rabotna.size() > 10)
				rabotna.poll();
			posledenToken = tokenizer.getStringValue();
			rabotna.add(posledenToken);
			// доколку токенот е pdf оператор
			if (tokenizer.getTokenType() == PRTokeniser.TokenType.OTHER) {
				opFlag = true;
			}
			// доколку токенот означува правоаголник
			if (posledenToken.equals("re") && opFlag && !grayFlag) {
				if (tokenizer.nextToken()) {
					if (tokenizer.getStringValue().equals("W") && opFlag) {
						if (tokenizer.nextToken()) {
							if (tokenizer.getStringValue().equals("n")
									&& opFlag) {
								continue;
							}
						}
					}
				}
				pomosna = rabotna.subList(rabotna.size() - 5,
						rabotna.size() - 1);
				if (!analyzeRect(pomosna))
					list.add(new Cell(pomosna, null));
			}
			// доколку токенот е оператор за поместување
			else if (posledenToken.equals("m") && opFlag) {
				moveto = new LinkedList<String>();
				moveto.add(rabotna.get(rabotna.size() - 3));
				moveto.add(rabotna.get(rabotna.size() - 2));
			}
			// доколку е линија
			else if (posledenToken.equals("l") && opFlag) {
				// if(rabotna.size()>6 &&
				// rabotna.get(rabotna.size()-4).equals("m")){
				if (moveto.size() == 2) {
					pomosna = new LinkedList<String>();
					pomosna.add(moveto.get(0));
					pomosna.add(moveto.get(1));
					pomosna.add(rabotna.get(rabotna.size() - 3));
					pomosna.add(rabotna.get(rabotna.size() - 2));
					lines.add(new Line(pomosna, ODREDI, true));
				}
			}
			// доколку е закривена линија
			else if (posledenToken.equals("c") && opFlag) {
				if (moveto.size() == 2) {
					pomosna = new LinkedList<String>();
					pomosna.add(moveto.get(0));
					pomosna.add(moveto.get(1));
					for (int i = 7; i > 1; i -= 2) {
						pomosna.add(rabotna.get(rabotna.size() - i));
						pomosna.add(rabotna.get(rabotna.size() - i + 1));
						// lines.add(new Line(pomosna,ODREDI,true));
						pomosna.remove(pomosna.size() - 1);
						pomosna.remove(pomosna.size() - 1);
					}
				}
			}
			// приказ на текст
			else if (posledenToken.equals("Tj") && opFlag) {
				String text = rabotna.get(rabotna.size() - 2);
				Content con = new Content(x_pos, y_pos, text, dataType, font,
						fontSize);
				contents.add(con);
			} else if (tokenizer.getTokenType() == PRTokeniser.TokenType.START_ARRAY) {
				Content con = null;
				String text = "";
				while (tokenizer.nextToken()) {
					if (tokenizer.getTokenType() == PRTokeniser.TokenType.END_ARRAY) {
						break;
					}
					if (tokenizer.getTokenType() == PRTokeniser.TokenType.STRING) {
						text = tokenizer.getStringValue();
						con = new Content(x_pos, y_pos, text, dataType, font,
								fontSize);
						tokenizer.nextToken();
						contents.add(con);
					} else if (tokenizer.getTokenType() == PRTokeniser.TokenType.NUMBER
							&& !tokenizer.getStringValue().equals("TJ")) {
						x_pos += Float.parseFloat(tokenizer.getStringValue());
					} else
						break;
				}
			}
			// позиционирање на текст
			else if (posledenToken.equals("Td") || posledenToken.equals("Tm")
					&& opFlag) {
				float x = Float.parseFloat(rabotna.get(rabotna.size() - 3));
				float y = Float.parseFloat(rabotna.get(rabotna.size() - 2));
				x_pos += x;
				y_pos += y;
			} else if (posledenToken.equals("TD") && opFlag) {
				float x = Float.parseFloat(rabotna.get(rabotna.size() - 3));
				float y = Float.parseFloat(rabotna.get(rabotna.size() - 2));
				leading = -y;
				x_pos += x;
				y_pos += y;
			}
			// фонт, големина на фонт
			else if (posledenToken.equals("Tf") && opFlag) {
				// String fontName = rabotna.get(rabotna.size()-3);
				// font = new com.itextpdf.text.Font(fontName);
				fontSize = Float.parseFloat(rabotna.get(rabotna.size() - 2));
			}
			// позиционирање
			else if (posledenToken.equals("TL") && opFlag)
				leading = Float.parseFloat(rabotna.get(rabotna.size() - 2));
			else if (posledenToken.equals("T*") && opFlag)
				y_pos += leading;
			else if (posledenToken.equals("BT") && opFlag)
				x_pos = y_pos = 0;
			else if (posledenToken.equalsIgnoreCase("G") && opFlag) {
				if (Float.parseFloat(rabotna.get(rabotna.size() - 2)) > GRAYLEVEL)
					grayFlag = true;
				else
					grayFlag = false;
			}
		}

		hor_linii = new Line[cntHor]; // хоризонтални линии
		vert_linii = new Line[cntVert]; // вертикални линии
		hor_line = new LinkedList<Line>(); // помошна листа за хоризонтални
		vert_line = new LinkedList<Line>(); // помошна листа за вертикални
		List<Integer> lineOff = new LinkedList<Integer>();
		List<Integer> redOff = new LinkedList<Integer>();
		List<Float> redPos = new LinkedList<Float>();
		Line l;
		int ix, maxI;
		float len;

		// доколку има барем една линија
		if (cntHor > 0 && cntVert > 0) {
			if (cntHor > 0) {
				// подели ги линиите по соодветната листа
				int ih = 0, iv = 0;
				for (int j = 0; j < lines.size(); j++) {
					if (lines.get(j).getMode() == HORIZ) {
						hor_linii[ih] = lines.get(j);
						ih++;
					} else if (lines.get(j).getMode() == VERT) {
						vert_linii[iv] = lines.get(j);
						iv++;
					}
				}
				lines.clear(); // оваа листа веќе не треба
				Arrays.sort(hor_linii, new HorComparator()); // сортирај
				// горе-лево
				// долу-десно
				Arrays.sort(vert_linii, new VertComparator());

				// определи должина за секоја редица линии
				len = 0;
				lenOffset = new float[hor_linii.length];
				l = hor_linii[0];
				len = l.getKraj() - l.getxPoc();
				lenOffset[0] = len;
				for (int i = 1; i < hor_linii.length; i++) {
					if (hor_linii[i].getyPoc() == l.getyPoc()) {
						len += hor_linii[i].getKraj() - hor_linii[i].getxPoc();
					} else {
						l = hor_linii[i];
						lineOff.add(i);
						lenOffset[i] = len;
						len = l.getKraj() - l.getxPoc();
					}
				}
				if (lineOff.size() > 0) {
					if (lineOff.get(lineOff.size() - 1) < hor_linii.length) {
						lineOff.add(hor_linii.length);
						lenOffset[hor_linii.length - 1] = (hor_linii[hor_linii.length - 1]
								.getKraj() - hor_linii[hor_linii.length - 1]
								.getxPoc());
					}
				
					for (int i = 0; i < lineOff.size(); i++) {
						redOff.add(lineOff.get(i));
					}

					for (int i = 1; i < redOff.size(); i++) {
						if (hor_linii[redOff.get(i - 1) - 1].getyPoc()
							- hor_linii[redOff.get(i) - 1].getyPoc() < THRESLY) {
							redOff.remove(i - 1);
							i--;
						}
					}
				// позиционирај ги изместените линии на вистинско место
					ix = 0;
					for (int i = 0; i < redOff.size(); i++) {
						int posleden = 0;
						List<Integer> listaDolz = new LinkedList<Integer>();
						for (; ix < lineOff.size()
							&& lineOff.get(ix) < redOff.get(i); ix++) {
							listaDolz.add(lineOff.get(ix));
							posleden = ix;
						}
						if (posleden < hor_linii.length)
							listaDolz.add(lineOff.get(posleden + 1));
						if (listaDolz.size() == 1)
							maxI = 0;
						else
							maxI = maxIndex(listaDolz);
						redPos.add(hor_linii[maxI].getyPoc());
					}
					ix = 0;
					for (int i = 0; i < redPos.size(); i++) {
						for (; ix < hor_linii.length && ix < redOff.get(i); ix++) {
							hor_linii[ix].setyPoc(redPos.get(i));
						}
					}
				// повторно сортирај
					Arrays.sort(hor_linii, new HorComparator());
					ix = 0;
				// спои ги линиите на исто ниво
					for (int i = 1; i < hor_linii.length; i++) {
						l = new Line(hor_linii[i - 1].getxPoc(), hor_linii[i - 1]
							.getyPoc(), hor_linii[i - 1].getKraj(),
							hor_linii[i - 1].getyPoc(), TableParser.HORIZ);
						hor_line.add(l);
						float max = l.getKraj();
						while (hor_linii[i - 1].getyPoc() == hor_linii[i].getyPoc()) {
						if (hor_line.get(hor_line.size() - 1).getKraj()
								+ THRESLX > hor_linii[i].getxPoc()) {
							if (hor_linii[i].getKraj() > max)
								max = hor_linii[i].getKraj();
							hor_line.get(hor_line.size() - 1).setKraj(max);
						} else {
							l = new Line(hor_linii[i].getxPoc(), hor_linii[i]
									.getyPoc(), hor_linii[i].getKraj(),
									hor_linii[i].getyPoc(), TableParser.HORIZ);
							hor_line.add(l);
							max = l.getKraj();
						}
						if (i + 1 < hor_linii.length)
							i++;
						else
							break;
						}
					}
					if (hor_line.size() != 0) {
						l = hor_line.get(hor_line.size() - 1);
						if (l.getyPoc() > hor_linii[hor_linii.length - 1].getyPoc()) {
							hor_line.add(hor_linii[hor_linii.length - 1]);
						}
					}
				}
				// vertikalni linii, сличен коментар важи и овде
				// --------------------------------------
			}
			if (cntVert > 0) {
				lineOff = new LinkedList<Integer>();
				redOff = new LinkedList<Integer>(); // reduced offset - so linii
				// gore-dole
				redPos = new LinkedList<Float>(); // reduced position

				lenOffset = new float[vert_linii.length];
				l = vert_linii[0];
				len = l.getyPoc() - l.getKraj();
				lenOffset[0] = len;
				for (int i = 1; i < vert_linii.length; i++) {
					if (vert_linii[i].getxPoc() == l.getxPoc()) {
						len += vert_linii[i].getyPoc()
								- vert_linii[i].getKraj();
					} else {
						l = vert_linii[i];
						lineOff.add(i);
						lenOffset[i] = len;
						len = l.getyPoc() - l.getKraj();
					}
				}
				if (lineOff.size() > 0) {
					if (lineOff.get(lineOff.size() - 1) < vert_linii.length) {
						lineOff.add(vert_linii.length);
						lenOffset[vert_linii.length - 1] = (vert_linii[vert_linii.length - 1]
								.getKraj() - vert_linii[vert_linii.length - 1]
								.getxPoc());
					}
				
					for (int i = 0; i < lineOff.size(); i++) {
						redOff.add(lineOff.get(i));
					}

					for (int i = 1; i < redOff.size(); i++) {
						if (vert_linii[redOff.get(i) - 1].getxPoc()
							- vert_linii[redOff.get(i - 1) - 1].getxPoc() < THRESLX) {
							redOff.remove(i - 1);
							i--;
						}
					}
					ix = 0;
					for (int i = 0; i < redOff.size(); i++) {
						List<Integer> listaDolz = new LinkedList<Integer>();
						int posleden = 0;
						for (; ix < lineOff.size()
							&& lineOff.get(ix) < redOff.get(i); ix++) {
							listaDolz.add(lineOff.get(ix));
							posleden = ix;
						}
						if (posleden < hor_linii.length)
							listaDolz.add(lineOff.get(posleden + 1));
						if (listaDolz.size() == 1)
							maxI = 0;
						else
							maxI = maxIndex(listaDolz);
						redPos.add(vert_linii[maxI].getxPoc());
					}
					ix = 0;
					for (int i = 0; i < redPos.size(); i++) {
						for (; ix < vert_linii.length && ix < redOff.get(i); ix++) {
							vert_linii[ix].setxPoc(redPos.get(i));
						}
					}
					Arrays.sort(vert_linii, new VertComparator());

					for (int i = 1; i < vert_linii.length; i++) {

						l = new Line(vert_linii[i - 1].getxPoc(), vert_linii[i - 1]
							.getyPoc(), vert_linii[i - 1].getxPoc(),
							vert_linii[i - 1].getKraj(), TableParser.VERT);
						vert_line.add(l);
						float min = l.getKraj();
						while (vert_linii[i - 1].getxPoc() == vert_linii[i]
							.getxPoc()) {
							if (vert_line.get(vert_line.size() - 1).getKraj()
								- THRESLY < vert_linii[i].getyPoc()) {
								if (vert_linii[i].getKraj() < min)
									min = vert_linii[i].getKraj();
								vert_line.get(vert_line.size() - 1).setKraj(min);
							} else {
								l = new Line(vert_linii[i].getxPoc(), vert_linii[i]
									.getyPoc(), vert_linii[i].getxPoc(),
									vert_linii[i].getKraj(), TableParser.VERT);
								vert_line.add(l);
								min = l.getKraj();
							}
							if (i + 1 < vert_linii.length)
								i++;
							else
								break;
						}
					}
				// додај ја последната линија
					if (vert_line.size() != 0) {
						l = vert_line.get(vert_line.size() - 1);
						if (l.getxPoc() < vert_linii[vert_linii.length - 1]
							.getxPoc()) {
							vert_line.add(vert_linii[vert_linii.length - 1]);
						}
					}
				}
			}

			hor_linii = new Line[hor_line.size()];
			vert_linii = new Line[vert_line.size()];
			for (int i = 0; i < hor_linii.length; i++) {
				hor_linii[i] = hor_line.get(i);
			}
			for (int i = 0; i < vert_linii.length; i++) {
				vert_linii[i] = vert_line.get(i);
			}
			// сортирај во формат погоден за позиционирање на табели
			Arrays.sort(hor_linii, new HorCompTab());
			Arrays.sort(vert_linii, new VertCompTab());
			// уште едни помошни низи за хоризонталните и вертикални линии
			vl = new LinkedList<Line>();
			hl = new LinkedList<Line>();

			for (int i = 0; i < hor_linii.length; i++) {
				hl.add(hor_line.get(i));
				hl.get(i).setId(i);
			}
			granica = hor_linii.length; // граница помеѓу хор. и верт. линии
			for (int i = 0; i < vert_linii.length; i++) {
				vl.add(vert_line.get(i));
				vl.get(i).setId(granica + i);
			}
			boolean[] posetena = new boolean[vl.size() + hl.size()];
			preseci(hl, vl);
			// формирај табели врз база на пресечните точки
			// за секоја табела определи припаѓачки линии
			Queue<Integer> q = new LinkedList<Integer>();
			Table t = new Table();
			Iterator<Integer> it;
			for (int i = 0; i < hl.size(); i++) {
				l = hl.get(i);
				if (posetena[l.getId()]) {
					continue;
				} else {
					t = new Table();
					t.addHor(hl.get(l.getId()));
					posetena[l.getId()] = true;
					q.addAll(l.getPreseci());
					while (!q.isEmpty()) {
						int elem = q.poll();
						if (elem < granica) {
							l = hl.get(elem);
							if (!posetena[elem]) {
								posetena[elem] = true;
								t.addHor(l);
							}
						} else {
							l = vl.get(elem - granica);
							if (!posetena[elem]) {
								posetena[elem] = true;
								t.addVert(l);
							}
						}

						it = l.getPreseci().iterator();
						for (int j = 0; j < l.getPreseci().size(); j++) {
							elem = it.next();
							if (!posetena[elem]) {
								posetena[elem] = true;
								if (elem < granica) {
									t.addHor(hl.get(elem));
								} else
									t.addVert(vl.get(elem - granica));
								q.add(elem);
							}
						}
					}
					tables.add(t);
				}
			}

			hor_line.clear();
			vert_line.clear();
			hl.clear();
			vl.clear();
			if (PRIKAZISTR == stranica + 1)
				pecati(PRIKAZISTR);
			tabeli();
		}
	}

	// додади ги пресеците во листата на пресеци
	public static void preseci(List<Line> h, List<Line> v) {
		for (int i = 0; i < h.size(); i++) {
			for (int j = 0; j < v.size(); j++) {
				if (seSecat(h.get(i), v.get(j))) {
					h.get(i).dodadiPresek(v.get(j).getId());
					v.get(j).dodadiPresek(h.get(i).getId());
				}
			}
		}
	}

	// манипулациија на линиите по табели
	public void tabeli() throws COSVisitorException, IOException {
		// отстрани линии што имаат до THRESKRATKI пресечни линии
		if (THRESKRATKIX > 0) {
			for (int i = 0; i < tables.size(); i++) {
				tables.get(i).brisiKratkiX();
			}
		}
		if (THRESKRATKIY > 0) {
			for (int i = 0; i < tables.size(); i++) {
				tables.get(i).brisiKratkiY();
			}
		}
		for (int i = 0; i < tables.size(); i++) {
			if (tables.get(i).getHor_line().size() < 2
					|| tables.get(i).getVert_line().size() < 2) {
				tables.remove(i);
				i--;
			}
		}

		// операции во класата Table
		for (int i = 0; i < tables.size(); i++) {
			tables.get(i).sort();
			tables.get(i).setId();
			tables.get(i).spoi();
			tables.get(i).napraviKelii();
		}

		// pecati(tables.size());
		/*
		 * int s[][]=tables.get(0).getSpoj(); int
		 * r=tables.get(0).getRedici().size(); int
		 * k=tables.get(0).getKoloni().size(); for(int i=0;i<r;i++){ for(int j =
		 * 0; j < k; j++){ System.out.print(s[i][j]+"  "); }
		 * System.out.println(); }
		 */

		// System.out.println(tables.size());
		// for(int i = 0; i < tables.get(0).getCells().size(); i++)
		// System.out.println(tables.get(0).getCells().toString());
		// for(int i = 0; i < tables.get(0).getHor_line().size(); i++)
		// System.out.println(tables.get(0).getHor_line().get(i).getPreseci().toString());
		// pecati(0);
	}

	public String kolona(int broj) {
		String out = "";
		int a = broj;
		while (a / 26 > 0) {
			a /= 26;
			out += String.valueOf((char) (65 + a % 26));
		}
		out += String.valueOf((char) (65 + broj % 26));
		return out;
	}

	// дали 2 хоризонтална и верт. линија се сечат
	public static boolean seSecat(Line a, Line b) {
		if (a.getMode() == HORIZ && b.getMode() == VERT) {

			float xap = a.getxPoc();
			float xak = a.getKraj();
			float ya = a.getyPoc();
			float ybp = b.getyPoc();
			float ybk = b.getKraj();
			float xb = b.getxPoc();
			if (xb > xap - THRESLX && xb < xak + THRESLX) {
				if (ya < ybp + THRESLY && ya > ybk - THRESLY)
					return true;
				else
					return false;
			} else
				return false;
		} else
			return false;
	}

	// максимален индекс на секвенцата со најдолема должина
	public int maxIndex(List<Integer> l) {
		if (l.size() < 2)
			return -1;
		float max = lenOffset[l.get(0)];
		int maxI = l.get(0);
		for (int i = l.get(1); i < l.get(l.size() - 1); i++) {
			if (lenOffset[i] >= max) {
				max = lenOffset[i];
				maxI = i;
			}
		}
		return maxI;
	}

	// анализирај правоаголник
	public boolean analyzeRect(List<String> pom) {
		float xDL = Math.abs(Float.parseFloat(pom.get(0)));
		float yDL = Math.abs(Float.parseFloat(pom.get(1)));
		float w = Math.abs(Float.parseFloat(pom.get(2)));
		float h = Math.abs(Float.parseFloat(pom.get(3)));

		if (w < THRESLX && h < THRESLX) {
			return true;
		} else if (w < THRESLX) {
			lines.add(new Line(pom, VERT, false));
			return true;
		} else if (h < THRESLX) {
			lines.add(new Line(pom, HORIZ, false));
			return true;
		} else {
			if (w < THRESHX && h < THRESHY) {
				float xDD = xDL + w;
				float yDD = yDL;
				float xGL = xDL;
				float yGL = yDL + h;
				float xGD = xDL + w;
				float yGD = yDL + h;

				lines.add(new Line(xDL, yDL, xDD, yDD, HORIZ));
				lines.add(new Line(xGD, yGD, xDD, yDD, VERT));
				lines.add(new Line(xGL, yGL, xGD, yGD, HORIZ));
				lines.add(new Line(xGL, yGL, xDL, yDL, VERT));
			}
			return false;
		}
	}

	// претстави во xml
	public void docToXML(List<Table> tabs) throws Exception {
		try {
			out.append("\t<Stranica>\n");
			out.append("\t\t<Tables>\n");
			for (Table t : tabs) {
				out.println(t.toXML() + "\n");
			}
			out.append("\t\t</Tables>\n");
			out.append("\t</Stranica>\n");
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public boolean areNeighbours(iCell c1, iCell c2) {
		Cell cell1 = (Cell) c1;
		Cell cell2 = (Cell) c2;
		if (Math.abs(cell1.getX() + cell1.getWidth() - cell2.getX()) < EPSILON
				|| Math.abs(cell2.getX() + cell2.getWidth() - cell1.getX()) < EPSILON) {
			if ((cell1.getY() >= cell2.getY() && cell1.getY()
					+ cell1.getHeight() <= cell2.getY() + cell2.getHeight())
					|| (cell1.getY() <= cell2.getY() && cell1.getY()
							+ cell1.getHeight() >= cell2.getY()
							+ cell2.getHeight())) {
				return true;
			}
		}
		if (Math.abs(cell1.getY() + cell1.getHeight() - cell2.getY()) < EPSILON
				|| Math.abs(cell2.getY() + cell2.getHeight() - cell1.getY()) < EPSILON) {
			if ((cell1.getX() >= cell2.getX() && cell1.getX()
					+ cell1.getWidth() <= cell2.getX() + cell2.getWidth())
					|| (cell1.getX() <= cell2.getX() && cell1.getX()
							+ cell1.getWidth() >= cell2.getX()
							+ cell2.getWidth())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void match() {
		// System.out.println("neigh: " + list.size());
		for (Content c : contents) {
			for (Cell cell : list) {
				if (cell.isInCell(c.getX(), c.getY())) {
					cell.setContent(c);
					// System.out.println(cell);
				}
			}
		}
	}

	@Override
	public void matchPDFBOX(PDDocument document) throws Exception {
		PDFTextStripperByArea stripper = new PDFTextStripperByArea();
		stripper.setSortByPosition(true);
		PDPage page = (PDPage) document.getDocumentCatalog().getAllPages().get(
				0);

		maxy = page.findMediaBox().getHeight();
		// System.out.println(page.findMediaBox().getUpperRightY());
		Rectangle2D rect = new Rectangle2D.Float();
		int counter = 1;
		for (Cell c : list) {
			rect = new Rectangle2D.Float();
			rect.setRect(c.getX(), maxy - c.getY() - c.getHeight(), c
					.getWidth(), c.getHeight());
			stripper.addRegion(counter + "", rect);
			counter++;
		}
		stripper.extractRegions(page);

		System.out.println("-----str----");
		for (int i = 1; i <= list.size(); i++) {
			String all = stripper.getTextForRegion(i + "");
			System.out.println(i + " " + all);
		}
	}

	@Override
	public int[][] findNeighbours() {
		int n = list.size();
		int[][] neigh = new int[n][n];
		for (int i = 0; i < neigh.length; i++) {
			for (int j = 0; j < neigh.length; j++) {
				neigh[i][j] = 0;
			}
		}
		for (Cell c1 : list) {
			for (Cell c2 : list) {
				if (areNeighbours(c1, c2)) {
					// System.out.println("sosedi " + c1.getId() + " "+ " " +
					// c2.getId() + " ");
					neigh[c1.getId()][c2.getId()] = neigh[c2.getId()][c1
							.getId()] = 1;
				}
			}
		}
		neighbours = neigh;
		return neigh;
	}

	public void pecati(int str) throws IOException, COSVisitorException {
		if (str == 0)
			return;
		String com = "";
		List<Line> s;
		for (int j = 0; j < tables.size(); j++) {
			s = tables.get(j).getHor_line();
			for (int i = 0; i < s.size(); i++) {
				// if(s.get(i).getKraj()-s.get(i).getxPoc()>500){
				com = s.get(i).getxPoc() + " " + (size - s.get(i).getyPoc())
						+ " m " + s.get(i).getKraj() + " "
						+ (size - s.get(i).getyPoc()) + " l S ";
				commands.add(com);
				// System.out.println("\""+com+"\\n\",");
				// }
			}

			s = tables.get(j).getVert_line();
			for (int i = 0; i < s.size(); i++) {
				com = s.get(i).getxPoc() + " " + (size - s.get(i).getyPoc())
						+ " m " + s.get(i).getxPoc() + " "
						+ (size - s.get(i).getKraj()) + " l S ";
				commands.add(com);
				// System.out.println("\""+com+"\\n\",");
			}
		}

		PDDocument doc = new PDDocument();
		PDPage page = new PDPage();
		PDRectangle mediaBox = new PDRectangle(PAGEWIDTH, PAGEHEIGHT);
		page.setMediaBox(mediaBox);
		doc.addPage(page);
		PDPageContentStream contentStream = new PDPageContentStream(doc, page);
		String base = g_uri.substring(0, g_uri.indexOf(".") + 1);
		base = "/home/igor/Desktop/python/xml.";
		String url = base + "pdf";
		for (int i = 0; i < commands.size(); i++)
			contentStream.appendRawCommands(commands.get(i));
		contentStream.close();
		// ovde se zacuvuva pdf datotekata za pregled
		doc.save(url);
		doc.close();
	}
}