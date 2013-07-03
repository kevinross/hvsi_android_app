package ca.hvsi.lib;
import java.util.LinkedList;

import org.apache.http.client.HttpClient;
import org.apache.http.protocol.HttpContext;

import com.google.gson.*;


public class ApiClient extends RpcClient {
	public static ApiClient global_client = null;
	public ApiClient(String base_endpoint) {
		super(base_endpoint);
		if (global_client == null) {
			global_client = this;
		}
	}
	public ApiClient(String base_endpoint, String endpoint) {
		super(base_endpoint, endpoint);
	}
	@Override
	public Object[] __marshall_args__(Object... args) {
		LinkedList<Object> argsList = new LinkedList<Object>();
		for (int i = 0; i < args.length; i++) {
			if (args[i] instanceof ApiClient) {
				argsList.add(String.format("hash:%s", ((ApiClient)args[i]).interface_.hash));
			} else if (args[i] instanceof Resolvable) {
				ObjectRef<Resolvable> ref = new ObjectRef<Resolvable>();
				ref.one = true;
				ref.sqlref = new SqlRef();
				Resolvable obj = (Resolvable)args[i];
				ref.sqlref.name = obj.__meta__.name;
				ref.sqlref.items = new int[1];
				ref.sqlref.items[0] = obj.__meta__.id;
				argsList.add(ref);
			} else {
				argsList.add(args[i]);
			}
		}
		return argsList.toArray();
	}
	public Object __resolve_references__(String resp) {
		Resolvable objs[] = null;
		JsonParser parser = new JsonParser();
		JsonElement jsonobj = parser.parse(resp);
		boolean isarray = jsonobj.isJsonArray();
		if (isarray) {
			objs = gson.fromJson(resp, Resolvable[].class);
		} else {
			objs = new Resolvable[1];
			objs[0] = gson.fromJson(resp, Resolvable.class);
		}
		LinkedList<Resolvable> objects = new LinkedList<Resolvable>();
		for (int i = 0; i < objs.length; i++) {
			Resolvable obj = objs[i];
			try {
				@SuppressWarnings("unchecked")
				Class<? extends Resolvable> klass = (Class<? extends Resolvable>) Class.forName("ca.hvsi.lib." + obj.__meta__.name);
				if (isarray) {
					objects.add(gson.fromJson(jsonobj.getAsJsonArray().get(i), klass));
				} else {
					objects.add(gson.fromJson(jsonobj, klass));
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (objects.size() == 1) {
			return objects.get(0);
		} else {
			return objects;
		}
	}
}
