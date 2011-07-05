package iText;


import java.util.List;
import java.util.PriorityQueue;

public class Line {
	private float xPoc;
	private float yPoc;
	private float Kraj;
	private Byte mode;	
	private int id;
	private PriorityQueue<Integer> preseci;
	
	public Line() {
		// TODO Auto-generated constructor stub
	}
	public Line(float xP, float yP, float xK, float yK, Byte m){
		//System.out.print(yK);
		//xP -= TableParser.PGOFFX;
		//xK -= TableParser.PGOFFX;
		//yP -= TableParser.PGOFFY; 
		//yK -= TableParser.PGOFFY;
		
		mode=m;
		//System.out.println("    "+yK);
		
		if(m==TableParser.ODREDI){
			if(yP==yK) mode=TableParser.HORIZ;
			else if(xP==xK)	mode=TableParser.VERT;			
		}		
		postaviL(xP,yP,xK,yK,mode);
		preseci = new PriorityQueue<Integer>();
	}
	public Line(List<String> rect, Byte m, boolean line){		
		mode=m;
		preseci=new PriorityQueue<Integer>();
		if(rect.size()==4){			
			float x = Float.parseFloat(rect.get(0));// - TableParser.PGOFFX;
			float y = Float.parseFloat(rect.get(1));
			float w = Float.parseFloat(rect.get(2));
			float h = Float.parseFloat(rect.get(3));			
			if(line==false){
				if(m==TableParser.ODREDI){
					if(Math.abs(h) <TableParser.THRESLY && Math.abs(w)<TableParser.THRESLX)
						mode=TableParser.TOCKA;
					else if(Math.abs(w) > Math.abs(h))
						mode=TableParser.VERT;
					else mode=TableParser.HORIZ;
				}
				postavi(x,y,w,h,mode);
			}
			else{
				if(x==w){
					mode=TableParser.VERT;
					TableParser.cntVert++;
					xPoc=x;
					if(y>h)	{
						yPoc = y;
						Kraj=h;
					}
					else {
						yPoc=h;
						Kraj=y;
					}
				}
				else if(y==h){
					mode=TableParser.HORIZ;
					TableParser.cntHor++;
					yPoc=y;
					if(x<w){
						xPoc=x;
						Kraj=w;
					}
					else{
						xPoc=w;
						Kraj=x;
					}
				}
			}
		}
		else System.out.println("Конструктор " +
				"Line(List<String> rect, boolean eofil): Неточен број аргументи");
	}
	
	public void postavi(float xDL,float yDL,float width, float height, Byte m){		
		if(m==TableParser.HORIZ){//horizontalna linija
			TableParser.cntHor++;
			if(width<0){
				xPoc = xDL+width;
				Kraj= xDL;
			}
			else {
				xPoc = xDL;
				Kraj = xDL+width;
			}
			yPoc = yDL+height/2;
		}
		else if(m==TableParser.VERT){//vertikalna linija
			TableParser.cntVert++;
			
			xPoc = xDL+width/2;
			
			if(height<0){
				yPoc = yDL;
				Kraj = yDL+height;
			}
			else{
				yPoc = yDL+height;
				Kraj = yDL;
			}
		}
		else if(m==TableParser.TOCKA){			
		}		
	}
	public void postaviL(float xP,float yP,float xK, float yK, Byte m){
		if(m==TableParser.HORIZ){
			TableParser.cntHor++;
			if(xP<xK){
				xPoc=xP;
				Kraj=xK;
			}
			else{
				xPoc=xK;
				Kraj=xP;
			}
			yPoc=yP;			
		}
		else if (m==TableParser.VERT){
			TableParser.cntVert++;
			if(yP<yK){
				yPoc=yK;
				Kraj=yP;
			}
			else{
				yPoc=yP;
				Kraj=yK;
			}
			xPoc=xP;
			//System.out.println(" "+xPoc+" "+yPoc+" "+xKraj+" "+yKraj);
		}
	}
	public void dodadiPresek(int id){
		preseci.add(id);
	}
	public PriorityQueue<Integer> getPreseci() {
		return preseci;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}	
	public Byte getMode() {
		return mode;
	}
	public float getKraj() {
		return Kraj;
	}
	public float getxPoc() {
		return xPoc;
	}
	public float getyPoc() {
		return yPoc;
	}
	public void setMode(Byte mode) {
		this.mode = mode;
	}
	public void setKraj(float Kraj) {
		this.Kraj = Kraj;
	}
	public void setxPoc(float xPoc) {
		this.xPoc = xPoc;
	}
	public void setyPoc(float yPoc) {
		this.yPoc = yPoc;
	}
}
