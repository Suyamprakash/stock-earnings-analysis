package main.java.suyam;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ResultTreeType;

public class EarningsAnalysis {

	List<Stock> stockList = new ArrayList<Stock>();
	private String resultTime=""; 

	private void showStocks() {
		for (int i = 0; i < stockList.size(); i++) {
			debug(stockList.get(i).toString());
		}
	}

	private void getStockListFromNasdaq(String strURL) {

		Document doc;
		try {

			// need http protocol
			doc = Jsoup.connect(strURL).get();

			// get all links
			Elements links = doc.select("#ECCompaniesTable a");
			for (Element link : links) {

				// get the value from href attribute
				// debug("\nlink : " + link.attr("href"));
				// debug("text : " + link.text());

				if (link.attr("id").contains("two_column_main_content_CompanyTable_companyname")) {
					Stock stock = new Stock();
					stock.setName(link.text().substring(0, link.text().indexOf("Market Cap")));
					stock.setReportLink(link.attr("href"));
					String[] bits = link.attr("href").split("/");
					stock.setTicker(bits[bits.length - 1]);
					//if(stock.getTicker().equalsIgnoreCase("aldx"))
					stockList.add(stock);
				}

			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private List<Date> getPrevEarningsFromNasdaq(String strURL) {
		List<Date> dateList = new ArrayList<Date>();
		Document doc;
		try {
			// need http protocol
			doc = Jsoup.connect(strURL).get();
			String strTemp = doc.toString();
			if(strTemp.contains("after market close")) {
				resultTime = "After Hours";
			}else if(strTemp.contains("before market open")) {
				resultTime = "Pre Market";
			}
			
			SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");

			// get all links
			Elements links = doc.select("#showdata-div tr td");
			for (Element link : links) {

				// get the value from href attribute
				// debug("text : " + link.text());

				if (link.text().contains("/") && !link.text().contains("n/a")) {
					debug("Earnings date : " + (link.text()));
					dateList.add(formatter.parse(link.text()));
					
					//get previous two trading days
 				    Calendar previousDate = Calendar.getInstance();
					previousDate.setTime(formatter.parse(link.text()));
					int jj=0;
  				    while(jj < 2){
  				    	previousDate = Util.getPreviousDate(previousDate.getTime());
  				    	if(Util.isBusinessDay(previousDate)){
  				    		debug("Previous " + jj + " date : " + previousDate.getTime());
  				    		dateList.add(previousDate.getTime());
  				    		jj++;
  				    	}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return dateList;
	}

	private StockPrice getPrice(String strURL) {
		StockPrice price = new StockPrice();
		List<String> temp = new ArrayList<String>();
		Document doc;
		try {
			// need http protocol
			doc = Jsoup.connect(strURL).get();
			// get all links
			Elements links = doc.select("#prices td");
			for (Element link : links) {
				temp.add(link.text());
			}
			price.setOpen(Double.parseDouble(temp.get(1)));
			price.setHigh(Double.parseDouble(temp.get(2)));
			price.setLow(Double.parseDouble(temp.get(3)));
			price.setClose(Double.parseDouble(temp.get(4)));

		} catch (Exception e) {
			e.printStackTrace();
		}

		return price;
	}

	private static void debug(String strMessage) {
		System.out.println(strMessage);
	}

	private void createHeaderRow(HSSFSheet worksheet) {
		HSSFRow row1 = worksheet.createRow((short) 0);

		HSSFCell cellA1 = row1.createCell((short) 0);
		cellA1.setCellValue("Date");

		HSSFCell cellA2 = row1.createCell((short) 1);
		cellA2.setCellValue("Ticker");

		HSSFCell cellA3 = row1.createCell((short) 2);
		cellA3.setCellValue("Company Name");

		HSSFCell cellA4 = row1.createCell((short) 3);
		cellA4.setCellValue("Open");

		HSSFCell cellA5 = row1.createCell((short) 4);
		cellA5.setCellValue("High");

		HSSFCell cellA6 = row1.createCell((short) 5);
		cellA6.setCellValue("Low");

		HSSFCell cellA7 = row1.createCell((short) 6);
		cellA7.setCellValue("Close");

		HSSFCell cellA8 = row1.createCell((short) 7);
		cellA8.setCellValue("Percentage");
		
		HSSFCell cellA9 = row1.createCell((short) 8);
		cellA9.setCellValue("Time");
		

	}

	public static void main(String arg[]) {

		try {
			FileOutputStream fileOut = new FileOutputStream("stock-ea-analysis.xls");
			HSSFWorkbook workbook = new HSSFWorkbook();

			HSSFCellStyle dateStyle = workbook.createCellStyle();
			dateStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("MM/dd/yyyy"));

			HSSFCellStyle doubleStyle = workbook.createCellStyle();
			doubleStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("#0.0###"));

			// get List of Stocks from reporing earnings for the given date.
			List<String> strDateList = new ArrayList<String>();
			strDateList.add("2017-May-15");
			strDateList.add("2017-May-16");
			strDateList.add("2017-May-17");
			strDateList.add("2017-May-18");
			strDateList.add("2017-May-19");
			

			for (int k = 0; k < strDateList.size(); k++) {

				String strDate = strDateList.get(k);
				String strURL = "http://www.nasdaq.com/earnings/earnings-calendar.aspx?date=" + strDate;

				EarningsAnalysis ea = new EarningsAnalysis();
				ea.getStockListFromNasdaq(strURL);
				// ea.showStocks();

				HSSFSheet worksheet = workbook.createSheet(strDate);
				ea.createHeaderRow(worksheet);
				int count = 0;

				for (int i = 0; i < ea.stockList.size(); i++) {
					Stock stock = ea.stockList.get(i);
					strURL = "http://www.nasdaq.com/earnings/report/";
					strURL += stock.getTicker();
					debug(strURL);
					List<Date> dateList = ea.getPrevEarningsFromNasdaq(strURL);
					SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy");
					SimpleDateFormat sdfExcel = new SimpleDateFormat("MM/dd/yyyy");

					for (int j = 0; j < dateList.size(); j++) {
						debug(dateList.get(j).toString());
						String date = sdf.format(dateList.get(j));
						strURL = "https://www.google.com/finance/historical?q=" + stock.getTicker();
						strURL += "&startdate=" + URLEncoder.encode(date, "UTF-8") + "&enddate="
								+ URLEncoder.encode(date, "UTF-8");
						debug("URL::" + strURL);
						StockPrice price = ea.getPrice(strURL);
						debug(price.toString());

						HSSFRow row1 = worksheet.createRow((short) ++count);

						HSSFCell cellA0 = row1.createCell((short) 0);
						cellA0.setCellValue(sdfExcel.format(dateList.get(j)));
						cellA0.setCellStyle(dateStyle);

						HSSFCell cellA1 = row1.createCell((short) 1);
						cellA1.setCellValue(stock.getTicker());

						HSSFCell cellA2 = row1.createCell((short) 2);
						cellA2.setCellValue(stock.getName());

						HSSFCell cellA3 = row1.createCell((short) 3);
						cellA3.setCellValue(price.getOpen());
						cellA3.setCellStyle(doubleStyle);

						HSSFCell cellA4 = row1.createCell((short) 4);
						cellA4.setCellValue(price.getHigh());
						cellA4.setCellStyle(doubleStyle);

						HSSFCell cellA5 = row1.createCell((short) 5);
						cellA5.setCellValue(price.getLow());
						cellA5.setCellStyle(doubleStyle);

						HSSFCell cellA6 = row1.createCell((short) 6);
						cellA6.setCellValue(price.getClose());
						cellA6.setCellStyle(doubleStyle);

						HSSFCell cellA7 = row1.createCell((short) 7);
						cellA7.setCellValue(((price.getHigh() - price.getOpen()) / price.getOpen()) * 100);
						cellA7.setCellStyle(doubleStyle);
						
						HSSFCell cellA8 = row1.createCell((short) 8);
						cellA8.setCellValue(ea.resultTime);


					}
				}
			}
			
			workbook.write(fileOut);
			fileOut.flush();
			fileOut.close();

			System.out.println("Done!!!!");

			// debug(GetURLContent.getData("https://www.google.com/finance/historical?q=dgaz&startdate=Mar%2014%202017"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
}
