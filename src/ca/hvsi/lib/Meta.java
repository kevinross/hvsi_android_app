package ca.hvsi.lib;

public class Meta {
	public int id;
	public String name;
	public String toString() {
		return String.format("<Meta name=%s id=%d>", name, id);
	}
}
