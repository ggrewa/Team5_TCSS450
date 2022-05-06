package edu.uw.tcss450.team5.holochat.ui.contacts.contact_tabs;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.MenuItem;

import edu.uw.tcss450.team5.holochat.R;
import edu.uw.tcss450.team5.holochat.databinding.ActivityContactTabbedBinding;
import edu.uw.tcss450.team5.holochat.ui.contacts.ui.main.SectionsPagerAdapter;

/**
 * Controls the logic to navigate through the top navigation contact tabs
 */
public class ContactTabbedActivity extends AppCompatActivity {

    private ActivityContactTabbedBinding binding;

    private TabLayout mTabs;

    private int[] mTabIcons = { R.drawable.ic_mycontacts_black_24dp,
            R.drawable.ic_friend_request_black_24dp,
            R.drawable.ic_user_search_black_24dp};

    private AppBarConfiguration mAppBarConfiguration;

    /**
     * Initialize the layout and navigation of the contacts activity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityContactTabbedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //tabs
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
        mTabs = binding.tabs;
        mTabs.setupWithViewPager(viewPager);
        FloatingActionButton fab = binding.fab;
        setupTabIcons();

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_contacts, R.id.navigation_recents, R.id.navigation_weather)
                .build();

        //on point action button
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Hello, this button does absolutely nothing rn ( ͡° ͜ʖ ͡°)", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

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