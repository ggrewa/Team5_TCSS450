package edu.uw.tcss450.team5.holochat.ui.contacts.search;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.uw.tcss450.team5.holochat.R;
import edu.uw.tcss450.team5.holochat.io.RequestQueueSingleton;
import edu.uw.tcss450.team5.holochat.ui.contacts.info.Contact;

/**
 * Stores info on member who have requested to be friends with the user
 * This is displayed on the contacts table as verified = 0
 *
 * @author Ken
 */

public class SearchViewModel extends AndroidViewModel {
    private MutableLiveData<List<Contact>> mContactList;
    private MutableLiveData<JSONObject> mResponse;

    private Contact mContact;

    public SearchViewModel(@NonNull Application application) {
        super(application);
        mContactList = new MutableLiveData<>(new ArrayList<>());
        mResponse = new MutableLiveData<>();
        mResponse.setValue(new JSONObject());
    }

    public Contact getmContact() {
        return mContact;
    }

    /**
     * Connects to webservice endpoint to search for a user based on a string
     *
     * @param jwt a valid jwt.
     */
    public void connectGet(String jwt, String theInput) {
        String base_url = getApplication().getResources().getString(R.string.base_url_service);
        //String url = base_url + "contacts/search";
        String url = base_url + "contacts/search/" + theInput;

        theInput = theInput.toLowerCase();
        Log.i("search_connectGet", theInput + "|" + jwt);
        System.out.println("I am doing " + url);

        Request request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                this::handleSuccess,
                this::handleError) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                // add headers <key,value>
                headers.put("Authorization", jwt);
                return headers;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                10_000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //Instantiate the RequestQueue and add the request to the queue
        Volley.newRequestQueue(getApplication().getApplicationContext())
                .add(request);

    }


    /**
     * Handles a successful connection with the webservice.
     * Places all found contacts into the list
     *
     * @param result result from webservice.
     */
    private void handleSuccess(final JSONObject result) {
        ArrayList<Contact> temp = new ArrayList<>();
        try {
            JSONArray contacts = result.getJSONArray("contacts");
            System.out.println(contacts.toString());
            for (int i = 0; i < contacts.length(); i++) {
                JSONObject contact = contacts.getJSONObject(i);
                String email = contact.getString("searchemail");
                String firstname = contact.getString(("firstname"));
                String lastname = contact.getString(("lastname"));
                String username = contact.getString("username");
                int memberID = contact.getInt("memberid");

                Contact entry = new Contact(email, firstname, lastname, username, memberID);
                temp.add(entry);
                //mResponse.setValue(contact);
                System.out.println("success contact "+ email + firstname + lastname + username + memberID);
            }
        } catch (JSONException e) {
            Log.e("JSON PARSE ERROR", "Found in handle Success SearchViewModel");
            Log.e("JSON PARSE ERROR", "Error: " + e.getMessage());
        }
        mContactList.setValue(temp);
    }

    public void addResponseObserver(@NonNull LifecycleOwner owner,
                                    @NonNull Observer<? super JSONObject> observer) {
        mResponse.observe(owner, observer);
    }

    /**
     * Add an observer to the contact request list view model.
     *
     * @param owner the owner
     * @param observer the observer
     */
    public void addContactSearchListObserver(@NonNull LifecycleOwner owner,
                                              @NonNull Observer<? super List<Contact>> observer){
        mContactList.observe(owner, observer);
    }


    /**
     * HAndles Errors when connecting to contacts endpoints
     *
     * @param error the error.
     */
    private void handleError(final VolleyError error) {
        Log.e("CONNECTION ERROR", "No contacts found!");
        //throw new IllegalStateException(error.getMessage());
    }

}
