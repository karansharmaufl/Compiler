package cop5556fa17;

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556fa17.Scanner.LexicalException;
import cop5556fa17.AST.*;

import cop5556fa17.Parser.SyntaxException;

import static cop5556fa17.Scanner.Kind.*;

public class ParserTest {

	// set Junit to be able to catch exceptions
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	// To make it easy to print objects and turn this output on and off
	static final boolean doPrint = true;
	private void show(Object input) {
		if (doPrint) {
			System.out.println(input.toString());
		}
	}

	/**
	 * Simple test case with an empty program. This test expects an exception
	 * because all legal programs must have at least an identifier
	 * 
	 * @throws LexicalException
	 * @throws SyntaxException
	 */
	@Test
	public void testEmpty1() throws LexicalException, SyntaxException {
		String input = ""; // The input is the empty string. Parsing should fail
		show(input); // Display the input
		Scanner scanner = new Scanner(input).scan(); // Create a Scanner and
														// initialize it
		show(scanner); // Display the tokens
		Parser parser = new Parser(scanner); //Create a parser
		thrown.expect(SyntaxException.class);
		try {
			ASTNode ast = parser.parse();; //Parse the program, which should throw an exception
		} catch (SyntaxException e) {
			show(e);  //catch the exception and show it
			throw e;  //rethrow for Junit
		}
	}


	@Test
	public void testNameOnly2() throws LexicalException, SyntaxException {
		String input = "prog";  //Legal program with only a name
		show(input);            //display input
		Scanner scanner = new Scanner(input).scan();   //Create scanner and create token list
		show(scanner);    //display the tokens
		Parser parser = new Parser(scanner);   //create parser
		Program ast = parser.parse();          //parse program and get AST
		show(ast);                             //Display the AST
		//System.out.println("I am here");
		assertEquals(ast.name, "prog");        //Check the name field in the Program object
		assertTrue(ast.decsAndStatements.isEmpty());   //Check the decsAndStatements list in the Program object.  It should be empty.
		System.out.println("I am here:"+ast.decsAndStatements.isEmpty());
	}

	@Test
	public void testDec12() throws LexicalException, SyntaxException {
		String input = "prog int k; karan=d&e;";
		show(input);
		Scanner scanner = new Scanner(input).scan(); 
		show(scanner); 
		Parser parser = new Parser(scanner);
		//System.out.println("Print:");
		Program ast = parser.parse();
		//System.out.println("Print:"+ast);
		show(ast);
//		assertEquals(ast.name, "prog"); 
//		//This should have one Declaration_Variable object, which is at position 0 in the decsAndStatements list
//		Declaration_Variable dec = (Declaration_Variable) ast.decsAndStatements
//				.get(0);  
//		assertEquals(KW_int, dec.type.kind);
//		assertEquals("k", dec.name);
//		assertNull(dec.e);
	}
	
	@Test
	public void indexXySelector() throws LexicalException, SyntaxException {
		String input = "x,y";
		show(input);
		Scanner scanner = new Scanner(input).scan(); 
		show(scanner); 
		Index i = new Parser(scanner).XySelector();
		//System.out.println("Print"+i);
		show(i);
		assertEquals(KW_x,i.firstToken.kind);
		assertEquals(Expression_PredefinedName.class,i.e0.getClass());
		assertEquals(Expression_PredefinedName.class,i.e1.getClass());
		//assertsEquals();

	}
	
	@Test
	public void indexRaSelector() throws LexicalException, SyntaxException {
		String input = "r,A";
		show(input);
		Scanner scanner = new Scanner(input).scan(); 
		show(scanner); 
		Index i = new Parser(scanner).RaSelector();
		//System.out.println("Print"+i);
		show(i);
		assertEquals(KW_r,i.firstToken.kind);
		assertEquals(Expression_PredefinedName.class,i.e0.getClass());
		assertEquals(Expression_PredefinedName.class,i.e1.getClass());
		//assertsEquals();

	}
	
	@Test
	public void functionAppIndexArg() throws LexicalException, SyntaxException {
		String input =  "++--x?x?R:a:abs[A,R]";
		show(input);
		Scanner scanner = new Scanner(input).scan(); 
		show(scanner); 
		Expression i = new Parser(scanner).expression();
		System.out.println("Print"+i.firstToken);
		show(i); 
	}
	
	@Test// To be completed
	public void functionAppExprArg() throws LexicalException, SyntaxException {
		String input = "cos(l)";
		show(input);
		Scanner scanner = new Scanner(input).scan(); 
		show(scanner); 
		Expression i = new Parser(scanner).functionApplication();
		System.out.println("Print"+i.firstToken);
		show(i);
//		assertEquals(KW_r,i.firstToken.kind);
//		assertEquals(Expression_PredefinedName.class,i.e0.getClass());
//		assertEquals(Expression_PredefinedName.class,i.e1.getClass());
		//assertsEquals();

	}
	
	@Test // Check - To be completed
	public void LHS1() throws LexicalException, SyntaxException {
		String input = "karan";
		show(input);
		Scanner scanner = new Scanner(input).scan(); 
		show(scanner); 
		LHS i = new Parser(scanner).Lhs();
		show(i);
//		assertEquals(KW_r,i.firstToken.kind);
//		assertEquals(Expression_PredefinedName.class,i.e0.getClass());
//		assertEquals(Expression_PredefinedName.class,i.e1.getClass());
		//assertsEquals();

	}

	
	@Test
	public void LHS2() throws LexicalException, SyntaxException {
		String input = "karan[[x,y]]";
		show(input);
		Scanner scanner = new Scanner(input).scan(); 
		show(scanner); 
		LHS i = new Parser(scanner).Lhs();
		show(i);
		assertEquals(IDENTIFIER,i.firstToken.kind);
		assertEquals("karan",i.name);
		assertEquals(Expression_PredefinedName.class, i.index.e0.getClass());
		assertEquals(Expression_PredefinedName.class, i.index.e1.getClass());

	}
	
	@Test  // To be completed
	public void Expression_PixelSelector() throws LexicalException, SyntaxException {
		String input = "karan[x,y]";
		show(input);
		Scanner scanner = new Scanner(input).scan(); 
		show(scanner); 
		Expression_PixelSelector i = (cop5556fa17.AST.Expression_PixelSelector) new Parser(scanner).identOrPixelSelectorExpression();
		show(i);
		assertEquals(IDENTIFIER,i.firstToken.kind);
		assertEquals("karan",i.name);
		assertEquals(Expression_PredefinedName.class, i.index.e0.getClass());
		assertEquals(Expression_PredefinedName.class, i.index.e1.getClass());

	}
	
	@Test  // To be completed
	public void booleanLiteral() throws LexicalException, SyntaxException {
		String input = "false";
		show(input);
		Scanner scanner = new Scanner(input).scan(); 
		show(scanner); 
		Expression_BooleanLit i = (Expression_BooleanLit) new Parser(scanner).primary();
		show(i);
		assertEquals(BOOLEAN_LITERAL,i.firstToken.kind);
		assertEquals(false,i.value);
		assertEquals(Expression_BooleanLit.class, i.getClass());
		//assertEquals(Expression_PredefinedName.class, i.index.e1.getClass());

	}
	
	@Test  // To be completed
	public void integerLiteral() throws LexicalException, SyntaxException {
		String input = "1234";
		show(input);
		Scanner scanner = new Scanner(input).scan(); 
		show(scanner); 
		Expression_IntLit i = (Expression_IntLit) new Parser(scanner).primary();
		show(i);
		assertEquals(INTEGER_LITERAL,i.firstToken.kind);
		assertEquals(1234,i.value);
		assertEquals(Expression_IntLit.class, i.getClass());
		//assertEquals(Expression_PredefinedName.class, i.index.e1.getClass());

	}
	
	@Test  // To be completed
	public void unaryExpressionNotPlusNotMinusl() throws LexicalException, SyntaxException {
		String input = "!12";
		show(input);
		Scanner scanner = new Scanner(input).scan(); 
		show(scanner); 
		Expression_Unary i = (Expression_Unary) new Parser(scanner).unaryExpressionNotPlusMinus();
		show(i);
		assertEquals(OP_EXCL,i.firstToken.kind);
//		assertEquals(Expression_IntLit,i.e);
//		assertEquals(Expression_IntLit.class, i.getClass());
//		//assertEquals(Expression_PredefinedName.class, i.index.e1.getClass());

	}
	
	@Test  // To be completed
	public void unaryExpression() throws LexicalException, SyntaxException {
		String input = "!x";
		show(input);
		Scanner scanner = new Scanner(input).scan(); 
		show(scanner); 
		Expression_Unary i = (Expression_Unary) new Parser(scanner).unaryExpressionNotPlusMinus();
		show(i);
		assertEquals(OP_EXCL,i.firstToken.kind);
//		assertEquals(Expression_IntLit,i.e);
//		assertEquals(Expression_IntLit.class, i.getClass());
//		//assertEquals(Expression_PredefinedName.class, i.index.e1.getClass());

	}
	

	@Test  // To be completed
	public void multExpression() throws LexicalException, SyntaxException {
		String input = "karan*sharma*christina";
		show(input);
		Scanner scanner = new Scanner(input).scan(); 
		show(scanner); 
		Expression_Binary i = (Expression_Binary) new Parser(scanner).multExpression();
		show(i);
		System.out.println("First Token:"+ i.firstToken);
//		assertEquals(OP_EXCL,i.firstToken.kind);
//		assertEquals(Expression_IntLit,i.e);
//		assertEquals(Expression_IntLit.class, i.getClass());
//		//assertEquals(Expression_PredefinedName.class, i.index.e1.getClass());

	}
	
	@Test  // To be completed
	public void addExpression() throws LexicalException, SyntaxException {
		String input = "karan*sharma+christina*vallejo";
		show(input);
		Scanner scanner = new Scanner(input).scan(); 
		show(scanner); 
		Expression_Binary i = (Expression_Binary) new Parser(scanner).addExpression();
		show(i);
		System.out.println("First Token:"+ i.firstToken);
		assertEquals(IDENTIFIER,i.firstToken.kind);
//		assertEquals(Expression_IntLit,i.e);
//		assertEquals(Expression_IntLit.class, i.getClass());
//		//assertEquals(Expression_PredefinedName.class, i.index.e1.getClass());

	}

	@Test  // To be completed
	public void relExpression() throws LexicalException, SyntaxException {
		String input = "karan>christina";
		show(input);
		Scanner scanner = new Scanner(input).scan(); 
		show(scanner); 
		Expression_Binary i = (Expression_Binary) new Parser(scanner).relExpression();
		show(i);
		System.out.println("First Token:"+ i.firstToken);
		assertEquals(IDENTIFIER,i.firstToken.kind);
//		assertEquals(Expression_IntLit,i.e);
//		assertEquals(Expression_IntLit.class, i.getClass());
//		//assertEquals(Expression_PredefinedName.class, i.index.e1.getClass());

	}
	
	@Test  // To be completed
	public void eqExpression() throws LexicalException, SyntaxException {
		String input = "d!=b";
		show(input);
		Scanner scanner = new Scanner(input).scan(); 
		show(scanner); 
		Expression_Binary i = (Expression_Binary) new Parser(scanner).eqExpression();
		show(i);
		System.out.println("First Token:"+ i.firstToken);
		assertEquals(IDENTIFIER,i.firstToken.kind);
//		assertEquals(Expression_IntLit,i.e);
//		assertEquals(Expression_IntLit.class, i.getClass());
//		//assertEquals(Expression_PredefinedName.class, i.index.e1.getClass());

	}
	
	@Test  // To be completed
	public void andExpression() throws LexicalException, SyntaxException {
		String input = "d&b";
		show(input);
		Scanner scanner = new Scanner(input).scan(); 
		show(scanner); 
		Expression_Binary i = (Expression_Binary) new Parser(scanner).andExpression();
		show(i);
		System.out.println("First Token:"+ i.firstToken);
		assertEquals(IDENTIFIER,i.firstToken.kind);
//		assertEquals(Expression_IntLit,i.e);
//		assertEquals(Expression_IntLit.class, i.getClass());
//		//assertEquals(Expression_PredefinedName.class, i.index.e1.getClass());

	}
	
	@Test  // To be completed
	public void orExpression() throws LexicalException, SyntaxException {
		String input = "d | b";
		show(input);
		Scanner scanner = new Scanner(input).scan(); 
		show(scanner); 
		Expression_Binary i = (Expression_Binary) new Parser(scanner).orExpression();
		show(i);
		System.out.println("First Token:"+ i.firstToken);
		assertEquals(IDENTIFIER,i.firstToken.kind);
	}
	
	@Test  // To be completed
	public void statementAssign1() throws LexicalException, SyntaxException {
		String input = "p=q";
		show(input);
		Scanner scanner = new Scanner(input).scan(); 
		show(scanner); 
		Statement_Assign st = (Statement_Assign) new Parser(scanner).assignmentStatement();
		show(st);
		System.out.println(st.lhs);
		

	}
	
	@Test  // To be completed
	public void statementAssign2() throws LexicalException, SyntaxException {
		String input = "karan[[x,y]]=123";
		show(input);
		Scanner scanner = new Scanner(input).scan(); 
		show(scanner); 
		Statement_Assign st = (Statement_Assign) new Parser(scanner).statement();
		show(st);
		//System.out.println(st.lhs);
		

	}
	
	@Test  // To be completed
	public void statementAssign3() throws LexicalException, SyntaxException {
		String input = "karan=karan";
		show(input);
		Scanner scanner = new Scanner(input).scan(); 
		show(scanner); 
		//Statement_Assign st = (Statement_Assign) new Parser(scanner).assignmentStatement();
		Statement st = (Statement_Assign) new Parser(scanner).statement();
		show(st);
		//System.out.println(st.lhs);
		

	}
	
	@Test  // To be completed
	public void source1() throws LexicalException, SyntaxException {
		String input = "\"karan\"";
		show(input);
		Scanner scanner = new Scanner(input).scan(); 
		show(scanner); 
		Source_StringLiteral src = (Source_StringLiteral) new Parser(scanner).source();
		show(src);
		//System.out.println(st.lhs);
		

	}
	
	@Test  // To be completed
	public void source2() throws LexicalException, SyntaxException {
		String input = "@p*q";
		show(input);
		Scanner scanner = new Scanner(input).scan(); 
		show(scanner); 
		Source_CommandLineParam src = (Source_CommandLineParam) new Parser(scanner).source();
		show(src);
		//System.out.println(st.lhs);
		

	}
	
	@Test  // To be completed
	public void imageInStatement() throws LexicalException, SyntaxException {
		String input = "karan <- \"christina\""; 
		show(input);
		Scanner scanner = new Scanner(input).scan(); 
		show(scanner); 
		Statement_In stin = (Statement_In) new Parser(scanner).statement();
		show(stin);
		//System.out.println(st.lhs);
		

	}
	
	@Test  // To be completed
	public void imageOutStatement() throws LexicalException, SyntaxException {
		String input = "karan -> SCREEN"; 
		show(input);
		Scanner scanner = new Scanner(input).scan(); 
		show(scanner); 
		Statement_Out stin = (Statement_Out) new Parser(scanner).imageOutStatement();
		show(stin);
		//System.out.println(st.lhs);
		

	}
	
	@Test  // To be completed
	public void statement1() throws LexicalException, SyntaxException {
		String input = "karan=p&q"; 
		show(input);
		Scanner scanner = new Scanner(input).scan(); 
		show(scanner); 
		Statement stin = (Statement) new Parser(scanner).statement();
		show(stin);
		//System.out.println(st.lhs);
		

	}

	
	@Test  // To be completed
	public void imageDeclaration1() throws LexicalException, SyntaxException {
		String input = "image[c&b,d&e]karan<-christina"; 
		show(input);
		Scanner scanner = new Scanner(input).scan(); 
		show(scanner); 
		Declaration d = (Declaration) new Parser(scanner).imageDeclaration();
		show(d);
		//assertEquals("KW_image",d.firstToken);
		//System.out.println(st.lhs);
		

	}
	
	@Test  // To be completed
	public void imageDeclaration2() throws LexicalException, SyntaxException {
		String input = "image Karan<-christina"; 
		show(input);
		Scanner scanner = new Scanner(input).scan(); 
		show(scanner); 
		Declaration d = (Declaration) new Parser(scanner).imageDeclaration();
		show(d);
		//assertEquals("KW_image",d.firstToken);
		//System.out.println(st.lhs);
		

	}
	
	@Test  // To be completed
	public void imageDeclaration3() throws LexicalException, SyntaxException {
		String input = "image Karan"; 
		show(input);
		Scanner scanner = new Scanner(input).scan(); 
		show(scanner); 
		Declaration d = (Declaration) new Parser(scanner).imageDeclaration();
		show(d);
		//assertEquals("KW_image",d.firstToken);
		//System.out.println(st.lhs);
		

	}
	
	@Test  // To be completed
	public void imageDeclaration4() throws LexicalException, SyntaxException {
		String input = "image [c&d,e&f]Karan"; 
		show(input);
		Scanner scanner = new Scanner(input).scan(); 
		show(scanner); 
		Declaration d = (Declaration) new Parser(scanner).imageDeclaration();
		show(d);
		//assertEquals("KW_image",d.firstToken);
		//System.out.println(st.lhs);
		

	}
	
	@Test  // To be completed
	public void sourecSinkDeclaration() throws LexicalException, SyntaxException {
		String input = "url wwwdotfacebookdotcom = @c&d"; 
		show(input);
		Scanner scanner = new Scanner(input).scan(); 
		show(scanner); 
		Declaration d = (Declaration) new Parser(scanner).declaration();
		show(d);
		//assertEquals("KW_image",d.firstToken);
		//System.out.println(st.lhs);
		

	}
	
	@Test  // To be completed
	public void variableDeclaration1() throws LexicalException, SyntaxException {
		String input = "int count = d&e"; 
		show(input);
		Scanner scanner = new Scanner(input).scan(); 
		show(scanner); 
		Declaration d = (Declaration) new Parser(scanner).variableDeclaration();
		show(d);
		//assertEquals("KW_image",d.firstToken);
		//System.out.println(st.lhs);
		

	}

	
	@Test  // To be completed
	public void variableDeclaration2() throws LexicalException, SyntaxException {
		String input = "int count"; 
		show(input);
		Scanner scanner = new Scanner(input).scan(); 
		show(scanner); 
		Declaration d = (Declaration) new Parser(scanner).declaration();
		show(d);
		//assertEquals("KW_image",d.firstToken);
		//System.out.println(st.lhs);
		

	}
	
	

	

	@Test
	public void testDec1() throws LexicalException, SyntaxException {
		String input = "prog int k;";
		show(input);
		Scanner scanner = new Scanner(input).scan(); 
		show(scanner); 
		Parser parser = new Parser(scanner);
		Program ast = parser.parse();
		show(ast);
		assertEquals(ast.name, "prog"); 
		//This should have one Declaration_Variable object, which is at position 0 in the decsAndStatements list
		Declaration_Variable dec = (Declaration_Variable) ast.decsAndStatements
				.get(0);  
		assertEquals(KW_int, dec.type.kind);
		assertEquals("k", dec.name);
		assertNull(dec.e);
	}
	
	@Test
	public void testcase1() throws SyntaxException, LexicalException {
		String input = "2";
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		thrown.expect(SyntaxException.class);
		try {
			parser.program();  //Parse the program
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		} 
	}
	@Test
	public void testcase2() throws SyntaxException, LexicalException {
		String input = "a bcd";
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		thrown.expect(SyntaxException.class);
		try {
			parser.program();  //Parse the program
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		} 
	}
	
	@Test
	public void testcase3() throws SyntaxException, LexicalException {
		String input = "cart_x cart_y";
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		thrown.expect(SyntaxException.class);
		try {
			parser.program();  //Parse the program
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		} 
	}
	
	@Test
	public void testcase4() throws SyntaxException, LexicalException {
		String input = "prog int 2";
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		thrown.expect(SyntaxException.class);
		try {
			parser.program();  //Parse the program
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		} 
	}
	
	@Test
	public void testcase5() throws SyntaxException, LexicalException {
		String input = "prog image[filepng,png] imageName <- imagepng"; //Error as there is not semicolon for ending the statement
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		//Parser.program();
		thrown.expect(SyntaxException.class);
		try {
			parser.program();  //Parse the program
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		} 
	}
	
	@Test
	public void testcase6() throws SyntaxException, LexicalException {
		String input = "imageDeclaration image[\"abcd\"] "; //Should fail for image[“”]
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		thrown.expect(SyntaxException.class);
		try {
			parser.program();  //Parse the program
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		} 
	}
	
	@Test
	public void testcase7() throws SyntaxException, LexicalException {
		String input = "prog image[filepng,png] imageName <- imagepng; \n boolean ab=true;"; 
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		Program ast=parser.program();
		show(ast);
		assertEquals("prog",ast.name);
		// First Declaration statement
		Declaration_Image dec = (Declaration_Image) ast.decsAndStatements.get(0);  
		assertEquals(KW_image, dec.firstToken.kind);
		assertEquals("imageName", dec.name);
		Expression_Ident ei=(Expression_Ident)dec.xSize;
		assertEquals("filepng",ei.name);
		ei=(Expression_Ident)dec.ySize;
		assertEquals("png",ei.name);
		Source_Ident s=(Source_Ident) dec.source;
	    assertEquals("imagepng",s.name);
		// Second Declaration statement
	    Declaration_Variable dec2 = (Declaration_Variable) ast.decsAndStatements.get(1);  
		assertEquals("ab", dec2.name);
		assertEquals(KW_boolean, dec2.firstToken.kind);
		Expression_BooleanLit ebi=(Expression_BooleanLit)dec2.e;
		assertEquals(true,ebi.value);		
	}
	
	@Test
	public void testcase8() throws SyntaxException, LexicalException {
		String input = "prog image[filepng,jpg] imageName;"; 
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		Program ast=parser.program();
		show(ast);
		assertEquals("prog",ast.name);
		Declaration_Image dec1 = (Declaration_Image) ast.decsAndStatements.get(0); 
		assertEquals(dec1.name,"imageName");
		Expression_Ident exi=(Expression_Ident)dec1.xSize;
		Expression_Ident eyi=(Expression_Ident)dec1.ySize;
		assertEquals(exi.name,"filepng");
		assertEquals(eyi.name,"jpg");
		assertNull(dec1.source);
	}
	
	@Test
	public void testcase10() throws SyntaxException, LexicalException {
		String input = "prog @expr k=12;"; 
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		Program ast=parser.program();  //Parse the program
		show(ast);
		assertEquals(ast.name,"prog");
		assertEquals(ast.decsAndStatements.size(),0);
	}
	
	@Test
	public void testcase10parse() throws SyntaxException, LexicalException {
		String input = "prog @expr k=12;"; 
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		thrown.expect(SyntaxException.class);
		try {
			ASTNode ast=parser.parse();  //Parse the program
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		} 
	}
	
	@Test
	public void testcase11() throws SyntaxException, LexicalException {
		String input = "prog \"abcded\" boolean a=true;"; 
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		Program ast=parser.program();  //Parse the program
		show(ast);
		assertEquals(ast.name,"prog");
		assertEquals(ast.decsAndStatements.size(),0);
	}
	
	@Test
	public void testcase11_parse() throws SyntaxException, LexicalException {
		String input = "prog \"abcded\" boolean a=true;"; 
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		thrown.expect(SyntaxException.class);
		try {
			ASTNode ast=parser.parse();  //Parse the program
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		} 
	}
	

	@Test
	public void testcase12() throws SyntaxException, LexicalException {
		String input = "isBoolean boolean ab=true; boolean cd==true; abcd=true ? return true: return false;"; //Should fail for ==
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		thrown.expect(SyntaxException.class);
		try {
			Program ast=parser.program();  //Parse the program
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		} 
	}
	
	@Test
	public void testcase13() throws SyntaxException, LexicalException {
		String input = "isBoolean boolean ab=true; boolean cd==true; abcd=true ? return true: return false;"; //Should fail for =
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		thrown.expect(SyntaxException.class);
		try {
			Program ast=parser.program();  //Parse the program
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		} 
	}
	
	@Test
	public void testcase14() throws SyntaxException, LexicalException {
		String input = "isUrl url filepng=\"abcd\"; \n @expr=12; url awesome=@expr; \n url filepng=abcdefg"; 
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		Program ast=parser.program();  //Parse the program
		show(ast);
		assertEquals(ast.name,"isUrl");
		assertEquals(ast.decsAndStatements.size(),1);
		Declaration_SourceSink dss=(Declaration_SourceSink)ast.decsAndStatements.get(0);
		assertEquals(dss.name,"filepng");
		assertEquals(dss.type,KW_url);
		Source_StringLiteral s=(Source_StringLiteral)dss.source;
		assertEquals(s.fileOrUrl,"abcd");
	}
	
	@Test
	public void testcase14_parse() throws SyntaxException, LexicalException {
		String input = "isUrl url filepng=\"abcd\"; \n @expr=12; url awesome=@expr; \n url filepng=abcdefg"; 
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		thrown.expect(SyntaxException.class);
		try {
			ASTNode ast=parser.parse();  //Parse the program
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}  
	}
	
	@Test
	public void testcase15() throws SyntaxException, LexicalException {
		String input = "isUrl url filepng=\"abcd\" \n @expr=12; url awesome=@expr; \n url filepng=abcdefg"; //Should fail for ; in line one
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		thrown.expect(SyntaxException.class);
		try {
			Program ast=parser.program();  //Parse the program
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}  
	}
	
	@Test
	public void testcase16() throws SyntaxException, LexicalException {
		String input = "isFile file filepng=\"abcd\"; \n @expr=12; url filepng=@expr; \n url filepng=abcdefg"; 
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		Program ast=parser.program();  //Parse the program
		show(ast);
		assertEquals(ast.name,"isFile");
		assertEquals(ast.decsAndStatements.size(),1);
		assertEquals(ast.firstToken.kind,IDENTIFIER);
		
		// Declaration Statements
		Declaration_SourceSink ds=(Declaration_SourceSink)ast.decsAndStatements.get(0);
		assertEquals(ds.type,KW_file);
		assertEquals(ds.name,"filepng");
		Source_StringLiteral s=(Source_StringLiteral)ds.source;
		assertEquals(s.fileOrUrl,"abcd");
		//assertEquals(ast.)
	}
	
	@Test
	public void testcase16_parse() throws SyntaxException, LexicalException {
		String input = "isFile file filepng=\"abcd\"; \n @expr=12; url filepng=@expr; \n url filepng=abcdefg"; 
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		thrown.expect(SyntaxException.class);
		try {
			ASTNode ast=parser.parse();  //Parse the program
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}  
	}
	
	
	
	@Test
	public void testcase18() throws SyntaxException, LexicalException {
		String input =  "isurl url urlname;";  //Should fail for url as url can only be initalised
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		thrown.expect(SyntaxException.class);
		try {
			Program ast=parser.program();  //Parse the program
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}  
	}
	
	@Test
	public void testcase19() throws SyntaxException, LexicalException {
		String input =  "declaration int xyz;\n boolean zya;\n image imagename;";  
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		Program ast=parser.program();  //Parse the program
		show(ast);
		assertEquals(ast.name,"declaration");
		assertEquals(ast.firstToken.kind,IDENTIFIER);
		
		//Declaration statements start
		Declaration_Variable dv1=(Declaration_Variable)ast.decsAndStatements.get(0);
		assertEquals(dv1.name,"xyz");
		assertEquals(dv1.type.kind,KW_int);
		assertNull(dv1.e);
		
		Declaration_Variable dv2=(Declaration_Variable)ast.decsAndStatements.get(1);
		assertEquals(dv2.name,"zya");
		assertEquals(dv2.type.kind,KW_boolean);
		assertNull(dv2.e);
		
		Declaration_Image dv3=(Declaration_Image)ast.decsAndStatements.get(2);	
		assertEquals(dv3.name,"imagename");
		assertNull(dv3.source);
		assertNull(dv3.xSize);
		assertNull(dv3.ySize);
		
		//Declaration statement end
	}
	
	@Test
	public void testcase20() throws SyntaxException, LexicalException {
		String input =  "imageProgram image imageName;"
				+ "\n imageName->abcdpng; "
				+ "\n imageName -> SCREEN; "
				+ "\n imageName <- \"awesome\";"
				+ "\n imageName <- @express; \n"
				+ "\n imageName <- abcdpng;";  // Image related Test cases
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		Program ast=parser.program();  //Parse the program
		show(ast);
		assertEquals(ast.name,"imageProgram");
		
		//Declaration statement start
		Declaration_Image dv1=(Declaration_Image)ast.decsAndStatements.get(0);
		assertEquals(dv1.name,"imageName");
		assertNull(dv1.xSize);
		assertNull(dv1.ySize);
		assertNull(dv1.source);
		
		Statement_Out dv2=(Statement_Out)ast.decsAndStatements.get(1);
		assertEquals(dv2.name,"imageName");
		Sink_Ident si2=(Sink_Ident)dv2.sink;
		assertEquals(si2.name,"abcdpng");
		}
}
