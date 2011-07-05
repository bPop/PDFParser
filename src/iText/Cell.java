package iText;
import java.util.List;

import interfaces.iCell;

public class Cell implements iCell, Comparable<Cell> {

	private int id;
	private float x;
	private float y;
	private float width;
	private float height;
	private int colspan;
	private int rowspan;
	private int kolona;
	private int redica;

	private String text;
	private Content content;

	public Cell() {
		id = -1;		
		x = 0;
		y = 0;
		width = 0;
		height = 0;		
	}

	public Cell(float x, float y, float width, float height, Content content) {
		this.x = x;
		this.y = y;
		this.height = height;
		this.width = width;
		this.content = content;		
	}
	
	public Cell(List<String> Kelija, Content content) {
		this.x = Float.parseFloat(Kelija.get(0));
		this.y = Float.parseFloat(Kelija.get(1));
		this.width = Float.parseFloat(Kelija.get(2));
		this.height = Float.parseFloat(Kelija.get(3));
		this.content = content;		
	}
	
	public int getColspan() {
		return colspan;
	}
	
	public void setColspan(int colspan) {
		this.colspan = colspan;
	}
	
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public float getHeight() {
		return height;
	}

	public float getWidth() {
		return width;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public Content getContent() {
		return content;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public void setX(float x) {
		this.x = x;
	}

	public void setY(float y) {
		this.y = y;
	}

	public void setContent(Content content) {
		this.content = content;
	}

	public int getKolona() {
		return kolona;
	}
	
	public void setKolona(int pocK) {
		kolona = pocK;
	}
	
	public int getRedica() {
		return redica;
	}
	
	public void setRedica(int redica) {
		this.redica = redica;
	}
	
	public int getRowspan() {
		return rowspan;
	}
	
	public void setRowspan(int rowspan) {
		this.rowspan = rowspan;
	}
	
	public void csPlus(){
		colspan++;
	}
	
	public void rsPlus(){
		rowspan++;
	}
	
	public void wPlus(float w){
		width +=w;
	}
	
	public void hPlus(float h){
		height += h;
	}
	@Override
	public boolean isInCell(float x, float y) {
		if (this.x <= x && this.x + this.width >= x && this.y <= y
				&& this.y + this.height >= y)
			return true;
		return false;
	}

	@Override
	public String toString() {
		String rez = "";
		try {
			rez+="\n> "+text+"\n+colspan "+colspan+"\n";
			rez += "id = " + id + "\nX = " + x + "\nY = " + y+ "\nWidth = " + width + "\nHeight = "
					+ height+"\n";
			
			rez += "===================\n";
			//rez +=y+ " ";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rez;
	}

	@Override
	public int compareTo(Cell o) {
		float x_1 = this.getX();
		float y_1 = this.getY();
		float x_2 = o.getX();
		float y_2 = o.getY();
		if(y_1 == y_2){
			if(x_1 < x_2) return -1;
			else if(x_1 > x_2) return 1;
			else return 0;
		}
		else if(y_1 < y_2) return 1;
		else return -1;
	}

	@Override
	public String toXML() {
		String rez = "";
		if(content == null) content = new Content();
		if(text != null) text = text.trim();
		rez += "\t\t\t<Cell id = \""+ id+"\">\n";		
		rez += "\t\t\t\t<X>" + x + "</X>\n";
		rez += "\t\t\t\t<Y>" + y + "</Y>\n";
		rez += "\t\t\t\t<Height>" + height + "</Height>\n";
		rez += "\t\t\t\t<Width>" + width + "</Width>\n";		
		rez += "\t\t\t\t<Text>" + text + "</Text>\n";
		rez += "\t\t\t\t<Type>" + content.getType() + "</Type>\n";
		rez += "\t\t\t\t<Font>\n";
		rez += "\t\t\t\t\t<Size>" + content.getFontSize() + "</Size>\n";
		rez += "\t\t\t\t\t<Type>" + content.getFont() + "</Type>\n";
		rez += "\t\t\t\t</Font>\n";		
		rez += "\t\t\t</Cell>\n";
		return rez;
	}
}
