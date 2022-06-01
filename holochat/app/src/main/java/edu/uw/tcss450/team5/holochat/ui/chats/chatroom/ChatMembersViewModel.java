package edu.uw.tcss450.team5.holochat.ui.chats.chatroom;

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
import edu.uw.tcss450.team5.holochat.ui.contacts.info.Contact;
import edu.uw.tcss450.team5.holochat.ui.contacts.info.ContactListSingle;

/**
 * Stores chat members that are in a specific chatroom
 *
 * @author Ken
 */
public class ChatMembersViewModel extends AndroidViewModel {

    /**
     * A Map of Lists of Chat Rooms.
     * The Key represents the chatid
     * The value represents the List of (known) members for that chatid
     */
    private Map<Integer, MutableLiveData<List<ContactListSingle>>> mMembers;
    private MutableLiveData<List<Contact>> mMembersList;
    private final MutableLiveData<JSONObject> mResponse;

    public ChatMembersViewModel(@NonNull Application application) {
        super(application);
        mMembers = new HashMap<>();
        mMembersList = new MutableLiveData<>(new ArrayList<>());
        mResponse = new MutableLiveData<>();
        mResponse.setValue(new JSONObject());
    }

    /**
     * Register as an observer to listen to a specific Emails list of chat rooms.
     * @param chatid the chatid of the room to observe
     * @param owner the fragments lifecycle owner
     * @param observer the observer
     */
    public void addContactObserver(Integer chatid,
                                   @NonNull LifecycleOwner owner,
                                   @NonNull Observer<? super List<ContactListSingle>> observer) {
        getOrCreateMapEntry(chatid).observe(owner, observer);
    }

    /**
     * Return a reference to the List<> associated with the email. If the View Model does
     * not have a mapping for this email, it will be created.
     *
     * WARNING: While this method returns a reference to a mutable list, it should not be
     * mutated externally in client code. Use public methods available in this class as
     * needed.
     *
     * @param chatID the members linked to the id to retrieve
     * @return a reference to the list of messages
     */
    public List<ContactListSingle> getContactListByChatID(final Integer chatID) {
        return getOrCreateMapEntry(chatID).getValue();
    }

    private MutableLiveData<List<ContactListSingle>> getOrCreateMapEntry(final Integer chatID) {
        if(!mMembers.containsKey(chatID)) {
            mMembers.put(chatID, new MutableLiveData<>(new ArrayList<>()));
        }
        return mMembers.get(chatID);
    }

    /**
     * Makes a request to the web service to get the rooms of a given email.
     * Parses the response and adds the ChatRoomSingle object to the List associated with the
     * email. Informs observers of the update.
     *
     * @param jwt the users signed JWT
     */
    public void getChatMembers(final String jwt, final int theChatID) {
        String url = getApplication().getResources().getString(R.string.base_url_service) +
                "chats/viewmembers/" + theChatID;

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
     * @param chatID
     * @param contact
     */
    public void addContact(final Integer chatID, final ContactListSingle contact) {
        List<ContactListSingle> list = getContactListByChatID(chatID);
        list.add(contact);
        getOrCreateMapEntry(chatID).setValue(list);
    }

    private void handleSuccess(final JSONObject response) {
        List<ContactListSingle> list;
        if (!response.has("contacts")) {
            throw new IllegalStateException("Unexpected response in ChatRoomViewModel: " + response);
        }
        try {
            int chatID = Integer.parseInt(response.getString("chatID"));
            list = getContactListByChatID(chatID);
            JSONArray contacts = response.getJSONArray("contacts");
            for(int i = 0; i < contacts.length(); i++) {
                JSONObject contact = contacts.getJSONObject(i);
                ContactListSingle cContact = new ContactListSingle(
                        contact.getInt("memberid"),
                        contact.getString("username"),
                        contact.getString("email")
                );
                if (list.stream().noneMatch(id -> cContact.getContactMemberID()==id.getContactMemberID())) {
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
            getOrCreateMapEntry(chatID).setValue(list);
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
        mMembersList.observe(owner, observer);
    }
}
