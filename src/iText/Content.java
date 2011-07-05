package iText;
import interfaces.iContent;

import java.awt.Font;


public class Content implements iContent{

	private float x;
	private float y;
	private String text;
	private String type;
	private Font font;
	private float fontSize;
	
	public Content() {
		x = 0;
		y = 0;
		text = "";
		type = "";
		font = null;
		fontSize = 0;
	}
	public Content(float x, float y, String text, String type, Font font, float fontSize){
		this.x=x;
		this.y=y;
		this.text=text;
		this.type=type;
		this.font=font;
		this.fontSize=fontSize;
	}
	public Font getFont() {
		return font;
	}
	public String getText() {
		if(text == null)
			return "";
		return text;
	}
	public float getFontSize() {
		return fontSize;
	}
	public String getType() {
		return type;
	}
	public float getX() {
		return x;
	}
	public float getY() {
		return y;
	}
	public void setFont(Font font) {
		this.font = font;
	}
	public void setFontSize(float fontSize) {
		this.fontSize = fontSize;
	}
	public void setText(String text) {
		this.text = text;
	}
	public void setType(String type) {
		this.type = type;
	}
	public void setX(float x) {
		this.x = x;
	}
	public void setY(float y) {
		this.y = y;
	}
	@Override
	public String toString() {
		String rez = "";
		rez +="Text = " + text + "\n";
		rez += "X = " + x + "\n";
		rez += "Y = " + y + "\n";
		return rez;
	}
}
