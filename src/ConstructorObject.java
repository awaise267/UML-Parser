import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.Parameter;

public class ConstructorObject {
	public String name, parameters;
	public ConstructorObject(ConstructorDeclaration c){
		this.name = c.getName();
		this.parameters = "";
		for(Parameter param: c.getParameters()){
			if(parameters.equals(""))
				parameters=param.getId() + " : " + param.getType();
			else
				parameters=", "+param.getId() + ":" + param.getType();
		}
	}
}
