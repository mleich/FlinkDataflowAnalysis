package analysis.dataset;

public class DataSetDependency {

	private DataSetElement source;
	private DataSetElement target;
	
	public DataSetDependency(DataSetElement source, DataSetElement target) {
		this.source = source;
		this.target = target;
	}
	
	public DataSetElement getSource() {
		return this.source;
	}
	
	public DataSetElement getTarget() {
		return this.target;
	}
}
