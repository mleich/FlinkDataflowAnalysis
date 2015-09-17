package analysis.dataset;

import java.util.ArrayList;
import java.util.List;

public class DataSetTransformation {

	String name;
	String operation;
	
	DataSet inputDataSet;
	DataSet outputDataSet;
	List<DataSetDependency> dependencies;
	
	public DataSetTransformation(String name, String operation, DataSet inputDataSet) {
		this(name, operation, inputDataSet, null);
	}
	
	public DataSetTransformation(String name, String operation, DataSet inputDataSet, DataSet outputDataSet) {
		this.name = name;
		this.operation = operation;
		
		this.inputDataSet = inputDataSet;
		inputDataSet.addNextTransformation(this);
		
		this.outputDataSet = (outputDataSet != null) ? outputDataSet : new DataSet(this);
		this.dependencies = new ArrayList<DataSetDependency>();
	}
	
	
	public void addOutputDataSetElement(DataSetElement element) {
		this.outputDataSet.add(element);
	}
	
	
	public DataSet getInputDataSet() {
		return inputDataSet;
	}
	
	
	public DataSet getOutputDataSet() {
		return outputDataSet;
	}
	
	
	public void addDataSetDependency(DataSetElement in, DataSetElement out) {
		if (!inputDataSet.contains(in)) {
			return;
		}
		
		if (!outputDataSet.contains(out)) {
			outputDataSet.add(out);
		}
		
		DataSetDependency newDependency = new DataSetDependency(in, out);
		
		if (!dependencies.contains(newDependency)) {
			dependencies.add(newDependency);
		}
	}
	
	
	public void addDataSetDependency(DataSetDependency dependency) {
		if (!this.inputDataSet.contains(dependency.getSource())) {
			return;
		}
		
		if (!this.outputDataSet.contains(dependency.getTarget())) {
			this.outputDataSet.add(dependency.getTarget());
		}
		
		if (!this.dependencies.contains(dependency)) {
			this.dependencies.add(dependency);
		}
	}
	
	
	public DataSetTransformation getNewTransformation(DataSet inputDataSet) {
		
		DataSetTransformation trans = new DataSetTransformation(this.name, this.operation, inputDataSet);
		
		for (DataSetDependency dep : this.dependencies) {
			trans.addDataSetDependency(dep);
		}
		
		return trans;
	}
}