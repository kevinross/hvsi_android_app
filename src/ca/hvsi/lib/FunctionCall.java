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
}
