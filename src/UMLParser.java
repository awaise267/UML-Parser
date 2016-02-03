

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.ReferenceType;
import net.sourceforge.plantuml.*;

public class UMLParser {
	private static List<classObject> classList = new ArrayList<classObject>();
	private static List<String> interfaceNameList = new ArrayList<String>();
	private static List<String> classNameList = new ArrayList<String>();
	public static List<Association> associationsList = new ArrayList<Association>();
	public static String UMLoutput = "";
	public static void main(String[] args) {
		
		//String directoryPath = "C:\\Users\\awais_000\\Downloads\\uml-parser-test-1";
		String directoryPath = args[0].toString();
		File dirPath = new File(directoryPath);
		
		FilenameFilter javaFilter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				String lowercaseName = name.toLowerCase();
				if (lowercaseName.endsWith(".java")) {
					return true;
				} else {
					return false;
				}
			}
		};
		
		File[]  dirListing = dirPath.listFiles(javaFilter);
		for(File file : dirListing){
			try {
				parseFile(file);
			} catch (Exception e) {				
				e.printStackTrace();
			}
		}

		getClass_Interfaces();
		
		//System.out.println("@startuml\n");
		UMLoutput+="@startuml\n";
		generateUMLComponents();
		drawDependencies();
		getAssoc();
		//System.out.println("\n@enduml");
		UMLoutput+="\n@enduml";
		
		System.out.println(UMLoutput);
		SourceStringReader s= new SourceStringReader(UMLoutput);
		try {
			FileOutputStream f = new FileOutputStream(args[1].toString());
			s.generateImage(f);
			f.close();
			
		} catch (Exception e) {
			//e.printStackTrace();
			System.out.println(e);
		}
	}
	
	public static void parseFile(File currentFile) throws Exception{
		FileInputStream in = new FileInputStream(currentFile);
		CompilationUnit cu;
        try {
            cu = JavaParser.parse(in);
        } finally {
        	in.close();
        }
        
		for(Node n : cu.getChildrenNodes())
			if(n.getClass()== ClassOrInterfaceDeclaration.class)				
				classList.add(new classObject((ClassOrInterfaceDeclaration) n));			
				
	}
	
	public static void generateUMLComponents(){
		String UMLstring = "";
		int firstCount = 0;
		
		for(classObject classObj : classList){
			if(!classObj.isInterface)
				UMLstring += "class " + classObj.className;
			else
				UMLstring += "interface " + classObj.className;
			
			if(classObj.extendsList!= null && !classObj.extendsList.isEmpty()){
				UMLstring += " extends ";
				firstCount =0;
				for(ClassOrInterfaceType c : classObj.extendsList)
				{
					if(firstCount == 0){
						UMLstring += c.getName();
						firstCount++;
					}
					else
						UMLstring += ", "+c.getName();
				}
			}
			
			if(classObj.implementsList!= null && !classObj.implementsList.isEmpty()){
				UMLstring += " implements ";
				firstCount =0;
				for(ClassOrInterfaceType c : classObj.implementsList)
				{
					if(firstCount == 0){
						UMLstring += c.getName();
						firstCount++;
					}
					else
						UMLstring += ", "+c.getName();
				}
			}
			 
			
			UMLstring += " {\n";
			for(attributeObject attrObj : classObj.attributesList)
				UMLstring += attrObj.accessSpecifier+attrObj.attributeName+":"+attrObj.attributeType+"\n";
			for(ConstructorObject constructor : classObj.constructorList)
				UMLstring += "+"+constructor.name+"("+constructor.parameters+")"+"\n";
			for(methodObject methodObj : classObj.methodsList)
				UMLstring += "+"+methodObj.methodName+"("+methodObj.parameters+")"+ " : "+methodObj.returnType +"\n";
			UMLstring += "}\n";			
		}
		
		
		//for(String s : interfaceList){
		//	UMLstring += "Interface "+s+"\n";
		//}		
		UMLoutput += UMLstring;
		//System.out.println(UMLstring);
		
	}
	public static void drawDependencies(){
		String dependencyString = "";
		List<String> dependencyList = new ArrayList<String>();
		for(classObject classObj : classList){
			if(!classObj.isInterface){
				for(String s : classObj.referencesList)
					if(!dependencyList.contains(classObj.className +" ..> "+s+":uses") && interfaceNameList.contains(s))
						dependencyList.add(classObj.className +" ..> "+s+":uses");
				for(attributeObject attrObj : classObj.attributesList){
					if(interfaceNameList.contains(attrObj.attributeType)){
						dependencyString = classObj.className +" ..> "+attrObj.attributeType+":uses";
						if(!dependencyList.contains(dependencyString))
							dependencyList.add(dependencyString);
					}
				}
				
				//dependency for methods in a class
				for(methodObject methodObj: classObj.methodsList){
					String methodInterfaceList = methodObj.getDependencies(interfaceNameList);
					for(String iteratorString : methodInterfaceList.split(",")){
						if (interfaceNameList.contains(iteratorString)){
							dependencyString = classObj.className +" ..> "+iteratorString+":uses";
							if(!dependencyList.contains(dependencyString))
								dependencyList.add(dependencyString);
						}
					}
				}
			}
		}

		if(!dependencyList.isEmpty())
			for(String dependencies : dependencyList){
				//System.out.println(dependencies);
				UMLoutput += "\n"+dependencies;
			}
		
	}
	
	public static void getClass_Interfaces(){
		for(classObject obj : classList)
			if(obj.isInterface)
				interfaceNameList.add(obj.className);
			else
				classNameList.add(obj.className);
	}
	
	public static void getAssoc(){
		for(classObject obj : classList)
			getAssociations(obj.declaration, obj.className, "");
		displayAssocations();
	}

	public static void getAssociations(Node iterationNode, String parentClass, String multiplicity){
		if(iterationNode.getClass() == ReferenceType.class){
			/*System.out.println(((ReferenceType)iterationNode).getType().toString());*/
			addAssociation(parentClass, ((ReferenceType)iterationNode).getType().toString(), multiplicity);
			if(iterationNode.getChildrenNodes()!=null)
				for(Node innerNode : iterationNode.getChildrenNodes()){
					getAssociations(innerNode, parentClass,"\"*\" ");
				}
		}
		else if(iterationNode.getChildrenNodes()!=null && iterationNode.getClass() != MethodDeclaration.class && iterationNode.getClass() != ConstructorDeclaration.class && iterationNode.getClass() != ObjectCreationExpr.class)
			for(Node innerNode : iterationNode.getChildrenNodes()){
				getAssociations(innerNode, parentClass, multiplicity);
			}
	}
	
	public static void addAssociation(String class1, String class2, String multiplicity2){
		Boolean present = false;
		for(Association association : associationsList){
			if(association.class1.equals(class1) && association.class2.equals(class2)){
				present = true;
				if(association.multiplicityClass2 != null && !association.multiplicityClass2.equals("\"*\" "))
					association.multiplicityClass2 = multiplicity2;
			}
			else if(association.class2.equals(class1) && association.class1.equals(class2)){
				present = true;
				if(association.multiplicityClass1 != null && !association.multiplicityClass1.equals("\"*\" "))
					association.multiplicityClass1 = multiplicity2;
			}
		}
		if(!present){
			Association association = new Association(class1, class2);
			association.multiplicityClass2 = multiplicity2;
			associationsList.add(association);
		}			
	}
	
	public static void displayAssocations(){
		for(Association association : associationsList){
			if(classNameList.contains(association.class1) && classNameList.contains(association.class2)){
				String assoc = association.class1 + association.multiplicityClass1 + " -- " + association.multiplicityClass2 + association.class2;
				//System.out.println(assoc);
				UMLoutput += "\n"+assoc;
			}
		}
	}
}
