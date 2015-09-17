package analysis.transformations;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.ObjectCreationExpr;

import analysis.dataset.DataSet;
import analysis.dataset.DataSetElement;
import analysis.dataset.DataSetTransformation;

public class FilterParser extends TransformationParser {

	public FilterParser(ClassOrInterfaceDeclaration cid) {
		super(cid.getName(), "FilterFunction", null);
		
	}

	public FilterParser(ObjectCreationExpr expr, DataSet inputDataSet) {
		super("AnonymousFilterFunction", "FilterFunction", inputDataSet);
		
	}
	
	public DataSetTransformation getDataSetTransformation() {
		
		DataSetTransformation transformation = new DataSetTransformation(name, operation, inputDataSet);
		
		for(DataSetElement element : inputDataSet) {
			transformation.addDataSetDependency(element, element.clone());
		}
		
		return transformation;
	}
}
