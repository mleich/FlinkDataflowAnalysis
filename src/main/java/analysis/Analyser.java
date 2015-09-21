package analysis;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;

public class Analyser {

	public static void main(String[] args) throws Exception {

		File source = new File("D:\\Downloads\\examples\\wordcount\\PojoExample.java");
		
		if(!source.exists() || !source.isFile()) {
			throw new FileNotFoundException();
		}
		
		CompilationUnit cu;
		
		try(FileInputStream in = new FileInputStream(source)) {
            cu = JavaParser.parse(in);
        }

        DependencyAnalyser da = new DependencyAnalyser(cu);
        
        da.getDependencyGraph();
	}
}
