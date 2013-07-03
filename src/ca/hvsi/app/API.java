package ca.hvsi.app;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import ca.hvsi.lib.ApiClient;

public class API extends Application{
	private static Context context_;
	private ApiClient root_, api_, globals_;
	public void onCreate() {
		API.context_ = getApplicationContext();
	}
	public static Context context() {
		return context_;
	}
	public static ApiClient root() {
		return ((API)context()).rootapi();
	}
	public static ApiClient api() {
		return ((API)context()).apiapi();
	}
	public static ApiClient globals() {
		return ((API)context()).globalsapi();
	}
	public static String lang() {
		return "e";
	}
	public ApiClient rootapi() {
		if (root_ == null)
			root_ = new ApiClient(isDebugBuild()?"http://192.168.1.102:9055/api":"http://hvsidevel.aws.af.cm/api");
		return root_;
	}
	public ApiClient apiapi() {
		if (api_ == null)
			api_ = (ApiClient) rootapi().get("api");
		return api_;
	}
	public ApiClient globalsapi() {
		if (globals_ == null)
			globals_ = (ApiClient) rootapi().get("globals");
		return globals_;
	}
	public boolean isDebugBuild() 
    {
        boolean dbg = false;
        try {
            PackageManager pm = getPackageManager();
            PackageInfo pi = pm.getPackageInfo(getPackageName(), 0);

            dbg = ((pi.applicationInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0);
        } catch (Exception e) {
        }
        return dbg;
    }
}
