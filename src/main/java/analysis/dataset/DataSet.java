package analysis.dataset;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;

import analysis.DependencyAnalyser;
import analysis.parser.custom.type.CustomTypeParser;

public class DataSet extends ArrayList<DataSetElement> {
	
	private static final long serialVersionUID = 1L;
	
	private String varName;
	
	private DataSetTransformation prevTransformation;
	private List<DataSetTransformation> nextTransformations;
	

	public DataSet(DataSetTransformation prevTransformation) {
		this.varName = null;
		this.prevTransformation = prevTransformation;
		this.nextTransformations = new ArrayList<DataSetTransformation>();
	}
	
	
	public DataSet(Type type) {
		this(type, null);
	}
	
	
	public DataSet(Type type, String name) {
		
		this.varName = name;
		this.prevTransformation = null;
		this.nextTransformations = new ArrayList<DataSetTransformation>();
		
		this.addElements((ClassOrInterfaceType) type.getChildrenNodes().get(0));
	}
	
	
	public DataSet(String name) {
		this(null, name);
	}
	
	
	public void addElements(ClassOrInterfaceType type) {
		
		if (type.getName().equals("DataSet")) {
			for (Type arg : type.getTypeArgs()) {
				ClassOrInterfaceType argType = (ClassOrInterfaceType)arg.getChildrenNodes().get(0);
				
				CustomTypeParser ct = DependencyAnalyser.getCustomType(argType.getName());
				
				if (ct != null) {
					for (DataSetElement elem : ct.getElements()) {
						this.add(elem);
					}
				} else {
					this.add(new DataSetElement(argType.getName(), size()));
				}
			}
		} else if (type.getTypeArgs() != null) {
			for (Type arg : type.getTypeArgs()) {
				addElements((ClassOrInterfaceType)arg.getChildrenNodes().get(0));
			}
		} else {
			add(new DataSetElement("Element" + size(), type.getName(), size()));
		}
	}
	
	
	public void setVarName(String name) {
		this.varName = name;
	}
	
	
	public String getVarName() {
		return varName;
	}
	
	
	public DataSetTransformation getPrevTransformation() {
		return this.prevTransformation;
	}
	
	
	public void addNextTransformation(DataSetTransformation transformation) {
		this.nextTransformations.add(transformation);
	}
	
	
	public List<DataSetTransformation> getNextTransformations() {
		return this.nextTransformations;
	}
	
	
	public DataSet findDataSet(String varName) {
		
		if(this.varName != null && this.varName.equals(varName)) {
			return this;
		}
		
		DataSet result;
		
		for(DataSetTransformation trans : this.nextTransformations) {
			if((result = trans.getOutputDataSet().findDataSet(varName)) != null) {
				return result;
			}
		}
		
		return null;
	}
	
	
	public boolean equals(Object obj) {
		
		if(obj != null && obj instanceof DataSet) {
			if(((DataSet)obj).getVarName().equals(varName)) {
				return true;
			}
		}
		
		return false;
	}
}
