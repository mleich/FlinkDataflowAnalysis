package analysis.dataset;

public class DataSetElement {

	String name;
	String format;
	int number;
	
	
	public DataSetElement(String format, int number) {
		this.format = format;
	}
	
	
	public DataSetElement(String name, String format, int number) {
		this.name = name;
		this.format = format;
		this.number = number;
	}
	
	
	public String getName() {
		return name;
	}
	
	
	public String getFormat() {
		return format;
	}
	
	
	public int getNumber() {
		return number;
	}
	
	
	public DataSetElement clone() {
		return new DataSetElement(name, format, number);
	}
	
	
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof DataSetElement) {
			DataSetElement o = (DataSetElement)obj;
			
			if(o.getFormat().equals(format) && o.getNumber() == number) {
				return true;
			}
		}
		
		return false;
	}
}
