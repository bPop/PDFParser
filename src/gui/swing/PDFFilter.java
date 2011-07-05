package gui.swing;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class PDFFilter extends FileFilter{

	@Override
	public boolean accept(File f) {
		if(f.isDirectory()) return true;
		String extension = Utils.getExtension(f);
		if(extension == null) return true;
		if(extension.equals("pdf")) return true;
		return false;
	}

	@Override
	public String getDescription() {		
		return "Filter out all non-pdf files";
	}

}
