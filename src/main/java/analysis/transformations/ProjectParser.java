package analysis.transformations;

import java.util.List;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import analysis.dataset.DataSet;
import analysis.dataset.DataSetDependency;
import analysis.dataset.DataSetElement;
import analysis.dataset.DataSetTransformation;

public class ProjectParser extends TransformationParser {

	List<Expression> projectFields;
	
	public ProjectParser(List<Expression> projectFields, DataSet inputDataSet) {
		super ("AnonymousProjectFunction", "ProjectFunction", inputDataSet);
		
		this.projectFields = projectFields;
	}

	
	@Override
	public DataSetTransformation getDataSetTransformation() {
		
		DataSetTransformation transformation = new DataSetTransformation(name, operation, inputDataSet);
		
		int i = 0;
		for (Expression field : projectFields) {
			if (field instanceof IntegerLiteralExpr) {
				int sourceNumber = Integer.parseInt(((IntegerLiteralExpr)field).getValue());
				
				if (inputDataSet.size() < sourceNumber) {
					DataSetElement source = inputDataSet.get(sourceNumber);
			
					transformation.addDataSetDependency(new DataSetDependency(source, new DataSetElement(source.getName(), source.getFormat(), i++)));
				}
			}
		}
		
		return transformation;
	}
}
