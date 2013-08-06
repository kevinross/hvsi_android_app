package ca.hvsi.app;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

import com.google.gson.internal.LazilyParsedNumber;
import com.google.gson.internal.LinkedTreeMap;

import ca.hvsi.lib.Post;
import ca.hvsi.lib.PostDict;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView;
import com.usepropeller.routable.*;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass. Activities that
 * contain this fragment must implement the
 * {@link BlogFragment.OnFragmentInteractionListener} interface to handle
 * interaction events. Use the {@link BlogFragment#newInstance} factory method
 * to create an instance of this fragment.
 * 
 */
public class BlogFragment extends RoboFragment implements OnItemClickListener, HvsIFragment {
	private OnFragmentInteractionListener mListener;
	private ListView posts;
	private ImageView no_posts;
	private LinearLayout blog_list_status;
	private List<PostDict> post_list;
	/**
	 * Use this factory method to create a new instance of this fragment using
	 * the provided parameters.
	 * 
	 * @param param1
	 *            Parameter 1.
	 * @param param2
	 *            Parameter 2.
	 * @return A new instance of fragment BlogFragment.
	 */
	// TODO: Rename and change types and number of parameters
	public static BlogFragment newInstance() {
		BlogFragment fragment = new BlogFragment();
		return fragment;
	}

	public BlogFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View tview = inflater.inflate(R.layout.fragment_blog, container, false);
		posts = (ListView) tview.findViewById(R.id.blog_post_list);
		no_posts = (ImageView) tview.findViewById(R.id.blog_list_no_posts);
		blog_list_status = (LinearLayout) tview.findViewById(R.id.blog_list_status);
		post_list = new ArrayList<PostDict>();
		posts.setOnItemClickListener(this);
		posts.setAdapter(new PostAdapter(getActivity().getApplicationContext(), R.layout.blog_list_textview, post_list));
		setRetainInstance(true);
		return tview;
	}
	private static class PostAdapter extends ArrayAdapter<PostDict> {
		private class ViewHolder {
			TextView title;
			TextView content_snippet;
			TextView date;
		}
		public PostAdapter(Context context, int resource,
				int textViewResourceId, List<PostDict> objects) {
			super(context, resource, textViewResourceId, objects);
			// TODO Auto-generated constructor stub
		}
		public PostAdapter(Context applicationContext, int blogListTextview,
				List<PostDict> obj) {
			// TODO Auto-generated constructor stub
			super(applicationContext, blogListTextview, obj);
		}
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
	        ViewHolder holder;
	        if (v == null) {
	            LayoutInflater vi =
	                (LayoutInflater)LayoutInflater.from(this.getContext().getApplicationContext());
	            v = vi.inflate(R.layout.blog_list_textview, null);
	            holder = new ViewHolder();
	            holder.title = (TextView) v.findViewById(R.id.blog_list_text_title);
	            holder.content_snippet = (TextView) v.findViewById(R.id.blog_list_text_content_snippet);
	            holder.date = (TextView)v.findViewById(R.id.blog_list_text_date);
	            v.setTag(holder);
	        }
	        else
	            holder=(ViewHolder)v.getTag();
	        PostDict obj = this.getItem(position);
	        if (obj != null) {
				String title = obj.content.title;
				String content = Html.fromHtml(obj.content.content).toString().trim();
				String date = obj.time.toYearMonthDay().toString();
	            holder.title.setText(title);
	            holder.content_snippet.setText(content);
	            holder.date.setText(date);
	        }
	        return v;
		}
	}
	private static class PostGrabber extends AsyncTask<BlogFragment, Void, LinkedList<PostDict>> {
		BlogFragment self;
		private int[] range(int start, int stop)
		{
			if (start==stop)
				return new int[]{start};
			if (stop-start <=0)
				return new int[]{};
			int[] result = new int[stop-start];

			for(int i=0;i<stop-start;i++)
				result[i] = start+i;

			return result;
		}
		protected void onPreExecute() {
			try {
				if (self.posts == null)
				return;
				if (!self.posts.isShown()) {
					self.no_posts.setVisibility(View.GONE);
					self.show_progress(true);
				}
			} catch (Exception ex) {
				return;
			}
		}
		@Override
		protected LinkedList<PostDict> doInBackground(BlogFragment... params) {
			self = params[0];
			LazilyParsedNumber first = (LazilyParsedNumber) API.blog().get("first_post_id");
			LazilyParsedNumber last = (LazilyParsedNumber) API.blog().get("last_post_id");
			LinkedList<PostDict> obj = null;
			Object val = API.blog().
					call("post_dicts", range(first.intValue(), last.intValue()));
			try {
				obj = (LinkedList<PostDict>) val;
			} catch (Exception ex) {
				try {
					obj = new LinkedList<PostDict>();
					obj.add((PostDict)val);
				} catch (Exception ex2) {
					return null;
				}
			}
			return obj;
		}

		@SuppressWarnings("unchecked")
		protected void onPostExecute(final LinkedList<PostDict> obj) {
			if (obj==null || obj.size() == 0) {
				self.no_posts.setVisibility(View.VISIBLE);
				self.posts.setVisibility(View.GONE);
				return;
			}
			self.post_list.clear();
			self.post_list.addAll(obj);
			((PostAdapter)self.posts.getAdapter()).notifyDataSetChanged();
			self.no_posts.setVisibility(View.GONE);
			self.show_progress(false);
			}
	}
	public void updateBlogPosts() {
		AsyncTask<BlogFragment, Void, LinkedList<PostDict>> blah = new PostGrabber();
		blah.execute(this);
	}
	public void onViewCreated(View view,Bundle savedInstanceState){
		updateBlogPosts();
		//new PostGrabber().execute(this);
	}

	// TODO: Rename method, update argument and hook method into UI event
	public void onButtonPressed(Uri uri) {
		if (mListener != null) {
			mListener.onFragmentInteraction(uri);
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnFragmentInteractionListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnFragmentInteractionListener");
		}
	}
	@SuppressLint("NewApi")
	private void show_progress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			blog_list_status.setVisibility(View.VISIBLE);
			blog_list_status.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							blog_list_status.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			posts.setVisibility(View.VISIBLE);
			posts.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							posts.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			blog_list_status.setVisibility(show ? View.VISIBLE : View.GONE);
			posts.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated to
	 * the activity and potentially other fragments contained in that activity.
	 * <p>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface OnFragmentInteractionListener {
		// TODO: Update argument type and name
		public void onFragmentInteraction(Uri uri);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Router.sharedRouter().open(String.format("post/view/%d", position+6));
	}

	@Override
	public void clear_self() {
		// TODO Auto-generated method stub
		
	}

}
