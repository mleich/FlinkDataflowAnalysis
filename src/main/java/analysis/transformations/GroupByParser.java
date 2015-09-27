package analysis.transformations;

import java.util.List;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;

import analysis.dataset.DataSet;
import analysis.dataset.DataSetDependency;
import analysis.dataset.DataSetElement;
import analysis.dataset.DataSetTransformation;

public class GroupByParser extends TransformationParser {

	private List<Expression> groupBy;
	
	public GroupByParser(List<Expression> groupBy, DataSet inputDataSet) {
		super("AnonymousGroupByFunction", "GroupByFunction", inputDataSet);
		
		this.groupBy = groupBy;
	}

	
	@Override
	public DataSetTransformation getDataSetTransformation() {
		
		DataSetTransformation transformation = new DataSetTransformation(name, operation, inputDataSet);
		
		int i = 0;
		boolean findField;
		for (DataSetElement elem : inputDataSet) {
			findField = false;
			
			for (Expression exp : groupBy) {
				if (exp instanceof IntegerLiteralExpr) {
					if (Integer.parseInt(((IntegerLiteralExpr)exp).getValue()) == elem.getNumber()) {
						findField = true; 
						break;
					}
				} else if (exp instanceof StringLiteralExpr) {
					if (((StringLiteralExpr)exp).getValue().equals(elem.getName())) {
						findField = true; 
						break;
					}
				}
			}
			
			if (findField) {
				transformation.addDataSetDependency(new DataSetDependency(elem, elem.clone(), DataSetDependency.GROUP_BY, i++));
			} else {
				transformation.addDataSetDependency(elem, elem.clone());
			}
		}
		
		return transformation;
	}
}
