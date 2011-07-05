package iText;
import interfaces.iCell;
import interfaces.iTable;

import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.apache.pdfbox.util.PDFTextStripperByArea;

public class Table implements iTable{
	
	private List<Cell> cells = new LinkedList<Cell>();
	private List<Line> vert_line;
	private List<Line> hor_line;
	private List<Float> redici;	//по y позиции
	private List<Float> koloni;	//по x позиции
	private List<Redica> keliiRedica = new LinkedList<Redica>();
	//келии во одредена редица
	List<SpoeniKelii> spoeni = new LinkedList<SpoeniKelii>();
	private boolean [][] preseciX; //пресеци со хоризонталните линии
	private boolean [][] preseciY; //пресеци со вертикалните линии
	private float xPoc,xKraj,yPoc,yKraj; //гранични координати на табелата
	private int [][] spoj;			//матрица со спојни точки
	//boolean prvaHor,posHor,prvaVer,posVer;
	
	public Table() {
		vert_line = new LinkedList<Line>();
		hor_line = new LinkedList<Line>();		
	}
	public Table(List<Line> hl, List<Line> vl) {
		hor_line = hl;
		vert_line = vl;		
	}
	public void brisiKratkiX(){
		for(int i=0;i<hor_line.size();i++){
			if(hor_line.get(i).getPreseci().size()<=TableParser.THRESKRATKIX){
				hor_line.remove(i);
				i--;
			}
		}
	}
	public void brisiKratkiY(){
		for(int i=0;i<vert_line.size();i++){
			if(vert_line.get(i).getPreseci().size()<=TableParser.THRESKRATKIY){
				vert_line.remove(i);
				i--;
			}
		}
	}
	//сортирај по позиција во табелата
	public void sort(){
		redici=new LinkedList<Float>();
		koloni=new LinkedList<Float>();
		//постави граници
		xPoc = hor_line.get(0).getxPoc();		
		xKraj = hor_line.get(0).getKraj();
		yPoc = vert_line.get(0).getyPoc();		
		yKraj = vert_line.get(0).getKraj();
		Line [] hor_linii = new Line[hor_line.size()];
		Line [] vert_linii = new Line[vert_line.size()];
		
		
		for(int i=0;i<hor_linii.length;i++) {
			if(hor_line.get(i).getxPoc()<xPoc) xPoc=hor_line.get(i).getxPoc();
			if(hor_line.get(i).getKraj()>xKraj) xKraj=hor_line.get(i).getKraj();
			hor_linii[i] = hor_line.get(i);
		}
		for(int i=0;i<vert_linii.length;i++) {
			if(vert_line.get(i).getyPoc()>yPoc) yPoc=vert_line.get(i).getyPoc();
			if(vert_line.get(i).getKraj()<yKraj) yKraj=vert_line.get(i).getKraj();
			vert_linii[i] = vert_line.get(i);
		}
		Arrays.sort(hor_linii, new HorComparator());
		Arrays.sort(vert_linii, new VertComparator());
			
		hor_line = new LinkedList<Line>();
		vert_line = new LinkedList<Line>();
		int i;
		//постави горна граница
		if(Math.abs(hor_linii[0].getyPoc()-yPoc)>TableParser.THRESLY){
			Line gore = new Line(xPoc,yPoc,xKraj,yPoc, TableParser.HORIZ);
			hor_line.add(gore);
		}
		else {
			hor_linii[0].setxPoc(xPoc);
			hor_linii[0].setKraj(xKraj);
		}
		
		for(i=0;i<hor_linii.length;i++) {
			hor_line.add(hor_linii[i]);
		}
		//постави лева граница
		if(Math.abs(vert_linii[0].getxPoc()-xPoc)>TableParser.THRESLX){
			Line levo = new Line(xPoc,yPoc,xPoc,yKraj, TableParser.VERT);
			vert_line.add(levo);
		}
		
		else{
			vert_linii[0].setyPoc(yPoc);
			vert_linii[0].setKraj(yKraj);
		}
		
		for(i=0;i<vert_linii.length;i++) {
			vert_line.add(vert_linii[i]);
		}
		//постави десна и долна граница
		if(Math.abs(hor_linii[hor_linii.length-1].getyPoc()-yKraj)>2*TableParser.THRESLY){
			Line dolu = new Line(xPoc,yKraj,xKraj,yKraj, TableParser.HORIZ);
			hor_line.add(dolu);			
		}
		
		if(Math.abs(vert_linii[vert_linii.length-1].getxPoc()-xKraj)>2*TableParser.THRESLX){
			Line desno = new Line(xKraj,yPoc,xKraj,yKraj, TableParser.VERT);
			vert_line.add(desno);
		}
		//добиј ги позициите на редиците
		float ref=yPoc;
		redici.add(ref);
		for(i=0;i<hor_linii.length;i++){
			if(hor_linii[i].getyPoc()<ref-TableParser.THRESLY){
				ref = hor_linii[i].getyPoc();
				redici.add(ref);
			}
		}
		if(yKraj<redici.get(redici.size()-1)-TableParser.THRESLY)
			redici.add(yKraj);
		//... и на колоните
		ref=xPoc;
		koloni.add(ref);
		for(i=0;i<vert_linii.length;i++){
			if(vert_linii[i].getxPoc()>ref+TableParser.THRESLX){
				ref = vert_linii[i].getxPoc();
				koloni.add(ref);
			}
		}
		if(xKraj>koloni.get(koloni.size()-1)+TableParser.THRESLX)
			koloni.add(xKraj);
	}
	//на колку колони раздалеченост
	public int rastPoKolona(float a, float b){
		if(a==b) return 0;
		int kA = getKolona(a);
		int kB = getKolona(b);
		if(a<b){
			return kB - kA;
		}
		return -1;
	}
	//поклопи граници на ќелија и текст
	public void napraviKelii() throws IOException{
		PDFTextStripperByArea stripper;		
		stripper = new PDFTextStripperByArea();		
        stripper.setSortByPosition(true);
        
        float maxy = 2*TableParser.size - TableParser.PAGEHEIGHT;
        Cell c; int kol,red;
        Rectangle2D rect;
        for(int i = 0;i<cells.size();i++){
        	c = cells.get(i);
        	if(c.getRowspan()==0) continue;
        	rect = new Rectangle2D.Float();
        	kol = c.getKolona();
        	red = c.getRedica();
        	if(kol + c.getColspan()==koloni.size()) c.setColspan(1);
        	else if(kol + c.getColspan()>koloni.size()) c.setColspan(0);
        	if(red + c.getRowspan()==redici.size()) c.setRowspan(1);
        	else if(red + c.getRowspan()>redici.size()) c.setRowspan(0);
        	float width = koloni.get(kol+c.getColspan())-koloni.get(kol);
        	float height = redici.get(red)-redici.get(red+c.getRowspan());
        	c.setX(koloni.get(kol));
        	c.setY(redici.get(red));        	
        	c.setWidth(width);
        	c.setHeight(height);
        	rect.setRect(c.getX() - TableParser.PGOFFX,
					maxy-c.getY()-TableParser.PGOFFY, width, height);
			stripper.addRegion(i+"", rect);
			
			if(c.getColspan()>1 || c.getRowspan()>1){				
				SpoeniKelii sk = new SpoeniKelii(kol, red,
						kol+c.getColspan()-1, red+c.getRowspan()-1);
				spoeni.add(sk);
			}
        }
		stripper.extractRegions(TableParser.page);
		String text="";		
		for(int i = 0; i < cells.size(); i++){
			if(cells.get(i).getRowspan()==0) continue;
			text = stripper.getTextForRegion(i+"");
			//this is new!!! Bojana
			Content c_tmp = new Content();
			c_tmp.setText(text);
			cells.get(i).setContent(c_tmp);
		}
	}
	//добиј текст за ќелија на ред i и колона j
	public String getTextKelija(int i, int j){		
		if(i>keliiRedica.size()) return "Грешка! Не постои таква редица!!!";
		if(j>keliiRedica.get(i).getKelii().size()) return "Грешка! Не постои таква колона!!!";		
		return cells.get(keliiRedica.get(i).getKelii().get(j)).getText();
	}
	//анализирај споеви на линиите
	public void spoi(){
		//матрица со споеви по x и y
		preseciX = new boolean[redici.size()][koloni.size()];
		preseciY = new boolean[koloni.size()][redici.size()];
		
		
		for(int i=0;i<hor_line.size();i++){
			for(int j=getKolona(hor_line.get(i).getxPoc());j<getKolona(hor_line.get(i).getKraj());j++){
				preseciX[getRedica(hor_line.get(i).getyPoc())][j] =true;
			}
		}
		
		for(int i=0;i<vert_line.size();i++){			
			for(int j=getRedica(vert_line.get(i).getyPoc());j<getRedica(vert_line.get(i).getKraj());j++){
				preseciY[getKolona(vert_line.get(i).getxPoc())][j] =true;				
			}
		}
		//матрица која обележува споени ќелии
		spoj = new int[redici.size()][koloni.size()];
		int ix=0;
		for(int i=0;i<redici.size()-1;i++){
			for(int j=0;j<koloni.size()-1;j++){
				boolean flag=false;
				while(i>0 && j<koloni.size()-1 && !preseciX[i][j]){
					spoj[i][j] = spoj[i-1][j];					
					if(j==koloni.size()-2){
						flag=true;
					}
					j++;
					if(flag) break;
				}
				if(flag) continue;
				if(preseciY[j][i]==true){
					ix++;
				}
				spoj[i][j] = ix;
			}
		}
		Redica r;
		//единствен идентификатрор на ќелии по редица
		int [][] spojRed = new int[redici.size()][koloni.size()];
		ix=0;
		for(int i=0;i<redici.size()-1;i++){
			spojRed[i][0] = ix;
			for(int j=1;j<koloni.size()-1;j++){
				if(spoj[i][j]==spoj[i][j-1])
					spojRed[i][j] = ix;
				else {
					ix++;
					spojRed[i][j] = ix;
				}
			}
			ix++;
		}
		r = new Redica(0);
		Cell c = new Cell();
		ix=0;
		for(int i=0;i<redici.size()-1;i++){
			c = new Cell();
			c.setId(ix);
			c.csPlus();
			c.rsPlus();
			c.setKolona(0);
			c.setRedica(i);
			
			int j = 1;
			for(;j<koloni.size();j++){
			
				if(spoj[i][j] == spoj[i][j-1]){				
					c.csPlus();
					continue;
				}
				r.add(ix);
				ix++;
				cells.add(c);
				c = new Cell();
				c.setId(ix);
				c.csPlus();
				c.rsPlus();
				c.setKolona(j);
				c.setRedica(i);
			}
			keliiRedica.add(r);
			r = new Redica(0);
		}
		
		for(int i = 0; i < redici.size() - 1; i++) {
			for(int j = 0; j < koloni.size() - 1; j++){
				spoj[i][j]--;
			}
		}
		//провери за rowspan, обележи со -1 ќелии со rowspan>1 и colspan>1
		//постави rowspan 0 за ќелии што се дел од друга ќелија
		for(int i = 1; i < redici.size() - 1; i++) {
			for(int j = 0; j < koloni.size() - 1; j++){
				if(spoj[i][j] == spoj[i-1][j]){
					if(cells.get(spoj[i][j]).getColspan()==cells.get(spoj[i-1][j]).getColspan()){						
						cells.get(spojRed[i][j]).setRowspan(0);
						int k;
						int cs = cells.get(spojRed[i][j]).getColspan() -1;
						j++;
						for(k = 0; k < cs; k++,j++){
							if(j==koloni.size() - 1) break;
							spojRed[i][j] = -1;
							spojRed[i-1][j] = -1;
						}
						j--;
					}
				}
			}
		}
		//постави точен rowspan
		for(int j = 0; j < koloni.size() - 1; j++) {
			for(int i = redici.size() - 2; i > -1 ; i--){
				if(spojRed[i][j]==-1) continue;
				if(cells.get(spojRed[i][j]).getRowspan()==0){
					ix=0;
					while(cells.get(spojRed[i][j]).getRowspan()==0){
						ix++;
						if(i > 0) i--;
						else break;
						if(spojRed[i][j]==-1) break;
					}
					if(spojRed[i][j]!=-1)
					for(int k = 0; k < ix; k++)
						cells.get(spojRed[i][j]).rsPlus();
				}
			}
		}
		for(int i=0;i<keliiRedica.size();i++){
			List<Integer> l = keliiRedica.get(i).getKelii();
			for(int j = 0; j < l.size(); j++){
				if(cells.get(l.get(j)).getRowspan()==0){
					l.remove(j);
					j--;
				}
			}
		}
		
		/*for(int i=0;i<redici.size();i++){
			for(int j=0;j<koloni.size();j++){
				System.out.print((spojRed[i][j])+" ");
			}
			System.out.println();
		}	
		
		System.out.println("-------------------------------------\n");
		
		/*for(int i=0;i<cells.size();i++){
			System.out.print(i+"  ");
			System.out.println(cells.get(i).getColspan());
		}
		for(int i=0;i<keliiRedica.size();i++){
			for(int j=0;j<keliiRedica.get(i).getKelii().size();j++){
				System.out.print(keliiRedica.get(i).getKelii().get(j)+"--");
				//System.out.print(cells.get(keliiRedica.get(i).getKelii().get(j)).getRedica()+"--");
				System.out.print(cells.get(keliiRedica.get(i).getKelii().get(j)).getRowspan()+"  ");
			}
			System.out.println();
		}*/
	}
	//добиј ќелија според id
	public int getCellByID(int id){
		for(int i=0;i<cells.size();i++){
			if(cells.get(i).getId()==id) return i;
		}
		return -1;
	}
	public void setId(){
		for(int i=0;i<hor_line.size();i++){
			hor_line.get(i).setId(getRedica(hor_line.get(i).getyPoc()));
			hor_line.get(i).getPreseci().clear();
		}
		for(int i=0;i<vert_line.size();i++){
			vert_line.get(i).setId(getKolona(vert_line.get(i).getxPoc()));
			vert_line.get(i).getPreseci().clear();
		}
		preseci(hor_line, vert_line);
	}
	//добиј колона за дадена позиција по x
	public int getKolona(float k){
		if(k<koloni.get(0)) return 0;
		for(int i=0;i<koloni.size();i++){
			if(Math.abs(koloni.get(i)-k)<2*TableParser.THRESLX) return i;
		}
		return koloni.size()-1;
	}
	public int getRedica(float r){
		if(r > redici.get(0)) return 0;
		for(int i=0;i<redici.size();i++){
			if(Math.abs(redici.get(i)-r)<2*TableParser.THRESLY) return i;
		}
		return redici.size()-1;
	}
	//пресеци на хор. и верт. линии во рамки на табелата
	public void preseci(List<Line> h, List<Line> v){
		for(int i=0;i<h.size();i++){
			for(int j=0; j<v.size();j++){
				if(seSecat(h.get(i), v.get(j))){
					h.get(i).dodadiPresek(v.get(j).getId());
					v.get(j).dodadiPresek(h.get(i).getId());
				}					
			}
		}		
	}
	//ако се сечат горе лево
	public boolean seSecat(Line a,Line b){		
		if(a.getMode()==TableParser.HORIZ && b.getMode()==TableParser.VERT){
			float xap = a.getxPoc();
			float xak = a.getKraj();
			float ya = a.getyPoc();
			float ybp = b.getyPoc();
			float ybk = b.getKraj();
			float xb = b.getxPoc();				
			
			if(xap - TableParser.THRESLX < xb && ybp + TableParser.THRESLY > ya){
				if(xak - TableParser.THRESLX > xb && ybk + TableParser.THRESLY < ya)				
				return true;				
			}
			else return false;
		}
		return false;
	}
	public int[][] getSpoj() {
		return spoj;
	}
	public List<Float> getRedici() {
		return redici;
	}
	public List<Float> getKoloni() {
		return koloni;
	}
	public List<Line> getHor_line() {
		return hor_line;
	}
	public List<Line> getVert_line() {
		return vert_line;
	}
	public List<Line> getlines() {
		List<Line> line = new LinkedList<Line>();
		line.addAll(hor_line);
		line.addAll(vert_line);
		return line;
	}

	public List<Redica> getKeliiRedica() {
		return keliiRedica;
	}
	
	public void setKeliiRedica(List<Redica> keliiRedica) {
		this.keliiRedica = keliiRedica;
	}
	
	public void setHor_line(List<Line> hor_line) {
		this.hor_line = hor_line;
	}
	public void setVert_line(List<Line> vert_line) {
		this.vert_line = vert_line;
	}
	public void addHor(Line h){
		hor_line.add(h);
	}
	public void addVert(Line v){
		vert_line.add(v);
	}
	public void setCells(List<Cell> cells) {
		this.cells = cells;
	}
	
	public List<Cell> getCells() {
		return cells;
	}
	@Override	
	public List<iCell> getColN(int n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<iCell> getRowN(int n) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public List<SpoeniKelii> getSpoeni() {
		return spoeni;
	}
	
	public void setSpoeni(List<SpoeniKelii> spoeni) {
		this.spoeni = spoeni;
	}

	@Override
	public String getCellContent(int row, int col) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String toXML() {
		String rez = "\t<Table>\n";
		rez += "\t\t<Cells number = \""+cells.size()+"\">\n";
		Iterator<Cell> iter = cells.iterator();
		while(iter.hasNext()){
			Cell c = (Cell)iter.next();
			rez += c.toXML();			
		}
		rez += "\t\t</Cells>\n";
		rez += "\t</Table>\n";
		return rez;
	}
	

}
