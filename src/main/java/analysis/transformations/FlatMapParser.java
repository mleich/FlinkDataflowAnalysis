package analysis.transformations;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import analysis.dataset.DataSet;
import analysis.dataset.DataSetDependency;
import analysis.dataset.DataSetTransformation;

public class FlatMapParser extends TransformationParser {

	public FlatMapParser(ClassOrInterfaceDeclaration cid) {
		super(cid.getName(), "FlatMapFunction", null);
		
		this.parseBody(cid.getMembers(), "flatMap");
	}

	
	public FlatMapParser(ObjectCreationExpr expr, DataSet inputDataSet) {
		super("AnonymousFlatMapFunction", "FlatMapFunction", inputDataSet);
		
		this.parseBody(expr.getAnonymousClassBody(), "flatMap");
	}

	
	@Override
	public DataSetTransformation getDataSetTransformation() {

		DataSetTransformation transformation = new DataSetTransformation(name, operation, inputDataSet);
		
		for(DataSetDependency dep : getDataSetDependencies()) {
			transformation.addDataSetDependency(dep);
		}
		
		return transformation;
	}
}
