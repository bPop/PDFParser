package proba;
import iText.Cell;
import iText.CellComparator;
import iText.Content;
import iText.ContentComparator;
import iText.Table;
import interfaces.iCell;


import java.awt.Font;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.util.PDFTextStripperByArea;

import com.itextpdf.text.pdf.PdfReader;

public class TableParser3{
	List<Cell> list = new LinkedList<Cell>();
	List<Content> contents = new LinkedList<Content>();
	double epsilon = 0.02;
	double maxy=841.8898;
	double dx=0.01;
	int neighbours[][];
	static String g_uri = "";
	List<Table> tables = new LinkedList<Table>();


	public void parseDocument(String uri) throws IOException {
		g_uri = uri;
		//System.out.println(g_uri);
		PdfReader reader = new PdfReader(uri);
		PDDocument document = PDDocument.load(uri);
		//int pages = reader.getNumberOfPages();
		for (int i = 1; i <= 1; i++) {
			try {
				list = new LinkedList<Cell>();
				parsePage(reader, document, i);			
			} catch (Exception e) {
				 e.printStackTrace();
			}
		}
		
		/*try{
			matchPDFBOX(document);
			// convert to to Xml
			new TableParser().docToXML(tables);
			// convert to Excel		
			String base = g_uri.substring(0, g_uri.indexOf(".") + 1);
			String url = base + "xls";
			AddInExcel excel = new AddInExcel(tables);
			excel.createExcel(url);
			//convert to HTML
			//
		}
		catch (Exception e) {e.printStackTrace();}
		*/
	}


	public void parsePage(PdfReader reader, PDDocument document, int n)
			throws Exception {
		String content = new String(reader.getPageContent(n));
		StringTokenizer st = new StringTokenizer(content, "\n");
		float x_pos = 0;
		float y_pos = 0;
		float leading = 0;
		Font font = null;
		float fontSize = 0;
		String dataType = null;
		while (st.hasMoreTokens()) {
			String line = st.nextToken().trim();
			
			// parse the content stream
			if (line.endsWith("re")) {
				StringTokenizer st2 = new StringTokenizer(line, " ");
				float x = Float.parseFloat(st2.nextToken());
				float y = Float.parseFloat(st2.nextToken());
				float width = Float.parseFloat(st2.nextToken());
				float height = Float.parseFloat(st2.nextToken());
				Cell tmp = new Cell(x, y, width, height, null);
				list.add(tmp);
			} else if (line.endsWith("TL")) {
				StringTokenizer st2 = new StringTokenizer(line, " ");
				leading = Float.parseFloat(st2.nextToken());
			} else if (line.endsWith("Tj")) {				
				try {
					String text = line.substring(line.indexOf("(") + 1, line
							.lastIndexOf(")"));
					Content con = new Content();
					con.setText(text);
					con.setFont(font);
					con.setFontSize(fontSize);
					con.setX(x_pos);
					con.setY(y_pos);
					con.setType(dataType);
					contents.add(con);
				} catch (Exception e) {
					// e.printStackTrace();
				}
			}
			else if(line.endsWith("TJ")){
				try {
					String text = line.substring(line.indexOf("[") + 1, line
							.lastIndexOf("]"));
					StringTokenizer stTokenizer = new StringTokenizer(line, "()");
					int stSize = stTokenizer.countTokens();
					while(stSize > 0){
						String string = stTokenizer.nextToken();
						Content con = new Content();
						con.setText(string);
						con.setFont(font);
						con.setFontSize(fontSize);
						con.setX(x_pos);
						con.setY(y_pos);
						con.setType(dataType);
						contents.add(con);
						stSize--;
						if(stSize == 0) break;
						int offset = Integer.parseInt(stTokenizer.nextToken());
						x_pos += offset;
						stSize--;
					}
					Content con = new Content();
					con.setText(text);
					con.setFont(font);
					con.setFontSize(fontSize);
					con.setX(x_pos);
					con.setY(y_pos);
					con.setType(dataType);
					contents.add(con);
				} catch (Exception e) {
					// e.printStackTrace();
				}
			}
			//else if (line.endsWith("\"")) {
					
			//} 
			//else if (line.endsWith("'")) {

			//} 
			else if (line.endsWith("Td")) {
				StringTokenizer st2 = new StringTokenizer(line, " ");
				float x = Float.parseFloat(st2.nextToken());
				float y = Float.parseFloat(st2.nextToken());
				x_pos += x;
				y_pos += y;
			} 
			else if (line.endsWith("TD")) {
				// implement this
				StringTokenizer st2 = new StringTokenizer(line);
				float x = Float.parseFloat(st2.nextToken());
				float y = Float.parseFloat(st2.nextToken());
				leading = -y;
				x_pos += x;
				y_pos += y;

			} 
			else if (line.endsWith("Tf")) {				
				StringTokenizer st2 = new StringTokenizer(line);				
				st2.nextToken();				
				fontSize = Float.parseFloat(st2.nextToken());
			} 
			else if (line.endsWith("Tm")) {
				StringTokenizer st2 = new StringTokenizer(line, " ");
				Float.parseFloat(st2.nextToken());
				Float.parseFloat(st2.nextToken());
				Float.parseFloat(st2.nextToken());
				Float.parseFloat(st2.nextToken());
				float x = Float.parseFloat(st2.nextToken());
				float y = Float.parseFloat(st2.nextToken());
				x_pos += x;
				y_pos += y;
			} 
			else if (line.endsWith("T*")) {								
				y_pos += leading;

			} 
			else if (line.endsWith("BT")) {
				x_pos = 0;
				y_pos = 0;
			}
		}
		// set unique ID for every cell
		int i = 0;
		for (Cell c : list) {
			c.setId(i);
			i++;
		}
		// match tables with content
		match();
		////// Sorting trial
		Content[] all_content = new Content[contents.size()];
		Cell[] all_cell = new Cell[list.size()];
		for (int j = 0; j < all_content.length; j++) {
			all_content[j] = contents.get(j);
		}
		for (int j = 0; j < all_cell.length; j++) {
			all_cell[j] = list.get(j);
		}
		
		Arrays.sort(all_content, new ContentComparator());
		Arrays.sort(all_cell, new CellComparator());
		for (int j = 0; j < all_content.length; j++) {
			System.out.println(all_content[j]);
		}
		for (int j = 0; j < all_cell.length; j++) {
			System.out.println(all_cell[j]);
		}
		////////
		
		findNeighbours();
		/*List<Table> tables_per_page = makeTables();
		// System.out.println();
		//printTables(tables_per_page);
		for (Table tab:tables_per_page) {
			tables.add(tab);
		}*/			
	}

	public void docToXML(List<Table> tabs) throws Exception {
		try {
			System.out.println(g_uri);
			String base = g_uri.substring(0, g_uri.indexOf(".") + 1);
			String url = base + "xml";
			System.out.println(url);
			//url = "D:\\tables.xml";
			PrintWriter out = new PrintWriter(new File(url));
			out.println("<Tables>");
			for (Table t : tabs) {
				out.println(t.toXML());
			}
			out.println("</Tables>");
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	public boolean areNeighbours(iCell c1, iCell c2) {
		Cell cell1 = (Cell) c1;
		Cell cell2 = (Cell) c2;
		if (Math.abs(cell1.getX() + cell1.getWidth() - cell2.getX()) < epsilon
				|| Math.abs(cell2.getX() + cell2.getWidth() - cell1.getX()) < epsilon) {
			if ((cell1.getY() >= cell2.getY() && cell1.getY()
					+ cell1.getHeight() <= cell2.getY() + cell2.getHeight())
					|| (cell1.getY() <= cell2.getY() && cell1.getY()
							+ cell1.getHeight() >= cell2.getY()
							+ cell2.getHeight())) {
				return true;
			}
		}
		if (Math.abs(cell1.getY() + cell1.getHeight() - cell2.getY()) < epsilon
				|| Math.abs(cell2.getY() + cell2.getHeight() - cell1.getY()) < epsilon) {
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

	
	public void match() {
		for (Content c : contents) {
			for (Cell cell : list) {
				if (cell.isInCell(c.getX(), c.getY())) {
					cell.setContent(c);
					// System.out.println(cell);
				}
			}
		}
	}

	
	public void matchPDFBOX(PDDocument document) throws Exception {
		PDFTextStripperByArea stripper = new PDFTextStripperByArea();
		stripper.setSortByPosition(true);
		PDPage page = (PDPage) document.getDocumentCatalog().getAllPages().get(0);
		
		maxy=page.findMediaBox().getHeight()-2;		
		//System.out.println(page.findMediaBox().getUpperRightY());
		Rectangle2D rect = new Rectangle2D.Float();
		int counter = 1;
		for (Cell c : list) {
			rect = new Rectangle2D.Float();			
			rect.setRect(c.getX(), maxy-c.getY(), c.getWidth(), c.getHeight());
			stripper.addRegion(counter+"", rect);					
			counter++;
		}
		stripper.extractRegions(page);
		
		System.out.println("-----str----");
		for (int i = 1; i <= list.size(); i++) {
			String all = stripper.getTextForRegion(i + "");
			System.out.println(i + " " + all);
		}
	}

	/*public List<Table> makeTables() {
		// System.out.println("neigh" + neightbours.length);
		int number_cells = list.size();  //broj na kelii
		Cell[] cells = new Cell[number_cells];//lista od kelii
		for (int i = 0; i < number_cells; i++) {
			cells[i] = list.get(i);		//niza od kelii
		}
		List<Table> tables1 = new LinkedList<Table>();//lista od tabeli
		int next = 1;
		boolean visited[] = new boolean[number_cells];
		// System.out.println("List size " + number_cells);
		for (int i = 0; i < number_cells; i++) {
			if (list.get(i).getInTable() == -1) {
				PriorityQueue<Cell> connected_cells = new PriorityQueue<Cell>();
				Table tab = new Table();				
				Queue<Integer> q = new LinkedList<Integer>();
				Cell tmp = cells[i];
				tmp.setInTable(next);
				cells[i] = tmp;
				q.add(i);
				for (int j = 0; j < visited.length; j++) {
					visited[j] = false;
				}
				// System.out.println("i :" + i);
				while (!q.isEmpty()) {
					int cell_id = q.poll();
					if (cell_id >= visited.length)
						break;
					if (visited[cell_id])
						continue;
					visited[cell_id] = true;
					// System.out.println("cell id " + cell_id);
					Cell current = cells[cell_id];
					connected_cells.add(current);
					// System.out.println(cell_id + " " + current.getInTable());
					for (int j = 0; j < neighbours.length; j++) {
						if (cell_id == j)
							continue;
						if (neighbours[cell_id][j] != 0) {
							// System.out.println(j);
							Cell neigh = list.get(j);
							neigh.setInTable(current.getInTable());
							if (!visited[j]) {
								q.add(j);
							}
						}
					}
				}
				tab.setCells(connected_cells);
				tables1.add(tab);
			}
			next++;
		}
		return tables1;
	}*/

	
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

/*	public void printTables(List<Table> t) {
		int i = 1;
		if (t == null)
			return;
		for (Table tab : t) {
			if (tab == null)
				break;
			System.out.println("**************");
			System.out.println(i);
			PriorityQueue<Cell> cells = tab.getCells();
			Iterator<Cell> iter = cells.iterator();
			while(iter.hasNext()){
				System.out.println(iter.next());
			}
			i++;
		}

	}
*/}
