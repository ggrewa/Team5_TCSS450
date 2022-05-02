package edu.uw.tcss450.team5.holochat.ui.contacts.ui.main;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import edu.uw.tcss450.team5.holochat.R;
import edu.uw.tcss450.team5.holochat.ui.contacts.contact_tabs.ContactListFragment;
import edu.uw.tcss450.team5.holochat.ui.contacts.contact_tabs.ContactRequestFragment;
import edu.uw.tcss450.team5.holochat.ui.contacts.contact_tabs.ContactSearchFragment;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.contact_tab_text_1, R.string.contact_tab_text_2, R.string.contact_tab_text_3};
    private final Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        //return PlaceholderFragment.newInstance(position + 1);
        Fragment fragment = null; //fragment to navigate too
        switch(position) {
            case 0:
                fragment = new ContactListFragment();
                break;
            case 1:
                fragment = new ContactRequestFragment();
                break;
            case 2:
                fragment = new ContactSearchFragment();
                break;
        }

        return fragment;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return 3;
    }
}