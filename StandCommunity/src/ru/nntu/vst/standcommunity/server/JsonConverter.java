package ru.nntu.vst.standcommunity.server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ru.nntu.vst.standcommunity.client.StockData;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.dev.resource.Resource;


import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.*;

public class JsonConverter extends HttpServlet {
	  @Override
	  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
	      throws ServletException, IOException {
		  //String jsonstr = req.getParameter("content");  //Получаем строку данных с клиента, 
		                                                 //которая была записана в параметр content
		  		    
		 // System.out.println(jsonstr+" 11******************");
		  //System.out.println(req.getParameter("content"));
		 //System.out.println(JsonUtils.class+" ---------------------------------------------");
		 //	  JsArray<JSStandardDocument> jssd = 
		 //      JsonUtils.<JsArray<JSStandardDocument>>safeEval(req.getParameter("content"));  // Перехватываем первый элемент из json
		  
		 //JSStandardDocument jssd = JsonUtils.<JSStandardDocument>safeEval(req.getParameter("content"));
		  
		 // System.out.println(jsonstr+" дааааааааааа");
		  
		                         // Считали ссылку на документ
		  
		  //JSStandardDocument jssd = JsonUtils.<JSStandardDocument>safeEval(jsonstr);
		  
		  
		  
		  
		  //System.out.println(GeneratorRDF.gen(jssd.getURI()));
		  
		  // Пример генерации json из класса и передача его на клиент -------------------
		  
		  //Gson gson = new GsonBuilder().setPrettyPrinting().create(); //Создаем объект Gson
		  //PrintWriter out = resp.getWriter();
		  //StandardDocument sd = new StandardDocument("http://my.my");
		  //String json = gson.toJson(sd);
		  //out.print(json);
		  //out.flush();
		  // (конец) Пример генерации json из класса и передача его на клиент -------------------
		  
		  
		  
		  //Пример генерации rdf с включением данных, полученных 
		  //из строки json с клиента (TextArea - HttpServletRequest - String - StandardDocument-
		  //                          GeneratorRDF - PrintWriter(resp.getWriter() - HttpServletResponse - TextArea )
		  
		  Gson gson = new GsonBuilder().setPrettyPrinting().create(); //Создаем объект Gson
		  PrintWriter out = resp.getWriter();		  		  
		  String json = req.getParameter("content");		  
		  StandardDocument sd = gson.fromJson(json, StandardDocument.class);		  
		  out.print(GeneratorRDF.gen(sd.getURI()));	  
		  out.flush();
	  }  
	}