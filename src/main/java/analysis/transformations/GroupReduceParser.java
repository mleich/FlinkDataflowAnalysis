package analysis.transformations;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.ObjectCreationExpr;

import analysis.dataset.DataSet;
import analysis.dataset.DataSetTransformation;

public class GroupReduceParser extends TransformationParser {

	public GroupReduceParser(ClassOrInterfaceDeclaration cid) {
		super(cid.getName(), "GroupReduceFunction", null);
		
		this.parseBody(cid.getMembers(), "reduce");
	}

	
	public GroupReduceParser(ObjectCreationExpr expr, DataSet inputDataSet) {
		super("AnonymousGroupReduceFunction", "GroupReduceFunction", inputDataSet);
		
		this.parseBody(expr.getAnonymousClassBody(), "reduce");
	}

	
	@Override
	public DataSetTransformation getDataSetTransformation() {
		
		DataSetTransformation transformation = new DataSetTransformation(name, operation, inputDataSet);
		
		
		
		return transformation;
	}
}
