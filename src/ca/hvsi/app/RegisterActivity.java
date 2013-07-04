package ca.hvsi.app;

import java.util.HashMap;
import javax.annotation.Nullable;

import roboguice.inject.InjectView;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;

import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockActivity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class RegisterActivity extends RoboSherlockActivity {

	/**
	 * The default email to populate the email field with.
	 */
	public static final String EXTRA_EMAIL = "com.example.android.authenticatordemo.extra.EMAIL";

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mAuthTask = null;

	// Values for email and password at the time of the login attempt.
	private enum Strings { mEmail, mPassword, mConfirm, mUsername, mName, mStudent, mTwitter, mCell, mLang };

	// UI references.
	@InjectView(R.id.email) private EditText mEmailView;
	@InjectView(R.id.password) private EditText mPasswordView;
	@InjectView(R.id.password_confirm) private EditText mConfirmView;
	@InjectView(R.id.username) private EditText mUsernameView;
	@InjectView(R.id.name) private EditText mNameView; 
	@InjectView(R.id.student_number) private EditText mStudentView;
	@InjectView(R.id.twitter) private EditText mTwitterView;
	@InjectView(R.id.cell) private EditText mCellView;
	@InjectView(R.id.language) private Spinner mLangView;
	@InjectView(R.id.login_form) private View mLoginFormView;
	@InjectView(R.id.register_status) private View mLoginStatusView;
	@InjectView(R.id.register_status_message) private TextView mLoginStatusMessageView;
	EditText[] views = new EditText[8];
	String[] values = new String[views.length + 1];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_register);
		views[0] = mEmailView;
		views[1] = mPasswordView;
		views[2] = mConfirmView;
		views[3] = mUsernameView;
		views[4] = mNameView;
		views[5] = mStudentView;
		views[6] = mTwitterView;
		views[7] = mCellView;

		mPasswordView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptRegister();
							return true;
						}
						return false;
					}
				});


		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptRegister();
					}
				});
	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptRegister() {
		if (mAuthTask != null) {
			return;
		}

		// Reset errors.
		for (int i = 0; i < views.length; i++) {
			views[i].setError(null);
		}
		// Store values at the time of the login attempt.
		for (int i = 0; i < views.length; i++) {
			values[i] = views[i].getText().toString();
		}
		values[views.length] = mLangView.getSelectedItem().toString();

		boolean cancel = false;
		View focusView = null;

		for (int i = 0; i < 6; i++) {
			if (TextUtils.isEmpty(views[i].getText().toString())) {
				views[i].setError(getString(R.string.error_field_required));
				focusView = views[i];
				cancel = true;
				break;
			}
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mLoginStatusMessageView.setText(R.string.register_progress_registering);
			showProgress(true);
			mAuthTask = new UserLoginTask();
			mAuthTask.execute((Void) null);
		}
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, HashMap<String,String>> {
		@Override
		protected HashMap<String,String> doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.
			return (HashMap<String,String>)API.api().call("register", values);
		}

		@Override
		protected void onPostExecute(final HashMap<String,String> result) {
			mAuthTask = null;
			showProgress(false);
			RegisterActivity.this.setResult(result.get("result").equals("true")?HvsIApp.REGISTER_OK:HvsIApp.REGISTER_INCOMPLETE);
			if (result.get("result").equals("true")) {
				finish();
			} else {
				AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
				int res = R.string.error_registration_other;
				res = result.get("message").equals("dup")?R.string.error_registration_dup:res;
				res = result.get("message").equals("pass")?R.string.error_registration_pass:res;
				builder.setMessage(getString(res)).
						setTitle(getString(R.string.error_bad_registration)).
						setPositiveButton("OK", null).
						show();
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}
}
