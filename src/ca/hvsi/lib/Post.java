package ca.hvsi.lib;

public class Post extends Resolvable {
	ObjectRef<DbString> title;
	ObjectRef<DbString> content;
	boolean allow_comments;
	ObjectRef<Comment> comments;
	Post() {
		
	}
	public String toString() {
		return String.format("<Post id=%d title=%s content=%s comments=%s>", id, title, content, comments);
	}
}
