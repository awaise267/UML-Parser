

import java.util.*;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.ModifierSet;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.ReferenceType;

public class classObject {
	public String className;
	public List<methodObject> methodsList = new ArrayList<methodObject>();
	public List<attributeObject> attributesList = new ArrayList<attributeObject>();
	public List<ClassOrInterfaceType> extendsList = new ArrayList<ClassOrInterfaceType>(), implementsList = new ArrayList<ClassOrInterfaceType>();
	public List<String> referencesList = new ArrayList<String>();
	public Boolean isInterface;
	public ClassOrInterfaceDeclaration declaration;
	public List<ConstructorObject> constructorList = new ArrayList<ConstructorObject>();
	public classObject(){
		
	}
	
	public classObject(ClassOrInterfaceDeclaration c){
		this.className = c.getName();
		this.declaration = c;
		getMethodsAndAttributes(c);
		if(c.getExtends()!=null)
			extendsList.addAll(c.getExtends());
		if(c.getImplements()!=null)
			implementsList.addAll(c.getImplements());
		if(c.isInterface())
			isInterface=true;
		else
			isInterface=false;
		getFinalDependencies(c);
	}
	
	private void getMethodsAndAttributes(ClassOrInterfaceDeclaration c){
		for (Node iteratorNode : c.getChildrenNodes()){			
			//if(iteratorNode.getClass() == MethodDeclaration.class && iteratorNode.toString().toLowerCase().contains("public")){
			if(iteratorNode.getClass() == MethodDeclaration.class){
				if(ModifierSet.getAccessSpecifier(((MethodDeclaration)iteratorNode).getModifiers()).toString().compareToIgnoreCase("public")==0)
				methodsList.add(new methodObject((MethodDeclaration) iteratorNode, className));				
			}
			//if(iteratorNode.getClass() == FieldDeclaration.class && iteratorNode.toString().contains("public")){
			if(iteratorNode.getClass() == FieldDeclaration.class){
				if(ModifierSet.getAccessSpecifier(((FieldDeclaration)iteratorNode).getModifiers()).toString().compareToIgnoreCase("public")==0 || ModifierSet.getAccessSpecifier(((FieldDeclaration)iteratorNode).getModifiers()).toString().compareToIgnoreCase("private")==0)
					for(Node n : iteratorNode.getChildrenNodes())
						if(n.getClass()==VariableDeclarator.class)
							attributesList.add(new attributeObject((VariableDeclarator) n, className, ((FieldDeclaration)iteratorNode).getType().toString(), ((FieldDeclaration)iteratorNode).getModifiers() ));		
			}
			
			if(iteratorNode.getClass() == ConstructorDeclaration.class)
				constructorList.add(new ConstructorObject((ConstructorDeclaration)iteratorNode));
		}
	}
	
	private void getFinalDependencies(Node iterationNode){
		if(iterationNode.getClass() == ReferenceType.class){
			referencesList.add(((ReferenceType)iterationNode).getType().toString());
		}
		else if(iterationNode.getChildrenNodes()!=null)
			for(Node innerNode : iterationNode.getChildrenNodes())
				getFinalDependencies(innerNode);
	}
	
	
}

