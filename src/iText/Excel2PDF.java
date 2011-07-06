package iText;

import java.io.File;
import java.io.FileOutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import jxl.CellFeatures;
import jxl.Range;
import jxl.Sheet;
import jxl.Workbook;

import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class Excel2PDF {

	private String fileName;
	private Workbook workbook;

	//state
	private Range[] range;
	String sheetContent[][];
	int tables[][];
	int merged[][];
	List<Integer> starts_i;
	List<Integer> starts_j;
	List<Integer> ends_i;
	List<Integer> ends_j;
	com.itextpdf.text.Document doc;
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileName() {
		return fileName;
	}

	
	public void readFile() throws Exception {
		workbook = Workbook.getWorkbook(new File(fileName));
		int numberSheets = workbook.getNumberOfSheets();
		openDocument();
		for (int i = 0; i < numberSheets; i++) {
			readSheet(i);
		}		
		closeDocument();
	}

	public void readSheet(int number) throws Exception{
		int di[] = { 1, 1, 0,  0, 1, -1, -1, -1};
		int dj[] = {-1, 1, 1, -1, 0,  0, -1,  1};
		Sheet current_sheet = workbook.getSheet(number);
		int n = current_sheet.getColumns();
		int m = current_sheet.getRows();
		System.out.println(n + " " + m);
		sheetContent = new String[m][n];
		tables = new int[m][n];
		merged = new int[m][n];
		starts_i = new LinkedList<Integer>();
		ends_i = new LinkedList<Integer>();
		starts_j = new LinkedList<Integer>();
		ends_j = new LinkedList<Integer>();
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				merged[i][j] = -1;
			}
		}
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				jxl.Cell tmp = current_sheet.getCell(j, i);
				CellFeatures cf = tmp.getCellFeatures();
				String contents = tmp.getContents();
				sheetContent[i][j] = contents;
				System.out.println(contents);
			}
		}		
		//information about merged cells on the sheet
		range = current_sheet.getMergedCells();
		for (int i = 0; i < range.length; i++) {
			int topLeft_r = range[i].getTopLeft().getRow();
			int topLeft_c = range[i].getTopLeft().getColumn();
			int bottomRight_r = range[i].getBottomRight().getRow();
			int bottomRight_c = range[i].getBottomRight().getColumn();
			String cont = sheetContent[topLeft_r][topLeft_c];
			// copy the same content in all the merged cells
			for (int j = topLeft_r; j <= bottomRight_r; j++) {
				for (int k = topLeft_c; k <= bottomRight_c; k++) {
					sheetContent[j][k] = cont;
					merged[j][k] = i;
				}
			}
		}
		
		int numb = 1;
		while (true) {
			// connect all the tables
			int start_i = -1, start_j = -1;
			int end_i = -1, end_j = -1;
			loop: for (int i = 0; i < m; i++) {
				for (int j = 0; j < n; j++) {
					if (!sheetContent[i][j].isEmpty() && tables[i][j] == 0) {
						start_i = i;
						start_j = j;
						break loop;
					}
				}
			}
			if(start_i == -1 && start_j == -1) break;
			tables[start_i][start_j] = numb;
			Queue<Integer> pos_i = new LinkedList<Integer>();
			Queue<Integer> pos_j = new LinkedList<Integer>();
			pos_i.add(start_i);
			pos_j.add(start_j);
			while (!pos_i.isEmpty()) {
				int ii = pos_i.poll();
				int jj = pos_j.poll();				
				for (int k = 0; k < di.length; k++) {
					int new_i = ii + di[k];
					int new_j = jj + dj[k];
					if(new_i >= 0 && new_j >= 0 && new_i < m && new_j < n){
						if(!sheetContent[new_i][new_j].isEmpty() && tables[new_i][new_j] == 0){
							if(new_i > end_i){
								end_i = new_i;
							}
							if(new_j > end_j){
								end_j = new_j;
							}
							tables[new_i][new_j] = numb;
							pos_i.add(new_i);
							pos_j.add(new_j);
						}
					}
				}
			}
			if(end_i == -1) end_i = start_i;
			if(end_j == -1) end_j = start_j;
			starts_i.add(start_i);
			starts_j.add(start_j);
			ends_i.add(end_i);
			ends_j.add(end_j);
			numb++;
		}
		for (int i = 0; i < starts_i.size(); i++) {
			System.out.println(starts_i.get(i) + " " + starts_j.get(i) + " " + ends_i.get(i) + " " + ends_j.get(i));
		}		
		writeOne();
	}

	public String pdfURL(){
		return fileName.substring(0, fileName.lastIndexOf(".")) + ".pdf";
	}
	
	public void openDocument() throws Exception{
		doc = new com.itextpdf.text.Document();
		PdfWriter.getInstance(doc, new FileOutputStream(pdfURL()));
		doc.open();		
	}
	public void closeDocument(){
		doc.close();
	}
	public void writeOne() throws Exception{		
	//	System.out.println(pdfURL());
		int n = starts_i.size();
		boolean mergeding[] = new boolean[range.length];
		//all of the tables
		for (int i = 0; i < n; i++) {			
			int s_i = starts_i.get(i);
			int s_j = starts_j.get(i);
			int e_i = ends_i.get(i);
			int e_j = ends_j.get(i);			
			PdfPTable table = new PdfPTable(Math.abs(e_j - s_j)+1);
		//	System.out.println(Math.abs(e_j - s_j)+1);
			for (int ii = s_i; ii <= e_i; ii++) {
				for (int jj = s_j; jj <= e_j; jj++) {
					PdfPCell cell = new PdfPCell(new Phrase(sheetContent[ii][jj]));					
					//System.out.println(sheetContent[ii][jj]);
					int merging_num = merged[ii][jj];
					if(merging_num != -1){
						Range r = range[merging_num];
						int colspan = Math.abs(r.getBottomRight().getColumn() - r.getTopLeft().getColumn())+1;
						int rowspan = Math.abs(r.getBottomRight().getRow() - r.getTopLeft().getRow())+1;
						if(!mergeding[merging_num]){
							System.out.println("merge" + colspan + " " + rowspan);
							mergeding[merging_num] = true;
							cell.setColspan(colspan);
							cell.setRowspan(rowspan);
							table.addCell(cell);
							continue;
						}
					}
					table.addCell(cell);
				}				
			}
			doc.add(new Paragraph("\n"));
			doc.add(table);			
		}
		doc.close();
	}
}
