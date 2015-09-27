package analysis.transformations;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.ObjectCreationExpr;

import analysis.dataset.DataSet;
import analysis.dataset.DataSetDependency;
import analysis.dataset.DataSetTransformation;

public class MapPartitionParser extends TransformationParser {

	public MapPartitionParser(ClassOrInterfaceDeclaration cid) {
		super(cid.getName(), "MapPartitionFunction", null);
		
		this.parseBody(cid.getMembers(), "mapPartition");
	}

	
	public MapPartitionParser(ObjectCreationExpr expr, DataSet inputDataSet) {
		super("AnonymousMapPartitionFunction", "MapPartitionFunction", inputDataSet);
		
		this.parseBody(expr.getAnonymousClassBody(), "mapPartition");
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
