package cop5556fa17;


import java.util.*;

import cop5556fa17.Scanner.Kind;
import cop5556fa17.Scanner.Token;
import cop5556fa17.Parser.SyntaxException;
import cop5556fa17.AST.Declaration;
import cop5556fa17.AST.Declaration_Image;
import cop5556fa17.AST.Declaration_SourceSink;
import cop5556fa17.AST.Declaration_Variable;
import cop5556fa17.AST.Expression;
import cop5556fa17.AST.Expression_Binary;
import cop5556fa17.AST.Expression_BooleanLit;
import cop5556fa17.AST.Expression_Conditional;
import cop5556fa17.AST.Expression_FunctionApp;
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
import cop5556fa17.AST.Sink;
import cop5556fa17.AST.Sink_Ident;
import cop5556fa17.AST.Sink_SCREEN;
import cop5556fa17.AST.Source;
import cop5556fa17.AST.Source_CommandLineParam;
import cop5556fa17.AST.Source_Ident;
import cop5556fa17.AST.Source_StringLiteral;
import cop5556fa17.AST.Statement;
import cop5556fa17.AST.Statement_Assign;
import cop5556fa17.AST.Statement_In;
import cop5556fa17.AST.Statement_Out;
import cop5556fa17.AST.ASTNode;

import static cop5556fa17.Scanner.Kind.*;

public class Parser {

	@SuppressWarnings("serial")
	public class SyntaxException extends Exception {
		Token t;

		public SyntaxException(Token t, String message) {
			super(message);
			this.t = t;
		}

	}


	Scanner scanner;
	Token t;

	Parser(Scanner scanner) {
		this.scanner = scanner;
		t = scanner.nextToken();
	}

	/**
	 * Main method called by compiler to parser input.
	 * Checks for EOF
	 * 
	 * @throws SyntaxException
	 */
	public Program parse() throws SyntaxException {

		Program p = program();

		matchEOF();

		return p;

		}


	/**
	 * Program ::=  IDENTIFIER   ( Declaration SEMI | Statement SEMI )*   
	 * 
	 * Program is start symbol of our grammar.
	 * @return 
	 * 
	 * @throws SyntaxException
	 */
	Program program() throws SyntaxException {
		//TODO  implement this
		Token firstToken = t;
		ArrayList<ASTNode> al = new ArrayList<ASTNode>();
		Program p = null;
		match(IDENTIFIER);
		while(t.kind.equals(KW_int) || t.kind.equals(KW_boolean) || t.kind.equals(KW_image) || t.kind.equals(KW_url)
			|| t.kind.equals(KW_file) || t.kind.equals(IDENTIFIER) ){
			if(t.kind.equals(IDENTIFIER)){
				al.add(statement());
				match(SEMI);
				p = new Program(firstToken, firstToken, al);
			}
			else if(t.kind.equals(KW_int) || t.kind.equals(KW_boolean) || t.kind.equals(KW_image) || t.kind.equals(KW_url)
					|| t.kind.equals(KW_file)){
				al.add(declaration());
				match(SEMI);
				p = new Program(firstToken, firstToken, al);
			}
			else{
				throw new SyntaxException(t,"Syntax error");
			}
		}
		if(!t.kind.equals(KW_int) && !t.kind.equals(KW_boolean) && !t.kind.equals(KW_image) && !t.kind.equals(KW_url)
			&& !t.kind.equals(KW_file) && !t.kind.equals(IDENTIFIER) && !t.kind.equals(EOF)){
			p = new Program(firstToken, firstToken, al);
		}
		else if(t.kind.equals(EOF)){
			p = new Program(firstToken, firstToken, al);
		}
		
		return p;
	}

	

	/**
	 * Expression ::=  OrExpression  OP_Q  Expression OP_COLON Expression    | OrExpression
	 * 
	 * Our test cases may invoke this routine directly to support incremental development.
	 * @return 
	 * 
	 * @throws SyntaxException
	 */
	public Expression expression() throws SyntaxException {
		//TODO implement this.
		Token firstToken = t;
		Expression e0 = orExpression();
		// Expression Condition
		if(t.kind.equals(OP_Q)){
			consume();
			Expression e1 = expression(); // True Expression
			if(t.kind.equals(OP_COLON)){
				consume();
				Expression e2 = expression(); // False Expression
				e0 = new Expression_Conditional(firstToken, e0, e1, e2);
			}else{
				throw new SyntaxException(t,"Syntax Exception");
			}
		}
		;
		return e0;
		
	}
	
	Statement statement() throws SyntaxException {
		 if(t.kind.equals(IDENTIFIER)){
			 if(scanner.peek().kind.equals(OP_RARROW))
				 return imageOutStatement();
			 else if(scanner.peek().kind.equals(OP_LARROW))
				 return imageInStatement();
			 else if(scanner.peek().kind.equals(LSQUARE))
				 return assignmentStatement();
			 else if(scanner.peek().kind.equals(OP_ASSIGN))
				 return assignmentStatement();	 
		 }
		 else
			 throw new SyntaxException(t,"Syntax Error");
		return null;
	 }
	 
	
	Statement assignmentStatement() throws SyntaxException {
		Statement st = null;
		Token firstToken = t;
		LHS lhs = Lhs();
		if(t.kind.equals(OP_ASSIGN)){
			consume();
			Expression e0 = expression();
			st = new Statement_Assign(firstToken, lhs, e0);
		}
		else{
			throw new SyntaxException(t,"Syntax Exception");
		}
		return st;
	}
	
	
	
	Statement imageInStatement() throws SyntaxException {
		Statement st = null;
		Token firstToken = t;
		if(t.kind.equals(IDENTIFIER)){
			consume();
			if(t.kind.equals(OP_LARROW)){
				consume();
				Source src = source();
				st = new Statement_In(firstToken, firstToken, src);
				
			}
			else{
				throw new SyntaxException(t,"Syntax Exception");
			}
		}else{
			throw new SyntaxException(t,"Syntax EXception");
		}
		return st;
	}
	
	Statement imageOutStatement() throws SyntaxException {
		 Statement st = null;
		 Token firstToken = t;
		 match(IDENTIFIER);
		 match(OP_RARROW);
		 Sink s = sink();
		 st = new Statement_Out(firstToken, firstToken, s);
		 return st;
	 }
	 
	 Sink sink() throws SyntaxException {
		 Token firstToken = t;
		 Sink s = null;
		 if(t.kind.equals(IDENTIFIER)){
			 s = new Sink_Ident(firstToken, firstToken);
			 consume();
		 }
		 else if(t.kind.equals(KW_SCREEN)){
			 s = new Sink_SCREEN(firstToken);
			 consume();
		 }
		
		 else{
			 throw new SyntaxException(t,"Syntax Exception");
		 }
		return s;
	 }
	
		
	 Source source() throws SyntaxException {
		Source src = null;
		Token firstToken = t;
		if(t.kind.equals(STRING_LITERAL)){
			consume();
			src = new Source_StringLiteral(firstToken, firstToken.getText());
		}
		else if(t.kind.equals(IDENTIFIER)){
			consume();
			src = new Source_Ident(firstToken, firstToken);
		}
		else if(t.kind.equals(OP_AT)){
			consume();
			Expression e0 = expression();
			System.out.println("Paramnum:"+e0);
			src = new Source_CommandLineParam(firstToken, e0);
		}
		else{
			throw new SyntaxException(t,"Syntax Exception");
		}
		return src;
		
	}
	 
	 
	 
	 
	 Declaration imageDeclaration() throws SyntaxException {
		 Token firstToken = t;
		 Declaration d = null;
		 match(KW_image);
		 if(t.kind.equals(LSQUARE)){
			 match(LSQUARE);
			 Expression e0 = expression();
			 match(COMMA);
			 Expression e1 = expression();
			 match(RSQUARE);
			 Token name = t;
			 match(IDENTIFIER);
			 if(t.kind.equals(OP_LARROW)){
				 match(OP_LARROW);
				 Source s = source();
				 d = new Declaration_Image(firstToken, e0, e1, name, s);
			 }
			 else{
				 d = new Declaration_Image(firstToken, e0, e1, name, null);
			 }
		 }
		 else{
			 Token name  = t;
			 match(IDENTIFIER);
			 if(t.kind.equals(OP_LARROW)){
				 match(OP_LARROW);
				 Source s = source();
				 d = new Declaration_Image(firstToken,null,null,name,s);
			 }
			 else{
				 d = new Declaration_Image(firstToken,null,null,name,null);
			 }
		 }
		return d;
	 }
	 
	Expression FunctionName() throws SyntaxException {
		Kind k = t.kind;
		Expression e0 = null;
		if(k.equals(KW_sin) || k.equals(KW_cos) || k.equals(KW_atan)){
			e0 = new Expression_PredefinedName(t, k);
			consume();
		}
		
		else if(k.equals(KW_abs) || k.equals(KW_cart_x) || k.equals(KW_cart_y)){
			e0 = new Expression_PredefinedName(t, k);
			consume();
		}
		
		else if(k.equals(KW_polar_a) || k.equals(KW_polar_r)){
			
			e0 = new Expression_PredefinedName(t, k);
			consume();
		}
		else{
			throw new SyntaxException(t,"Syntax Exception");
		}
		return e0;
	}
	
	Expression functionApplication() throws SyntaxException {
		Expression e=null;
		Token tok=t; // Final changes ..........................
		FunctionName();
		//System.out.println("jshjs"+t);
		if(t.kind.equals(LPAREN)){
			consume();
			Expression expArg = expression();
			if(t.kind.equals(RPAREN)){
				consume();
				e=new Expression_FunctionAppWithExprArg(tok, tok.kind, expArg);
			}
			else{
				throw new SyntaxException(t,"Synatx Exception");
			}
		}// ----------------------------------------------------
		else if(t.kind.equals(LSQUARE)){
			if(t.kind.equals(LSQUARE)){
				consume();
				Index indexArg = Selector(); // PLEASE IMPLEMENT THE SELECTOR
				if(t.kind.equals(RSQUARE)){
					//System.out.println("Print here");
					consume();
					System.out.println("Token:	"+tok);
					e=new Expression_FunctionAppWithIndexArg(tok, tok.kind , indexArg);
					
					
				}
				else{
					throw new SyntaxException(t,"Syntax Exception");
				}
			}
			else{
				throw new SyntaxException(t,"Syntax Expression");
			}
		}// ----------------------------------------------------
		else {
			throw new SyntaxException(t, "Syntax Exception");
		}
		//System.out.println("Print here"+e);
		return e;
		
			
	}
	
	
	Declaration sourceSinkDeclaration() throws SyntaxException {
		Token firstToken = t;
		if(t.kind.equals(KW_url)||t.kind.equals(KW_file)){
			match(t.kind);
		}
		Token name = t;
		match(IDENTIFIER);
		match(OP_ASSIGN);
		Source s = source();
		return new Declaration_SourceSink(firstToken, firstToken, name, s);
	}
	

	
	Declaration variableDeclaration() throws SyntaxException {
		Declaration dec = null;
		Kind k =t.kind;
		Token firstToken = t;
		if(k.equals(KW_int) || k.equals(KW_boolean)){
			consume();
		}
		else {
			throw new SyntaxException(t,"Syntax EXception");
		}
		Token name = t;
		match(IDENTIFIER);
		if(t.kind.equals(OP_ASSIGN)){
			consume();
			Expression e0 = expression();
			dec = new Declaration_Variable(firstToken, firstToken, name, e0);
		}
		else{
			dec = new Declaration_Variable(firstToken, firstToken, name, null);
		}
		return dec;
	}
	
	Declaration declaration() throws SyntaxException {
		Declaration d = null;
		if(t.kind.equals(KW_int) || t.kind.equals(KW_boolean)){
			d=variableDeclaration();
		}
		else if(t.kind.equals(KW_image)){
			d=imageDeclaration();
		}
		
		else if(t.kind.equals(KW_url) || t.kind.equals(KW_file)){
			d=sourceSinkDeclaration();
		}
		else{
			throw new SyntaxException(t,"Syntax Exception");
		}
		return d;
	}
	
	
	LHS Lhs() throws SyntaxException{
		LHS lhs = null;
		Token tok=t;
		if(t.kind.equals(IDENTIFIER)){
			consume();
			if(t.kind.equals(LSQUARE)){
				consume();
				Index i = LhsSelector();
				if(t.kind.equals(RSQUARE)){
					lhs=new LHS(tok,tok,i); // Because it asks for name for first token
					consume();
				}
				else{
					throw new SyntaxException(t,"Syntax Exception");
				}
			}
			else{
				lhs=new LHS(tok,tok,null); // Because it asks for name of first token
			}
		}
		else{
			throw new SyntaxException(t,"Syntax EXception");
		}
		return lhs;
	}
	
	Expression identOrPixelSelectorExpression() throws SyntaxException{
		Expression exp=null;
		Token tok=t;
		if(t.kind.equals(IDENTIFIER)){
			consume();
			if(t.kind.equals(LSQUARE)){
				consume();
				Index i = Selector();
				if(t.kind.equals(RSQUARE)){
					consume();
					exp = new Expression_PixelSelector(tok, tok, i);
				}else{
					throw new SyntaxException(t, "Syntax Exception");
				}
			}
			else {
				exp=new Expression_Ident(tok,tok);
			}
		}
		else{
			throw new SyntaxException(t,"Syntax Exception");
		}
		return exp;
	}
	
	Expression primary() throws SyntaxException{
		Expression exp=null;
		Token tok = t;
		Kind k = t.kind;
		if(t.kind.equals(INTEGER_LITERAL)){
			exp = new Expression_IntLit(tok, Integer.parseInt(t.getText()));
			consume();
			
		}
		else if(t.kind.equals(BOOLEAN_LITERAL)){
			boolean b = Boolean.valueOf(t.getText());
			consume();
			exp = new Expression_BooleanLit(tok, b);
		}
		else if(t.kind.equals(LPAREN)){
			consume();
			exp=expression();
			if(t.kind.equals(RPAREN)){
				consume();
			}
			else {
				throw new SyntaxException(t,"Syntax Exception");
			}
		}
		else if(k.equals(KW_sin) || k.equals(KW_cos) || k.equals(KW_atan) || k.equals(KW_abs)){
			return functionApplication();
		}
		
		else if(k.equals(KW_cart_x) || k.equals(KW_cart_y) || k.equals(KW_polar_a) || k.equals(KW_polar_r)){
			return functionApplication();
		}
		
		else {
			throw new SyntaxException(t,"Syntax Exception");
		}
		return exp;
		
	}
	
	Expression unaryExpressionNotPlusMinus() throws SyntaxException {
		Expression exp = null;
		Kind k = t.kind;
		Token tok = t;
		if(t.kind.equals(OP_EXCL)){
			Token op=t;
			consume();
			Expression e = unaryExpression();
			exp = new Expression_Unary(tok, op, e);
		}
		
		else if(t.kind.equals(INTEGER_LITERAL) || t.kind.equals(LPAREN) || t.kind.equals(BOOLEAN_LITERAL)){
			return primary();
		}
		
		else if(k.equals(KW_sin) || k.equals(KW_cos) || k.equals(KW_atan) || k.equals(KW_abs)){
			return primary();
		}
		
		else if(k.equals(KW_cart_x) || k.equals(KW_cart_y) || k.equals(KW_polar_a) || k.equals(KW_polar_r)){
			return primary();
		}
		
		else if(k.equals(IDENTIFIER)){
			return identOrPixelSelectorExpression();
		}
		
		else if(k.equals(KW_x) || k.equals(KW_y) || k.equals(KW_r) || k.equals(KW_a)){
			Expression e = new Expression_PredefinedName(tok, t.kind);
			consume();
			return e;
		}
		
		else if(k.equals(KW_X) || k.equals(KW_Y) || k.equals(KW_Z) || k.equals(KW_A) || k.equals(KW_R) || k.equals(KW_DEF_X) || k.equals(KW_DEF_Y)){
			Expression e = new Expression_PredefinedName(tok, t.kind);
			consume();
			return e;
		}
		
		else {
			throw new SyntaxException(t,"Syntax Exception");
		}

		return exp;		
	}
	
	// Make sure this is correct
	Expression unaryExpression() throws SyntaxException {
		Expression exp = null;
		Token tok = t;
		if(t.kind.equals(OP_PLUS)){
			Token op = t;
			consume();
			Expression e = unaryExpression();
			exp = new Expression_Unary(tok, op, e);
		}
		else if(t.kind.equals(OP_MINUS)){
			Token op = t;
			consume();
			Expression e = unaryExpression();
			exp = new Expression_Unary(tok, op, e);
		}
		else{
			 return unaryExpressionNotPlusMinus();
		}
		return exp;
		
	}
	// Check what will happen in else if there is only unary expression and nothing after it
	Expression multExpression() throws SyntaxException {
		Token tok = t;
		Expression e0 = unaryExpression();
		if(t.kind.equals(OP_TIMES) || t.kind.equals(OP_DIV) || t.kind.equals(OP_MOD)){
			while(t.kind.equals(OP_TIMES) || t.kind.equals(OP_DIV) || t.kind.equals(OP_MOD)){
				Token op = t; 
				consume();
				Expression e1 = unaryExpression();
				e0 = new Expression_Binary(tok, e0, op, e1);
			}
		}
		else{

		}
		return e0;
		
	}
	
	Expression addExpression() throws SyntaxException {
		Token tok = t;
		Expression e0 = multExpression();
		if(t.kind.equals(OP_PLUS) || t.kind.equals(OP_MINUS)){
			while(t.kind.equals(OP_PLUS) || t.kind.equals(OP_MINUS)){
				Token op= t;
				consume();
				Expression e1 = multExpression();
				e0 = new Expression_Binary(tok, e0, op, e1);
			}
		}
		else{
			
		}
		return e0;
		
	}
	
	Expression relExpression() throws SyntaxException {
		Token tok = t;
		Expression e0 = addExpression();
		if(t.kind.equals(OP_LT) || t.kind.equals(OP_GT) || t.kind.equals(OP_LE) || t.kind.equals(OP_GE)){
			while(t.kind.equals(OP_LT) || t.kind.equals(OP_GT) || t.kind.equals(OP_LE) || t.kind.equals(OP_GE)){
				Token op = t;
				consume();
				Expression e1 = addExpression();
				e0 = new Expression_Binary(tok, e0, op, e1);
			}
		}
		return e0;
	}
	
	Expression eqExpression() throws SyntaxException {
		Token tok =t;
		Expression e0 = relExpression();
		if(t.kind.equals(OP_EQ) || t.kind.equals(OP_NEQ)){
			while(t.kind.equals(OP_EQ) || t.kind.equals(OP_NEQ)){
				Token op = t;
				consume();
				Expression e1 = relExpression();
				e0 = new Expression_Binary(tok, e0, op, e1);
			}	
		}
		return e0;
	}
	
	Expression andExpression() throws SyntaxException {
		Token tok = t;
		Expression e0 = eqExpression();
		if(t.kind.equals(OP_AND)){
			while(t.kind.equals(OP_AND)){
				Token op = t;
				consume();
				Expression e1 = eqExpression();
				e0 = new Expression_Binary(tok, e0, op, e1);
			}
		}
		return e0;
	}
	
	Expression orExpression() throws SyntaxException {
		Token firstToken = t;
		Expression e0 = andExpression();
		if(t.kind.equals(OP_OR)){
			while(t.kind.equals(OP_OR)){
				Token op = t;
				consume();
				Expression e1 = andExpression();
				e0 = new Expression_Binary(firstToken, e0, op, e1);
			}
		}
		return e0;
	}
	
	Index Selector() throws SyntaxException {
		// TODO Auto-generated method stub
		Index i=null;
		Token tok =t;
		Expression e1 = expression();
//		System.out.println("EXprs"+e1);
//		System.out.println("Token help"+e1);
		match(COMMA);
		Expression e2 = expression();
		i = new Index(t,e1,e2);
		return i;
		
	}

	Index LhsSelector() throws SyntaxException {
		Index i =null;
		Kind k =t.kind;

		if(k.equals(LSQUARE)){
			consume();
			if(t.kind.equals(KW_x)){
				i=XySelector();
				if(t.kind.equals(RSQUARE)){
					consume();
				}
				else{
					throw new SyntaxException(t,"Syntax exception");
				}
			}
			else if(t.kind.equals(KW_r)){
				i=RaSelector();
				if(t.kind.equals(RSQUARE)){
					consume();
				}
				else{
					throw new SyntaxException(t,"Syntax exception");
				}
			}
			else {
				throw new SyntaxException(t,"Syntax exception");
			}
		}
		else{
			throw new SyntaxException(t,"SyntaxException");
		}
		return i;
	}
	
	Index XySelector() throws SyntaxException {
		Index i=null;
		Token tok = t;
		//Expression e1=null;
		//Expression e2=null;
		if(t.kind.equals(KW_x)){
			Expression_PredefinedName e1=new Expression_PredefinedName(tok,t.kind);
			consume();
			if(t.kind.equals(COMMA)){
				//tok=t;
				consume();
				if(t.kind.equals(KW_y)){
					Expression_PredefinedName e2=new Expression_PredefinedName(tok,t.kind);
					consume();
					i=new Index(tok,e1,e2);
				}
				else{
					throw new SyntaxException(t,"SyntaxException");
				}
			}
			else{
				throw new SyntaxException(t,"SyntaxException");
			}
		}
		else{
			throw new SyntaxException(t,"SyntaxException");
		}
		
		
		
		return i;
	}
	
	Index RaSelector() throws SyntaxException {
		Index i=null;
		Token tok=t;

		if(t.kind.equals(KW_r)){
			Expression_PredefinedName e1=new Expression_PredefinedName(tok,t.kind);
			consume();
			if(t.kind.equals(COMMA)){
				consume();
				if(t.kind.equals(KW_a)){
					Expression_PredefinedName e2=new Expression_PredefinedName(tok,t.kind);
					consume();
					i=new Index(tok,e1,e2);
				}
				else{
					throw new SyntaxException(t,"SyntaxException");
				}
			}
			else{
				throw new SyntaxException(t,"SyntaxException");
			}
		}
		else{
			throw new SyntaxException(t,"SyntaxException");
		}
		return i;
	}
	
	private Token consume() throws SyntaxException {
		Token tmp = t;
		t = scanner.nextToken();
		return tmp;
	}
	
	private Token match(Kind kind) throws SyntaxException {
		if (t.kind.equals(kind)) {
			return consume();
		}
		else{
		throw new SyntaxException(t, "Syntax exception");
		}
	}



	/**
	 * Only for check at end of program. Does not "consume" EOF so no attempt to get
	 * nonexistent next Token.
	 * 
	 * @return
	 * @throws SyntaxException
	 */
	private Token matchEOF() throws SyntaxException {
		if (t.kind == EOF) {
			return t;
		}
		String message =  "Expected EOL at " + t.line + ":" + t.pos_in_line;
		throw new SyntaxException(t, message);
	}
}
