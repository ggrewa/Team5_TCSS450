/*
 * TCSS450
 * Mobile Application Programming
 * Spring 2022
 */
package edu.uw.tcss450.team5.holochat;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import edu.uw.tcss450.team5.holochat.model.PushyTokenViewModel;
import edu.uw.tcss450.team5.holochat.utils.AppSharedPref;
import me.pushy.sdk.Pushy;

/*
 * Class for the Authorization Activity.
 *
 * @author Charles Bryan
 * @version Spring 2022
 */
public class AuthActivity extends AppCompatActivity {

    AppSharedPref mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        // start of theme implementation. Sets theme at start of app run based on settings.
        mPref = new AppSharedPref(this);
        mPref.initializeTheme(); //set theme based on preference in AppSharedPref class

        //If it is not already running, start the Pushy listening service
        Pushy.listen(this);
        initiatePushyTokenRequest();
    }

    private void initiatePushyTokenRequest() {
        new ViewModelProvider(this).get(PushyTokenViewModel.class).retrieveToken();
    }

}
