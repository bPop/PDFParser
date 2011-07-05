package iText;
public class DemoParser {

	private String fileName = "D:\\Text extraction\\target_PDF.pdf";
	public boolean isXML;
	public boolean isExcel;
	public boolean isHTML;
	public int pageFrom;
	public int pageTo;
	public float scaleX;
	public float scaleY;
	public float offsetX;
	public float offsetY;
	public int crossX;
	public int crossY;
	
	public String parse() throws Exception{
		String rez = "Done";
		try{
			//System.out.println(fileName);
			TableParser parser = new TableParser();
			TableParser.sheet = 0;
			parser.parseDocument(fileName, isXML, isExcel, isHTML, pageFrom, pageTo, scaleX, scaleY, offsetX, offsetY, crossX, crossY);
		}
		catch(Exception e){
			e.printStackTrace();			
			rez = e.toString();
			throw e;
		}
		return rez;
	}
	
	/*public static void main(String[] args) {
		DemoParser dp = new DemoParser();
		dp.parse();
	}*/
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public boolean isExcel() {
		return isExcel;
	}
	public boolean isHTML() {
		return isHTML;
	}
	public boolean isXML() {
		return isXML;
	}
	public void setXML(boolean isXML) {
		this.isXML = isXML;
	}
	public void setHTML(boolean isHTML) {
		this.isHTML = isHTML;
	}
	public void setExcel(boolean isExcel) {
		this.isExcel = isExcel;
	}
	public int getCrossX() {
		return crossX;
	}
	public int getCrossY() {
		return crossY;
	}
	public float getOffsetX() {
		return offsetX;
	}
	public float getOffsetY() {
		return offsetY;
	}
	public int getPageFrom() {
		return pageFrom;
	}
	public int getPageTo() {
		return pageTo;
	}
	public float getScaleX() {
		return scaleX;
	}
	public float getScaleY() {
		return scaleY;
	}
	public void setCrossX(int crossX) {
		this.crossX = crossX;
	}
	public void setCrossY(int crossY) {
		this.crossY = crossY;
	}
	public void setOffsetX(float offsetX) {
		this.offsetX = offsetX;
	}
	public void setOffsetY(float offsetY) {
		this.offsetY = offsetY;
	}
	public void setPageFrom(int pageFrom) {
		this.pageFrom = pageFrom;
	}
	public void setPageTo(int pageTo) {
		this.pageTo = pageTo;
	}
	public void setScaleX(float scaleX) {
		this.scaleX = scaleX;
	}
	public void setScaleY(float scaleY) {
		this.scaleY = scaleY;
	}
}
