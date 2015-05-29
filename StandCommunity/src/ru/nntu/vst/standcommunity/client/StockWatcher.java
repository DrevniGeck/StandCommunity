package ru.nntu.vst.standcommunity.client;

import java.util.ArrayList;
import java.util.Date;




import java.util.Iterator;

import ru.nntu.vst.standcommunity.shared.FieldVerifier;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.core.client.JsArray;
//--------------------------------------------------------------------

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class StockWatcher implements EntryPoint {	
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network "
			+ "connection and try again.";

	private static final int REFRESH_INTERVAL = 5000;

	/**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */
	private final GreetingServiceAsync greetingService = GWT
			.create(GreetingService.class);

	
	private static final String JSON_URL = GWT.getModuleBaseURL() + "jsonStockPrices?q=";  //адрес генератора json
	private static final String JSONOWL_URL = GWT.getModuleBaseURL() + "jsonOwl?content=";  //адрес генератора json
	
	//http://localhost:8888/stockwatcher/jsonOwl?content=ABCsdss
	
	//private static final String JSON_URL = GWT.getModuleBaseURL() + "badurl?q=";  //адрес генератора json
	
	
	//*********************************************************************************
	//
	//  Код учебного примера
	//
	//*********************************************************************************
	private VerticalPanel mainPanel = new VerticalPanel();
    private FlexTable stocksFlexTable = new FlexTable();
	private HorizontalPanel addPanel = new HorizontalPanel();
	private TextBox newSymbolTextBox = new TextBox();
	private Button addStockButton = new Button("Add");
	private Label lastUpdatedLabel = new Label();
	private ArrayList<String> stocks = new ArrayList<String>();
	 
	
	private StockPriceServiceAsync stockPriceSvc = GWT.create(StockPriceService.class);
	private Label errorMsgLabel = new Label();  //метка, на которую будет выведен текст исключения

	
	//Тестирование конвертации json - owl и owl - json
	private TextArea jsnta = new TextArea();
	private TextArea owlta = new TextArea();
	private Button sendJSONButton = new Button("Send");
	
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		// Create table for stock data.
	    stocksFlexTable.setText(0, 0, "Symbol");
	    stocksFlexTable.setText(0, 1, "Price");
	    stocksFlexTable.setText(0, 2, "Change");
	    stocksFlexTable.setText(0, 3, "Remove");
	    
	    // Add styles to elements in the stock list table.
	    stocksFlexTable.setCellPadding(6);

	    
	 // Add styles to elements in the stock list table.
	    stocksFlexTable.getRowFormatter().addStyleName(0, "watchListHeader");
	    stocksFlexTable.addStyleName("watchList");
	    stocksFlexTable.getCellFormatter().addStyleName(0, 1, "watchListNumericColumn");
	    stocksFlexTable.getCellFormatter().addStyleName(0, 2, "watchListNumericColumn");
	    stocksFlexTable.getCellFormatter().addStyleName(0, 3, "watchListRemoveColumn");
	    
	    // Assemble Add Stock panel.
	    addPanel.add(newSymbolTextBox);
	    addPanel.add(addStockButton);
	    addPanel.addStyleName("addPanel");

	    // Assemble Main panel.
	    errorMsgLabel.setStyleName("errorMessage");
	    errorMsgLabel.setVisible(false);

	    mainPanel.add(errorMsgLabel);
	    mainPanel.add(stocksFlexTable);
	    mainPanel.add(addPanel);
	    mainPanel.add(lastUpdatedLabel);
	    
	    //Настройка textarea и кнопки отправки json
	    jsnta.setSize("800", "600");  owlta.setSize("800", "600");
	    	    
	    RootPanel.get("jsontextarea").add(jsnta);
	    RootPanel.get("owltextarea").add(owlta);
	    RootPanel.get("btnsendjson").add(sendJSONButton);
	    
	    // Associate the Main panel with the HTML host page.
	    RootPanel.get("stockList").add(mainPanel);
	    
	    // Move cursor focus to the input box.
	    newSymbolTextBox.setFocus(true);
	    
	    // Setup timer to refresh list automatically.
	      Timer refreshTimer = new Timer() {
	        @Override
	        public void run() {
	          refreshWatchList();
	        }
	      };
	      refreshTimer.scheduleRepeating(REFRESH_INTERVAL);
	      
	    // Listen for mouse events on the Add button.
	    addStockButton.addClickHandler(new ClickHandler() {
	      public void onClick(ClickEvent event) {
	        addStock();
	      }	      
	    });
	    
	    // Listen for mouse events on the Add button.
	    sendJSONButton.addClickHandler(new ClickHandler() {
	      public void onClick(ClickEvent event) {
	    	  sendJsonMess();
	      }	      
	    });	    
	    
	    
	    // Listen for keyboard events in the input box.
	    newSymbolTextBox.addKeyDownHandler(new KeyDownHandler() {
	      public void onKeyDown(KeyDownEvent event) {
	        if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
	          addStock();
	        }
	      }
	    });
	    }
	    
	    private void addStock() {
	        final String symbol = newSymbolTextBox.getText().toUpperCase().trim();
	        newSymbolTextBox.setFocus(true);
	        
	        // Stock code must be between 1 and 10 chars that are numbers, letters, or dots.
	        if (!symbol.matches("^[0-9A-Z&#92;&#92;.]{1,10}$")) {
	          Window.alert("'" + symbol + "' is not a valid symbol.");
	          newSymbolTextBox.selectAll();                                                                
	          return;
	        }

	        newSymbolTextBox.setText("");                                                                     //выделяем содержимое строки ввода

	        // TODO Don't add the stock if it's already in the table.
	        if (stocks.contains(symbol))
	            return;
	        
	        
	        // TODO Add the stock to the table
	        int row = stocksFlexTable.getRowCount();
	        stocks.add(symbol);
	        stocksFlexTable.setText(row, 0, symbol);
	        stocksFlexTable.setWidget(row, 2, new Label());
	        stocksFlexTable.getCellFormatter().addStyleName(row, 1, "watchListNumericColumn");
	        stocksFlexTable.getCellFormatter().addStyleName(row, 2, "watchListNumericColumn");
	        stocksFlexTable.getCellFormatter().addStyleName(row, 3, "watchListRemoveColumn");
	        	               
	        // TODO Add a button to remove this stock from the table.
	        Button removeStockButton = new Button("x");
	        removeStockButton.addStyleDependentName("remove");
	        
	        removeStockButton.addClickHandler(new ClickHandler() {
	          public void onClick(ClickEvent event) {
	            int removedIndex = stocks.indexOf(symbol);
	            stocks.remove(removedIndex);
	            stocksFlexTable.removeRow(removedIndex + 1);
	          }
	        });
	        stocksFlexTable.setWidget(row, 3, removeStockButton);
	        
	        // TODO Get the stock price.	     
	        refreshWatchList();
	    };
	    
	    
	    // Передаем содержимое jsontextarea на сервер и возвращаем сгенерированный owl  
		   private void sendJsonMess(){
			   String url = JSONOWL_URL;
			   url+=jsnta.getText();
			   
			   url = URL.encode(url);
   	           
			   // TODO Send request to server and handle errors.
   	           RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
   	           
   	           try {
	        	  Request request = builder.sendRequest(null, new RequestCallback() {
	        	    public void onError(Request request, Throwable exception) {
	        	      displayError("Couldn't retrieve JSON");
	        	    }

	        	    public void onResponseReceived(Request request, Response response) {
	        	      if (200 == response.getStatusCode()) {
	        	        owlta.setText(response.getText());	        	      
	        	      } else {
	        	    	owlta.setText(response.getText());
	        	        displayError("Couldn't retrieve JSON (" + response.getStatusText()
	        	            + ")");
	        	      }
	        	    }
	        	  });
	        	} catch (RequestException e) {
	        	  displayError("Couldn't retrieve JSON");
	        	}
   	           
		   }
	    
	    
	    //   Обновляем список курсов валют для JSON -----------------------------------------------------------------------------
	    private void refreshWatchList() {
	    	if (stocks.size() == 0) {
	    	      return;
	    	    }

	    	        String url = JSON_URL;

	    	        // Append watch list stock symbols to query URL.
	    	    Iterator<String> iter = stocks.iterator();
	    	    while (iter.hasNext()) {
	    	      url += iter.next();
	    	      if (iter.hasNext()) {
	    	        url += "+";
	    	      }
	    	    }
	    	        
	    	        //System.out.println(url);

	    	        url = URL.encode(url);

	    	        // TODO Send request to server and handle errors.
	    	        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
	    	        
	    	        try {
	    	        	  Request request = builder.sendRequest(null, new RequestCallback() {
	    	        	    public void onError(Request request, Throwable exception) {
	    	        	      displayError("Couldn't retrieve JSON");
	    	        	    }

	    	        	        public void onResponseReceived(Request request, Response response) {
	    	        	      if (200 == response.getStatusCode()) {
	    	        	        updateTable(JsonUtils.<JsArray<StockData>>safeEval(response.getText()));
	    	        	        //owlta.setText(response.getText());
	    	        	      } else {
	    	        	        displayError("Couldn't retrieve JSON (" + response.getStatusText()
	    	        	            + ")");
	    	        	      }
	    	        	    }
	    	        	  });
	    	        	} catch (RequestException e) {
	    	        	  displayError("Couldn't retrieve JSON");
	    	        	}
	    }
	    
     	//   конец Обновляем список курсов валют для JSON -----------------------------------------------------------------------------
	    
	    //   Обновляем список курсов валют для RPC -----------------------------------------------------------------------------
	    private void refreshWatchList_rpc() {
	    	// Initialize the service proxy.
	        if (stockPriceSvc == null) {
	          stockPriceSvc = GWT.create(StockPriceService.class);
	        }

	         // Set up the callback object.
	        AsyncCallback<StockPrice[]> callback = new AsyncCallback<StockPrice[]>() {
	          public void onFailure(Throwable caught) {
	            // TODO: Do something with errors.
	        	// If the stock code is in the list of delisted codes, display an error message.
	        	    String details = caught.getMessage();
	        	    if (caught instanceof DelistedException) {
	        	      details = "Company '" + ((DelistedException)caught).getSymbol() + "' was delisted";
	        	    }

	        	    errorMsgLabel.setText("Error: " + details);
	        	    errorMsgLabel.setVisible(true);
	          }

	          public void onSuccess(StockPrice[] result) {
	            updateTable_RPC(result);
	          }
	        };

	         // Make the call to the stock price service.
	        stockPriceSvc.getPrices(stocks.toArray(new String[0]), callback);
	    }
	    
	     //  конец. Обновляем список курсов валют -----------------------------------------------------------------------------
	    
	    // Обновление таблицы для JSON
	    private void updateTable(JsArray<StockData> prices) {
	    	// TODO Auto-generated method stub
	    	for (int i = 0; i < prices.length(); i++) {
	            updateTable(prices.get(i));
	          }
	    	
	    	
	    	 // Display timestamp showing last refresh.
	        lastUpdatedLabel.setText("Last update : " +
	        DateTimeFormat.getMediumDateTimeFormat().format(new Date()));

	        // Clear any errors.
	        errorMsgLabel.setVisible(false);    	
	    	
	    }
	    
	    private void updateTable(StockData price) {
		     // Make sure the stock is still in the stock table.
		     if (!stocks.contains(price.getSymbol())) {
		       return;
		     }   
		     
		     int row = stocks.indexOf(price.getSymbol()) + 1;

		     // Format the data in the Price and Change fields.
		     String priceText = NumberFormat.getFormat("#,##0.00").format(
		         price.getPrice());
		     NumberFormat changeFormat = NumberFormat.getFormat("+#,##0.00;-#,##0.00");
		     String changeText = changeFormat.format(price.getChange());
		     String changePercentText = changeFormat.format(price.getChangePercent());

		     // Populate the Price and Change fields with new data.
		     stocksFlexTable.setText(row, 1, priceText);
		     Label changeWidget = (Label)stocksFlexTable.getWidget(row, 2);
		     changeWidget.setText(changeText + " (" + changePercentText + "%)");

		         // Change the color of text in the Change field based on its value.
		     String changeStyleName = "noChange";
		     if (price.getChangePercent() < -0.1f) {
		       changeStyleName = "negativeChange";
		     }
		     else if (price.getChangePercent() > 0.1f) {
		       changeStyleName = "positiveChange";
		     }

		         changeWidget.setStyleName(changeStyleName);
		    }
	    
	 // конец Обновление таблицы для JSON
	    
	    
	    /**
	       * If can't get JSON, display error message.
	       * @param error
	   */
	  private void displayError(String error) {
	    errorMsgLabel.setText("Error: " + error);
	    errorMsgLabel.setVisible(true);
	  }
	    
	    
	    // Обновление таблицы для RPC	    
	    private void updateTable_RPC(StockPrice[] prices) {
	    	  // TODO Auto-generated method stub
	    	for (int i = 0; i < prices.length; i++) {
	            updateTable_RPC(prices[i]);
	          }
	    	
	    	
	    	 // Display timestamp showing last refresh.
	        lastUpdatedLabel.setText("Last update : " +
	        DateTimeFormat.getMediumDateTimeFormat().format(new Date()));

	        // Clear any errors.
	        errorMsgLabel.setVisible(false);
	    	
	    	}
	    
	   
	    
	    /**
	     * Update a single row in the stock table.
	     *
	     * @param price Stock data for a single row.
	     */
	    private void updateTable_RPC(StockPrice price) {
	     // Make sure the stock is still in the stock table.
	     if (!stocks.contains(price.getSymbol())) {
	       return;
	     }

	     int row = stocks.indexOf(price.getSymbol()) + 1;

	     // Format the data in the Price and Change fields.
	     String priceText = NumberFormat.getFormat("#,##0.00").format(
	         price.getPrice());
	     NumberFormat changeFormat = NumberFormat.getFormat("+#,##0.00;-#,##0.00");
	     String changeText = changeFormat.format(price.getChange());
	     String changePercentText = changeFormat.format(price.getChangePercent());

	     // Populate the Price and Change fields with new data.
	     stocksFlexTable.setText(row, 1, priceText);
	     Label changeWidget = (Label)stocksFlexTable.getWidget(row, 2);
	     changeWidget.setText(changeText + " (" + changePercentText + "%)");

	         // Change the color of text in the Change field based on its value.
	     String changeStyleName = "noChange";
	     if (price.getChangePercent() < -0.1f) {
	       changeStyleName = "negativeChange";
	     }
	     else if (price.getChangePercent() > 0.1f) {
	       changeStyleName = "positiveChange";
	     }

	         changeWidget.setStyleName(changeStyleName);
	    }
	    // конец Обновление таблицы для RPC
}