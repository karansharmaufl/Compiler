package cop5556fa17;

import java.net.MalformedURLException;
import java.net.URL;

import cop5556fa17.Scanner.Kind;
import cop5556fa17.Scanner.Token;
import cop5556fa17.TypeUtils.Type;
import cop5556fa17.AST.ASTNode;
import cop5556fa17.AST.ASTVisitor;
import cop5556fa17.AST.Declaration_Image;
import cop5556fa17.AST.Declaration_SourceSink;
import cop5556fa17.AST.Declaration_Variable;
import cop5556fa17.AST.Expression_Binary;
import cop5556fa17.AST.Expression_BooleanLit;
import cop5556fa17.AST.Expression_Conditional;
import cop5556fa17.AST.Expression_FunctionAppWithExprArg;
import cop5556fa17.AST.Expression_FunctionAppWithIndexArg;
import cop5556fa17.AST.Expression_Ident;
import cop5556fa17.AST.Expression_IntLit;
import cop5556fa17.AST.Expression_PixelSelector;
import cop5556fa17.AST.Expression_PredefinedName;
import cop5556fa17.AST.Expression_Unary;
import cop5556fa17.AST.Index;
import cop5556fa17.AST.LHS;
import cop5556fa17.AST.Program;
import cop5556fa17.AST.Sink_Ident;
import cop5556fa17.AST.Sink_SCREEN;
import cop5556fa17.AST.Source_CommandLineParam;
import cop5556fa17.AST.Source_Ident;
import cop5556fa17.AST.Source_StringLiteral;
import cop5556fa17.AST.Statement_Assign;
import cop5556fa17.AST.Statement_In;
import cop5556fa17.AST.Statement_Out;
import cop5556fa17.SymbolTable;


public class TypeCheckVisitor implements ASTVisitor {
	
	SymbolTable st = new SymbolTable();
		@SuppressWarnings("serial")
		public static class SemanticException extends Exception {
			Token t;

			public SemanticException(Token t, String message) {
				super("line " + t.line + " pos " + t.pos_in_line + ": "+  message);
				this.t = t;
			}

		}		
		

	
	/**
	 * The program name is only used for naming the class.  It does not rule out
	 * variables with the same name.  It is returned for convenience.
	 * 
	 * @throws Exception 
	 */
	public Object visitProgram(Program program, Object arg) throws Exception {
		for (ASTNode node: program.decsAndStatements) {
			//System.out.println("Node:	"+node);
			node.visit(this, arg);
		}
		return program.name;
	}

	public Object visitDeclaration_Variable( //Check this may be faulty
			Declaration_Variable declaration_Variable, Object arg)
			throws Exception {
		if(declaration_Variable.e != null){
			declaration_Variable.e.type_basic = (Type) declaration_Variable.e.visit(this, null);
			System.out.println("Hello world		"+declaration_Variable.e.type_basic);
		}
		Object type = null;
		if(st.lookupType(declaration_Variable.name) == null){
			st.insert(declaration_Variable.name, declaration_Variable);
			declaration_Variable.type_basic = TypeUtils.getType(declaration_Variable.firstToken);
			// The if statement below needs to be cross-verifed ..........
			if(declaration_Variable.e != null ){
				if(declaration_Variable.type_basic == declaration_Variable.e.type_basic){
					type = declaration_Variable.type_basic;
				}else{
					throw new SemanticException(declaration_Variable.firstToken, "Semantic exception");
				}
			}else{
				type = declaration_Variable.type_basic;
				//throw new SemanticException(declaration_Variable.firstToken,"Semantic exception");
			}	
		}
		else{
			throw new SemanticException(declaration_Variable.firstToken,"Semantic exception");
		}
		return type;
	}

	public Object visitExpression_Binary(Expression_Binary expression_Binary,
			Object arg) throws Exception {
		Object type = null;
		if(expression_Binary.e0 != null){
			expression_Binary.e0.type_basic = (Type) expression_Binary.e0.visit(this, null);
		}
		if(expression_Binary.e1 != null){
			expression_Binary.e1.type_basic = (Type) expression_Binary.e1.visit(this, null);
		}
		
		
		if(expression_Binary.op.equals(Kind.OP_EQ) || expression_Binary.op.equals(Kind.OP_NEQ)){
			expression_Binary.type_basic = Type.BOOLEAN;
			type = expression_Binary.type_basic; 
		}
		else if((
				expression_Binary.op.equals(Kind.OP_GE) ||
				expression_Binary.op.equals(Kind.OP_GT) ||
				expression_Binary.op.equals(Kind.OP_LE) ||
				expression_Binary.op.equals(Kind.OP_LT)) && expression_Binary.e0.type_basic == Type.INTEGER){
			expression_Binary.type_basic = Type.BOOLEAN;
			type = expression_Binary.type_basic;
		}
		
		else if((
				expression_Binary.op.equals(Kind.OP_AND) ||
				expression_Binary.op.equals(Kind.OP_OR) 
				) && 
				(expression_Binary.e0.type_basic == Type.INTEGER
						|| expression_Binary.e0.type_basic == Type.BOOLEAN)){
			expression_Binary.type_basic = Type.BOOLEAN;
			type = expression_Binary.type_basic;
		}
		else if((
				expression_Binary.op.equals(Kind.OP_DIV) ||
				expression_Binary.op.equals(Kind.OP_MINUS) ||
				expression_Binary.op.equals(Kind.OP_MOD) ||
				expression_Binary.op.equals(Kind.OP_PLUS) ||
				expression_Binary.op.equals(Kind.OP_POWER) ||
				expression_Binary.op.equals(Kind.OP_TIMES)
				) && (expression_Binary.e0.type_basic == Type.INTEGER) ){
			expression_Binary.type_basic = Type.INTEGER;
			type = expression_Binary.type_basic;
		}
		else{
			// Make sure this is correct
			expression_Binary.type_basic = null;
			type = expression_Binary.type_basic;
		}
		
		
		
		
		
		
		
		
		if((expression_Binary.e0.type_basic == expression_Binary.e1.type_basic) && (expression_Binary.type_basic != null)){
			//expression_Binary.type
//			if(expression_Binary.op.equals(Kind.OP_EQ) || expression_Binary.op.equals(Kind.OP_NEQ)){
//				expression_Binary.type_basic = Type.BOOLEAN;
//				type = expression_Binary.type_basic; 
//			}
//			else if((
//					expression_Binary.op.equals(Kind.OP_GE) ||
//					expression_Binary.op.equals(Kind.OP_GT) ||
//					expression_Binary.op.equals(Kind.OP_LE) ||
//					expression_Binary.op.equals(Kind.OP_LT)) && expression_Binary.e0.type_basic == Type.INTEGER){
//				expression_Binary.type_basic = Type.BOOLEAN;
//				type = expression_Binary.type_basic;
//			}
//			
//			else if((
//					expression_Binary.op.equals(Kind.OP_AND) ||
//					expression_Binary.op.equals(Kind.OP_OR) 
//					) && 
//					(expression_Binary.e0.type_basic == Type.INTEGER
//							|| expression_Binary.e0.type_basic == Type.BOOLEAN)){
//				expression_Binary.type_basic = Type.BOOLEAN;
//				type = expression_Binary.type_basic;
//			}
//			else if((
//					expression_Binary.op.equals(Kind.OP_DIV) ||
//					expression_Binary.op.equals(Kind.OP_MINUS) ||
//					expression_Binary.op.equals(Kind.OP_MOD) ||
//					expression_Binary.op.equals(Kind.OP_PLUS) ||
//					expression_Binary.op.equals(Kind.OP_POWER) ||
//					expression_Binary.op.equals(Kind.OP_TIMES)
//					) && (expression_Binary.e0.type_basic == Type.INTEGER) ){
//				expression_Binary.type_basic = Type.INTEGER;
//				type = expression_Binary.type_basic;
//			}
//			else{
//				// Make sure this is correct
//				expression_Binary.type_basic = null;
//				type = expression_Binary.type_basic;
//			}
			type = expression_Binary.type_basic;
		}
		else{
			throw new SemanticException(expression_Binary.firstToken, "Semantic exception");
		}
		return type;
	}


	public Object visitExpression_Unary(Expression_Unary expression_Unary,
			Object arg) throws Exception {
		Object type = null;
		// Visiting the node
		if(expression_Unary.e != null){
			expression_Unary.e.type_basic = (Type) expression_Unary.e.visit(this, null);
		}
		
		//Checking the pre-s
		if(expression_Unary.op.toString().equals("OP_EXCL") && (expression_Unary.e.type_basic == Type.BOOLEAN || expression_Unary.e.type_basic == Type.INTEGER) ){
			expression_Unary.type_basic = expression_Unary.e.type_basic;
			type = expression_Unary.type_basic;
		}
		else if(expression_Unary.op.toString().equals("OP_PLUS") || expression_Unary.op.toString().equals("OP_MINUS")){
				expression_Unary.type_basic= Type.INTEGER;
				type = expression_Unary.type_basic;
		}
		else{
				type = null;
			}
			
		// Checking the require
		if(expression_Unary.type_basic != null){
		
		}else{
			throw new SemanticException(expression_Unary.firstToken, "Semantic excepption");
		}	
		return expression_Unary.type_basic;
	}


	public Object visitIndex(Index index, Object arg) throws Exception {
		Object type = null;
		if(index.e0 != null){
			index.e0.type_basic = (Type) index.e0.visit(this, null);
		}
		if(index.e1 != null){
			index.e1.type_basic = (Type) index.e1.visit(this, null);
		}
		
		if(index.e0.type_basic == Type.INTEGER && index.e1.type_basic == Type.INTEGER){
			index.setCartesian(!((index.e0.toString().equals("KW_r")) && index.e1.toString().equals("KW_a")));
		}
		return index.type_basic;
	}

	public Object visitExpression_PixelSelector(
			Expression_PixelSelector expression_PixelSelector, Object arg)
			throws Exception {
		Object type = null;		
		Type t1 = st.lookupType(expression_PixelSelector.name);
		if(t1==Type.IMAGE){
			expression_PixelSelector.type_basic = Type.INTEGER;
			type = expression_PixelSelector.type_basic;
		}else if(expression_PixelSelector.index == null){
			expression_PixelSelector.type_basic = t1;
			type = expression_PixelSelector.type_basic; 
		}else{
			expression_PixelSelector.type_basic = null;
		}
		
		if(expression_PixelSelector.type_basic != null){
			
		}
		else{
			throw new SemanticException(expression_PixelSelector.firstToken, "Semantic exception");
		}
		return type;
	}


	public Object visitExpression_Conditional(
			Expression_Conditional expression_Conditional, Object arg)
			throws Exception {
		
		Object type = null;
		// Check if this is not null
		if(expression_Conditional.condition != null){
			expression_Conditional.condition.type_basic=(Type) expression_Conditional.condition.visit(this,null);
		}
		if(expression_Conditional.trueExpression != null){
			expression_Conditional.trueExpression.type_basic=(Type) expression_Conditional.trueExpression.visit(this,null);
		}
		if(expression_Conditional.falseExpression != null){
			expression_Conditional.falseExpression.type_basic=(Type) expression_Conditional.falseExpression.visit(this,null);
		}
		
		
		if(expression_Conditional.condition.type_basic == Type.BOOLEAN && (expression_Conditional.trueExpression.type_basic == expression_Conditional.falseExpression.type_basic)){
			expression_Conditional.type_basic = expression_Conditional.trueExpression.type_basic;
			type = expression_Conditional.type_basic;
		}
		else{
			throw new SemanticException(expression_Conditional.firstToken, "Semantic exception");
		}
		return type;
		
	}


	public Object visitDeclaration_Image(Declaration_Image declaration_Image,
			Object arg) throws Exception {
		Object type = null;
		if(declaration_Image.xSize != null){
			declaration_Image.xSize.type_basic = (Type) declaration_Image.xSize.visit(this, null);
		}
		if(declaration_Image.ySize != null){
			declaration_Image.ySize.type_basic = (Type) declaration_Image.ySize.visit(this, null);
		}
		
		if(st.lookupType(declaration_Image.name) == null){
			st.insert(declaration_Image.name, declaration_Image);
			declaration_Image.type_basic = Type.IMAGE;
			if(declaration_Image.xSize != null){
				if((declaration_Image.ySize != null) && (declaration_Image.xSize.type_basic == Type.INTEGER) && (declaration_Image.ySize.type_basic == Type.INTEGER)){
					
				}else{
					throw new SemanticException(declaration_Image.firstToken, "Semantic Exception");
				}
					
			}else{
				//throw new SemanticException(declaration_Image.firstToken, "Semantic Exception");
			}
		}else{
			throw new SemanticException(declaration_Image.firstToken, "Semantic exception");
		}
		return type;
	}


	public Object visitSource_StringLiteral(
			Source_StringLiteral source_StringLiteral, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		Object type;
		try{
			URL fileOrUrl = new java.net.URL(source_StringLiteral.fileOrUrl);
			source_StringLiteral.type_basic = Type.URL;
			type = source_StringLiteral.type_basic;
		}
		catch(MalformedURLException mue){
			source_StringLiteral.type_basic = Type.FILE;
			type = source_StringLiteral.type_basic;
		}
		return type ;
		
	}


	public Object visitSource_CommandLineParam(
			Source_CommandLineParam source_CommandLineParam, Object arg)
			throws Exception {
		Object type = null;
		
		if(source_CommandLineParam.paramNum != null){
			//System.out.println("In here");
			source_CommandLineParam.paramNum.type_basic = (Type) source_CommandLineParam.paramNum.visit(this,null);
		}
//		else{
//			throw new SemanticException(source_CommandLineParam.firstToken, "Semantic exception");
//		}
		
		source_CommandLineParam.type_basic = source_CommandLineParam.paramNum.type_basic;
		if(source_CommandLineParam.type_basic == Type.INTEGER){
			type = source_CommandLineParam.type_basic;
		}
		else{
			throw new SemanticException(source_CommandLineParam.firstToken, "Semantic exception");
		}
		return type;
				
	}


	public Object visitSource_Ident(Source_Ident source_Ident, Object arg)
			throws Exception {
		Object type=null;
		source_Ident.type_basic = st.lookupType(source_Ident.name);
		if(source_Ident.type_basic == Type.FILE || source_Ident.type_basic == Type.URL){
			type = source_Ident.type_basic; }
		return type;
	}


	public Object visitDeclaration_SourceSink(
			Declaration_SourceSink declaration_SourceSink, Object arg)
			throws Exception {
		Object type = null;
		if(declaration_SourceSink.source != null){
			//System.out.println("Hello world");
			declaration_SourceSink.source.type_basic = (Type) declaration_SourceSink.source.visit(this, null);
			//System.out.println("I am here"+ declaration_SourceSink.source.type_basic);
		}
		
		if(st.lookupType(declaration_SourceSink.name) == null){
			st.insert(declaration_SourceSink.name, declaration_SourceSink);
			declaration_SourceSink.type_basic = TypeUtils.getType(declaration_SourceSink.firstToken);
			//System.out.println("I am here"+ declaration_SourceSink.type_basic);
			if(declaration_SourceSink.source.type_basic == declaration_SourceSink.type_basic){
				
			}else{
				throw new SemanticException(declaration_SourceSink.firstToken, "Semantic Exception");
			}
		}else{
			throw new SemanticException(declaration_SourceSink.firstToken, "Semantic exception");
		}
		return type;
	}


	public Object visitExpression_IntLit(Expression_IntLit expression_IntLit,
			Object arg) throws Exception {
		Object type = null;
		expression_IntLit.type_basic = Type.INTEGER;
		type = expression_IntLit.type_basic; 
		return type;
	}


	public Object visitExpression_FunctionAppWithExprArg(
			Expression_FunctionAppWithExprArg expression_FunctionAppWithExprArg,
			Object arg) throws Exception {
		Object type =null;
		if(expression_FunctionAppWithExprArg.arg != null){
			expression_FunctionAppWithExprArg.arg.type_basic = (Type) expression_FunctionAppWithExprArg.arg.visit(this, null);
		}
		
		if(expression_FunctionAppWithExprArg.arg.type_basic == Type.INTEGER){
			expression_FunctionAppWithExprArg.type_basic = Type.INTEGER;
			type = expression_FunctionAppWithExprArg.type_basic; 
		}
		else{
			throw new SemanticException(expression_FunctionAppWithExprArg.firstToken, "Semantic exception");
		}
		return type;
	}


	public Object visitExpression_FunctionAppWithIndexArg(
			Expression_FunctionAppWithIndexArg expression_FunctionAppWithIndexArg,
			Object arg) throws Exception {
		Object type = null;
		expression_FunctionAppWithIndexArg.type_basic = Type.INTEGER;
		type = expression_FunctionAppWithIndexArg.type_basic; 
		return type;
	}


	public Object visitExpression_PredefinedName(
			Expression_PredefinedName expression_PredefinedName, Object arg)
			throws Exception {
		Object type = null;
		expression_PredefinedName.type_basic = Type.INTEGER;
		type = expression_PredefinedName.type_basic;
		return type;
	}


	public Object visitStatement_Out(Statement_Out statement_Out, Object arg)
			throws Exception {
		Object type = null;
		if(statement_Out.sink != null){
			statement_Out.sink.type_basic = (Type) statement_Out.sink.visit(this, null);
		}
		statement_Out.setDec(st.lookupDec(statement_Out.name));
		if(st.lookupDec(statement_Out.name) != null){
			if(
					(
							(st.lookupType(statement_Out.name) == Type.INTEGER 
								|| st.lookupType(statement_Out.name) == Type.BOOLEAN
							) &&  statement_Out.sink.type_basic == Type.SCREEN
								)
						|| 
								(
							(st.lookupType(statement_Out.name)== Type.IMAGE) 
							&&
							(statement_Out.sink.type_basic == Type.FILE 
								|| statement_Out.sink.type_basic == Type.SCREEN)
								)	
					){
				type = statement_Out.type_basic;
			}else{
				throw new SemanticException(statement_Out.firstToken, "Semantic Exception");
			}
		}else{
			throw new SemanticException(statement_Out.firstToken, "Semantic exception");
		}
		
		
//		if(st.lookupDec(statement_Out.name) != null && 
//				(
//						(
//					(st.lookupType(statement_Out.name) == Type.INTEGER 
//						|| st.lookupType(statement_Out.name) == Type.BOOLEAN
//					) &&  statement_Out.sink.type == Type.SCREEN
//						)
//				|| 
//						(
//					(st.lookupType(statement_Out.name)== Type.IMAGE) 
//					&&
//					(statement_Out.sink.type == Type.FILE 
//						|| statement_Out.sink.type == Type.SCREEN)
//						)
//				)){
//			statement_Out.setDec(st.lookupDec(statement_Out.name));
//			type = statement_Out.getDec();// Check again
//		}
//		else{
//			throw new SemanticException(statement_Out.firstToken, "Semantic EXception");
//		}
		
		
		return type;
	}


	public Object visitStatement_In(Statement_In statement_In, Object arg)
			throws Exception {
		Object type = null;
		if(statement_In.source != null){
			statement_In.source.type_basic = (Type) statement_In.source.visit(this, null);
		}
		statement_In.setDec(st.lookupDec(statement_In.name));
		if((st.lookupDec(statement_In.name) != null) && (st.lookupType(statement_In.name) == statement_In.source.type_basic)){
			type = statement_In.type_basic;
		}
		else{
			throw new SemanticException(statement_In.firstToken, "Semantic EXception");
		}
		return type;
	}


	public Object visitStatement_Assign(Statement_Assign statement_Assign,
			Object arg) throws Exception {
		Object type = null;
		if(statement_Assign.lhs != null){
			
			statement_Assign.lhs.type_basic = (Type) statement_Assign.lhs.visit(this, null);
			//System.out.println("In here1"+ statement_Assign.lhs.type_basic);
		}
		//System.out.println("Check if null"+ statement_Assign.lhs);
		if(statement_Assign.e != null){ 
			statement_Assign.e.type_basic = (Type) statement_Assign.e.visit(this, null);
		}
		if(statement_Assign.lhs.type_basic == statement_Assign.e.type_basic){	
			statement_Assign.setCartesian(statement_Assign.lhs.isCartesian());
		}
		else{
			throw new SemanticException(statement_Assign.firstToken, "Semantic exception");
		}
		return statement_Assign.type_basic;
	}

// Check your implementation
	public Object visitLHS(LHS lhs, Object arg) throws Exception {
		Object type = null;
//		if(lhs.index != null){
//			lhs.index = lhs.index.visit(this, null);
//		}
		if(st.lookupDec(lhs.name)==null)
		{
			throw new SemanticException(lhs.firstToken, "Semantic exception");
		}else{
			lhs.type_basic = st.lookupDec(lhs.name).type_basic;
			if(lhs.index != null)
			lhs.setCartesian(lhs.index.isCartesian()); // Check to make sure
			type = lhs.type_basic;
		}
		return lhs.type_basic;
	}


	public Object visitSink_SCREEN(Sink_SCREEN sink_SCREEN, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		//throw new UnsupportedOperationException();
		Object type = null;
		sink_SCREEN.type_basic = Type.SCREEN; 
		type = sink_SCREEN.type_basic;
		return type;
	}


	public Object visitSink_Ident(Sink_Ident sink_Ident, Object arg)
			throws Exception {
		Object type = null;
		sink_Ident.type_basic = st.lookupType(sink_Ident.name);
		if(sink_Ident.type_basic == Type.FILE){
			type = sink_Ident.type_basic;
		}
		else{
			throw new SemanticException(sink_Ident.firstToken, "Semantic Exception");
		}
		return type;
	}

	public Object visitExpression_BooleanLit(
			Expression_BooleanLit expression_BooleanLit, Object arg)
			throws Exception {
		Object type = null;
		expression_BooleanLit.type_basic = Type.BOOLEAN;
		return expression_BooleanLit.type_basic;
	}

	public Object visitExpression_Ident(Expression_Ident expression_Ident,
			Object arg) throws Exception {
		Object type = null;
		//System.out.println("In here ......");
		//System.out.println(st);
		expression_Ident.type_basic = st.lookupType(expression_Ident.name);
		type = expression_Ident.type_basic;
		System.out.println(type);
		return type;
	}

	public Object visitStatement_Transform(Statement_Assign statement_Assign,
			Object arg) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
