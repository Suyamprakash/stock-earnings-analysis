package main.java.suyam;

public class Stock {
	
	private String name;
	private String ticker;
	private String reportLink;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTicker() {
		return ticker;
	}
	public void setTicker(String ticker) {
		this.ticker = ticker;
	}
	public String getReportLink() {
		return reportLink;
	}
	public void setReportLink(String reportLink) {
		this.reportLink = reportLink;
	}
	
	public String toString(){
		return "Name:"+name+"|||Ticker:"+ticker+"|||Report:"+reportLink;
	}
	
}
