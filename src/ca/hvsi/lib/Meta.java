package ca.hvsi.lib;

public class Meta {
	int id;
	String name;
	public String toString() {
		return String.format("<Meta name=%s id=%d>", name, id);
	}
}
