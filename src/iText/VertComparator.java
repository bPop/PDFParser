package iText;

import java.util.Comparator;

public class VertComparator implements Comparator<Line> {

	@Override
	public int compare(Line a, Line b) {
		// TODO Auto-generated method stub
		float xa=a.getxPoc();
		float ya=a.getyPoc();
		float xb=b.getxPoc();
		float yb=b.getyPoc();
		if(xa == xb){
			if(ya > yb) return -1;
			else if(ya < yb) return 1;
			else return 0;
		}
		else if(xa > xb) return 1;
		else return -1;
	}

}
