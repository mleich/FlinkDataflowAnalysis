package analysis.parser.custom.type;

import java.util.ArrayList;
import java.util.List;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.type.Type;

import analysis.dataset.DataSetElement;
import analysis.parser.Parser;
import analysis.transformations.DependencyNode;

public class CustomTypeParser extends Parser {

	private String className;
	private ArrayList<DependencyNode> fields;
	private List<ConstructorParser> constructors;
	
	
	public CustomTypeParser(String className) {
		this.className = className;
		this.fields = new ArrayList<DependencyNode>();
	}
	
	
	public CustomTypeParser(String name, List<Type> args) {
		this.className = name;
		this.fields = new ArrayList<DependencyNode>();
		
		int i = 0;
		for (Type arg : args) {
			fields.add(new DependencyNode("f" + i, arg.toString(), DependencyNode.FIELD, i++));
		}
	}
	
	
	public CustomTypeParser(ClassOrInterfaceDeclaration cid) {
		className = cid.getName();
		fields = new ArrayList<DependencyNode>();
		constructors = new ArrayList<ConstructorParser>();
		
		int i = 0;
		for (Node node : cid.getChildrenNodes()) {
			if (node instanceof FieldDeclaration) {
				
				FieldDeclaration field = (FieldDeclaration)node;
				
				for (VariableDeclarator var : field.getVariables()) {
					fields.add(new DependencyNode(var.getId().getName(), field.getType().toStringWithoutComments(), DependencyNode.FIELD, i++));
				}
				
			} else if (node instanceof ConstructorDeclaration) {
				
				constructors.add(new ConstructorParser((ConstructorDeclaration)node, this));
				
			} else if (node instanceof MethodDeclaration) {
				
				MethodDeclaration method = (MethodDeclaration)node;
				
				methods.put(method.getName(), method);
			}
		}
		
		for (ConstructorParser cp : constructors) {
			cp.parseConstructor();
		}
	}
	
	
	public String getClassName() {
		return className;
	}
	
	
	public ArrayList<DependencyNode> getFields() {
		
		ArrayList<DependencyNode> temp = new ArrayList<DependencyNode>();
		
		for(DependencyNode field : fields) {
			temp.add(field.clone());
		}
		
		return temp;
	}
	
	
	public List<DataSetElement> getElements() {
		
		ArrayList<DataSetElement> temp = new ArrayList<DataSetElement>();
		
		for(DependencyNode field : fields) {
			temp.add(new DataSetElement(field.getName(), field.getFormat(), field.getNumber()));
		}
		
		return temp;
	}
	
	
	public DependencyNode getDependencyNode(String name) {

		DependencyNode node = new DependencyNode(name ,className, DependencyNode.DATASET);
		
		for (DependencyNode field : fields) {
			node.addAfterNode(new DependencyNode(field.getName(), field.getFormat(), DependencyNode.INPUT, field.getNumber()));
		}
		
		return node;
	}
	
	
	public DependencyNode getDependencyNode(String name, ArrayList<DependencyNode> args) {

		for (ConstructorParser cp : constructors) {
			if (cp.compareParameters(args)) {
				return cp.createDependencyNode(name, args);
			}
		}
		
		return null;
	}
}
