package analysis.transformations;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.ObjectCreationExpr;

import analysis.dataset.DataSet;

public class MapParser extends TransformationParser {

	public MapParser(ClassOrInterfaceDeclaration cid) {
		super(cid.getName(), "MapFunction", null);
		
		this.parseBody(cid.getMembers(), "map");
	}

	public MapParser(ObjectCreationExpr expr, DataSet inputDataSet) {
		super("AnonymousMapFunction", "MapFunction", inputDataSet);
		
		this.parseBody(expr.getAnonymousClassBody(), "map");
	}
	
}
