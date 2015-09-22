package analysis.transformations;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;

import analysis.dataset.DataSet;
import analysis.dataset.DataSetDependency;
import analysis.dataset.DataSetElement;

public class SortGroupParser extends TransformationParser {

	public SortGroupParser(Expression field, Expression order, DataSet inputDataSet) {
		super("AnonymousSortGroupFunction", "SortGroupFunction", inputDataSet);
		
		int orderNumber = (order.toStringWithoutComments().indexOf("ASCENDING") >= 0) ? DataSetDependency.SORT_GROUP_ASCENDING : DataSetDependency.SORT_GROUP_DESCENDING;
		
		if (field instanceof IntegerLiteralExpr) {
			int number = Integer.parseInt(((IntegerLiteralExpr)field).getValue());
			
			for (DataSetElement elem : inputDataSet) {
				if (elem.getNumber() == number) {
					this.dependencies.add(new DataSetDependency(elem, elem.clone(), orderNumber));
				} else {
					this.dependencies.add(new DataSetDependency(elem, elem.clone()));
				}
			}
		} else if (field instanceof StringLiteralExpr) {
			String name = ((StringLiteralExpr)field).getValue();
			
			for (DataSetElement elem : inputDataSet) {
				if (elem.getName().equals(name)) {
					this.dependencies.add(new DataSetDependency(elem, elem.clone(), orderNumber));
				} else {
					this.dependencies.add(new DataSetDependency(elem, elem.clone()));
				}
			}
		}
	}
}
