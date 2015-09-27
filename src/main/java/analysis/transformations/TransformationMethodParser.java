package analysis.transformations;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.*;

import analysis.CustomType;
import analysis.DependencyAnalyser;
import analysis.dataset.DataSetElement;

public class TransformationMethodParser {

	private TransformationParser transformation;
	private DependencyNode outputNode;
	private List<DependencyNode> methodArgs;
	
	
	public TransformationMethodParser(TransformationParser transformation) {
		this.transformation = transformation;
		this.outputNode = null;
		this.methodArgs = new ArrayList<DependencyNode>();
	}
	
	
	public DependencyNode parseMethod(MethodDeclaration method) {
		
		List<Parameter> params = method.getParameters();
		
		for (Parameter param : params) {
			if (param.getType().toStringWithoutComments().indexOf("Collector") >= 0) {
				this.outputNode = new DependencyNode(param.getId().getName(), param.getType().toString(), DependencyNode.COLLECTOR);
			} else if (param.getType().toStringWithoutComments().indexOf("Iterable") >= 0) {
				methodArgs.add(new DependencyNode(param.getId().getName(), param.getType().toStringWithoutComments(), DependencyNode.INPUT));
			} else {
				methodArgs.add(new DependencyNode(param.getId().getName(), param.getType().toStringWithoutComments(), DependencyNode.INPUT));
			}
		}
		
		this.parseStatement(method.getBody());
		
		return this.outputNode;
	}
	
	
	public DependencyNode parseMethod(MethodDeclaration method, List<DependencyNode> args) {
		
		List<Parameter> params = method.getParameters();
		ListIterator<DependencyNode> argsIt = args.listIterator();
		
		for(Parameter param : params) {
			DependencyNode node = new DependencyNode(param.getId().getName(), param.getType().toStringWithoutComments());
			
			node.addDependency(argsIt.next());
			this.methodArgs.add(node);
		}
		
		this.parseStatement(method.getBody());
		
		return this.outputNode;
	}
	
	
	private DependencyNode parseStatement(Statement statement) {
		
		DependencyNode current = null;
		
		if(statement instanceof AssertStmt) {
		} else if (statement instanceof BlockStmt) {
			
			List<Statement> stmts = ((BlockStmt)statement).getStmts();
			ListIterator<Statement> it = stmts.listIterator(stmts.size());
			
			while(it.hasPrevious()) {
				parseStatement(it.previous());
			}
			
		} else if (statement instanceof BreakStmt) {
		} else if (statement instanceof ContinueStmt) {
		} else if (statement instanceof DoStmt) {
		} else if (statement instanceof EmptyStmt) {
		} else if (statement instanceof ExplicitConstructorInvocationStmt) {
		} else if (statement instanceof ExpressionStmt) {
			
			ExpressionStmt stmt = (ExpressionStmt)statement;
			parseExpression(stmt.getExpression(), null);
			
		} else if (statement instanceof ForeachStmt) {
			
			ForeachStmt foreach = (ForeachStmt)statement;			
			
			this.parseStatement(foreach.getBody());
			
			for(VariableDeclarator var : foreach.getVariable().getVars()) {
				
				DependencyNode va = this.findNode(var.getId().getName());
				
				if(va != null) {
					if(va.getFormat() == null) {
						va.setFormat(foreach.getVariable().getType().toStringWithoutComments());
					}
					
					this.parseExpression(foreach.getIterable(), va);
				}
			}
			
		} else if (statement instanceof ForStmt) {
			
			ForStmt stmt = (ForStmt)statement;
			this.parseStatement(stmt.getBody());
			
		} else if (statement instanceof IfStmt) {
			
			IfStmt stmt = (IfStmt)statement;
			parseStatement(stmt.getThenStmt());
			parseStatement(stmt.getElseStmt());
			parseExpression(stmt.getCondition(), null);
			
		} else if (statement instanceof LabeledStmt) {
		} else if (statement instanceof ReturnStmt) {
			
			ReturnStmt stmt = (ReturnStmt)statement;
			
			current = parseExpression(stmt.getExpr(), null);
			
			this.outputNode = current;
			
		} else if (statement instanceof SwitchEntryStmt) {
		} else if (statement instanceof SwitchStmt) {
		} else if (statement instanceof SynchronizedStmt) {
		} else if (statement instanceof ThrowStmt) {
		} else if (statement instanceof TryStmt) {
		} else if (statement instanceof TypeDeclarationStmt) {
		} else if (statement instanceof WhileStmt) {
		}
		
		return current;
	}
	
	
	private DependencyNode parseExpression(Expression expr, DependencyNode node) {
	
		DependencyNode current = null;
		
		if(expr instanceof AnnotationExpr) {
		} else if (expr instanceof ArrayAccessExpr) {
		} else if (expr instanceof ArrayCreationExpr) {
		} else if (expr instanceof ArrayInitializerExpr) {
		} else if (expr instanceof AssignExpr) {
		} else if (expr instanceof BinaryExpr) {
		} else if (expr instanceof CastExpr) {
		} else if (expr instanceof ClassExpr) {
		} else if (expr instanceof ConditionalExpr) {
		} else if (expr instanceof EnclosedExpr) {
		} else if (expr instanceof FieldAccessExpr) {
		} else if (expr instanceof InstanceOfExpr) {
		} else if (expr instanceof LiteralExpr) {
			
			LiteralExpr literal = (LiteralExpr)expr;
			String format = literal.getClass().getSimpleName();
			current = new DependencyNode("Literal", format.substring(0, format.length() - 11), literal.toStringWithoutComments());
			
			if (node != null) {
				node.addDependency(current);
			}
			
		} else if (expr instanceof MethodCallExpr) {
			
			MethodCallExpr method = (MethodCallExpr)expr;
			Expression scope = method.getScope();
			
			current = this.parseExpression(scope, null);
			
			if (current != null) {
				if (method.getArgs() != null) {
					for (Expression arg : method.getArgs()) {
						this.parseExpression(arg, current);
					}
				}
			} else {
				List<DependencyNode> args = new ArrayList<DependencyNode>();
				
				for (Expression arg : method.getArgs()) {
					args.add(this.parseExpression(arg, null));
				}
				
				current = new TransformationMethodParser(transformation).parseMethod(transformation.methods.get(method.getName()), args);
			}
			
			if (node != null) {
				node.addDependency(current);
			}
			
		} else if (expr instanceof NameExpr) {
			
			NameExpr name = (NameExpr)expr;
			
			current = this.findNode(name.getName());
			
			if (current == null) {
				current = new DependencyNode(name.getName());
			}
			
			if (node != null) {
				node.addDependency(current);
			}
			
		} else if (expr instanceof ObjectCreationExpr) {
			
			ObjectCreationExpr obj = (ObjectCreationExpr)expr;
			
			CustomType ct = DependencyAnalyser.customTypes.get(obj.getType().getName());
			
			if (node != null) {
				DependencyNode dataset = new DependencyNode(null ,obj.getType().getName(), DependencyNode.DATASET);
				node.addDependency(dataset);
				
				if (ct != null) {
					ListIterator<Expression> it = obj.getArgs().listIterator();
					
					for (DataSetElement e : ct.getFields()) {
						Expression ex = it.next();
						
						if (ex != null) {
							parseExpression(ex, dataset);
						} else {
							dataset.addDependency(new DependencyNode(e.getName(), e.getFormat()));
						}
					}
				} else {
					for (Expression e : obj.getArgs()) {
						parseExpression(e, node);
					}
				}
			}
			
		} else if (expr instanceof SuperExpr) {
		} else if (expr instanceof ThisExpr) {
			
			return null;
			
		} else if (expr instanceof UnaryExpr) {
		} else if (expr instanceof VariableDeclarationExpr) {
			
			VariableDeclarationExpr var = (VariableDeclarationExpr)expr;
			
			for (VariableDeclarator v : var.getVars()) {
				
				current = this.findNode(v.getId().getName());
				
				if (current != null) {
					if (current.getFormat() == null) {
						current.setFormat(var.getType().toStringWithoutComments());
					}
					
					this.parseExpression(v.getInit(), current);
				}
			}
		}
		
		return current;
	}
	
	
	private DependencyNode findNode(String name) {
		
		DependencyNode result = null;
		
		if (this.outputNode != null) {
			result = this.outputNode.findNode(name);
		}
		
		if (result == null) {
			for (DependencyNode node : this.methodArgs) {
				if (node.getName().equals(name)) {
					return node;
				}
			}
		}
		
		return result;
	}
}
