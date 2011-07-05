package iText;

import java.util.Comparator;

public class HorCompTab implements Comparator<Line> {
	@Override
	public int compare(Line a, Line b) {
		// TODO Auto-generated method stub
		float xap=a.getxPoc();
		float xak=a.getKraj();		
		float xbp=b.getxPoc();
		float xbk=b.getKraj();
		float ya=a.getyPoc();
		float yb=b.getyPoc();
		
		if(Math.abs(xap-xbp)<TableParser.THRESLX) {			
			if(ya>yb) return -1;
			else return 1;
		}
		if(xap<xbp) return -1;
		if(xbp<xap) return 1;
		if(xbk>xap) {
			if(Math.abs(xak-xbk)<TableParser.THRESLX) return 0;
			else return 1;
		}
		if(xak>xbp) {
			if(Math.abs(xak-xbk)<TableParser.THRESLX) return 0;
			return -1;
		}
		if(xak<xbk)	{
			if(ya>=yb) return -1;
			else return 1;
		}		
		return 0;
	}
}
