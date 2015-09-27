package analysis.transformations;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import analysis.dataset.DataSet;
import analysis.dataset.DataSetDependency;
import analysis.dataset.DataSetElement;
import analysis.dataset.DataSetTransformation;

public class AggregateParser extends TransformationParser {

	public static final String MAX_AGGREGATION = "MAX";
	public static final String MIN_AGGREGATION = "MIN";
	public static final String SUM_AGGREGATION = "SUM";
	
	private Expression field;
	private int aggregation;
	
	
	public AggregateParser(Expression field, Expression aggregation, DataSet inputDataSet) {
		super("AnonymousAggregateFunction", "AggregateFunction", inputDataSet);
		
		this.field = field;
		String aggr = ((StringLiteralExpr)aggregation).getValue();
		
		if (aggr.indexOf(MAX_AGGREGATION) >= 0) {
			this.name = "MaxAggregateFunction";
			this.aggregation = DataSetDependency.MAX_AGGREGATION;
		} else if (aggr.indexOf(MIN_AGGREGATION) >= 0) {
			this.name = "MinAggregateFunction";
			this.aggregation = DataSetDependency.MIN_AGGREGATION;
		} else if (aggr.indexOf(SUM_AGGREGATION) >= 0) {
			this.name = "SumAggregateFunction";
			this.aggregation = DataSetDependency.SUM_AGGREGATION;
		}
	}
	
	
	@Override
	public DataSetTransformation getDataSetTransformation() {
		
		DataSetTransformation transformation = new DataSetTransformation(name, operation, inputDataSet);
		
		if (field instanceof IntegerLiteralExpr) {
			int number = Integer.parseInt(((IntegerLiteralExpr)field).getValue());
			
			for (DataSetElement elem : inputDataSet) {
				if (elem.getNumber() == number) {
					transformation.addDataSetDependency(new DataSetDependency(elem, elem.clone(), aggregation));
				} else {
					transformation.addDataSetDependency(new DataSetDependency(elem, elem.clone()));
				}
			}
		} else if (field instanceof StringLiteralExpr) {
			String name = ((StringLiteralExpr)field).getValue();
			
			for (DataSetElement elem : inputDataSet) {
				if (elem.getName().equals(name)) {
					transformation.addDataSetDependency(new DataSetDependency(elem, elem.clone(), aggregation));
				} else {
					transformation.addDataSetDependency(new DataSetDependency(elem, elem.clone()));
				}
			}
		}
		
		return transformation;
	}
}
