package ca.hvsi.lib;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.*;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.entity.StringEntity;
import com.google.gson.*;
import org.joda.time.*;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public abstract class RpcClient {
	protected String base_endpoint = null;
	private String endpoint = null;
	public Interface interface_ = null;
	private CookieStore jar = Http.jar();//(ca.hvsi.app.API.context());
	private HttpClient client = Http.client();
	private HttpContext context = Http.context();
	protected Gson gson = new GsonBuilder().registerTypeAdapter(DateTime.class, new DateTimeTypeConverter()).create();
	public RpcClient(String base_endpoint) {
		this(base_endpoint, null);
	}
	public RpcClient(String base_endpoint, String endpoint) {
		this.base_endpoint = base_endpoint;
		this.endpoint = base_endpoint + ((endpoint != null)?("/" + endpoint):"");
		this.interface_ = (Interface) __rpccall__(new FunctionCall("__interface__"));
	}
	public abstract Object[] __marshall_args__(Object... args);
	public abstract Object __resolve_references__(String obj);
	public Object __parse_response__(String resp) throws RemoteException {
		JsonParser parser = new JsonParser();
		JsonObject response = (JsonObject) parser.parse(resp);
		int result = response.get("result").getAsInt();
		JsonElement value = response.get("value");
		String valuestr = gson.toJson(response.get("value"));
		if (result != 0) {
			throw new RemoteException(response.get("exception").getAsString(), response.get("message").getAsString());
		}
		if (valuestr.contains("hash:")) {
			Class<? extends RpcClient> klass = this.getClass();
			try {
				Constructor<? extends RpcClient> m = klass.getDeclaredConstructor(String.class, String.class);
				String new_endpoint = gson.fromJson(value, String.class);
				return m.newInstance(this.base_endpoint, new_endpoint.replace("hash:", ""));
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		} else if (valuestr.contains("funcs")) {
			return gson.fromJson(valuestr, Interface.class);
		}
		if (value.isJsonPrimitive()) {
			JsonPrimitive val = value.getAsJsonPrimitive();
			if (val.isBoolean()) {
				return val.getAsBoolean();
			} else if (val.isNumber()) {
				return val.getAsBigInteger();
			} else if (val.isString()) {
				DateTimeFormatter dtparser = ISODateTimeFormat.dateHourMinuteSecond();
				try {
					return dtparser.parseDateTime(val.getAsString());
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				return val.getAsString();
			}
		} else if (value.isJsonNull()) {
			return null;
		} else if (value.isJsonObject() && !value.getAsJsonObject().has("__meta__")) {
			return gson.fromJson(value, HashMap.class);
		}
		return __resolve_references__(valuestr);
	}
	public Object __rpccall__(FunctionCall func) {
		return __rpccall__(this.endpoint, func);
	}
	public Object __rpccall__(String endpoint, FunctionCall func) {
		func.args = __marshall_args__(func.args);
		HttpPost post = new HttpPost(endpoint);
		StringEntity req = null;
		try {
			req = new StringEntity(
					gson.toJson(func),
					"UTF-8"
					);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		post.setEntity(req);
		post.setHeader(HTTP.CONTENT_TYPE, "application/json");
		HttpResponse resp = null;
		try {
			resp = client.execute(post, context);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		try {
			BufferedReader rd = new BufferedReader
					  (new InputStreamReader(resp.getEntity().getContent()));
			String out = "";
			String line = null;
			while ((line = rd.readLine()) != null) {
				out += line;
			}
			return __parse_response__(out);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	public Object call(String func, Object... args) {
		return __rpccall__(this.endpoint, new FunctionCall(func, args));
	}
	public Object get(String attr) {
		return __rpccall__(this.base_endpoint, 
				new FunctionCall("globals.getattr", 
						String.format("hash:%d",this.interface_.hash), 
						attr));
	}
	public void set(String attr, Object value) {
		__rpccall__(this.base_endpoint, new FunctionCall("globals.setattr", attr, value));
	}
	private class DateTimeTypeConverter implements JsonSerializer<DateTime>, JsonDeserializer<DateTime> {
		  // No need for an InstanceCreator since DateTime provides a no-args constructor
		  @Override
		  public JsonElement serialize(DateTime src, Type srcType, JsonSerializationContext context) {
		    return new JsonPrimitive(src.toString());
		  }
		  @Override
		  public DateTime deserialize(JsonElement json, Type type, JsonDeserializationContext context)
		      throws JsonParseException {
		    return new DateTime(json.getAsString());
		  }
		}
}
