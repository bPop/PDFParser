package iText;

public class DemoExcel {

	public static void main(String[] args) {
		Excel2PDF ex = new Excel2PDF();
		ex.setFileName("C:\\Users\\Bojana\\Desktop\\осми семестар\\iis\\Book1.xls");
		try{
			ex.readFile();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}
