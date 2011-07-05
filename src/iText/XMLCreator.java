package iText;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class XMLCreator {
	
	Node root = new Node();
	List<Table> tables;
	PrintWriter out;
	int pageNum;
	public XMLCreator() {
	}

	public XMLCreator(PrintWriter out, List<Table> tables, int pageNum) {
		this.out = out;
		this.tables = tables;
		this.pageNum = pageNum;
	}

	public void analyze(){
		try{
			generateXMLTree();
			treeToDocument();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void generateXMLTree() throws Exception {		
		Map<String, String> map = new HashMap<String, String>();
		map.put("pageNum", pageNum + "");
		root.setAttributes(map);
		root.setName("Page");
		List<Node> tabs = new LinkedList<Node>();
		for(Table t:tables){
			Node tmp = new Node();
			tmp.setName("Table");
			map = new HashMap<String, String>();
			map.put("numberCells", t.getCells().size() + "");
			tmp.setAttributes(map);
			List<Node> childrenTable = new LinkedList<Node>();						
			for(Cell c:t.getCells()){			
				Node c_node = new Node();
				List<Node> childrenCell = new LinkedList<Node>();
				c_node.setName("Cell");
				map = new HashMap<String, String>();
				map.put("id", c.getId()+"");
				c_node.setAttributes(map);
				float x = c.getX();
				Node x_node = new Node();
				x_node.setChildren(new LinkedList<Node>());
				x_node.setContent(x+"");
				x_node.setName("X");
				childrenCell.add(x_node);
				float y = c.getY();
				Node y_node = new Node();
				y_node.setChildren(new LinkedList<Node>());
				y_node.setContent(y+"");
				y_node.setName("Y");
				childrenCell.add(y_node);
				float height = c.getHeight();
				Node h_node = new Node();
				h_node.setChildren(new LinkedList<Node>());
				h_node.setContent(height+"");
				h_node.setName("Height");
				childrenCell.add(h_node);			
				float width = c.getWidth();
				Node w_node = new Node();
				w_node.setChildren(new LinkedList<Node>());
				w_node.setContent(width+"");
				w_node.setName("Width");
				childrenCell.add(w_node);
				
				Node content_node = new Node();
				content_node.setName("Content");
				List<Node> contentChildren = new LinkedList<Node>();
				
				String text = "";
				if(c.getContent() != null)
					text = c.getContent().getText();
				//System.out.println(text);
				Node t_node = new Node();
				t_node.setChildren(new LinkedList<Node>());
				t_node.setContent(text+"");
				t_node.setName("Text");
				contentChildren.add(t_node);
				
				Node type = new Node();
				type.setContent("Not none");
				type.setName("Type");
				contentChildren.add(type);			
				
				Node font = new Node();
				font.setName("Font");
				List<Node> fontChildren = new LinkedList<Node>();
				Node fontName = new Node();
				fontName.setName("Name");
				fontName.setContent("unknown");
				fontChildren.add(fontName);
				Node fontSize = new Node();
				fontSize.setName("Size");
				fontSize.setContent("Size");
				fontChildren.add(fontSize);
				font.setChildren(fontChildren);
				contentChildren.add(font);
				
				content_node.setChildren(contentChildren);
				childrenCell.add(content_node);
				c_node.setChildren(childrenCell);
				childrenTable.add(c_node);
			}		
			tmp.setChildren(childrenTable);
			tabs.add(tmp);			
		}
		root.setChildren(tabs);
	}
	
	public void treeToDocument(){
		recursive(root, 1);		
	}
	
	
	public void tabulation(int n){
		for (int i = 0; i < n; i++) {
			out.print("\t");
		}		
	}
	
	public void recursive(Node n, int lvl){	
		tabulation(lvl);
		out.print("<" + n.getName());
		if(n.getAttributes() != null){
			for(String s:n.getAttributes().keySet()){
				out.print(" " + s + "=\"" + n.getAttributes().get(s) +"\"");
			}
		}			
		if(n.getChildren().size() == 0 && n.getContent().isEmpty()){			
			out.println("/>");
		}
		else if(!n.getContent().isEmpty()){		
			out.println(">");
			tabulation(lvl+1);
			out.println(n.getContent().trim());
			tabulation(lvl);
			out.println("</" + n.getName()+ ">");
		}
		else if(n.getChildren().size() != 0){
			out.println(">");
			for(Node child:n.getChildren()){
				recursive(child, lvl+1);			
			}
			tabulation(lvl);
			out.println("</" + n.getName()+ ">");
		}		
	}	
}
