package cop5556fa17;

import java.util.ArrayList;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import cop5556fa17.Scanner.Kind;
import cop5556fa17.TypeUtils.Type;
import cop5556fa17.AST.ASTNode;
import cop5556fa17.AST.ASTVisitor;
import cop5556fa17.AST.Declaration;
import cop5556fa17.AST.Declaration_Image;
import cop5556fa17.AST.Declaration_SourceSink;
import cop5556fa17.AST.Declaration_Variable;
import cop5556fa17.AST.Expression;
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
import cop5556fa17.AST.Source;
import cop5556fa17.AST.Source_CommandLineParam;
import cop5556fa17.AST.Source_Ident;
import cop5556fa17.AST.Source_StringLiteral;
import cop5556fa17.AST.Statement_In;
import cop5556fa17.AST.Statement_Out;
import cop5556fa17.AST.Statement_Assign;
import cop5556fa17.ImageFrame;
import cop5556fa17.ImageSupport;

public class CodeGenVisitor implements ASTVisitor, Opcodes {

	/**
	 * All methods and variable static.
	 */

	/**
	 * @param DEVEL
	 *            used as parameter to genPrint and genPrintTOS
	 * @param GRADE
	 *            used as parameter to genPrint and genPrintTOS
	 * @param sourceFileName
	 *            name of source file, may be null.
	 */
	public CodeGenVisitor(boolean DEVEL, boolean GRADE, String sourceFileName) {
		super();
		this.DEVEL = DEVEL;
		this.GRADE = GRADE;
		this.sourceFileName = sourceFileName;
	}

	ClassWriter cw;
	String className;
	String classDesc;
	String sourceFileName;

	MethodVisitor mv; // visitor of method currently under construction

	/** Indicates whether genPrint and genPrintTOS should generate code. */
	final boolean DEVEL;
	final boolean GRADE;

	public Object visitProgram(Program program, Object arg) throws Exception {
		cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		className = program.name;
		classDesc = "L" + className + ";";
		String sourceFileName = (String) arg;
		cw.visit(52, ACC_PUBLIC + ACC_SUPER, className, null,
				"java/lang/Object", null);
		cw.visitSource(sourceFileName, null);
		// create main method
		mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "main",
				"([Ljava/lang/String;)V", null, null);
		// initialize
		mv.visitCode();
		// add label before first instruction
		Label mainStart = new Label();
		mv.visitLabel(mainStart);
		// if GRADE, generates code to add string to log
		//CodeGenUtils.genLog(GRADE, mv, "entering main");

		// visit decs and statements to add field to class
		// and instructions to main method, respectivley
		ArrayList<ASTNode> decsAndStatements = program.decsAndStatements;
		for (ASTNode node : decsAndStatements) {
			node.visit(this, arg);
		}

		// generates code to add string to log
		//CodeGenUtils.genLog(GRADE, mv, "leaving main");

		// adds the required (by the JVM) return statement to main
		mv.visitInsn(RETURN);

		// adds label at end of code
		Label mainEnd = new Label();
		mv.visitLabel(mainEnd);

		// handles parameters and local variables of main. Right now, only args
		mv.visitLocalVariable("args", "[Ljava/lang/String;", null, mainStart,
				mainEnd, 0);

		// Sets max stack size and number of local vars.
		// Because we use ClassWriter.COMPUTE_FRAMES as a parameter in the
		// constructor,
		// asm will calculate this itself and the parameters are ignored.
		// If you have trouble with failures in this routine, it may be useful
		// to temporarily set the parameter in the ClassWriter constructor to 0.
		// The generated classfile will not be correct, but you will at least be
		// able to see what is in it.
		mv.visitLocalVariable("x", "I", null, mainStart, mainEnd, 1);
		mv.visitLocalVariable("y", "I", null, mainStart, mainEnd, 2);
		mv.visitLocalVariable("X", "I", null, mainStart, mainEnd, 3);
		mv.visitLocalVariable("Y", "I", null, mainStart, mainEnd, 4);
		mv.visitLocalVariable("r", "I", null, mainStart, mainEnd, 5);
		mv.visitLocalVariable("a", "I", null, mainStart, mainEnd, 6);
		mv.visitLocalVariable("R", "I", null, mainStart, mainEnd, 7);
		mv.visitLocalVariable("A", "I", null, mainStart, mainEnd, 8);
//		mv.visitLocalVariable("A", "I", null, mainStart, mainEnd, 9);
//		mv.visitLocalVariable("A", "I", null, mainStart, mainEnd, 10);
//		mv.visitLocalVariable("A", "I", null, mainStart, mainEnd, 11);
		mv.visitMaxs(0, 0);
		// terminate construction of main method
		mv.visitEnd();

		// terminate class construction
		cw.visitEnd();

		// generate classfile as byte array and return
		return cw.toByteArray();
	}

	public Object visitDeclaration_Variable(
			Declaration_Variable declaration_Variable, Object arg)
			throws Exception {
		// TODO
		//throw new UnsupportedOperationException();
			
		if(declaration_Variable.type_basic ==  Type.INTEGER){
			cw.visitField(ACC_STATIC, declaration_Variable.name, "I", null, null);
			cw.visitEnd();
		}else if(declaration_Variable.type_basic ==  Type.BOOLEAN){
			cw.visitField(ACC_STATIC, declaration_Variable.name, "Z", null, null);
			cw.visitEnd();
		}
		
		if(declaration_Variable.e != null){
			declaration_Variable.e.visit(this,arg);
			if(declaration_Variable.type_basic == Type.INTEGER)
			mv.visitFieldInsn(PUTSTATIC, className, declaration_Variable.name, "I");
			else if(declaration_Variable.type_basic == Type.BOOLEAN)
			mv.visitFieldInsn(PUTSTATIC, className, declaration_Variable.name, "Z");
				
		}
		
		 return null;
	}

	public Object visitExpression_Binary(Expression_Binary expression_Binary,
			Object arg) throws Exception {
		// TODO
		Kind op = expression_Binary.op; // get the op
		expression_Binary.e0.visit(this, arg); // get the expressions
		expression_Binary.e1.visit(this, arg);

		Type exp1Type = expression_Binary.e0.type_basic;// get the types
		Type exp2Type = expression_Binary.e1.type_basic;

		if (op == Kind.OP_PLUS) {
			if (exp1Type == Type.INTEGER && exp2Type == Type.INTEGER) {
				mv.visitInsn(IADD);
			}
		} else if (op == Kind.OP_MINUS) {
			if (exp1Type == Type.INTEGER && exp2Type == Type.INTEGER) {
				mv.visitInsn(ISUB);
			}
		} else if (op == Kind.OP_TIMES) {
			if (exp1Type == Type.INTEGER && exp2Type == Type.INTEGER) {
				mv.visitInsn(IMUL);
			}
		} else if (op == Kind.OP_TIMES) {
			if (exp1Type == Type.INTEGER && exp2Type == Type.INTEGER) {
				mv.visitInsn(IMUL);
			}
		} else if (op == Kind.OP_DIV) {
			if (exp1Type == Type.INTEGER && exp2Type == Type.INTEGER) {
				mv.visitInsn(IDIV);
			}
		} else if (op == Kind.OP_MOD) {
			if (exp1Type == Type.INTEGER && exp2Type == Type.INTEGER) {
				mv.visitInsn(IREM); // Logical int remainder
			}
		} else if (op == Kind.OP_OR) {
//			Label labelStart = new Label();
//			Label labelEnd = new Label();
//			mv.visitJumpInsn(IFNE, labelStart);
//			mv.visitInsn(ICONST_0);
//			mv.visitJumpInsn(GOTO, labelEnd);
//			mv.visitLabel(labelStart);
//			mv.visitInsn(ICONST_1);
//			mv.visitLabel(labelEnd);
			mv.visitInsn(IOR);
		} else if (op == Kind.OP_AND) {
			mv.visitInsn(IAND);
//			Label labelStartAnd = new Label();
//			Label labelEndAnd = new Label();
//			mv.visitJumpInsn(IFEQ, labelStartAnd);
//			mv.visitInsn(ICONST_0);
//			mv.visitJumpInsn(GOTO, labelEndAnd);
//			mv.visitLabel(labelStartAnd);
//			mv.visitInsn(ICONST_1);
//			mv.visitLabel(labelEndAnd);
		} else if (op == Kind.OP_LT) {
			Label labelStartLT = new Label();
			Label labelEndLT = new Label();
			mv.visitJumpInsn(IF_ICMPGE, labelStartLT);
			mv.visitInsn(ICONST_1);
			mv.visitJumpInsn(GOTO, labelEndLT);
			mv.visitLabel(labelStartLT);
			mv.visitInsn(ICONST_0);
			mv.visitLabel(labelEndLT);
		} else if (op == Kind.OP_LE) {
			Label labelStartLE = new Label();
			Label labelEndLE = new Label();
			mv.visitJumpInsn(IF_ICMPGT, labelStartLE);
			mv.visitInsn(ICONST_1);
			mv.visitJumpInsn(GOTO, labelEndLE);
			mv.visitLabel(labelStartLE);
			mv.visitInsn(ICONST_0);
			mv.visitLabel(labelEndLE);
		} else if (op == Kind.OP_GT) {
			Label labelStartGT = new Label();
			Label labelEndGT = new Label();
			mv.visitJumpInsn(IF_ICMPLE, labelStartGT);
			mv.visitInsn(ICONST_1);
			mv.visitJumpInsn(GOTO, labelEndGT);
			mv.visitLabel(labelStartGT);
			mv.visitInsn(ICONST_0);
			mv.visitLabel(labelEndGT);
		} else if (op == Kind.OP_GE) {
			Label labelStartGE = new Label();
			Label labelEndGE = new Label();
			mv.visitJumpInsn(IF_ICMPLT, labelStartGE);
			mv.visitInsn(ICONST_1);
			mv.visitJumpInsn(GOTO, labelEndGE);
			mv.visitLabel(labelStartGE);
			mv.visitInsn(ICONST_0);
			mv.visitLabel(labelEndGE);
		} else if (op == Kind.OP_EQ) {
			if (exp1Type == Type.INTEGER || exp2Type == Type.BOOLEAN) {
				Label labelStartEq = new Label();
				Label labelEndEq = new Label();
				mv.visitJumpInsn(IF_ICMPNE, labelStartEq);
				mv.visitInsn(ICONST_1);
				mv.visitJumpInsn(GOTO, labelEndEq);
				mv.visitLabel(labelStartEq);
				mv.visitInsn(ICONST_0);
				mv.visitLabel(labelEndEq);
			} else {
				Label labelStartEq = new Label();
				Label labelEndEq = new Label();
				mv.visitJumpInsn(IF_ACMPNE, labelStartEq);
				mv.visitInsn(ICONST_1);
				mv.visitJumpInsn(GOTO, labelEndEq);
				mv.visitLabel(labelStartEq);
				mv.visitInsn(ICONST_0);
				mv.visitLabel(labelEndEq);
			}
		} else if (op == Kind.OP_NEQ) {
			if (exp1Type == Type.INTEGER || exp2Type == Type.BOOLEAN) {
				Label labelStartNotEq = new Label();
				Label labelEndNotEq = new Label();
				mv.visitJumpInsn(IF_ICMPEQ, labelStartNotEq);
				mv.visitInsn(ICONST_1);
				mv.visitJumpInsn(GOTO, labelEndNotEq);
				mv.visitLabel(labelStartNotEq);
				mv.visitInsn(ICONST_0);
				mv.visitLabel(labelEndNotEq);
			} else {
				Label labelStartNotEq = new Label();
				Label labelEndNotEq = new Label();
				mv.visitJumpInsn(IF_ACMPEQ, labelStartNotEq);
				mv.visitInsn(ICONST_1);
				mv.visitJumpInsn(GOTO, labelEndNotEq);
				mv.visitLabel(labelStartNotEq);
				mv.visitInsn(ICONST_0);
				mv.visitLabel(labelEndNotEq);
			}
		}
		// throw new UnsupportedOperationException();
		//CodeGenUtils.genLogTOS(GRADE, mv, expression_Binary.getType());
		return null;
	}

	public Object visitExpression_Unary(Expression_Unary expression_Unary,
			Object arg) throws Exception {
		// TODO
		//throw new UnsupportedOperationException();
		Kind op = expression_Unary.op;
		expression_Unary.e.visit(this, arg);
		if(op == Kind.OP_PLUS){
			
		}else if(op == Kind.OP_MINUS){
			mv.visitInsn(INEG);
		}else if(op == Kind.OP_EXCL){
			if(expression_Unary.e.type_basic == Type.INTEGER){
				mv.visitLdcInsn(Integer.MAX_VALUE);
				mv.visitInsn(IXOR);
			}else if(expression_Unary.e.type_basic == Type.BOOLEAN){
				
				Label start = new Label();
				Label end = new Label();
				mv.visitJumpInsn(IFEQ, start);
				mv.visitInsn(ICONST_0);
				mv.visitJumpInsn(GOTO, end);
				mv.visitLabel(start);
				mv.visitInsn(ICONST_1);
				mv.visitLabel(end);
			}
		}
		
		//CodeGenUtils.genLogTOS(GRADE, mv, expression_Unary.getType());
		return null;
	}

	// generate code to leave the two values on the stack
	public Object visitIndex(Index index, Object arg) throws Exception {
		// TODO HW6
		//throw new UnsupportedOperationException();
		System.out.print(index.isCartesian());
		index.e0.visit(this, arg);
		index.e1.visit(this, arg);
		if(index.isCartesian()){
			// Done
		}else{
			mv.visitInsn(DUP2);
			mv.visitMethodInsn(INVOKESTATIC,RuntimeFunctions.className,"cart_x",RuntimeFunctions.cart_xSig,false);
			mv.visitInsn(DUP_X2);
			mv.visitInsn(POP);
			mv.visitMethodInsn(INVOKESTATIC,RuntimeFunctions.className,"cart_y",RuntimeFunctions.cart_ySig,false);
		}
		return null;
	}

	public Object visitExpression_PixelSelector(
			Expression_PixelSelector expression_PixelSelector, Object arg)
			throws Exception {
		// TODO HW6
		//throw new UnsupportedOperationException();
		mv.visitFieldInsn(GETSTATIC,className,expression_PixelSelector.name,ImageSupport.ImageDesc); // buffereimage
		if(expression_PixelSelector.index!=null) {
			expression_PixelSelector.index.visit(this, arg);
			mv.visitMethodInsn(INVOKESTATIC,ImageSupport.className,"getPixel",ImageSupport.getPixelSig,false);
		}
		return null;
	}

	public Object visitExpression_Conditional(
			Expression_Conditional expression_Conditional, Object arg)
			throws Exception {
		// TODO
		expression_Conditional.condition.visit(this, arg);
		Label start = new Label();
		Label end = new Label();
		mv.visitJumpInsn(IFEQ, start);
		expression_Conditional.trueExpression.visit(this, arg);
		mv.visitJumpInsn(GOTO, end);
		mv.visitLabel(start);
		expression_Conditional.falseExpression.visit(this, arg);
		mv.visitLabel(end);
//		CodeGenUtils.genLogTOS(GRADE, mv,
//				expression_Conditional.trueExpression.getType());
		return null;
	}

	public Object visitDeclaration_Image(Declaration_Image declaration_Image,
			Object arg) throws Exception {
		// TODO HW6
		//throw new UnsupportedOperationException();
		FieldVisitor fv;
		fv = cw.visitField(ACC_STATIC, declaration_Image.name, ImageSupport.ImageDesc, null, null);
		fv.visitEnd();
		
		if(declaration_Image.source!=null) {
			declaration_Image.source.visit(this, arg);	
			if(declaration_Image.xSize!=null && declaration_Image.ySize!=null) {
				declaration_Image.xSize.visit(this, arg);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
				declaration_Image.ySize.visit(this, arg);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
			}else {
				mv.visitInsn(ACONST_NULL);
				mv.visitInsn(ACONST_NULL);
			}
			mv.visitMethodInsn(INVOKESTATIC,ImageSupport.className,"readImage",ImageSupport.readImageSig,false);	
		}else {
			if(declaration_Image.xSize==null && declaration_Image.ySize==null ) {
				
				//System.out.println("In here");
				mv.visitLdcInsn(256);
				mv.visitLdcInsn(256);
			}else {
				declaration_Image.xSize.visit(this, arg);
				declaration_Image.ySize.visit(this, arg);
			}
			mv.visitMethodInsn(INVOKESTATIC,ImageSupport.className,"makeImage",ImageSupport.makeImageSig,false);
		}
		mv.visitFieldInsn(PUTSTATIC,className,declaration_Image.name,ImageSupport.ImageDesc);
	return null;
	}

	public Object visitSource_StringLiteral(
			Source_StringLiteral source_StringLiteral, Object arg)
			throws Exception {
		// TODO HW6
		//source_StringLiteral.
		//throw new UnsupportedOperationException();
		mv.visitLdcInsn(source_StringLiteral.fileOrUrl);
		//mv.visitInsn(ASTORE);
		return null;
	}

	public Object visitSource_CommandLineParam(
			Source_CommandLineParam source_CommandLineParam, Object arg)
			throws Exception {
		// TODO
		// throw new UnsupportedOperationException();
		mv.visitVarInsn(ALOAD, 0);
		source_CommandLineParam.paramNum.visit(this, arg); 
		mv.visitInsn(AALOAD);
		return null;

	}

	public Object visitSource_Ident(Source_Ident source_Ident, Object arg)
			throws Exception {
		
		//mv.visitFieldInsn(PUTSTATIC, className, arg2, arg3);
		if(source_Ident.type_basic== Type.FILE) {
			mv.visitFieldInsn(GETSTATIC, className, source_Ident.name,"Ljava/lang/String;");	
		} else if(source_Ident.type_basic==Type.URL) {
			mv.visitFieldInsn(GETSTATIC, className, source_Ident.name,"Ljava/lang/String;");	
		}
		return null;
		// TODO HW6
		//throw new UnsupportedOperationException();
	}

	public Object visitExpression_FunctionAppWithIndexArg(
			Expression_FunctionAppWithIndexArg expression_FunctionAppWithIndexArg,
			Object arg) throws Exception {
		// TODO HW6
		//throw new UnsupportedOperationException();
		expression_FunctionAppWithIndexArg.arg.e0.visit(this, arg);
		expression_FunctionAppWithIndexArg.arg.e1.visit(this, arg);
		if(expression_FunctionAppWithIndexArg.function.toString().equals("KW_cart_x")){
			mv.visitMethodInsn(INVOKESTATIC, "cop5556fa17/RuntimeFunctions", "cart_x", RuntimeFunctions.cart_xSig, false);
		}else if(expression_FunctionAppWithIndexArg.function.toString().equals("KW_cart_y")){
			mv.visitMethodInsn(INVOKESTATIC, "cop5556fa17/RuntimeFunctions", "cart_y", RuntimeFunctions.cart_ySig, false);
		}else if(expression_FunctionAppWithIndexArg.function.toString().equals("KW_polar_r")){
			mv.visitMethodInsn(INVOKESTATIC, "cop5556fa17/RuntimeFunctions", "polar_r", RuntimeFunctions.polar_rSig, false);
		}else if(expression_FunctionAppWithIndexArg.function.toString().equals("KW_polar_a")){
			mv.visitMethodInsn(INVOKESTATIC, "cop5556fa17/RuntimeFunctions", "polar_a", RuntimeFunctions.polar_aSig, false);
		}
		return null;
	}

	public Object visitDeclaration_SourceSink(
			Declaration_SourceSink declaration_SourceSink, Object arg)
			throws Exception {
		// TODO HW6
		FieldVisitor fv;
		System.out.println("Type"+declaration_SourceSink.type_basic);
		if(declaration_SourceSink.type_basic==Type.FILE) {
			 fv=cw.visitField(ACC_STATIC,declaration_SourceSink.name,"Ljava/lang/String;",null,null);
			 fv.visitEnd();
		}else if(declaration_SourceSink.type_basic==Type.URL) {
			 fv=cw.visitField(ACC_STATIC,declaration_SourceSink.name,"Ljava/lang/String;",null,null);
			 fv.visitEnd();
		}
		
		if(declaration_SourceSink.source!=null) {
			declaration_SourceSink.source.visit(this, arg);
			mv.visitFieldInsn(PUTSTATIC,className,declaration_SourceSink.name,declaration_SourceSink.type_basic==Type.FILE?"Ljava/lang/String;":"Ljava/lang/String;");
		}
		return null;
		//throw new UnsupportedOperationException();
	}

	public Object visitExpression_IntLit(Expression_IntLit expression_IntLit,
			Object arg) throws Exception {
		// TODO
		// throw new UnsupportedOperationException();
		mv.visitLdcInsn(expression_IntLit.value);
		//CodeGenUtils.genLogTOS(GRADE, mv, Type.INTEGER);
		return null;
	}

	public Object visitExpression_FunctionAppWithExprArg(
			Expression_FunctionAppWithExprArg expression_FunctionAppWithExprArg,
			Object arg) throws Exception {
		// TODO HW6
		//throw new UnsupportedOperationException();
		expression_FunctionAppWithExprArg.arg.visit(this, arg);
		if(expression_FunctionAppWithExprArg.function.toString().equals("KW_abs")){
			mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "abs", RuntimeFunctions.absSig, false);
		}else if(expression_FunctionAppWithExprArg.function.toString().equals("")){
			
		}
		return null;
	}

	public Object visitExpression_PredefinedName(
			Expression_PredefinedName expression_PredefinedName, Object arg)
			throws Exception {
		// TODO HW6
		//throw new UnsupportedOperationException();
		//System.out.println("I am here"+expression_PredefinedName.firstToken.getText());
		if(expression_PredefinedName.firstToken.getText().equals("x")){
			mv.visitVarInsn(ILOAD, 1);
		}else if(expression_PredefinedName.firstToken.getText().equals("y")){
			mv.visitVarInsn(ILOAD, 2);
		}else if(expression_PredefinedName.firstToken.getText().equals("X")){
			mv.visitVarInsn(ILOAD, 3);
		}else if(expression_PredefinedName.firstToken.getText().equals("Y")){
			mv.visitVarInsn(ILOAD, 4);
		}else if(expression_PredefinedName.firstToken.getText().equals("r")){
			mv.visitVarInsn(ILOAD, 1);
			mv.visitVarInsn(ILOAD, 2);
			mv.visitMethodInsn(INVOKESTATIC,RuntimeFunctions.className,"polar_r",RuntimeFunctions.polar_rSig,false);
			mv.visitVarInsn(ISTORE, 5);
			mv.visitVarInsn(ILOAD, 5);
		}else if(expression_PredefinedName.firstToken.getText().equals("a")){
			mv.visitVarInsn(ILOAD, 1);
			mv.visitVarInsn(ILOAD, 2);
			mv.visitMethodInsn(INVOKESTATIC,RuntimeFunctions.className,"polar_a",RuntimeFunctions.polar_aSig,false);
			mv.visitVarInsn(ISTORE, 6);		
			mv.visitVarInsn(ILOAD, 6);
		}else if(expression_PredefinedName.firstToken.getText().equals("R")){
			mv.visitVarInsn(ILOAD, 7);
		}else if(expression_PredefinedName.firstToken.getText().equals("A")){
			mv.visitVarInsn(ILOAD, 8);
		}else if(expression_PredefinedName.firstToken.getText().equals("DEF_X")){
			mv.visitLdcInsn(256);
		}else if(expression_PredefinedName.firstToken.getText().equals("DEF_Y")){
			mv.visitLdcInsn(256);
		}else if(expression_PredefinedName.firstToken.getText().equals("Z")){
			mv.visitLdcInsn(16777215);
		}
		
		return null;
	}

	/**
	 * For Integers and booleans, the only "sink"is the screen, so generate code
	 * to print to console. For Images, load the Image onto the stack and visit
	 * the Sink which will generate the code to handle the image.
	 */
	//@SuppressWarnings("deprecation")
	public Object visitStatement_Out(Statement_Out statement_Out, Object arg) throws Exception {
		//throw new UnsupportedOperationException();
		if(statement_Out.getDec().type_basic == Type.INTEGER){	
			//System.out.println("Hello world!!");
			mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
			mv.visitFieldInsn(GETSTATIC, className, statement_Out.name, "I");
			CodeGenUtils.genLogTOS(GRADE, mv, statement_Out.getDec().type_basic);
			mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "print", "(I)V", false);
		}else if(statement_Out.getDec().type_basic == Type.BOOLEAN){
			mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
			mv.visitFieldInsn(GETSTATIC, className, statement_Out.name, "Z");
			CodeGenUtils.genLogTOS(GRADE, mv, statement_Out.getDec().type_basic);
			mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "print", "(Z)V", false);
		}else if(statement_Out.getDec().type_basic == Type.IMAGE){
			mv.visitFieldInsn(GETSTATIC, className, statement_Out.name, ImageSupport.ImageDesc);
			CodeGenUtils.genLogTOS(GRADE, mv, Type.IMAGE);
			statement_Out.sink.visit(this, arg);
		}
		
		// Ask if this is correct
		//CodeGenUtils.genLogTOS(GRADE, mv, statement_Out.getDec().type_basic);
		return null;
	}

	/**
	 * Visit source to load rhs, which will be a String, onto the stack
	 * 
	 * In HW5, you only need to handle INTEGER and BOOLEAN Use
	 * java.lang.Integer.parseInt or java.lang.Boolean.parseBoolean to convert
	 * String to actual type.
	 * 
	 * TODO HW6 remaining types
	 */
	public Object visitStatement_In(Statement_In statement_In, Object arg)
			throws Exception {
		statement_In.source.visit(this, arg);
		if(statement_In.getDec().type_basic == Type.INTEGER){			
			mv.visitMethodInsn(INVOKESTATIC,"java/lang/Integer","parseInt","(Ljava/lang/String;)I",false);
		    mv.visitFieldInsn(PUTSTATIC, className,statement_In.name,"I");
			
		}else if(statement_In.getDec().type_basic == Type.BOOLEAN){
			mv.visitMethodInsn(INVOKESTATIC,"java/lang/Boolean","parseBoolean","(Ljava/lang/String;)Z",false);
		    mv.visitFieldInsn(PUTSTATIC, className,statement_In.name,"Z");
		}else if(statement_In.getDec().type_basic == Type.IMAGE){
			if(statement_In.source != null){
				Declaration_Image dim = (Declaration_Image)statement_In.getDec();
				if(dim.xSize != null){
					dim.xSize.visit(this, arg);
					mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);	
				}else{
					mv.visitInsn(ACONST_NULL);
				}
				if(dim.ySize != null){
					dim.xSize.visit(this, arg);
					mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
				}else{
					mv.visitInsn(ACONST_NULL);
				}
				mv.visitMethodInsn(INVOKESTATIC,ImageSupport.className,"readImage",ImageSupport.readImageSig,false);
				mv.visitFieldInsn(PUTSTATIC,className,statement_In.name,ImageSupport.ImageDesc);
			}
		}
		return null;
	}

	/**
	 * In HW5, only handle INTEGER and BOOLEAN types.
	 */
	public Object visitStatement_Transform(Statement_Assign statement_Assign,
			Object arg) throws Exception {
		// TODO (see comment)
		throw new UnsupportedOperationException();
	}

	/**
	 * In HW5, only handle INTEGER and BOOLEAN types.
	 */
	public Object visitLHS(LHS lhs, Object arg) throws Exception {
		// TODO (see comment)
		String cmp = lhs.type_basic.toString();
		if (cmp.equals("INTEGER")) {
			mv.visitFieldInsn(PUTSTATIC, className, lhs.name, "I");
		} else if (cmp.equals("BOOLEAN")) {
			mv.visitFieldInsn(PUTSTATIC, className, lhs.name, "Z");
		}else if(cmp.equals("IMAGE")){
			//System.out.println("I am here");
			mv.visitFieldInsn(GETSTATIC,className,lhs.name,ImageSupport.ImageDesc); 
			mv.visitVarInsn(ILOAD, 1);
			mv.visitVarInsn(ILOAD, 2);
		    mv.visitMethodInsn(INVOKESTATIC,ImageSupport.className,"setPixel",ImageSupport.setPixelSig,false);
		}
		return null;
		// throw new UnsupportedOperationException();
	}

	public Object visitSink_SCREEN(Sink_SCREEN sink_SCREEN, Object arg)
			throws Exception {
		// TODO HW6
		//throw new UnsupportedOperationException();
		mv.visitMethodInsn(INVOKESTATIC,ImageFrame.className,"makeFrame",ImageSupport.makeFrameSig,false);
		mv.visitInsn(POP);
		return null;
	}

	public Object visitSink_Ident(Sink_Ident sink_Ident, Object arg)
			throws Exception {
		// TODO HW6
		mv.visitFieldInsn(GETSTATIC,className,sink_Ident.name,ImageSupport.StringDesc);
		//System.out.println("Sink ident"+sink_Ident.type_basic);
		mv.visitMethodInsn(INVOKESTATIC,ImageSupport.className,"write",ImageSupport.writeSig,false);
		return null;
		//throw new UnsupportedOperationException();
	}

	public Object visitExpression_BooleanLit(
			Expression_BooleanLit expression_BooleanLit, Object arg)
			throws Exception {
		// TODO
		// throw new UnsupportedOperationException();
		//System.out.println("I am here"+expression_BooleanLit.value);
		mv.visitLdcInsn(expression_BooleanLit.value);
		//CodeGenUtils.genLogTOS(GRADE, mv, Type.BOOLEAN);
		return null;
	}

	public Object visitExpression_Ident(Expression_Ident expression_Ident,
			Object arg) throws Exception {
		// TODO
		 //expression_Ident.visit(this, null);
//		 mv.visitVarInsn(ALOAD, 0);
//		 mv.visitInsn(SWAP);
//		 //mv.visitFieldInsn(PUTSTATIC, className, expression_Ident.name, expression_Ident.getExpressi);
		if(expression_Ident.type_basic == Type.INTEGER){
			mv.visitFieldInsn(GETSTATIC, className, expression_Ident.name, "I");
		}else if(expression_Ident.type_basic == Type.BOOLEAN){
			mv.visitFieldInsn(GETSTATIC, className, expression_Ident.name, "Z");
		}
		
		 //CodeGenUtils.genLogTOS(GRADE, mv, expression_Ident.getType());
		 return null;
	}

	public Object visitStatement_Assign(Statement_Assign statement_Assign,
			Object arg) throws Exception {
		System.out.println("Statement "+statement_Assign.lhs.type_basic);
		// TODO Auto-generated method stub
		if(statement_Assign.lhs.type_basic == Type.INTEGER || statement_Assign.lhs.type_basic == Type.BOOLEAN){
			statement_Assign.e.visit(this, arg);
			statement_Assign.lhs.visit(this, arg);
		}else if(statement_Assign.lhs.type_basic == Type.IMAGE){
			System.out.println("I am hereZZZZZZ");
			mv.visitFieldInsn(GETSTATIC, className, statement_Assign.lhs.name, ImageSupport.ImageDesc);
			mv.visitInsn(DUP);
			mv.visitMethodInsn(INVOKESTATIC,ImageSupport.className,"getX",ImageSupport.getXSig,false);
			mv.visitVarInsn(ISTORE,3);
			mv.visitMethodInsn(INVOKESTATIC,ImageSupport.className,"getY",ImageSupport.getYSig,false);
			mv.visitVarInsn(ISTORE,4);	
			
			
			mv.visitInsn(ICONST_0);
			mv.visitVarInsn(ISTORE, 1);
		    Label l1 = new Label();
	  	    mv.visitLabel(l1);
	 	    Label l2 = new Label();
	    	mv.visitJumpInsn(GOTO, l2);
			Label l3 = new Label();
			mv.visitLabel(l3);
			mv.visitFrame(Opcodes.F_APPEND,1, new Object[] {Opcodes.INTEGER}, 0, null);
			mv.visitInsn(ICONST_0);
			mv.visitVarInsn(ISTORE, 2);
			Label l4 = new Label();
			mv.visitLabel(l4);
			Label l5 = new Label();
			mv.visitJumpInsn(GOTO, l5);
			Label l6 = new Label();
			mv.visitLabel(l6);
			mv.visitFrame(Opcodes.F_APPEND,1, new Object[] {Opcodes.INTEGER}, 0, null);
			statement_Assign.e.visit(this, arg);
			statement_Assign.lhs.visit(this, arg);
			Label l7 = new Label();
		    mv.visitLabel(l7);
	     	mv.visitIincInsn(2, 1);
			mv.visitLabel(l5);
			mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
			mv.visitVarInsn(ILOAD, 2);
			mv.visitVarInsn(ILOAD, 4);
			mv.visitJumpInsn(IF_ICMPLT, l6);
			Label l8 = new Label();
			mv.visitLabel(l8);
		    mv.visitIincInsn(1, 1);
			mv.visitLabel(l2);
			mv.visitFrame(Opcodes.F_CHOP,1, null, 0, null);
			mv.visitVarInsn(ILOAD, 1);
			mv.visitVarInsn(ILOAD, 3);
		    mv.visitJumpInsn(IF_ICMPLT, l3);
		}
		
		return null;
	}

}
