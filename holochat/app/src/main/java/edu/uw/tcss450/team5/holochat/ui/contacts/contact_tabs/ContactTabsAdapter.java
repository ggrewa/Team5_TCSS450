package edu.uw.tcss450.team5.holochat.ui.contacts.contact_tabs;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;


/**
 * Fragment state adapter used for contacts
 *
 * @author Kenneth Ahrens
 */
public class ContactTabsAdapter extends FragmentStateAdapter {

    private String mLabel;

    public ContactTabsAdapter(Fragment fragment) {
        super(fragment);
    }

    /**
     * Creates a fragment to use in a tab based on the passed in pos
     * @param thePosition tab index
     * @return
     */
    @NonNull
    @Override
    public Fragment createFragment(int thePosition) {
        Fragment fragment = null;
        switch(thePosition) {
            case 0:
                fragment = new ContactListFragment();
                break;
            case 1:
                fragment = new ContactRequestListFragment();
                break;
            case 2:
                fragment = new ContactSearchFragment();
                break;
        }
        return fragment;
    }

    /**
     * Gets the label.
     * @return a string representing the label of the tab.
     */
    public String getLabel(int position){
        String theLabel;
        if(position  == 0){
            theLabel = "Contacts";
        }else if(position == 1){
            theLabel = "Requests";
        }else{
            theLabel = "Search";
        }
        return theLabel;
    }

    @Override
    public int getItemCount() {
        return 3;
    }

}
