package analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.*;

import analysis.dataset.DataSet;
import analysis.transformations.FilterParser;
import analysis.transformations.FlatMapParser;
import analysis.transformations.MapParser;
import analysis.transformations.MapPartitionParser;
import analysis.transformations.ProjectParser;
import analysis.transformations.TransformationParser;

public class MethodParser {

	private DependencyAnalyser analyser;
	private List<DataSet> methodArgs;
	
	
	public MethodParser(DependencyAnalyser analyser) {
		this.analyser = analyser;
		methodArgs = new ArrayList<DataSet>();
	}
	
	
	public DataSet parseMethod(MethodDeclaration method) {
		
		
		
		return parseStatement(method.getBody());
	}
	
	
	public DataSet parseMethod(MethodDeclaration method, List<DataSet> args) {
		
		HashMap<String, String> varNames = new HashMap<String, String>();
		List<Parameter> params = method.getParameters();
		
		if (params != null) {
			ListIterator<Parameter> li = params.listIterator();
			
			for (DataSet arg : args) {
				Parameter param = li.next();
				
				if (arg != null) {
					varNames.put(param.getId().getName(), arg.getVarName());
					arg.setVarName(param.getId().getName());
				}
			}
		}
		
		DataSet result = parseStatement(method.getBody());
		
		if (params != null) {
			for (DataSet arg : args) {
				if (arg != null) {
					arg.setVarName(varNames.get(arg.getVarName()));
				}
			}
		}
		
		return result;
	}

	
	protected DataSet parseStatement(Statement statement) {
		
		DataSet current = null;
		
		if(statement instanceof AssertStmt) {
			
		} else if (statement instanceof BlockStmt) {
			
			BlockStmt stmt = (BlockStmt)statement;
			for(Statement s : stmt.getStmts()) {
				this.parseStatement(s);
			}
			
		} else if (statement instanceof BreakStmt) {
			
		} else if (statement instanceof ContinueStmt) {
			
		} else if (statement instanceof DoStmt) {
			
		} else if (statement instanceof EmptyStmt) {
			
		} else if (statement instanceof ExplicitConstructorInvocationStmt) {
			
		} else if (statement instanceof ExpressionStmt) {
			
			ExpressionStmt stmt = (ExpressionStmt)statement;
			current = this.parseExpression(stmt.getExpression(), null);
			
		} else if (statement instanceof ForeachStmt) {
			
		} else if (statement instanceof ForStmt) {
			
		} else if (statement instanceof IfStmt) {
			
			IfStmt stmt = (IfStmt)statement;
			this.parseExpression(stmt.getCondition(), null);
			this.parseStatement(stmt.getThenStmt());
			this.parseStatement(stmt.getElseStmt());
			
		} else if (statement instanceof LabeledStmt) {
			
		} else if (statement instanceof ReturnStmt) {
			
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
	
	
	protected DataSet parseExpression(Expression expr, DataSet ds) {
		
		DataSet current = null;
		
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
		} else if (expr instanceof MethodCallExpr) {
			
			MethodCallExpr method = (MethodCallExpr)expr;
			
			if(method.getName().equals("println")) {
				return null;
			}
			
			current = this.parseExpression(method.getScope(), null);
			
			if(current != null) {
				Expression arg = method.getArgs().get(0);
				TransformationParser tp = null;
				
				if(arg instanceof ObjectCreationExpr) {
					ObjectCreationExpr obj = (ObjectCreationExpr)arg;
					
					if(obj.getAnonymousClassBody() != null) {
						switch(method.getName()) {
							case "map": tp = new MapParser(obj, current); break;
							case "flatMap": tp = new FlatMapParser(obj, current); break;
							case "mapPartition": tp = new MapPartitionParser(obj, current); break;
							case "filter": tp = new FilterParser(obj, current); break;
						}
						
						if(tp != null) {
							return tp.getDataSetTransformation().getOutputDataSet();
						}
					} else {
						tp = DependencyAnalyser.transformations.get(obj.getType().getName());
						
						if(tp != null) {
							return tp.getDataSetTransformation(ds).getOutputDataSet();
						}
					}
					
					for(Expression a : method.getArgs()) {
						this.parseExpression(a, current);
					}
				} else {
					switch(method.getName()) {
						case "project": tp = new ProjectParser(method.getArgs(), current); break;
					}
					
					if(tp != null) {
						return tp.getDataSetTransformation().getOutputDataSet();
					}
				}
			} else {
				List<DataSet> args = new ArrayList<DataSet>();
				
				for(Expression arg : method.getArgs()) {
					args.add(this.parseExpression(arg, null));
				}
				
				current = parseMethod(DependencyAnalyser.methods.get(method.getName()), args);
			}
			
		} else if (expr instanceof NameExpr) {
			
			NameExpr name = (NameExpr)expr;
			current = findVar(name.getName());
			
		} else if (expr instanceof ObjectCreationExpr) {
		} else if (expr instanceof SuperExpr) {
		} else if (expr instanceof ThisExpr) {
		} else if (expr instanceof UnaryExpr) {
			
			UnaryExpr unary = (UnaryExpr)expr;
			parseExpression(unary.getExpr(), null);
			
		} else if (expr instanceof VariableDeclarationExpr) {
			
			VariableDeclarationExpr var = (VariableDeclarationExpr)expr;
			
			for(VariableDeclarator v : var.getVars()) {
				
				current = findVar(v.getId().getName());
				
				if(current == null) {
					if(analyser.getInputs() == null) {
						if (var.getType().toStringWithoutComments().equals("ExecutionEnvironment")) {
							analyser.setExecutionEnvironment(v.getId().getName());
						} else if(v.toStringWithoutComments().indexOf(analyser.getExecutionEnvironment()) >= 0) {
							methodArgs.add(new DataSet(var.getType(), v.getId().getName()));
							analyser.setInputs(methodArgs);
						}
						
						return null;
					} else {
						current = new DataSet(var.getType(), v.getId().getName());
					}
				}
				
				this.parseExpression(v.getInit(), current);
			}
		}
		
		return current;
	}
	
	
	protected DataSet findVar(String name) {
		
		DataSet result;
		
		for (DataSet arg : methodArgs) {
			if ((result = arg.findDataSet(name)) != null) {
				return result;
			}
		}
		
		return null;
	}
}
