package iText;

import java.util.Comparator;

public class VertCompTab1 implements Comparator<Line> {

	@Override
	public int compare(Line a, Line b) {
		float yap=a.getxPoc();		
		float ybp=b.getxPoc();
		int kolA = a.getId();
		int kolB = b.getId();
		
		if(kolA > kolB) return -1;
		else if(kolA < kolB) return 1;
		else {
			if(yap>ybp) return -1;
			if(yap<ybp) return 1;
			return 0;
		}		
	}

}
