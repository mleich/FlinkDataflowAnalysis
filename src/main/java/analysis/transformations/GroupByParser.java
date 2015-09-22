package analysis.transformations;

import java.util.List;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;

import analysis.dataset.DataSet;
import analysis.dataset.DataSetDependency;
import analysis.dataset.DataSetElement;

public class GroupByParser extends TransformationParser {

	public GroupByParser(List<Expression> groupBy, DataSet inputDataSet) {
		super("AnonymousGroupByFunction", "GroupByFunction", inputDataSet);
		
		int i = 0;
		boolean findField;
		for (DataSetElement elem : inputDataSet) {
			findField = false;
			
			for (Expression exp : groupBy) {
				if (exp instanceof IntegerLiteralExpr) {
					if (Integer.parseInt(((IntegerLiteralExpr)exp).getValue()) == elem.getNumber()) {
						this.dependencies.add(new DataSetDependency(elem, elem.clone(), DataSetDependency.GROUP_BY, i++));
						findField = true; 
						break;
					}
				} else if (exp instanceof StringLiteralExpr) {
					if (((StringLiteralExpr)exp).getValue().equals(elem.getName())) {
						this.dependencies.add(new DataSetDependency(elem, elem.clone(), DataSetDependency.GROUP_BY, i++));
						findField = true; 
						break;
					}
				}
			}
			
			if (findField) {
				this.dependencies.add(new DataSetDependency(elem, elem.clone()));
			}
		}
	}
}
