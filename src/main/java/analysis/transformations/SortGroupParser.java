package analysis.transformations;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;

import analysis.dataset.DataSet;
import analysis.dataset.DataSetDependency;
import analysis.dataset.DataSetElement;
import analysis.dataset.DataSetTransformation;

public class SortGroupParser extends TransformationParser {

	private int orderNumber;
	private Expression field;
	
	public SortGroupParser(Expression field, Expression order, DataSet inputDataSet) {
		super("AnonymousSortGroupFunction", "SortGroupFunction", inputDataSet);
		
		this.orderNumber = (order.toStringWithoutComments().indexOf("ASCENDING") >= 0) ? DataSetDependency.SORT_GROUP_ASCENDING : DataSetDependency.SORT_GROUP_DESCENDING;
		this.field = field;
	}

	
	@Override
	public DataSetTransformation getDataSetTransformation() {

		DataSetTransformation transformation = new DataSetTransformation(name, operation, inputDataSet);
		
		if (field instanceof IntegerLiteralExpr) {
			int number = Integer.parseInt(((IntegerLiteralExpr)field).getValue());
			
			for (DataSetElement elem : inputDataSet) {
				if (elem.getNumber() == number) {
					transformation.addDataSetDependency(new DataSetDependency(elem, elem.clone(), orderNumber));
				} else {
					transformation.addDataSetDependency(new DataSetDependency(elem, elem.clone()));
				}
			}
		} else if (field instanceof StringLiteralExpr) {
			String name = ((StringLiteralExpr)field).getValue();
			
			for (DataSetElement elem : inputDataSet) {
				if (elem.getName().equals(name)) {
					transformation.addDataSetDependency(new DataSetDependency(elem, elem.clone(), orderNumber));
				} else {
					transformation.addDataSetDependency(new DataSetDependency(elem, elem.clone()));
				}
			}
		}
		
		return transformation;
	}
}
