package iText;

import java.util.LinkedList;
import java.util.List;

public class Redica {
	private int offset;
	private int maxKelija;
	List<Integer> kelii = new LinkedList<Integer>();
	public Redica() {
		// TODO Auto-generated constructor stub
	}
	
	public Redica(int off) {
		offset = off;
		maxKelija=off;
	}
	public void add(int kelija){
		kelii.add(kelija);
	}
	public List<Integer> getKelii() {
		return kelii;
	}
	public int getOffset() {
		return offset;
	}
	public void setKelii(List<Integer> kelii) {
		this.kelii = kelii;
	}
	public void setOffset(int offset) {
		this.offset = offset;
	}
	public void setMaxKelija(int maxKelija) {
		this.maxKelija = maxKelija;
	}
	public int getMaxKelija() {
		return maxKelija;
	}
}
