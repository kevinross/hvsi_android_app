package ca.hvsi.app;

import ca.hvsi.lib.Admin;
import ca.hvsi.lib.Player;
import ca.hvsi.lib.Station;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class HvsIFragments extends FragmentPagerAdapter {
	public Class[] fragments = {InfoFragment.class, BlogFragment.class, TagFragment.class, ProfileFragment.class, StationFragment.class, AdminFragment.class};
	public HvsIFragments(FragmentManager fragmentManager) {
		super(fragmentManager);
	}
	
	@Override
	public String getPageTitle(int arg0) {
		return HvsIApp.self.getResources().getStringArray(R.array.tabs)[arg0];
	}

	@Override
	public Fragment getItem(int arg0) {
		try {
			return (Fragment) fragments[arg0].newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public int getCount() {
		if (API.logged_in() && API.self() != null && API.self().getClass().equals(Player.class))
			return 4;
		if (API.logged_in() && API.self() != null && API.self().getClass().equals(Station.class))
			return 5;
		if (API.logged_in() && API.self() != null && API.self().getClass().equals(Admin.class))
			return 6;
		return 2;
	}

}
