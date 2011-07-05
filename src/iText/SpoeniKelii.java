package iText;

public class SpoeniKelii {
	private int redOd; //gore levo pocetna pozicija
	private int kolonaOd;//-||-
	private int redDo;
	private int kolonaDo;
	public SpoeniKelii() {
	// TODO Auto-generated constructor stub
	}
	SpoeniKelii(int ko, int ro, int kd, int rd){
		redOd = ro;
		kolonaOd = ko;
		redDo = rd;
		kolonaDo = kd;
	}
	public int getKolonaDo() {
		return kolonaDo;
	}
	
	public int getRedDo() {
		return redDo;
	}
	
	public int getKolonaOd() {
		return kolonaOd;
	}
	
	public int getRedOd() {
		return redOd;
	}
	
	public void setKolonaDo(int kolonaDo) {
		this.kolonaDo = kolonaDo;
	}
	
	public void setRedDo(int redDo) {
		this.redDo = redDo;
	}
	
	public void setKolonaOd(int kolonaOd) {
		this.kolonaOd = kolonaOd;
	}
	
	public void setRedOd(int redOd) {
		this.redOd = redOd;
	}
		
	@Override
	public String toString() {
		String out="";
		out += "ro: "+redOd + " ko "+kolonaOd+ " rd " +redDo+ " kd "+kolonaDo;
		return out;
	}
}
