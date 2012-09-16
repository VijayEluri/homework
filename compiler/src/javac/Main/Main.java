package javac.Main;
import java.io.FileInputStream;

import java.io.InputStream;

import javac.absyn.*;
import javac.parser.*;
import javac.env.*;
import java.io.*;
import java.util.*;



public final class Main {
	
	public static List<Translator> functions;
	
	
	public static void main(String[] args) throws Exception {
		//compile("/home/mrain/source/homework/sophomore/compiler-testcases/semantic/good/naive3.java");
		//compile("test.java");
		if (args.length < 1) {
			System.out.println("Usage: java -jar mid.jar {sourcefile}");
			System.exit(-2);
		}
		compile(args[0]);
	}
	public static void compile(String filename) throws Exception {
		TranslationUnit ctribUnit = null, translationUnit = null;
		try {
			//final InputStream ctrib = new FileInputStream("/home/mrain/source/homework/sophomore/compiler/res/contrib.java");
			final InputStream ctrib = new BufferedInputStream(Main.class.getResourceAsStream("contrib.res"));
			final Parser fst = new Parser(new Yylex(ctrib));
			final java_cup.runtime.Symbol ctribTree = fst.parse();
			ctrib.close();
			ctribUnit = (TranslationUnit) ctribTree.value;
			
			final InputStream in = new FileInputStream(filename);
			final Parser parser = new Parser(new Yylex(in));
			final java_cup.runtime.Symbol parseTree = parser.parse();
			in.close();
			translationUnit = (TranslationUnit) parseTree.value;
		} catch (Exception e) {
			e.printStackTrace(System.err);
			System.exit(1);
		}
		
		Env global_env = new Env();
		GlobalVisitor global_visitor = new GlobalVisitor(global_env);
		ctribUnit.accept(global_visitor);
		translationUnit.accept(global_visitor);
		
		DeclVisitor decl_visitor = new DeclVisitor(global_env);
		ctribUnit.accept(decl_visitor);
		translationUnit.accept(decl_visitor);
		
		FinalVisitor final_visitor = new FinalVisitor(global_env);
		FinalVisitor ctrib_visitor = new FinalVisitor(new Env(global_env));
		translationUnit.accept(final_visitor);
		ctribUnit.accept(ctrib_visitor);
		
		//System.out.println(AbsynFormatter.format(ctribUnit.toString()));
		
		//Translation Begins
		String DataSegment = "";
		String TextSegment = "";
		functions = new ArrayList<Translator>();
		for (ExternalDecl ext : translationUnit.externalDeclarations)
			if (ext instanceof FunctionDef) {
				Translator t = new Translator((FunctionDef)ext);
				functions.add(t);
				Assembler asm = new Assembler(t);
				DataSegment += t.DataSegment();
				TextSegment += asm.toString();
			}
		for (ExternalDecl ext : ctribUnit.externalDeclarations)
			if (ext instanceof FunctionDef) {
				Translator t = new Translator((FunctionDef)ext);
				functions.add(t);
				Assembler asm = new Assembler(t);
				DataSegment += t.DataSegment();
				TextSegment += asm.toString();
			}
		
		String outfile = filename.substring(0, filename.lastIndexOf('.')) + ".s";
		
		PrintWriter pw = new PrintWriter(new FileOutputStream(outfile));
		pw.write(".data\n");
		pw.write(DataSegment);
		pw.write(".text\n");
		pw.write(TextSegment);
		InputStream runtime = new BufferedInputStream(Main.class.getResourceAsStream("runtime.lib"));
		Scanner scan = new Scanner(runtime);
		while (scan.hasNext())
			pw.write(scan.nextLine() + "\n");
		pw.close();
		
		System.exit(0);
	}
}
