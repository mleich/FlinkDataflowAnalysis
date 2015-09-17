package analysis;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.type.Type;

import analysis.dataset.DataSetElement;

public class CustomType {

	private String className;
	private List<DataSetElement> fields;
	
	
	public CustomType(String className) {
		this.className = className;
		this.fields = new ArrayList<DataSetElement>();
	}
	
	
	public CustomType(String name, List<Type> args) {
		this.className = name;
		this.fields = new ArrayList<DataSetElement>();
		
		int i = 0;
		for (Type arg : args) {
			fields.add(new DataSetElement(arg.toString(), i++));
		}
	}
	
	
	public CustomType(ClassOrInterfaceDeclaration cid) {
		this.className = cid.getName();
		this.fields = new ArrayList<DataSetElement>();
		
		int i = 0;
		for (Node n : cid.getChildrenNodes()) {
			if (n instanceof FieldDeclaration) {
				FieldDeclaration field = (FieldDeclaration)n;
				
				for (VariableDeclarator var : field.getVariables()) {
					fields.add(new DataSetElement(var.getId().getName(), field.getType().toStringWithoutComments(), i++));
				}
			}
		}
	}
	
	
	public String getClassName() {
		return this.className;
	}
	
	
	public List<DataSetElement> getFields() {
		ArrayList<DataSetElement> temp = new ArrayList<DataSetElement>();
		
		for(DataSetElement elem : fields) {
			temp.add(elem.clone());
		}
		
		return temp;
	}
}
