package analysis.parser.custom.type;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.Parameter;

import analysis.parser.MethodParser;
import analysis.transformations.DependencyNode;

public class ConstructorParser extends MethodParser {
	
	private CustomTypeParser ctp;
	private List<Allocation> allocations;
	private ConstructorDeclaration cd;
	
	
	public ConstructorParser(ConstructorDeclaration cd, CustomTypeParser ctp) {
		super(ctp.getAllMethods());
		
		this.cd = cd;
		this.ctp = ctp;
		this.allocations = new ArrayList<Allocation>();
				
		List<Parameter> params = cd.getParameters();
		int i = 0;
		for (Parameter param : params) {
			methodArgs.add(new DependencyNode(param.getId().getName(), param.getType().toStringWithoutComments(), DependencyNode.INPUT, i++));
		}
		
		outputNode = new DependencyNode("this", DependencyNode.DATASET);
		for(DependencyNode field : ctp.getFields()) {
			outputNode.addDependencyNode(field);
		}
	}
	
	
	public void parseConstructor() {
		
		parseStatement(cd.getBlock());
		
		for (DependencyNode field : outputNode.getDependencyNodes()) {
			if(field.getDependencyNodes() != null) {
				for (DependencyNode dep : field.getDependencyNodes()) {
					findAllocations(field, dep);
				}
			}
		}
	}
	
	
	public void findAllocations(DependencyNode field, DependencyNode currentNode) {
		
		if (currentNode.isInput()) {
			allocations.add(new Allocation(currentNode.getNumber(), field.getNumber()));
		} else {
			for (DependencyNode dep : currentNode.getDependencyNodes()) {
				findAllocations(field, dep);
			}
		}
	}
	
	
	public boolean compareParameters(List<DependencyNode> args) {
		
		if(args.size() != methodArgs.size()) {
			return false;
		}
		
		int i = 0;
		for (DependencyNode arg : args) {
			if(!arg.getFormat().equals(methodArgs.get(i++).getFormat())) {
				return false;
			}
		}
		
		return true;
	}
	
	
	public DependencyNode createDependencyNode(String name, ArrayList<DependencyNode> args) {
		
		DependencyNode node = new DependencyNode(name ,ctp.getClassName(), DependencyNode.DATASET);
		
		ArrayList<DependencyNode> fields = ctp.getFields();
		
		for (DependencyNode field : fields) {
			node.addDependencyNode(field);
		}
		
		for (Allocation a : allocations) {
			fields.get(a.getTargetField()).addDependencyNode(args.get(a.getArgNumber()));
		}
		
		return node;
	}
	
	
	private class Allocation {
		
		private int argNumber;
		private int targetField;
		
		
		public Allocation(int argNumber, int targetField) {
			this.argNumber = argNumber;
			this.targetField = targetField;
		}
		
		
		public int getArgNumber() {
			return argNumber;
		}
		
		
		public int getTargetField() {
			return targetField;
		}
	}
}
