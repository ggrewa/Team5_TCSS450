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

public class AllMemberListViewModel extends AndroidViewModel {
    private MutableLiveData<List<Contact>> mContactList;
    private final MutableLiveData<JSONObject> mResponse;

    public AllMemberListViewModel(@NonNull Application application) {
        super(application);
        mContactList = new MutableLiveData<>(new ArrayList<>());
        mResponse = new MutableLiveData<>();
        mResponse.setValue(new JSONObject());

    }

    /**
     * Add an observer to the contact request list view model.
     *
     * @param owner    the owner
     * @param observer the observer
     */
    public void addContactRequestListObserver(@NonNull LifecycleOwner owner,
                                              @NonNull Observer<? super List<Contact>> observer) {
        mContactList.observe(owner, observer);
    }

    public void addResponseObserver(@NonNull LifecycleOwner owner,
                                    @NonNull Observer<? super JSONObject> observer) {
        mResponse.observe(owner, observer);
    }

    /**
     * Connects to webservice endpoint to retrieve a list of contacts.
     *
     * @param jwt a valid jwt.
     */
    public void connectGet(String jwt, int theUserID) {
        String base_url = getApplication().getResources().getString(R.string.base_url_service);
        String url = base_url + "contacts/getNonFriends/" + theUserID;
        Request request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null, //no body for this get request
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
     *
     * @param result result from webservice.
     */
    private void handleSuccess(final JSONObject result) {
        ArrayList<Contact> temp = new ArrayList<>();
        try {
            JSONArray contacts = result.getJSONArray("contacts");
            for (int i = 0; i < contacts.length(); i++) {
                JSONObject contact = contacts.getJSONObject(i);
                String email = contact.getString("email");
                String firstName = contact.getString("firstName");
                String lastName = contact.getString("lastName");
                String username = contact.getString("userName");
                int memberID = contact.getInt("memberId");
                Contact entry = new Contact(email, firstName, lastName, username, memberID);
                temp.add(entry);
            }
        } catch (JSONException e) {
            Log.e("JSON PARSE ERROR", "Found in handle Success ContactViewModel");
            Log.e("JSON PARSE ERROR", "Error: " + e.getMessage());
        }
        mContactList.setValue(temp);
    }

    /**
     * Send a friend request to the passed in member id
     * Will be verified with 0 until the user accepts then it becomes 1
     *
     * @param jwt A valid jwt
     * @param memberID the member ID to send the request to
     */
    public void postFriendRequest(final String jwt, int memberID) {
        String base_url = getApplication().getResources().getString(R.string.base_url_service);
        String url = base_url + "contacts";

        Log.i("SEND_FRIEND", "/contacts/" + memberID);

        JSONObject body = new JSONObject();
        try {
            body.put("memberId", memberID);
            body.put("verified", 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Request request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                body, //push token found in the JSONObject body
                mResponse::setValue, // we get a response but do nothing with it
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
        RequestQueueSingleton.getInstance(getApplication().getApplicationContext())
                .addToRequestQueue(request);
    }

    /**
     * HAndles Errors when connecting to contacts endpoints
     *
     * @param error the error.
     */
    private void handleError(final VolleyError error) {
        Log.e("CONNECTION ERROR", "Oooops no contacts");
        //throw new IllegalStateException(error.getMessage());
    }

}
