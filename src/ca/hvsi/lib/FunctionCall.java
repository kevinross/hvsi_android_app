package ca.hvsi.lib;

public class FunctionCall {
	public String func;
	public Object[] args;
	public FunctionCall() {
		
	}
	public FunctionCall(String func, Object... args) {
		this.func = func;
		this.args = args;
	}
	public String toString() {
		String val = func + "(";
		for (Object i : args) {
			val += i.toString() + ", ";
		}
		val+= ")";
		return val.replace(", )", ")");
	}
}
