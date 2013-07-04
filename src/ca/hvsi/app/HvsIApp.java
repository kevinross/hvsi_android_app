package ca.hvsi.app;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockActivity;

public class HvsIApp extends RoboSherlockActivity {
	private String auth_task;
	public static final int LOGIN_REQUEST_CODE = 1;
	public static final int REGISTER_REQUEST_CODE = 2;
	public static final int FORGOT_PASSWORD_REQUEST_CODE = 3;
	public static final int LOGIN_OK = 1;
	public static final int LOGIN_INCOMPLETE = 2;
	public static final int REGISTER_OK = 3;
	public static final int REGISTER_INCOMPLETE = 4;
	public static final int LOGINOUT_ID = 1;
	public static final int REGISTER_ID = 2;
	public static final int FORGOT_ID = 3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hvsi_app);
		new setOptionsMenuTask().execute(this);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		menu.add(0, LOGINOUT_ID, 0, getString(API.logged_in()?R.string.logout:R.string.login));
		ca.hvsi.lib.Account self = API.self();
		if ((!API.logged_in() && API.can_register()) || (self != null && self.getClass().equals(ca.hvsi.lib.Admin.class)))
			menu.add(0, REGISTER_ID, 0, getString(R.string.register));
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case LOGINOUT_ID:
			if (API.logged_in()) {
				new Thread(new Runnable() {
					public void run() {
						API.api().call("logout");
						new setOptionsMenuTask().execute(this);
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
			if (res == LOGIN_OK) {
				new setOptionsMenuTask().execute(this);
			} else if (res == REGISTER_OK) {
				new Thread(new Runnable() {
					public void run() {
						final AlertDialog.Builder builder = new AlertDialog.Builder(HvsIApp.this);
						String msg = getString(R.string.register_progress_registered);
						DateTime t = (DateTime) API.game().get("start_time");
						if (t == null)
							return;
						builder.setMessage(
									String.format(
											msg, 
											t.toString(
													DateTimeFormat.forPattern("MMMM dd, yyyy")),
											t.toString(
													DateTimeFormat.forPattern("hh:mmaa")))).
							setTitle(getString(R.string.register_progress_registered_title)).
							setPositiveButton("OK", null);
						HvsIApp.this.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								builder.show();
							}
						});
					}}).start();
				new setOptionsMenuTask().execute(this);
			}
		}
	}
	public static class setOptionsMenuTask extends AsyncTask<RoboSherlockActivity, Void, Void> {
		private RoboSherlockActivity target = null;
		protected Void doInBackground(RoboSherlockActivity... activity) {
			// TODO Auto-generated method stub
			API.logged_in((Boolean)API.api().get("logged_in"));
			API.can_register((Boolean)API.game().get("can_register"));
			API.self((ca.hvsi.lib.Account)API.api().get("self"));
			target = activity[0];
			return null;
		}
		
		protected void onPostExecute(Void result) {
			target.invalidateOptionsMenu();
		}
	}

}
