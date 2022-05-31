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

import edu.uw.tcss450.team5.holochat.R;
import edu.uw.tcss450.team5.holochat.io.RequestQueueSingleton;
import edu.uw.tcss450.team5.holochat.ui.contacts.Contact;
import edu.uw.tcss450.team5.holochat.ui.contacts.ContactListSingle;

/**
 * utilizes a web service to retrieve all contacts of a user and stores into a view model
 *
 * @author Ken
 */
public class ChatMembersViewModel extends AndroidViewModel {

    /**
     * A Map of Lists of Chat Rooms.
     * The Key represents the Email
     * The value represents the List of (known) rooms for that Email.
     */
    private Map<Integer, MutableLiveData<List<ContactListSingle>>> mMembers;
    private MutableLiveData<List<Contact>> mContactList;
    private final MutableLiveData<JSONObject> mResponse;

    public ChatMembersViewModel(@NonNull Application application) {
        super(application);
        mMembers = new HashMap<>();
        mContactList = new MutableLiveData<>(new ArrayList<>());
        mResponse = new MutableLiveData<>();
        mResponse.setValue(new JSONObject());
    }

    /**
     * Register as an observer to listen to a specific Emails list of chat rooms.
     * @param chatId to observer
     * @param owner the fragments lifecycle owner
     * @param observer the observer
     */
    public void addMemberObserver(int chatId,
                                   @NonNull LifecycleOwner owner,
                                   @NonNull Observer<? super List<ContactListSingle>> observer) {
        getOrCreateMapEntry(chatId).observe(owner, observer);
    }

    /**
     * Return a reference to the List<> associated with the email. If the View Model does
     * not have a mapping for this email, it will be created.
     *
     * WARNING: While this method returns a reference to a mutable list, it should not be
     * mutated externally in client code. Use public methods available in this class as
     * needed.
     *
     * @param theChatID
     * @return a reference to the list of messages
     */
    public List<ContactListSingle> getContactListByChatID(final int theChatID) {
        return getOrCreateMapEntry(theChatID).getValue();
    }

    private MutableLiveData<List<ContactListSingle>> getOrCreateMapEntry(final int theChatID) {
        if(!mMembers.containsKey(theChatID)) {
            mMembers.put(theChatID, new MutableLiveData<>(new ArrayList<>()));
        }
        return mMembers.get(theChatID);
    }

    /**
     * Makes a request to the web service to get the members of a given chat id
     * Parses the response and adds the ChatRoomSingle object to the List associated with the
     * email. Informs observers of the update.
     *
     * @param jwt the users signed JWT
     */
    public void getMembers( final String jwt, final int theChatID) {
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
    }

    private void handleSuccess(final JSONObject response) {
        List<ContactListSingle> list;
        if (!response.has("contacts") && !response.has("chatId")) {
            throw new IllegalStateException("Unexpected response in ChatRoomViewModel: " + response);
        }
        try {
            System.out.println("member chatid: " + response.getInt("chatId"));
            list = getContactListByChatID(response.getInt("chatId"));
            JSONArray contacts = response.getJSONArray("contacts");
            for(int i = 0; i < contacts.length(); i++) {
                JSONObject contact = contacts.getJSONObject(i);
                //store the contact from the contact row
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
            getOrCreateMapEntry(response.getInt("chatId")).setValue(list);
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
    public void addMemberListObserver(@NonNull LifecycleOwner owner,
                                       @NonNull Observer<? super List<Contact>> observer){
        mContactList.observe(owner, observer);
    }
}
