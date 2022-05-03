/*
 * TCSS450
 * Mobile Application Programming
 * Spring 2022
 */
package edu.uw.tcss450.team5.holochat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import edu.uw.tcss450.team5.holochat.model.UserInfoViewModel;

/*
 * Class for the Main Activity.
 *
 * @author Charles Bryan
 * @version Spring 2022
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MainActivityArgs args = MainActivityArgs.fromBundle(getIntent().getExtras());

        new ViewModelProvider(this,
                new UserInfoViewModel.UserInfoViewModelFactory(args.getEmail(), args.getJwt())
        ).get(UserInfoViewModel.class);

        setContentView(R.layout.activity_main);

    }
}