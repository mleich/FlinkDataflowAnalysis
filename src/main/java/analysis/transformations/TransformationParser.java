package analysis.transformations;

import java.util.List;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

import analysis.dataset.DataSet;
import analysis.dataset.DataSetDependency;
import analysis.dataset.DataSetTransformation;
import analysis.parser.Parser;

public abstract class TransformationParser extends Parser {

	protected String name;
	protected String operation;
	protected DataSet inputDataSet;
	protected TransformationMethodParser methodParser;
	
	
	public TransformationParser(String name, String operation, DataSet inputDataSet) {
		this.name = name;
		this.operation = operation;
		this.inputDataSet = inputDataSet;
		this.methodParser = null;
	}
	
	
	public List<DataSetDependency> getDataSetDependencies() {
		return methodParser.getDataSetDependencies();
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
		
		methodParser = new TransformationMethodParser(this);
		methodParser.parseMethod(methods.get(method));
	}
}
