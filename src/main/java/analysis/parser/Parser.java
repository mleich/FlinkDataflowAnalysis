package analysis.parser;

import java.util.HashMap;

import com.github.javaparser.ast.body.MethodDeclaration;

public class Parser {
	
	protected HashMap<String, MethodDeclaration> methods;
	
	
	public Parser() {
		methods = new HashMap<String, MethodDeclaration>();
	}
	
	
	public Parser(HashMap<String, MethodDeclaration> methods) {
		this.methods = methods;
	}
	
	
	public MethodDeclaration getMethod(String name) {
		return methods.get(name);
	}
	
	
	public HashMap<String, MethodDeclaration> getAllMethods() {
		return methods;
	}
}
