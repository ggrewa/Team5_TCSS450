package edu.uw.tcss450.team5.holochat.ui.contacts.contact_tabs;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import edu.uw.tcss450.team5.holochat.R;

/**
 * Fragment that contains tabs used for contact fragments.
 * Allows top navigation to switch between tabbed related tasks.
 *
 * @author Kenneth Ahrens
 */
public class ContactTabFragment extends Fragment {

    private TabLayout mTabs; //layout manager for tabs like text and icon
    private ContactTabsAdapter mCollectionAdapter; //gets the fragment of the tab
    private ViewPager2 mViewPager; //allows switching between fragments

    /*
     * Icons of the tabs
     */
    private int[] mTabIcons = { R.drawable.ic_mycontacts_black_24dp,
            R.drawable.ic_friend_request_black_24dp,
            R.drawable.ic_user_search_black_24dp};


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contact_tab, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mCollectionAdapter = new ContactTabsAdapter(this);
        mViewPager = view.findViewById(R.id.pager);
        mViewPager.setAdapter(mCollectionAdapter);

        mTabs = view.findViewById(R.id.contact_tab_layout);
        new TabLayoutMediator(mTabs, mViewPager,
                (tab, position) -> tab.setText(mCollectionAdapter.getLabel(position))
        ).attach();

        setupTabIcons();
    }

    /**
     * Sets the icons of the top navigation tabs
     * It is hard coded in:
     * 0 = contacts
     * 1 = requests
     * 2 = search
     */
    private void setupTabIcons() {
        mTabs.getTabAt(0).setIcon(mTabIcons[0]);
        mTabs.getTabAt(1).setIcon(mTabIcons[1]);
        mTabs.getTabAt(2).setIcon(mTabIcons[2]);
    }


}