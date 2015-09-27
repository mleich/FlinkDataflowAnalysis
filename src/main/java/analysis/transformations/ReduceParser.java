package analysis.transformations;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.ObjectCreationExpr;

import analysis.dataset.DataSet;
import analysis.dataset.DataSetTransformation;

public class ReduceParser extends TransformationParser {

	public ReduceParser(ClassOrInterfaceDeclaration cid) {
		super(cid.getName(), "ReduceFunction", null);
		
		this.parseBody(cid.getMembers(), "reduce");
	}

	
	public ReduceParser(ObjectCreationExpr expr, DataSet inputDataSet) {
		super("AnonymousReduceFunction", "ReduceFunction", inputDataSet);
		
		this.parseBody(expr.getAnonymousClassBody(), "reduce");
	}

	
	@Override
	public DataSetTransformation getDataSetTransformation() {
		
		DataSetTransformation transformation = new DataSetTransformation(name, operation, inputDataSet);
		
		
		
		return transformation;
	}
}
