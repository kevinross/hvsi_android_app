package ca.hvsi.app;


import java.util.HashMap;

import roboguice.inject.InjectView;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import ca.hvsi.lib.PostDict;

import com.usepropeller.routable.Router;

import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockActivity;
import com.google.gson.internal.LinkedTreeMap;

import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;

@SuppressLint("NewApi")
public class PostActivity extends RoboSherlockActivity {
	
	@InjectView(R.id.post_content_container) View post_content_container;
	@InjectView(R.id.post_content) WebView post_content;
	@InjectView(R.id.post_load_status) View post_load_status;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post);
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setTitle(getString(R.string.pages_post_loading));
		Bundle intent_extras = getIntent().getExtras();
		new updatePostDetails().execute(this, Integer.parseInt(intent_extras.getString("pid")));
		post_content.setWebViewClient(new WebViewClient() {  
			  @Override  
			  public boolean shouldOverrideUrlLoading(WebView view, String url) {  
			    if (url.startsWith("http://") || url.startsWith("https://")) { //NON-NLS  
			    	Router.sharedRouter().openExternal(url); 
			    } else {
			    	Router.sharedRouter().open(url.substring(1));
			    }
			    return true;  
			  }  
			});
	}
	private void show_progress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			post_load_status.setVisibility(View.VISIBLE);
			post_load_status.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							post_load_status.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			post_content_container.setVisibility(View.VISIBLE);
			post_content_container.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							post_content_container.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			post_load_status.setVisibility(show ? View.VISIBLE : View.GONE);
			post_content_container.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}
	private class updatePostDetails extends AsyncTask<Object, Void, HashMap> {
		RoboSherlockActivity activity;
		String lang;
		@SuppressWarnings("rawtypes")
		@Override
		protected HashMap doInBackground(Object... params) {
			activity = (RoboSherlockActivity) params[0];
			lang = API.lang();
			return (HashMap) API.blog().call("post_dict", params[1]);
		}
		
		@SuppressWarnings("rawtypes")
		protected void onPostExecute(HashMap result) {
			activity.setTitle((String)((LinkedTreeMap)result.get(lang)).get("title"));
			String content = ((String)((LinkedTreeMap)result.get(lang)).get("content"));
			post_content.loadData(String.format("<html><body>%s</body></html>", content), "text/html", "utf-8");
			show_progress(false);
		}
	}
}
