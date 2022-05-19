/*
 * TCSS450
 * Mobile Application Programming
 * Spring 2022
 */
package edu.uw.tcss450.team5.holochat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.auth0.android.jwt.JWT;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import edu.uw.tcss450.team5.holochat.model.UserInfoViewModel;
import edu.uw.tcss450.team5.holochat.ui.chats.MyAdapter;
import edu.uw.tcss450.team5.holochat.utils.AppSharedPref;

/*
 * Class for the Main Activity.
 *
 * @author Charles Bryan
 * @version Spring 2022
 */
public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    AppSharedPref mPref;

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MainActivityArgs args = MainActivityArgs.fromBundle(getIntent().getExtras());

/*        new ViewModelProvider(this,
                new UserInfoViewModel.UserInfoViewModelFactory(args.getEmail(), args.getJwt())
        ).get(UserInfoViewModel.class);*/

        //JWT view model (Note: these labels are token stored in the web service JWT sign!)
        JWT jwt = new JWT(args.getJwt());
        int memberID = jwt.getClaim("memberid").asInt();
        String username = jwt.getClaim("username").asString();
        String firstName = jwt.getClaim("first").asString();
        String lastName = jwt.getClaim("last").asString();
        String email= jwt.getClaim("email").asString();
        Log.i("MAIN", " Logged In: " + email + "|" + memberID + "|" + username + "|" +
                firstName + "|" + lastName);
        new ViewModelProvider(this,
                new UserInfoViewModel.UserInfoViewModelFactory(args.getEmail(), firstName,
                        lastName, username, memberID, args.getJwt())
        ).get(UserInfoViewModel.class);

        setContentView(R.layout.activity_main);

        //BOTTOM NAVIGATION
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_contacts, R.id.navigation_home, R.id.navigation_weather)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);


        // start of theme implementation. Sets theme at start of app run based on settings.
        mPref = new AppSharedPref(this);
        mPref.initializeTheme(); //set theme based on preference in AppSharedPref class



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(item.getItemId()) {
            case R.id.action_settings:
                Log.d("SETTINGS", "Clicked");
                openSettings();
                return true;
            case R.id.action_signout:
                Log.d("SIGNOUT", "Clicked");
                signOut();
                return true;
            case android.R.id.home:
                this.finish(); //back button hit pop off the stack
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }

    }

    /**
     * Navigates the activity to the settings fragment
     */
    public void openSettings() {
        NavController navController2 = Navigation.findNavController(this, R.id.nav_host_fragment);
        navController2.navigate(R.id.navigation_settings);
    }



    /**
     * Signs the user out
     */
    public void signOut() {

    }



}