package analysis.transformations;

import java.util.List;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import analysis.dataset.DataSet;
import analysis.dataset.DataSetDependency;
import analysis.dataset.DataSetElement;

public class ProjectParser extends TransformationParser {

	public ProjectParser(List<Expression> project, DataSet inputDataSet) {
		super ("AnonymousProjectFunction", "ProjectFunction", inputDataSet);
		
		int i = 0;
		for (Expression exp : project) {
			if (exp instanceof IntegerLiteralExpr) {
				int sourceNumber = Integer.parseInt(((IntegerLiteralExpr)exp).getValue());
				
				if (inputDataSet.size() < sourceNumber) {
					DataSetElement source = inputDataSet.get(sourceNumber);
			
					dependencies.add(new DataSetDependency(source, new DataSetElement(source.getName(), source.getFormat(), i++)));
				}
			}
		}
	}
}
