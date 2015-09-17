package analysis.transformations;

import java.util.HashMap;
import java.util.List;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import analysis.dataset.DataSet;
import analysis.dataset.DataSetDependency;
import analysis.dataset.DataSetTransformation;

public class TransformationParser {

	protected String name;
	protected String operation;
	protected DataSet inputDataSet;
	protected List<DataSetDependency> dependencies;
	
	public HashMap<String, MethodDeclaration> methods;
	
	
	public TransformationParser(String name, String operation, DataSet inputDataSet) {
		this.name = name;
		this.operation = operation;
		this.inputDataSet = inputDataSet;
		this.dependencies = null;
		this.methods = new HashMap<String, MethodDeclaration>();
	}
	
	
	public DataSetTransformation getDataSetTransformation() {
		
		if(dependencies == null) {
			return null;
		}
		
		DataSetTransformation transformation = new DataSetTransformation(name, operation, inputDataSet);
		
		for(DataSetDependency dep : dependencies) {
			transformation.addDataSetDependency(dep);
		}
		
		return transformation;
	}
	
	
	public DataSetTransformation getDataSetTransformation(DataSet inputDataSet) {
		
		this.inputDataSet = inputDataSet;
		
		return getDataSetTransformation();
	}
	
	
	protected void parseBody(List<BodyDeclaration> body, String method) {
		
		for(BodyDeclaration b : body) {
			
			if(b instanceof MethodDeclaration) {
				MethodDeclaration m = (MethodDeclaration)b;
				methods.put(m.getName(), m);
			}
		}
		
		TransformationMethodParser parser = new TransformationMethodParser(this);
		dependencies = parser.parseMethod(methods.get(method));
	}
}
