package ru.nntu.vst.standcommunity.server;

public class StandardDocument {
	private String URI;
	public StandardDocument() {
		// TODO Auto-generated constructor stub
	}
	public StandardDocument(String URI) {
		// TODO Auto-generated constructor stub
		this.URI = URI;
	}
	
	public String getURI(){
		return URI;
	}
	
	public void setURI(String URI){
		this.URI = URI;
	}
}
