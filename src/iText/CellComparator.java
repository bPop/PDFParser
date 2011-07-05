package iText;
import java.util.Comparator;


public class CellComparator implements Comparator<Cell>{

	@Override
	public int compare(Cell o1, Cell o2) {
		float x_1 = o1.getX();
		float y_1 = o1.getY();
		float x_2 = o2.getX();
		float y_2 = o2.getY();
		if(y_1 == y_2){
			if(x_1 < x_2) return -1;
			else if(x_1 > x_2) return 1;
			else return 0;
		}
		else if(y_1 < y_2) return 1;
		else return -1;		
	}

}
