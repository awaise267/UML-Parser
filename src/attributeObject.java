
import com.github.javaparser.ast.body.ModifierSet;
import com.github.javaparser.ast.body.VariableDeclarator;

public class attributeObject {
	public String attributeType, attributeName, parentClass, accessSpecifier;
	public attributeObject(){
		
	}
	
	public attributeObject(VariableDeclarator v, String parentClass, String variableType, int modifier){
		this.parentClass = parentClass;
		attributeType = variableType;
		attributeName = v.getId().toString();
		if(ModifierSet.getAccessSpecifier(modifier).toString().compareToIgnoreCase("public")==0)
			accessSpecifier = "+";
		else
			accessSpecifier = "-";
	}
}
