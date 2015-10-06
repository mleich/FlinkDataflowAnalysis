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
	public static final int FIELD = 5;
	
	private String name;
	private String format;
	private int type;
	private String value;
	private int number;
	
	private List<DependencyNode> dependencyNodes;
	private List<DependencyNode> afterNodes;
	
	
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
		this(name, format, type, 0);
	}
	
	
	public DependencyNode(String name, String format, int type, int number) {
		this.name = name;
		this.format = findPrimitiveType(format);
		this.type = type;
		this.number = number;
		this.dependencyNodes = new ArrayList<DependencyNode>();
		this.afterNodes = new ArrayList<DependencyNode>();
	}
	
	
	public DependencyNode(String name, String format, String value) {
		this.name = name;
		this.format = findPrimitiveType(format);
		this.type = VALUE;
		this.value = value;
		this.dependencyNodes = null;
		this.afterNodes = new ArrayList<DependencyNode>();
	}
	
	
	private String findPrimitiveType(String type) {
		
		if(type != null) {
			switch (type) {
				case "byte": return "Byte";
				case "short": return "Short";
				case "int": return "Integer";
				case "long": return "Long";
				case "float": return "Float";
				case "double": return "Double";
				case "boolean": return "Boolean";
				case "char": return "Character";
			}
		}
		
		return type;
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
	
	
	public int getNumber() {
		return number;
	}
	
	
	public void setNumber(int number) {
		this.number = number;
	}
	
	
	public List<DependencyNode> getDependencyNodes() {
		return dependencyNodes;
	}
	
	
	public List<DependencyNode> getAfterNodes() {
		return afterNodes;
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
	
	
	public boolean isField() {
		return type == FIELD;
	}
	
	
	public void addDependencyNode(DependencyNode dependency) {
		dependencyNodes.add(dependency);
		dependency.addAfterNode(this, false);
	}
	
	
	public void addDependencyNode(DependencyNode dependency, boolean bilateral) {
		
		dependencyNodes.add(dependency);
		
		if (bilateral) {
			dependency.addAfterNode(this);
		}
	}
	
	
	public void addAfterNode(DependencyNode after) {
		afterNodes.add(after);
		after.addDependencyNode(this, false);
	}
	
	
	public void addAfterNode(DependencyNode after, boolean bilateral) {
		afterNodes.add(after);
		
		if (bilateral) {
			after.addDependencyNode(this);
		}
	}
	
	
	public DependencyNode findNode(String name) {
		
		if(this.name != null && this.name.equals(name)) {
			return this;
		}
		
		if(this.type == VALUE) {
			return null;
		}
		
		DependencyNode result; 
		for(DependencyNode node : dependencyNodes) {
			if((result = node.findNode(name)) != null) {
				return result;
			}
		}
		
		return null;
	}
	
	
	public DependencyNode findAfterNode(String name) {
				
		for(DependencyNode after : afterNodes) {
			if(after.getName().equals(name)) {
				return after;
			}	
		}
		
		DependencyNode result; 
		
		for(DependencyNode after : afterNodes) {
			if((result = after.findAfterNode(name)) != null) {
				return result;
			}
		}
		
		return null;
	}
	
	
	public List<DataSetDependency> findDataSetDependencies() {
		
		List<DataSetDependency> dependencies = new ArrayList<DataSetDependency>();
		int i = 0;
		
		if (type == COLLECTOR) {
			for (DependencyNode node : dependencyNodes) {
				if (node.isDataSet()) {
					for (DependencyNode data : node.getDependencyNodes()) {
						data.findDependencies(dependencies, new DataSetElement(data.getName(), data.getFormat(), i++));
					}
				} else {
					node.findDependencies(dependencies, new DataSetElement(node.getName(), node.getFormat(), i++));
				}
			}
		} else if (type == DATASET) {
			for (DependencyNode data : dependencyNodes) {
				data.findDependencies(dependencies, new DataSetElement(data.getName(), data.getFormat(), i++));
			}
		} else {
			this.findDependencies(dependencies, new DataSetElement(this.name, this.format, i));
		}
		
		return dependencies;
	}
	
	
	public void findDependencies(List<DataSetDependency> dependencies, DataSetElement output) {
			
		if (type == INPUT) {
			dependencies.add(new DataSetDependency(new DataSetElement(name, format, number), output));
		} else if (type == VALUE) {
			dependencies.add(new DataSetDependency(new DataSetElement(name, format, -1), output));
		} else {
			for (DependencyNode node : dependencyNodes) {
				node.findDependencies(dependencies, output);
			}
		}
	}
	
	
	@Override
	public DependencyNode clone() {
		return new DependencyNode(name, format, type, number);
	}
	
	
	@Override
	public boolean equals(Object obj) {
		
		if (obj != null && obj instanceof DependencyNode) {
			if (((DependencyNode)obj).getName().equals(name)) {
				return true;
			}
		}
		
		return false;
	}
}
