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
	public static final int MAX_AGGREGATION = 4;
	public static final int MIN_AGGREGATION = 5;
	public static final int SUM_AGGREGATION = 6;
	
	
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
	
	
	public boolean isAggregation() {
		return type == MAX_AGGREGATION || type == MIN_AGGREGATION || type == SUM_AGGREGATION;
	}
	
	
	@Override
	public boolean equals(Object obj) {
		
		if(obj != null && obj instanceof DataSetDependency) {
			DataSetDependency dsd = (DataSetDependency)obj;
			
			if(source.equals(dsd.getSource()) && target.equals(dsd.getTarget())) {
				return true;
			}
		}
		
		return false;
	}
}
