package analysis.dataset;

public class DataSetDependency {

	private DataSetElement source;
	private DataSetElement target;
	private int type;
	private int num;
	
	public static final int DEFAULT = 0;
	public static final int GROUP_BY = 1;
	public static final int SORT_GROUP_ASCENDING = 2;
	public static final int SORT_GROUP_DESCENDING = 3;
	
	public DataSetDependency(DataSetElement source, DataSetElement target) {
		this(source, target, DEFAULT);
	}
	
	
	public DataSetDependency(DataSetElement source, DataSetElement target, int type) {
		this(source, target, type, 0);
	}
	
	
	public DataSetDependency(DataSetElement source, DataSetElement target, int type, int num) {
		this.source = source;
		this.target = target;
		this.type = type;
		this.num = num;
	}
	
	
	public DataSetElement getSource() {
		return this.source;
	}
	
	
	public DataSetElement getTarget() {
		return this.target;
	}
	
	
	public int getType() {
		return type;
	}
	
	
	public int getNumber() {
		return num;
	}
	
	
	public boolean isGroupBy() {
		return type == GROUP_BY;
	}
	
	
	public boolean isSortGroup() {
		return type == SORT_GROUP_ASCENDING || type == SORT_GROUP_DESCENDING;
	}
}
