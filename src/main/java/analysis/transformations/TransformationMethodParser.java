package analysis.transformations;

import java.util.List;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;

import analysis.DependencyAnalyser;
import analysis.dataset.DataSetDependency;
import analysis.parser.MethodParser;
import analysis.parser.custom.type.CustomTypeParser;

public class TransformationMethodParser extends MethodParser {

	public TransformationMethodParser(TransformationParser transformation) {
		super(transformation.getAllMethods());
	}


	public List<DataSetDependency> getDataSetDependencies() {
		return outputNode.findDataSetDependencies();
	}
	
		
	public void parseMethod(MethodDeclaration method) {
		
		List<Parameter> params = method.getParameters();
		
		for (Parameter param : params) {
			if (param.getType().toStringWithoutComments().indexOf("Collector") >= 0) {
				this.outputNode = new DependencyNode(param.getId().getName(), param.getType().toStringWithoutComments(), DependencyNode.COLLECTOR);
			} else if (param.getType().toStringWithoutComments().indexOf("Iterable") >= 0) {
				methodArgs.add(new DependencyNode(param.getId().getName(), param.getType().toStringWithoutComments(), DependencyNode.INPUT));
			} else {
				CustomTypeParser ctp = DependencyAnalyser.getCustomType(param.getType().toStringWithoutComments());
				
				if(ctp != null) {
					methodArgs.add(ctp.getDependencyNode(param.getId().getName()));
				} else {
					methodArgs.add(new DependencyNode(param.getId().getName(), param.getType().toStringWithoutComments(), DependencyNode.INPUT));
				}
			}
		}
		
		this.parseStatement(method.getBody());
	}
}
