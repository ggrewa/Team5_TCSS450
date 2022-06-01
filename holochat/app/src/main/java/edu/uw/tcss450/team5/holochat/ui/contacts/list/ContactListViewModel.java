package edu.uw.tcss450.team5.holochat.ui.contacts.list;

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

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import edu.uw.tcss450.team5.holochat.io.RequestQueueSingleton;
import edu.uw.tcss450.team5.holochat.R;
import edu.uw.tcss450.team5.holochat.model.UserInfoViewModel;
import edu.uw.tcss450.team5.holochat.ui.contacts.info.Contact;
import edu.uw.tcss450.team5.holochat.ui.contacts.info.ContactListSingle;

/**
 * utilizes a web service to retrieve all contacts of a user and stores into a view model
 *
 * @author Ken
 * @author Tarnveer
 */
public class ContactListViewModel extends AndroidViewModel {

    /**
     * A Map of Lists of Chat Rooms.
     * The Key represents the Email
     * The value represents the List of (known) rooms for that Email.
     */
    private Map<String, MutableLiveData<List<ContactListSingle>>> mContact;
    private MutableLiveData<List<Contact>> mContactList;
    private final MutableLiveData<JSONObject> mResponse;

    private MutableLiveData<List<Contact>> mContacts;
    private UserInfoViewModel userInfoViewModel;

    public ContactListViewModel(@NonNull Application application) {
        super(application);
        mContact = new HashMap<>();
        mContacts = new MutableLiveData<>();
        mContacts.setValue(new ArrayList<>());
        mContactList = new MutableLiveData<>(new ArrayList<>());
        mResponse = new MutableLiveData<>();
        mResponse.setValue(new JSONObject());
    }

    /**
     * Register as an observer to listen to a specific Emails list of chat rooms.
     * @param email the email of the room to observer
     * @param owner the fragments lifecycle owner
     * @param observer the observer
     */
    public void addContactObserver(String email,
                                   @NonNull LifecycleOwner owner,
                                   @NonNull Observer<? super List<ContactListSingle>> observer) {
        getOrCreateMapEntry(email).observe(owner, observer);
    }

    /**
     * Return a reference to the List<> associated with the email. If the View Model does
     * not have a mapping for this email, it will be created.
     *
     * WARNING: While this method returns a reference to a mutable list, it should not be
     * mutated externally in client code. Use public methods available in this class as
     * needed.
     *
     * @param email the email of the room List to retrieve
     * @return a reference to the list of messages
     */
    public List<ContactListSingle> getContactListByEmail(final String email) {
        return getOrCreateMapEntry(email).getValue();
    }

    private MutableLiveData<List<ContactListSingle>> getOrCreateMapEntry(final String email) {
        if(!mContact.containsKey(email)) {
            mContact.put(email, new MutableLiveData<>(new ArrayList<>()));
        }
        return mContact.get(email);
    }

    /**
     * Makes a request to the web service to get the rooms of a given email.
     * Parses the response and adds the ChatRoomSingle object to the List associated with the
     * email. Informs observers of the update.
     *
     * @param jwt the users signed JWT
     */
    public void getContacts( final String jwt) {
        String url = getApplication().getResources().getString(R.string.base_url_service) +
                "contacts/";

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
        RequestQueueSingleton.getInstance(getApplication().getApplicationContext())
                .addToRequestQueue(request);

        //code here will run
    }

    /**
     * When a room is created from outside the view model. Add it with this method.
     * @param email
     * @param contact
     */
    public void addContact(final String email, final ContactListSingle contact) {
        List<ContactListSingle> list = getContactListByEmail(email);
        list.add(contact);
        getOrCreateMapEntry(email).setValue(list);
    }

    /**
     * Connects to webservice endpoint to delete a given contact
     *
     * @param jwt a valid jwt
     * @param memberID to delete/unfriend
     */
    public void deleteContact (String jwt, int memberID){
        String url = getApplication().getResources().getString(R.string.base_url_service)  + "contacts/" + memberID;
        Request request = new JsonObjectRequest(
                Request.Method.DELETE,
                url,
                null, //no body for this request
                mResponse::setValue,
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
     * Handles a successful connection with webservice
     * In this view model it puts all "verified" contacts and stores it into a list
     * @param response
     */
    private void handleSuccess(final JSONObject response) {
        List<ContactListSingle> list;
        if (!response.has("contacts")) {
            throw new IllegalStateException("Unexpected response in ChatRoomViewModel: " + response);
        }
        try {
            list = getContactListByEmail(response.getString("email"));
            JSONArray contacts = response.getJSONArray("contacts");
            for(int i = 0; i < contacts.length(); i++) {
                JSONObject contact = contacts.getJSONObject(i);
                ContactListSingle cContact = new ContactListSingle(
                        contact.getInt("memberId"),
                        contact.getString("userName"),
                        contact.getString("email")
                );
                //only get verified contacts to show on contactlist
                int verified = contact.getInt("verified");
                if (verified == 1 && list.stream().noneMatch(id -> cContact.getContactMemberID()==id.getContactMemberID())) {
                    // don't add a duplicate
                    list.add(0, cContact);
                } else {
                    // this shouldn't happen but could with the asynchronous
                    // nature of the application
                    Log.wtf("Chat room already received",
                            "Or duplicate id:" + cContact.getContactMemberID());
                }

            }
            //inform observers of the change (setValue)
            getOrCreateMapEntry(response.getString("email")).setValue(list);
        }catch (JSONException e) {
            Log.e("JSON PARSE ERROR", "Found in handle Success ChatRoomViewModel");
            Log.e("JSON PARSE ERROR", "Error: " + e.getMessage());
        }
    }

    private void handleError(final VolleyError error) {
        if (Objects.isNull(error.networkResponse)) {
            Log.e("NETWORK ERROR", error.getMessage());
        }
        else {
            String data = new String(error.networkResponse.data, Charset.defaultCharset());
            Log.e("CLIENT ERROR",
                    error.networkResponse.statusCode +
                            " " +
                            data);
        }
    }

    /**
     * Add an observer to the contact list view model.
     *
     * @param owner the owner
     * @param observer the observer
     */
    public void addContactListObserver(@NonNull LifecycleOwner owner,
                                       @NonNull Observer<? super List<Contact>> observer){
        mContactList.observe(owner, observer);
    }
}
