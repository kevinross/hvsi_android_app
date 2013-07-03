package ca.hvsi.lib;

import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import ca.hvsi.app.API;

public class Http {
	private static HttpClient client_;
	private static HttpContext context_;
	private static CookieStore jar_;
	public static HttpClient client() {
		if (client_ == null)
			client_ = new DefaultHttpClient();
		return client_;
	}
	public static HttpContext context() {
		if (context_ == null) {
			context_ = new BasicHttpContext();
			context_.setAttribute(ClientContext.COOKIE_STORE, jar());
		}
		return context_;
	}
	public static CookieStore jar() {
		if (jar_ == null)
			jar_ = new PersistentCookieStore(ca.hvsi.app.API.context());
		return jar_;
	}
}
