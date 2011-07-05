package iText;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class AddInExcel {

	List<Integer> list;
	//List<Table> tables;
	File file;
	WritableWorkbook workbook;
	int brTab;
	int tekovenRed;
	
	public void setList(List<Integer> list) {
		this.list = list;
	}

	public AddInExcel(String inputFile) throws IOException {
		file = new File(inputFile);
		WorkbookSettings wbSettings = new WorkbookSettings();
		wbSettings.setLocale(new Locale("en", "EN"));
		workbook = Workbook.createWorkbook(file, wbSettings);
		brTab=0;
		tekovenRed = 0;
	}
	
	private void addCell(WritableSheet sh, int col, int row, Cell c)
			throws Exception {	
		Label label = new Label(col, row, c.getContent().getText());
		sh.addCell(label);
	}

	private void addNumber(WritableSheet sh, int col, int row, double val)
			throws Exception {
		Number num = new Number(col, row, val);
		sh.addCell(num);
	}

	public void writeArrayHorizontaly(WritableSheet sheat) throws Exception {
		int size = list.size();
		for (int i = 0; i < size; i++) {
			addNumber(sheat, i, 0, list.get(i));
		}
	}

	public void writeArrayVerticaly(WritableSheet sheat) throws Exception {
		int size = list.size();
		for (int i = 0; i < size; i++) {
			addNumber(sheat, 0, i, list.get(i));
		}
	}

	public void writeTable(WritableSheet sh, Table table) throws Exception {
		List<Cell> l = table.getCells();
		List<Redica> redici = table.getKeliiRedica();
		List<SpoeniKelii> spoeni = table.getSpoeni();
		for(int i = spoeni.size()-1; i > -1 ; i--){
			sh.mergeCells(spoeni.get(i).getKolonaOd(), 
					spoeni.get(i).getRedOd() + tekovenRed,
					spoeni.get(i).getKolonaDo(),
					spoeni.get(i).getRedDo() + tekovenRed);
		}
		/*int odnos = Math.round(TableParser.PAGEWIDTH/150/table.getKoloni().size());
		for(int i=0; i < table.getKoloni().size();i++){
			sh.setColumnView(i, odnos);
		}*/
		for(int i=tekovenRed; i < tekovenRed + table.getRedici().size()-1;i++){
			sh.setRowView(i, 600);
		}
		
		Cell c;
		for(int i = 0; i < redici.size(); i++){
			for(int j = 0; j < redici.get(i).getKelii().size();j++){
				c = l.get(redici.get(i).getKelii().get(j));
				if(c.getRowspan()==0) continue;
				addCell(sh, c.getKolona(), tekovenRed + i, c);
			}
		}
	}

	public void add(List<Table> tables) throws Exception {		
		//iteriraj niz site najdeni tabeli
		// poseben sheet za sekoja tabela
		tekovenRed = 0;
		workbook.createSheet("Табела" + (TableParser.stranica + 1), TableParser.stranica);
		WritableSheet excelSheet = workbook.getSheet(TableParser.sheet);
		
		int numTables = tables.size();
		for (int i = brTab; i < brTab+numTables; i++) {
			Table table = tables.get(i-brTab);			
			writeTable(excelSheet, table);
			tekovenRed += table.getRedici().size()+1;
		}
		// createLabel(excelSheet);
		
		brTab += numTables;
	}
	public void closeXcl() throws WriteException, IOException{
		workbook.write();
		workbook.close();
	}
}
