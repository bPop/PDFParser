package iText;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Node {
	
	private String name;	
	private String content;
	
	List<Node> children = new LinkedList<Node>();
	Map<String, String> attributes = new HashMap<String, String>();
	
	public Node() {
		name = "";
		content = "";		
	}
	
	public Node(String name, String content, boolean isAt){
		this.name = name;		
		this.content = content;	
	}
	public List<Node> getChildren() {
		return children;
	}
	public String getContent() {
		return content;
	}
	public String getName() {
		return name;
	}	
	public void setChildren(List<Node> children) {
		this.children = children;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public void setName(String name) {
		this.name = name;
	}	
	public Map<String, String> getAttributes() {
		return attributes;
	}
	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}
}
