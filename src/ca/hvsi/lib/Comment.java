package ca.hvsi.lib;
import org.joda.time.*;

public class Comment extends Resolvable {
	int postID;
	int userID;
	String content;
	ObjectRef<Account> user;
	DateTime time;
	ObjectRef<Post> post;
	public String toString() {
		return String.format("<Comment id=%d post=%d user=%d content=\"%s\" time=%s>", id, postID, userID, content, time.toString());
	}
}
