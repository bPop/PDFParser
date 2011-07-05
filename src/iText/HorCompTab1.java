package iText;

import java.util.Comparator;

public class HorCompTab1 implements Comparator<Line> {
	@Override
	public int compare(Line a, Line b) {
		// TODO Auto-generated method stub
		float xap=a.getxPoc();		
		float xbp=b.getxPoc();
		int redA = a.getId();
		int redB = b.getId();
		
		if(redA < redB) return -1;
		else if(redA > redB) return 1;
		else {
			if(xap<xbp) return -1;
			if(xap>xbp) return 1;
			return 0;
		}		
	}
}
