package gui.swing;

import iText.DemoParser;
import iText.FilleHellperClass;

import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class GUI extends JFrame {

	private FilleHellperClass fh = new FilleHellperClass();
	private int numPages;
	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JButton btnSubmit = null;
	private JButton btnChoseFile = null;
	private JLabel jLabel1 = null;
	private JCheckBox chkXML = null;
	private JCheckBox chkExcel = null;
	final JFileChooser fc = new JFileChooser(); // @jve:decl-index=0:visual-constraint="506,54"

	final DemoParser parser = new DemoParser(); // @jve:decl-index=0:
	private String path = ""; // @jve:decl-index=0:
	private boolean isXML = false;
	private boolean isExcel = false;
	private int startPage = -2;
	private int endPage = -2;
	private float scaleX = 0;
	private float scaleY = 0;
	private float offsetX = 0;
	private float offsetY = 0;
	private int crossX = 1;
	private int crossY = 1;

	private JLabel jStartPage = null;
	private JLabel jEndPage = null;
	private JLabel jChoseFile = null;
	private JComboBox cmbStartPage = null;
	private JComboBox cmbEndPage = null;
	private JLabel jLabel2 = null;
	private JButton btnClear = null;
	private JLabel jScaleX = null;
	private JLabel jScaleY = null;
	private JTextField txtScaleX = null;
	private JTextField txtScaleY = null;
	private JLabel jOffsetX = null;
	private JLabel jOffsetY = null;
	private JTextField txtOffX = null;
	private JTextField txtOffY = null;
	private JLabel jCrossX = null;
	private JLabel jCrossY = null;
	private JComboBox txtCrossX = null;
	private JComboBox txtCrossY = null;

	/**
	 * This is the default constructor
	 */
	public GUI() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		path = "";
		offsetX = 0;
		offsetY = 0;
		scaleX = 250;
		scaleY = 150;
		this.setSize(373, 427);
		this.setContentPane(getJContentPane());
		this.setLayout(new GridLayout(0, 2));
		this.setTitle("Content aware PDF parser v1.0");
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setFileFilter(new PDFFilter());
		this.setResizable(false);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jCrossY = new JLabel("Num. crossing points Y : ", SwingConstants.RIGHT);
			jCrossX = new JLabel("Num. crossing points X : ", SwingConstants.RIGHT);
			jOffsetY = new JLabel("Offset Y : ", SwingConstants.RIGHT);						
			jOffsetX = new JLabel("Offset X : ", SwingConstants.RIGHT);
			jScaleY = new JLabel("Scale Y : ", SwingConstants.RIGHT);
			jScaleX = new JLabel("Scale X : ", SwingConstants.RIGHT);
			jLabel2 = new JLabel();
			jLabel2.setText("");
			jLabel2.setHorizontalTextPosition(JLabel.CENTER);
			jStartPage = new JLabel("Start page : ", SwingConstants.RIGHT);
			jLabel1 = new JLabel("", SwingConstants.RIGHT);
			jEndPage = new JLabel("End page : ", SwingConstants.RIGHT);			
			jChoseFile = new JLabel("Chose a PDF file : ", SwingConstants.RIGHT);
			jContentPane = new JPanel();
			jContentPane.setLayout(new GridLayout(0, 2, 10, 6));
			// status label
			jContentPane.add(jLabel1);
			jContentPane.add(jLabel2);
			// chose file button
			jContentPane.add(jChoseFile);
			jContentPane.add(getBtnChoseFile());
			// check box
			jContentPane.add(getChkXML());
			// check box
			jContentPane.add(getChkExcel());
			// start page
			jContentPane.add(jStartPage);
			jContentPane.add(getCmbStartPage());
			// end page
			jContentPane.add(jEndPage);
			jContentPane.add(getCmbEndPage(), null);
			// scale parametars
			jContentPane.add(jScaleX, null);
			jContentPane.add(getTxtScaleX(), null);
			jContentPane.add(jScaleY, null);
			jContentPane.add(getTxtScaleY(), null);
			// offset
			jContentPane.add(jOffsetX, null);
			jContentPane.add(getTxtOffX(), null);
			jContentPane.add(jOffsetY, null);
			jContentPane.add(getTxtOffY(), null);
			// cross
			jContentPane.add(jCrossX, null);
			jContentPane.add(getTxtCrossX(), null);
			jContentPane.add(jCrossY, null);
			jContentPane.add(getTxtCrossY(), null);
			// buttons
			jContentPane.add(getBtnSubmit());
			jContentPane.add(getBtnClear(), null);
		}
		return jContentPane;
	}

	/**
	 * This method initializes btnSubmit
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getBtnSubmit() {
		if (btnSubmit == null) {
			btnSubmit = new JButton();
			btnSubmit.setText("Parse");
			btnSubmit.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if (path.isEmpty()) {
						jLabel1.setText("Please chose a file");
					} else {
						String sScaleX = txtScaleX.getText();
						String sScaleY = txtScaleX.getText();
						String sOffsetX = txtOffX.getText();
						String sOffsetY = txtOffY.getText();
						String sCrossX = (String) txtCrossX.getSelectedItem();
						String sCrossY = (String) txtCrossY.getSelectedItem();
						if (sScaleX.isEmpty() || sScaleY.isEmpty()
								|| sOffsetX.isEmpty() || sOffsetY.isEmpty()
								|| sCrossX.isEmpty() || sCrossY.isEmpty()) {
							jLabel1.setText("Please chose a file");
						} else {
							if (startPage == -2 || endPage == -2
									|| (endPage != -1 && startPage > endPage)) {
								jLabel1
										.setText("Please choose valid page numbers");
							} else {
								jLabel1.setText("Working");
								boolean ok = true;
								try {
									scaleX = Float.parseFloat(sScaleX);
									scaleY = Float.parseFloat(sScaleY);
									offsetX = Float.parseFloat(sOffsetX);
									offsetY = Float.parseFloat(sOffsetY);
									crossX = Integer.parseInt(sCrossX);
									crossY = Integer.parseInt(sCrossY);
								} catch (NumberFormatException ex) {
									jLabel1.setText("Enter numbers only");
									ok = false;
								}
								if (ok) {
									parser.setFileName(path);
									parser.setXML(isXML);
									parser.setExcel(isExcel);
									parser.setHTML(false);
									parser.setCrossX(crossX);
									parser.setCrossY(crossY);
									parser.setOffsetX(offsetX);
									parser.setOffsetY(offsetY);
									parser.setScaleX(scaleX);
									parser.setScaleY(scaleY);
									parser.setPageFrom(startPage);
									parser.setPageTo(endPage);
									String rez = "Done";
									try {
										parser.parse();
									} catch (Exception ex) {
										rez = ex.getMessage();
									}
									jLabel1.setText(rez);
									System.out.println(path);
									System.out.println(rez);
								}
							}
						}
					}
				}
			});
		}
		return btnSubmit;
	}

	private void initCombo(JComboBox cmb, int num) {
		cmb.removeAllItems();
		cmb.addItem("Choose a start page");
		cmb.addItem("First");
		cmb.addItem("Last");
		for (int i = 1; i <= num; i++) {
			cmb.addItem("" + i);
		}
	}

	/**
	 * This method initializes btnChoseFile
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getBtnChoseFile() {
		if (btnChoseFile == null) {
			btnChoseFile = new JButton();
			btnChoseFile.setText("Chose a file");
			btnChoseFile.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					int retVal = fc.showOpenDialog(GUI.this);
					if (retVal == JFileChooser.APPROVE_OPTION) {
						File file = fc.getSelectedFile();
						path = file.getAbsolutePath();
						try {
							numPages = fh.numberPages(path);
						} catch (Exception exc) {
							jLabel2.setText(exc.getMessage());
						}
						jLabel1.setText("File selected");
						initCombo(cmbStartPage, numPages);
						initCombo(cmbEndPage, numPages);
					}
				}
			});
		}
		return btnChoseFile;
	}

	/**
	 * This method initializes chkXML
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getChkXML() {
		if (chkXML == null) {
			chkXML = new JCheckBox();
			chkXML.setHorizontalAlignment(SwingConstants.CENTER);
			chkXML.setText("XML");
			chkXML.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(java.awt.event.ItemEvent e) {
					if (e.getStateChange() == ItemEvent.SELECTED) {
						isXML = true;
					} else if (e.getStateChange() == ItemEvent.DESELECTED) {
						isXML = false;
					}
				}
			});
		}
		return chkXML;
	}

	/**
	 * This method initializes chkExcel
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getChkExcel() {
		if (chkExcel == null) {
			chkExcel = new JCheckBox();
			chkExcel.setText("Excel");
			chkExcel.setHorizontalAlignment(SwingConstants.CENTER);
			chkExcel.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(java.awt.event.ItemEvent e) {
					if (e.getStateChange() == ItemEvent.SELECTED) {
						isExcel = true;
					} else if (e.getStateChange() == ItemEvent.DESELECTED) {
						isExcel = false;
					}
				}
			});
			chkExcel.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(java.awt.event.ItemEvent e) {
					System.out.println("itemStateChanged()"); // TODO
					// Auto-generated
					// Event stub
					// itemStateChanged()
				}
			});
		}
		return chkExcel;
	}

	/**
	 * This method initializes cmbStartPage
	 * 
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getCmbStartPage() {
		if (cmbStartPage == null) {
			cmbStartPage = new JComboBox();
			cmbStartPage.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(java.awt.event.ItemEvent e) {
					String s = (String) e.getItem();
					try {
						startPage = Integer.parseInt(s);
						cmbEndPage.removeAllItems();
						cmbEndPage.addItem("Choose end page");
						if (startPage == 1)
							cmbEndPage.addItem("First");
						cmbEndPage.addItem("Last");
						for (int i = startPage; i <= numPages; i++) {
							cmbEndPage.addItem(i + "");
						}
					} catch (Exception ex) {
						if (s.equals("First")) {
							startPage = 0;
						} else if (s.equals("Last")) {
							startPage = -1;
						} else {
							startPage = -2;
						}
					}
					System.out.println("Selected start " + startPage);
				}
			});
			cmbStartPage.addItem("Choose start page");
		}
		return cmbStartPage;
	}

	/**
	 * This method initializes cmbEndPage
	 * 
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getCmbEndPage() {
		if (cmbEndPage == null) {
			cmbEndPage = new JComboBox();
			cmbEndPage.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(java.awt.event.ItemEvent e) {
					String s = (String) e.getItem();
					try {
						endPage = Integer.parseInt(s);
					} catch (Exception ex) {
						if (s.equals("First")) {
							endPage = 0;
						} else if (s.equals("Last")) {
							endPage = -1;
						} else {
							endPage = -2;
						}
					}
					System.out.println("Selected end " + endPage);
				}
			});
			cmbEndPage.addItem("Choose end page");
		}
		return cmbEndPage;
	}

	/**
	 * This method initializes btnClear
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getBtnClear() {
		if (btnClear == null) {
			btnClear = new JButton();
			btnClear.setText("Clear");
			btnClear.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					jLabel1.setText("Ready");
					crossX = 1;
					txtCrossX.setSelectedIndex(1);
					crossY = 1;
					txtCrossY.setSelectedIndex(1);
					offsetX = 0;
					offsetY = 0;
					txtOffX.setText(offsetX + "");
					txtOffY.setText(offsetY + "");
					scaleX = 250;
					scaleY = 150;
					txtScaleX.setText(scaleX +"");
					txtScaleY.setText(scaleY +"");
					startPage = -2;
					endPage = -2;
					cmbStartPage.removeAllItems();
					cmbEndPage.removeAllItems();
					cmbStartPage.addItem("Choose start page");
					cmbEndPage.addItem("Choose end page");
					chkExcel.setSelected(false);
					chkXML.setSelected(false);
					isExcel = false;
					isXML = false;
					path = "";					
				}
			});
		}
		return btnClear;
	}

	public void adjustText(JTextField tf, char c) {
		if (!Character.isDigit(c)) {
			String text = tf.getText() + c;
			if (text.length() != 0)
				text = text.substring(0, text.length() - 1);
			else
				text = "";
			tf.setText(text);
			System.out.println(text);
			return;
		}
	}

	/**
	 * This method initializes txtScaleX
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getTxtScaleX() {
		if (txtScaleX == null) {
			txtScaleX = new JTextField();
			txtScaleX.setText(scaleX + "");
			txtScaleX.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					char c = e.getKeyChar();
					adjustText(txtScaleX, c);
				}
			});
		}
		return txtScaleX;
	}

	/**
	 * This method initializes txtScaleY
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getTxtScaleY() {
		if (txtScaleY == null) {
			txtScaleY = new JTextField();
			txtScaleY.setText(scaleY + "");
			txtScaleY.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					char c = e.getKeyChar();
					adjustText(txtScaleY, c);
				}
			});
		}
		return txtScaleY;
	}

	/**
	 * This method initializes txtOffX
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getTxtOffX() {
		if (txtOffX == null) {
			txtOffX = new JTextField();
			txtOffX.setText(offsetX + "");
			txtOffX.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					char c = e.getKeyChar();
					adjustText(txtOffX, c);
				}
			});
		}
		return txtOffX;
	}

	/**
	 * This method initializes txtOffY
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getTxtOffY() {
		if (txtOffY == null) {
			txtOffY = new JTextField();
			txtOffY.setText(offsetY + "");
			txtOffY.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					char c = e.getKeyChar();
					adjustText(txtOffY, c);
				}
			});
		}
		return txtOffY;
	}

	/**
	 * This method initializes txtCrossX
	 * 
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getTxtCrossX() {
		if (txtCrossX == null) {
			txtCrossX = new JComboBox();
			txtCrossX.addItem("0");
			txtCrossX.addItem("1");
			txtCrossX.addItem("2");
			txtCrossX.setSelectedIndex(1);
		}
		return txtCrossX;
	}

	/**
	 * This method initializes txtCrossY
	 * 
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getTxtCrossY() {
		if (txtCrossY == null) {
			txtCrossY = new JComboBox();
			txtCrossY.addItem("0");
			txtCrossY.addItem("1");
			txtCrossY.addItem("2");
			txtCrossY.setSelectedIndex(1);
		}
		return txtCrossY;
	}

} // @jve:decl-index=0:visual-constraint="45,56"
