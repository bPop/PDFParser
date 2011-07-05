package iText;
import java.util.Comparator;


public class ContentComparator implements Comparator<Content>{

	@Override
	public int compare(Content c1, Content c2) {
		float x1 = c1.getX();
		float y1 = c1.getY();
		float x2 = c2.getX();
		float y2 = c2.getY();
		if(y1 == y2){
			if(x1 == x2) return 0;
			else if(x1 > x2) return 1;
			else return -1;
		}
		else if(y1 > y2)
		{
			return -1;
		}
		else return 1;		
	}

}
