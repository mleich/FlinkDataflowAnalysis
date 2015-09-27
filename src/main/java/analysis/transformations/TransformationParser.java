package analysis.transformations;

import java.util.HashMap;
import java.util.List;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import analysis.dataset.DataSet;
import analysis.dataset.DataSetDependency;
import analysis.dataset.DataSetTransformation;

public abstract class TransformationParser {

	protected String name;
	protected String operation;
	protected DataSet inputDataSet;
	protected DependencyNode output;
	
	public HashMap<String, MethodDeclaration> methods;
	
	
	public TransformationParser(String name, String operation, DataSet inputDataSet) {
		this.name = name;
		this.operation = operation;
		this.inputDataSet = inputDataSet;
		this.output = null;
		this.methods = new HashMap<String, MethodDeclaration>();
	}
	
	
	public List<DataSetDependency> getDataSetDependencies() {
		return output.getDataSetDependencies();
	}
	
	
	public abstract DataSetTransformation getDataSetTransformation();
	
	
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
		output = parser.parseMethod(methods.get(method));
	}
}
