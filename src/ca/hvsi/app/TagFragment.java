package ca.hvsi.app;

import java.util.EnumMap;
import java.util.Map;

import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import ca.hvsi.lib.Account;
import ca.hvsi.lib.Player;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentIntegratorSupportV4;
import com.google.zxing.integration.android.IntentResult;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass. Activities that
 * contain this fragment must implement the
 * {@link TagFragment.OnFragmentInteractionListener} interface to handle
 * interaction events. Use the {@link TagFragment#newInstance} factory method to
 * create an instance of this fragment.
 * 
 */
public class TagFragment extends RoboFragment implements HvsIFragment {

	private OnFragmentInteractionListener mListener;
	@InjectView(R.id.qr_code) ImageView qr_code_view;
	@InjectView(R.id.human_code) EditText human_code_view;
	@InjectView(R.id.zombie_code) EditText zombie_code_view;
	@InjectView(R.id.human_code_doscan) Button human_code_doscan;
	@InjectView(R.id.human_code_dotag) Button human_code_dotag;
	@InjectView(R.id.id_code) TextView id_code;

	/**
	 * Use this factory method to create a new instance of this fragment using
	 * the provided parameters.
	 * 
	 * @param param1
	 *            Parameter 1.
	 * @param param2
	 *            Parameter 2.
	 * @return A new instance of fragment TagFragment.
	 */
	// TODO: Rename and change types and number of parameters
	public static TagFragment newInstance(String param1, String param2) {
		TagFragment fragment = new TagFragment();
		return fragment;
	}

	public TagFragment() {
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
		View me =  inflater.inflate(R.layout.fragment_tag, container, false);
		new updateTagView().execute(this);
		me.findViewById(R.id.human_code_doscan).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				IntentIntegratorSupportV4 scanner = new IntentIntegratorSupportV4(TagFragment.this);
				scanner.initiateScan();
			}
		});
		me.findViewById(R.id.human_code_dotag).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				handleTag(human_code_view.getText().toString());
				human_code_view.setText(null);
			}
		});
		return me;
	}
	private void handleTag(String tag) {
		new AlertDialog.Builder(getActivity()).setMessage(tag).setTitle("Code").show();
	}
	public void onActivityResult(int req, int res, Intent data) {
		switch(req) {
		case IntentIntegratorSupportV4.REQUEST_CODE:
			IntentResult scanResult = IntentIntegratorSupportV4.parseActivityResult(req, res, data);
			if (scanResult!=null) {
				handleTag(scanResult.getContents());
			}
			break;
		}
	}
	
	public void updateMode() {
		new updateTagView().execute(this);
	}
	// true = show only stuff relevant to humans (QR Code, text code)
	// false = show only stuff relevant to zombies (human code entry, buttons)
	// null = show zombie stuff plus zombie id for stations&admins
	public void set_mode(Boolean human) {
		if (human == null) {
			zombie_code_view.setVisibility(View.VISIBLE);
			human = false;
		} else {
			zombie_code_view.setVisibility(View.GONE);
		}
		qr_code_view.setVisibility(human?View.VISIBLE:View.GONE);
		id_code.setVisibility(human?View.VISIBLE:View.GONE);
		human_code_doscan.setVisibility(!human?View.VISIBLE:View.GONE);
		human_code_dotag.setVisibility(!human?View.VISIBLE:View.GONE);
		human_code_view.setVisibility(!human?View.VISIBLE:View.GONE);
	}
	private static class updateTagView extends AsyncTask<TagFragment, Void, Account> {
		TagFragment parent;
		@Override
		protected Account doInBackground(TagFragment... params) {
			parent = params[0];
			return (Account)API.self();
		}
		
		protected void onPostExecute(Account self) {
			if (self.__meta__.name.equals("Player")) {
				Player p = (Player)self;
				if (!p.signedin || p.state.equals("human")) {
					parent.set_mode(true);
					try {
						Bitmap bm = encodeAsBitmap(p.game_id, 300);
						parent.qr_code_view.setImageBitmap(bm);
						parent.id_code.setText(p.game_id);
					} catch (WriterException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else if (p.state.equals("zombie")) {
					parent.set_mode(false);
				}
			} else {
				parent.set_mode(null);
			}
		}
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
	public void clear_self() {
		ViewGroup v = (ViewGroup)this.getView();
		if (v == null)
			return;
		for (int i = 0; i < v.getChildCount(); i++)
			v.getChildAt(i).setVisibility(View.GONE);
	}

	  private static final int WHITE = 0xFFFFFFFF;
	  private static final int BLACK = 0xFF000000;

	static Bitmap encodeAsBitmap(String contents, int dimension) throws WriterException {
	    String contentsToEncode = contents;
	    if (contentsToEncode == null) {
	      return null;
	    }
	    Map<EncodeHintType,Object> hints = null;
	    String encoding = guessAppropriateEncoding(contentsToEncode);
	    if (encoding != null) {
	      hints = new EnumMap<EncodeHintType,Object>(EncodeHintType.class);
	      hints.put(EncodeHintType.CHARACTER_SET, encoding);
	    }
	    BitMatrix result;
	    try {
	      result = new MultiFormatWriter().encode(contentsToEncode, BarcodeFormat.QR_CODE, dimension, dimension, hints);
	    } catch (IllegalArgumentException iae) {
	      // Unsupported format
	      return null;
	    }
	    int width = result.getWidth();
	    int height = result.getHeight();
	    int[] pixels = new int[width * height];
	    for (int y = 0; y < height; y++) {
	      int offset = y * width;
	      for (int x = 0; x < width; x++) {
	        pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
	      }
	    }

	    Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
	    bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
	    return bitmap;
	  }

	  private static String guessAppropriateEncoding(CharSequence contents) {
	    // Very crude at the moment
	    for (int i = 0; i < contents.length(); i++) {
	      if (contents.charAt(i) > 0xFF) {
	        return "UTF-8";
	      }
	    }
	    return null;
	  }


}
