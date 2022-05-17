package edu.uw.tcss450.team5.holochat.ui.contacts;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.uw.tcss450.team5.holochat.io.RequestQueueSingleton;
import edu.uw.tcss450.team5.holochat.model.UserInfoViewModel;

/**
 * utilizes a web service to retrieve all contacts of a user and stores into a view model
 * @author tarnveermangat
 */
public class ContactListViewModel extends AndroidViewModel {
    private MutableLiveData<List<Contact>> mContacts;
    private UserInfoViewModel userInfoViewModel;

    public ContactListViewModel(@NonNull Application application) {
        super(application);
        mContacts = new MutableLiveData<>();
        mContacts.setValue(new ArrayList<>());
    }
    public void addContactListObserver(@NonNull LifecycleOwner owner,
                                       @NonNull Observer<? super List<Contact>> observer) {
        mContacts.observe(owner, observer);
    }


    /**
     * Method to handle volley errors.
     * @param error VolleyError object.
     */
    private void handleError(final VolleyError error) {
        if (error != null && error.getMessage() != null) {
            Log.e("CONNECTION ERROR", error.getMessage());
            throw new IllegalStateException(error.getMessage());
        }
    }

    /**
     * @author Tarnveer Mangat
     * method to handle the result from the database call
     * @param result of the database call
     */
    private void handleResult(final JSONObject result) {
        Log.i("contacts", result.toString());
        if (!result.has("values")) {
            throw new IllegalStateException("Unexpected response in ContactsViewModel: " + result);
        }
        try {
            ArrayList<Contact> contacts = new ArrayList<>();

            JSONArray jsonUsers = result.getJSONArray("values");
            for(int i = 0; i < jsonUsers.length(); i++) {
                JSONObject user = (new JSONObject(jsonUsers.getString(i)));
                contacts.add(new Contact(user.getString("Email"), user.getString("FirstName"),
                        user.getString("LastName"), user.getString("Username"), i));
            }
            mContacts.setValue(contacts);
            Log.d("CONTACTS", "" + mContacts.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * connects to the database and recievers a list of contacts for the current user.
     * @param email the email of the current user
     * @param jwt the auth token
     */
    public void connect(final String email, final String jwt) {



        String url = "url endpoint" + email;
        JSONObject body = new JSONObject();


        Request request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                body,
                this::handleResult,
                this::handleError) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                // add headers <key,value>
                headers.put("Authorization", jwt);
                return headers;
            }
        };
        Log.i("contacts", url);
        request.setRetryPolicy(new DefaultRetryPolicy(
                10_000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //Instantiate the RequestQueue and add the request to the queue
        RequestQueueSingleton.getInstance(getApplication().getApplicationContext())
                .addToRequestQueue(request);


    }

}