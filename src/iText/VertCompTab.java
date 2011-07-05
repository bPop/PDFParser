package iText;

import java.util.Comparator;

public class VertCompTab implements Comparator<Line> {
	@Override
	public int compare(Line a, Line b) {
		// TODO Auto-generated method stub
		float yap=a.getyPoc();
		float yak=a.getKraj();
		float ybp=b.getyPoc();
		float ybk=a.getKraj();
		float xa=a.getxPoc();
		float xb=b.getxPoc();
		
		
		if(Math.abs(yap-ybp)<TableParser.THRESLY) {			
			if(xa<xb) return -1;
			else return 1;
		}
		if(yap>ybp) return -1;
		if(ybp>yap) return 1;
		if(ybk>yap) return 1;
		if(yak<ybp) return -1;
		if(yak>ybk)	{
			if(xa<=xb) return -1;
			else return 1;
		}
		return 0;
	}
}
