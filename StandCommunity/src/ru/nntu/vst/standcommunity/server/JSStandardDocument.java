package ru.nntu.vst.standcommunity.server;

import com.google.gwt.core.client.JavaScriptObject;

public class JSStandardDocument extends JavaScriptObject{
	// <span style="color:black;">**(1)**</span>
	// Overlay types always have protected, zero argument constructors.
	
	protected JSStandardDocument(){} // <span style="color:black;">**(2)**</span>
	// JSNI methods to get stock data.
	public final native String getURI() /*-{ return this.URI; }-*/;	// <span style="color:black;">**(3)**</span>
}
