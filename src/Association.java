
public class Association {
	public String class1, multiplicityClass1, class2 , multiplicityClass2;
	public Association(){
		this.class1 = "";
		this.class2 = "";
		this.multiplicityClass1 = "";
		this.multiplicityClass2 = "";
	}
	public Association(String class1, String class2){
		this.class1 = class1;
		this.class2 = class2;
		this.multiplicityClass1 = "";
		this.multiplicityClass2 = "";
	}
}
