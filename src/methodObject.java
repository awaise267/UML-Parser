

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;

public class methodObject {
	public String methodName, parentClass, returnType, parameters;	
	public MethodDeclaration declarationNode;
	public methodObject(){
		
	}
	public methodObject(MethodDeclaration m, String parentClass){
		methodName = m.getName();
		returnType = m.getType().toString();
		parameters = m.getParameters().toString();
		parameters="";
		for(Parameter param: m.getParameters()){
			if(parameters.equals(""))
				parameters=param.getId() + " : " + param.getType();
			else
				parameters=", "+param.getId() + ":" + param.getType();
		}			
		this.parentClass = parentClass;
		declarationNode = m;
	}
	
	public String getDependencies(List<String> interfaceList){
		String returnVal="";

		for(FieldDeclaration fieldsDecln : getVariableDeclarations((Node)declarationNode)){
			String tempStr = fieldsDecln.getType().toString();
			if(interfaceList.contains(tempStr))
				returnVal += tempStr+",";
		}
		return returnVal;
	}
	
	public List<FieldDeclaration> getVariableDeclarations(Node parseTree){
		List<FieldDeclaration> returnList = new ArrayList<FieldDeclaration>(),tempList = new ArrayList<FieldDeclaration>(); 
		
		for(Node iteratorNode : parseTree.getChildrenNodes()){
			tempList = new ArrayList<FieldDeclaration>();
			if(iteratorNode.getClass() == FieldDeclaration.class)
				returnList.add((FieldDeclaration) iteratorNode);
			else
				if(tempList.addAll(getVariableDeclarations(iteratorNode)))
					returnList.addAll(tempList);			
		}
		
		return returnList;
	}
	
}
