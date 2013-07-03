package ca.hvsi.app;

import ca.hvsi.lib.ApiClient;
import com.actionbarsherlock.app.SherlockActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class HvsIApp extends SherlockActivity {
	private String auth_task;
	private boolean logged_in = false;
	public static final int LOGIN_REQUEST_CODE = 1;
	public static final int REGISTER_REQUEST_CODE = 1;
	public static final int LOGIN_OK = 1;
	public static final int LOGIN_INCOMPLETE = 2;
	public static final int REGISTER_OK = 1;
	public static final int REGISTER_INCOMPLETE = 2;
	public static final int LOGINOUT_ID = 1;
	public static final int REGISTER_ID = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hvsi_app);
		new setOptionsMenuTask().execute((Void)null);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		menu.add(0, LOGINOUT_ID, 0, getString(logged_in?R.string.logout:R.string.login));
		if (!logged_in)
			menu.add(0, REGISTER_ID, 0, getString(R.string.register));
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case LOGINOUT_ID:
			if (logged_in) {
				new Thread(new Runnable() {
					public void run() {
						API.api().call("logout");
						new setOptionsMenuTask().execute((Void)null);
					}
				}).start();
			} else {
				Intent do_login_intent = new Intent(this, LoginActivity.class);
				startActivityForResult(do_login_intent, LOGIN_REQUEST_CODE);
			}
			break;
		case REGISTER_ID:
			Intent do_register_intent = new Intent(this, RegisterActivity.class);
			startActivityForResult(do_register_intent, REGISTER_REQUEST_CODE);
			break;
		}
		
		return super.onOptionsItemSelected(item);
	}
	@Override
	protected void onActivityResult(int req, int res, Intent data) {
		if (req == LOGIN_REQUEST_CODE || req == REGISTER_REQUEST_CODE) {
			if (res == LOGIN_OK || res == REGISTER_OK) {
				new setOptionsMenuTask().execute((Void)null);
			}
		}
	}
	public class setOptionsMenuTask extends AsyncTask {
		@Override
		protected Object doInBackground(Object... arg0) {
			// TODO Auto-generated method stub
			logged_in = (Boolean)API.api().get("logged_in");
			return null;
		}
		
		protected void onPostExecute(Void result) {
			invalidateOptionsMenu();
		}
	}

}
