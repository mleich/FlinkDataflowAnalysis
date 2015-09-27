package analysis.transformations;

import java.util.ArrayList;
import java.util.List;

import analysis.dataset.DataSetDependency;
import analysis.dataset.DataSetElement;

public class DependencyNode {

	public static final int DATA = 0;
	public static final int COLLECTOR = 1;
	public static final int DATASET = 2;
	public static final int INPUT = 3;
	public static final int VALUE = 4;
	
	private String name;
	private String format;
	private int type;
	private String value;
	private List<DependencyNode> dependencies;
	
	
	public DependencyNode(String name) {
		this(name, null, DATA);
	}
	
	
	public DependencyNode(String name, String format) {
		this(name, format, DATA);
	}
	
	
	public DependencyNode(String name, int type) {
		this(name, null, type);
	}
	
	
	public DependencyNode(String name, String format, int type) {
		this.name = name;
		this.format = format;
		this.type = type;
		this.dependencies = new ArrayList<DependencyNode>();
	}
	
	
	public DependencyNode(String name, String format, String value) {
		this.name = name;
		this.format = format;
		this.type = VALUE;
		this.value = value;
		this.dependencies = null;
	}
	
	
	public String getName() {
		return name;
	}
	
	
	public String getFormat() {
		return format;
	}
	
	
	public void setFormat(String format) {
		this.format = format;
	}
	
	
	public String getValue() {
		return value;
	}
	
	
	public List<DependencyNode> getDependencies() {
		return dependencies;
	}
	
	
	public boolean isCollector() {
		return type == COLLECTOR;
	}
	
	
	public boolean isDataSet() {
		return type == DATASET;
	}
	
	
	public boolean isInput() {
		return type == INPUT;
	}
	
	
	public boolean isValue() {
		return type == VALUE;
	}
	
	
	public void addDependency(DependencyNode dependency) {
		dependencies.add(dependency);
	}
	
	
	public DependencyNode findNode(String name) {
		
		if(this.name != null && this.name.equals(name)) {
			return this;
		}
		
		if(this.type == VALUE) {
			return null;
		}
		
		DependencyNode result; 
		for(DependencyNode node : this.dependencies) {
			if((result = node.findNode(name)) != null) {
				return result;
			}
		}
		
		return null;
	}
	
	
	public List<DataSetDependency> getDataSetDependencies() {
		
		List<DataSetDependency> dependencies = new ArrayList<DataSetDependency>();
		int i = 0, j = 0;
		
		if (this.type == COLLECTOR) {
			for (DependencyNode node : this.dependencies) {
				if (node.isDataSet()) {
					for (DependencyNode data : node.getDependencies()) {
						j = data.findDependencies(dependencies, new DataSetElement(data.getName(), data.getFormat(), i++), j);
					}
				} else {
					j = node.findDependencies(dependencies, new DataSetElement(node.getName(), node.getFormat(), i++), j);
				}
			}
		} else {
			this.findDependencies(dependencies, new DataSetElement(this.name, this.format, i), j);
		}
		
		return dependencies;
	}
	
	
	public int findDependencies(List<DataSetDependency> dependencies, DataSetElement output, int count) {
			
		if(type == INPUT) {
			dependencies.add(new DataSetDependency(new DataSetElement(name, format, count++), output));
		} else if(type == VALUE) {
			dependencies.add(new DataSetDependency(new DataSetElement(name, format, -1), output));
		} else {
			for(DependencyNode node : this.dependencies) {
				count = node.findDependencies(dependencies, output, count);
			}
		}
		
		return count;
	}
	
	
	@Override
	public boolean equals(Object obj) {
		
		if(obj != null && obj instanceof DependencyNode) {
			if(((DependencyNode)obj).getName().equals(name)) {
				return true;
			}
		}
		
		return false;
	}
}
