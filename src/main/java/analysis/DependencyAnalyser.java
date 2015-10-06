package analysis;

import java.util.HashMap;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

import analysis.dataset.DataSet;
import analysis.graph.DependencyGraph;
import analysis.parser.custom.type.CustomTypeParser;
import analysis.transformations.FilterParser;
import analysis.transformations.FlatMapParser;
import analysis.transformations.GroupReduceParser;
import analysis.transformations.MapParser;
import analysis.transformations.MapPartitionParser;
import analysis.transformations.ReduceParser;
import analysis.transformations.TransformationParser;

public class DependencyAnalyser {

	private CompilationUnit cu;
	private List<DataSet> inputs;
	private String env;
	
	private static HashMap<String, CustomTypeParser> customTypes = new HashMap<String, CustomTypeParser>();
	private static HashMap<String, TransformationParser> transformations = new HashMap<String, TransformationParser>();
	private static HashMap<String, MethodDeclaration> methods = new HashMap<String, MethodDeclaration>();
	
	
	public static CustomTypeParser getCustomType(String name) {
		return customTypes.get(name);
	}
	
	
	public static TransformationParser getTransformation(String name) {
		return transformations.get(name);
	}
	
	public static MethodDeclaration getMethod(String name) {
		return methods.get(name);
	}
	
	
	public DependencyAnalyser(CompilationUnit cu) {
		this.cu = cu;
		this.inputs = null;
		this.env = null;
	}
	
	
	public DependencyGraph getDependencyGraph() {
		
		parseSource();
		
		return null;
	}
	
	
	public List<DataSet> getInputs() {
		return inputs;
	}
	
	
	public void setInputs(List<DataSet> inputs) {
		this.inputs = inputs;
	}
	
	
	public String getExecutionEnvironment() {
		return env;
	}
	
	
	public void setExecutionEnvironment(String env) {	
		this.env = env;
	}
	

	private void parseSource() {
		
		for (TypeDeclaration type : cu.getTypes()) {
			
			List<BodyDeclaration> menbers = type.getMembers();
			
			for (BodyDeclaration member : menbers) {

				if (member instanceof MethodDeclaration) {
					
					MethodDeclaration method = (MethodDeclaration)member;
					
					methods.put(method.getName(), method);
					
				} else if (member instanceof ClassOrInterfaceDeclaration) {
					
					this.parseClass((ClassOrInterfaceDeclaration)member);
				}
			}
		}
		
		MethodParser parser = new MethodParser(this);
		parser.parseMethod(methods.get("main"));
	}
	
	
	private void parseClass(ClassOrInterfaceDeclaration cid) {

		List<ClassOrInterfaceType> imps = cid.getImplements();
		
		if (imps == null) {
			List<ClassOrInterfaceType> exts = cid.getExtends();
			
			if (exts == null) {
				customTypes.put(cid.getName(), new CustomTypeParser(cid));
			} else {
				for (ClassOrInterfaceType e : exts) {
					if (e.getName().matches("^Tuple\\d{1,2}$")) {
						customTypes.put(cid.getName(), new CustomTypeParser(cid.getName(), e.getTypeArgs()));
					}
				}
			}
		} else {
			for (ClassOrInterfaceType implement : imps) {
				
				TransformationParser parser = null;
				
				switch (implement.getName()) {
					case "MapFunction": parser = new MapParser(cid); break;
					case "FlatMapFunction": parser = new FlatMapParser(cid); break;
					case "MapPartitionFunction": parser = new MapPartitionParser(cid); break;
					case "FilterFunction": parser = new FilterParser(cid); break;
					case "ReduceFunction": parser = new ReduceParser(cid); break;
					case "GroupReduceFunction": parser = new GroupReduceParser(cid); break;
				}
				
				if(parser != null) {
					transformations.put(cid.getName(), parser);
				}
			}
		}
	}
}
