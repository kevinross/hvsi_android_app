package ca.hvsi.lib;

public class DbString extends Resolvable {
	String lang;
	String field;
	String content;
	ObjectRef<Post> post;
	public String toString() {
		return String.format("<DbString lang=%s field=%s content=%s post=%d>",
							lang, field, content, post.sqlref.items[0]);
	}
}
