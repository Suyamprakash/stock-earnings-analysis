package main.java.suyam;

public class StockPrice {
	
	private double open;
	private double high;
	private double low;
	private double close;
	
	public double getOpen() {
		return open;
	}
	public void setOpen(double open) {
		this.open = open;
	}
	public double getHigh() {
		return high;
	}
	public void setHigh(double high) {
		this.high = high;
	}
	public double getLow() {
		return low;
	}
	public void setLow(double low) {
		this.low = low;
	}
	public double getClose() {
		return close;
	}
	public void setClose(double close) {
		this.close = close;
	}
	
	public String toString(){
		return "Open::"+open+"||High::"+high+"||Low::"+low+"||Close::"+close;
	}
	
	

}
