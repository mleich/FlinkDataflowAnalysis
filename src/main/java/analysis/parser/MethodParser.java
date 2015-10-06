package analysis.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.ArrayAccessExpr;
import com.github.javaparser.ast.expr.ArrayCreationExpr;
import com.github.javaparser.ast.expr.ArrayInitializerExpr;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.expr.ConditionalExpr;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.InstanceOfExpr;
import com.github.javaparser.ast.expr.LiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.SuperExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.AssertStmt;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.BreakStmt;
import com.github.javaparser.ast.stmt.ContinueStmt;
import com.github.javaparser.ast.stmt.DoStmt;
import com.github.javaparser.ast.stmt.EmptyStmt;
import com.github.javaparser.ast.stmt.ExplicitConstructorInvocationStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.ForeachStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.LabeledStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.SwitchEntryStmt;
import com.github.javaparser.ast.stmt.SwitchStmt;
import com.github.javaparser.ast.stmt.SynchronizedStmt;
import com.github.javaparser.ast.stmt.ThrowStmt;
import com.github.javaparser.ast.stmt.TryStmt;
import com.github.javaparser.ast.stmt.TypeDeclarationStmt;
import com.github.javaparser.ast.stmt.WhileStmt;

import analysis.DependencyAnalyser;
import analysis.parser.custom.type.CustomTypeParser;
import analysis.transformations.DependencyNode;

public class MethodParser extends Parser {

	protected ArrayList<DependencyNode> methodArgs;
	protected DependencyNode outputNode;
	
	
	public MethodParser(HashMap<String, MethodDeclaration> methods) {
		super(methods);
		outputNode = null;
		methodArgs = new ArrayList<DependencyNode>();
	}
	
	
	public DependencyNode getOutputNode() {
		return outputNode;
	}
	
	
	public DependencyNode parseMethod(MethodDeclaration method, List<DependencyNode> args) {
		
		List<Parameter> params = method.getParameters();
		ListIterator<DependencyNode> argsIt = args.listIterator();
		
		for(Parameter param : params) {
			DependencyNode node = new DependencyNode(param.getId().getName(), param.getType().toStringWithoutComments());
			
			node.addDependencyNode(argsIt.next());
			this.methodArgs.add(node);
		}
		
		this.parseStatement(method.getBody());
		
		return this.outputNode;
	}


	protected DependencyNode parseStatement(Statement statement) {
		
		DependencyNode current = null;
		
		if(statement instanceof AssertStmt) {
		} else if (statement instanceof BlockStmt) {
			
			List<Statement> stmts = ((BlockStmt)statement).getStmts();
			
			if(stmts != null) {
				for (Statement stmt : stmts) {
					parseStatement(stmt);
				}
			}
			
		} else if (statement instanceof BreakStmt) {
		} else if (statement instanceof ContinueStmt) {
		} else if (statement instanceof DoStmt) {
		} else if (statement instanceof EmptyStmt) {
		} else if (statement instanceof ExplicitConstructorInvocationStmt) {
		} else if (statement instanceof ExpressionStmt) {
			
			parseExpression(((ExpressionStmt)statement).getExpression(), null);
			
		} else if (statement instanceof ForeachStmt) {
			
			ForeachStmt foreach = (ForeachStmt)statement;
			
			for(VariableDeclarator var : foreach.getVariable().getVars()) {
				
				DependencyNode iter = new DependencyNode(var.getId().getName(), foreach.getVariable().getType().toStringWithoutComments());
				
				this.parseExpression(foreach.getIterable(), iter);
			}
			
			this.parseStatement(foreach.getBody());
			
		} else if (statement instanceof ForStmt) {
			
			this.parseStatement(((ForStmt)statement).getBody());
			
		} else if (statement instanceof IfStmt) {
			
			IfStmt stmt = (IfStmt)statement;
			parseExpression(stmt.getCondition(), null);
			parseStatement(stmt.getThenStmt());
			parseStatement(stmt.getElseStmt());
			
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
	
	
	protected DependencyNode parseExpression(Expression expr, DependencyNode node) {
	
		DependencyNode current = null;
		
		if(expr instanceof AnnotationExpr) {
		} else if (expr instanceof ArrayAccessExpr) {
		} else if (expr instanceof ArrayCreationExpr) {
		} else if (expr instanceof ArrayInitializerExpr) {
		} else if (expr instanceof AssignExpr) {
			
			AssignExpr assign = (AssignExpr)expr;
			DependencyNode target = parseExpression(assign.getTarget(), null);
			parseExpression(assign.getValue(), target);
			
		} else if (expr instanceof BinaryExpr) {
			
			BinaryExpr binary = (BinaryExpr)expr;
			current = new DependencyNode(binary.getOperator().name() + "Operator");
			parseExpression(binary.getLeft(), current);
			DependencyNode right = parseExpression(binary.getRight(), current);
			
			current.setFormat(right.getFormat());
			
		} else if (expr instanceof CastExpr) {
		} else if (expr instanceof ClassExpr) {
		} else if (expr instanceof ConditionalExpr) {
		} else if (expr instanceof EnclosedExpr) {
		} else if (expr instanceof FieldAccessExpr) {
			
			FieldAccessExpr fieldExpr = (FieldAccessExpr)expr;
			Expression scope = fieldExpr.getScope();
			
			DependencyNode dataSet = this.parseExpression(scope, null);
			
			current = dataSet.findNode(fieldExpr.getField());
			
			if (current == null) {
				current = dataSet.findAfterNode(fieldExpr.getField());
			}
			
			if (current == null) {
				current = new DependencyNode(fieldExpr.getField());
				current.addDependencyNode(dataSet);
			}
			
			if (node != null) {
				node.addDependencyNode(current);
			}
			
		} else if (expr instanceof InstanceOfExpr) {
		} else if (expr instanceof LiteralExpr) {
			
			LiteralExpr literal = (LiteralExpr)expr;
			String format = literal.getClass().getSimpleName();
			String formatName = format.substring(0, format.length() - 11);
			current = new DependencyNode(formatName + "Literal", formatName, literal.toStringWithoutComments());
			
			if (node != null) {
				node.addDependencyNode(current);
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
				
				current = new MethodParser(methods).parseMethod(getMethod(method.getName()), args);
			}
			
			if (node != null) {
				node.addDependencyNode(current);
			}
			
		} else if (expr instanceof NameExpr) {
			
			NameExpr name = (NameExpr)expr;
			
			current = this.findNode(name.getName());
			
			if (current == null) {
				current = new DependencyNode(name.getName());
			}
			
			if (node != null) {
				node.addDependencyNode(current);
			}
			
		} else if (expr instanceof ObjectCreationExpr) {
			
			ObjectCreationExpr obj = (ObjectCreationExpr)expr;
			
			CustomTypeParser ctp = DependencyAnalyser.getCustomType(obj.getType().getName());
			
			if (ctp != null) {
				ArrayList<DependencyNode> args = new ArrayList<DependencyNode>();
				for (Expression exp : obj.getArgs()) {
					args.add(parseExpression(exp, null));
				}
				
				current = ctp.getDependencyNode(null, args);
			
				if (node != null) {
					node.addDependencyNode(current);
				}
			} else {
				for (Expression e : obj.getArgs()) {
					parseExpression(e, node);
				}
			}
			
		} else if (expr instanceof SuperExpr) {
		} else if (expr instanceof ThisExpr) {
			
			if(outputNode != null) {
				current = outputNode.findNode("this");
			}
			
		} else if (expr instanceof UnaryExpr) {
		} else if (expr instanceof VariableDeclarationExpr) {
			
			VariableDeclarationExpr var = (VariableDeclarationExpr)expr;
			
			for (VariableDeclarator v : var.getVars()) {
				
				CustomTypeParser ctp = DependencyAnalyser.getCustomType(var.getType().toStringWithoutComments());
				
				if (ctp != null) {
					current = ctp.getDependencyNode(v.getId().getName());
				} else {
					current = new DependencyNode(v.getId().getName(), var.getType().toStringWithoutComments());
				}
				
				this.parseExpression(v.getInit(), current);
			}
		}
		
		return current;
	}
	
	
	protected DependencyNode findNode(String name) {
		
		DependencyNode node = null;
		for (DependencyNode arg : methodArgs) {
			if (arg.getName().equals(name)) {
				return arg;
			}
			
			node = arg.findAfterNode(name);
		}
		
		if (node == null) {
			node = outputNode.findNode(name);
		}

		return node;
	}
}
