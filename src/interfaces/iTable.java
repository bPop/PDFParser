package interfaces;

import java.util.List;

public interface iTable {

	public List<iCell> getRowN(int num);
	public List<iCell> getColN(int num);
	public String getCellContent(int row, int col);
	public String toXML();
}
