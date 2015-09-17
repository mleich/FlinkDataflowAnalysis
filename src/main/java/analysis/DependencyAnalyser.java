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
import analysis.transformations.FlatMapParser;
import analysis.transformations.MapParser;
import analysis.transformations.TransformationParser;

public class DependencyAnalyser {

	private CompilationUnit cu;
	private List<DataSet> inputs;
	private String env;
	
	public static HashMap<String, CustomType> customTypes;
	public static HashMap<String, TransformationParser> transformations;
	public static HashMap<String, MethodDeclaration> methods;
	
	
	public DependencyAnalyser(CompilationUnit cu) {

		this.cu = cu;
		
		this.inputs = null;
		this.env = null;
		
		this.customTypes = new HashMap<String, CustomType>();
		this.transformations = new HashMap<String, TransformationParser>();
		this.methods = new HashMap<String, MethodDeclaration>();
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
	
	/*
	private String findExecutionEnvironment(MethodDeclaration method) {
		
		for (Statement stmt : method.getBody().getStmts()) {
			if (stmt.toStringWithoutComments().indexOf("ExecutionEnvironment") >= 0) {
				return iterateNode(stmt);
			}
		}
		
		return null;
	}
	
	
	private String iterateNode(Node node) {
		if(node instanceof VariableDeclarationExpr) {
			VariableDeclarationExpr varDeclExpr = (VariableDeclarationExpr)node;
			for(VariableDeclarator varDecl : varDeclExpr.getVars()) {
				if(varDecl.getInit().toStringWithoutComments().indexOf("ExecutionEnvironment") >= 0) {
					return varDecl.getId().getName();
				}
			}
		} else {
			String executionEnvironmentVarName;
			
			if(node.getChildrenNodes() != null) {
				for(Node children : node.getChildrenNodes()) {
					if((executionEnvironmentVarName = iterateNode(children)) != null) {
						return executionEnvironmentVarName;
					}
				}
			}
		}
		
		return null;
	}
	
	
	private void parseMain(MethodDeclaration main) {
		
		String executionEnvironmentVar = null;
		DataSet current = null;
		
		for (Statement statement : main.getBody().getStmts()) {
			
			if (current != null) {
				current = this.parseStatement(statement);
			} else {
				if(statement.toStringWithoutComments().indexOf("ExecutionEnvironment") >= 0) {
					executionEnvironmentVar = iterateNode(statement);
				} else if(executionEnvironmentVar != null) {
					if(statement.toStringWithoutComments().indexOf(executionEnvironmentVar) >= 0) {
						current = findRoot(statement, executionEnvironmentVar);
						this.inputs.add(current);
					}
				}
			}
		}
	}
	
	
	private DataSet findRoot (Node node, String executionEnvironmentVar) {
		if(node instanceof VariableDeclarationExpr) {
			VariableDeclarationExpr varDeclExpr = (VariableDeclarationExpr)node;
			for(VariableDeclarator varDecl : varDeclExpr.getVars()) {
				if(varDecl.getInit().toStringWithoutComments().indexOf(executionEnvironmentVar) >= 0) {
					return new DataSet(varDeclExpr.getType(), varDecl.getId().getName());
				}
			}
		} else {
			DataSet root;
			
			if(node.getChildrenNodes() != null) {
				for(Node children : node.getChildrenNodes()) {
					if((root = findRoot(children, executionEnvironmentVar)) != null) {
						return root;
					}
				}
			}
		}
		
		return null;
	}
	*/
		
	private void parseClass(ClassOrInterfaceDeclaration cid) {

		List<ClassOrInterfaceType> imps = cid.getImplements();
		
		if (imps == null) {
			List<ClassOrInterfaceType> exts = cid.getExtends();
			
			if (exts == null) {
				customTypes.put(cid.getName(), new CustomType(cid));
			} else {
				for (ClassOrInterfaceType e : exts) {
					if (e.getName().matches("^Tuple\\d{1,2}$")) {
						customTypes.put(cid.getName(), new CustomType(cid.getName(), e.getTypeArgs()));
					}
				}
			}
		} else {
			for (ClassOrInterfaceType implement : imps) {
				
				switch (implement.getName()) {
					case "MapFunction": transformations.put(cid.getName(), new MapParser(cid)); break;
					case "FlatMapFunction": transformations.put(cid.getName(), new FlatMapParser(cid)); break;
				}
			}
		}
	}
}
