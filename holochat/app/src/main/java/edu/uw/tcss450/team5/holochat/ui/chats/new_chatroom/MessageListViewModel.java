package edu.uw.tcss450.team5.holochat.ui.chats.new_chatroom;

import android.app.Application;
import android.util.Log;
import edu.uw.tcss450.team5.holochat.R;
import edu.uw.tcss450.team5.holochat.ui.chats.chatroom_list.MessagePost;

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

/**
 * Stores info about a list of messages (chatrooms)
 *
 * @author Ken
 */
public class MessageListViewModel extends AndroidViewModel {

    private MutableLiveData<List<MessagePost>> mMessageList;
    private final MutableLiveData<JSONObject> mResponse;

    /**
     * A View model for message lists.
     *
     * @param application the application
     *
     */
    public MessageListViewModel(@NonNull Application application){
        super(application);
        mMessageList = new MutableLiveData<>(new ArrayList<>());
        mResponse = new MutableLiveData<>();
        mResponse.setValue(new JSONObject());
    }

    /**
     * Adds a observer to the message list.
     *
     * @param owner the owner
     * @param observer the observer.
     */
    public void addMessageListObserver(@NonNull LifecycleOwner owner,
                                       @NonNull Observer<? super List<MessagePost>> observer) {
        mMessageList.observe(owner, observer);
    }


    /**
     * HAndles any errors with connecting to the webservice.
     *
     * @param error the error
     */
    private void handleError(final VolleyError error) {
        Log.e("CONNECTION ERROR", "OOOPS no message lists");
    }

    /**
     * Handles results from a call to the webservice.
     */
    private void handleResult(final JSONObject result) {
        ArrayList<MessagePost> temp = new ArrayList<>();
        try {
            JSONArray messages = result.getJSONArray("chats");
            for (int i = 0; i < messages.length(); i++) {
                JSONObject message = messages.getJSONObject(i);
                String name = message.getString("name");
                int key = message.getInt("chat");
                MessagePost post = new MessagePost(name, key);
                temp.add(post);
            }
        } catch (JSONException e) {
            Log.e("JSON PARSE ERROR", "Found in handle Success MessageListViewModel");
            Log.e("JSON PARSE ERROR", "Error: " + e.getMessage());
        }
        mMessageList.setValue(temp);
    }

    /**
     * Connects to a webservice to get a list of chatrooms a member is a part of.
     *
     * @param jwt a valid jwt
     */
    public void connectGet (String jwt){
        //need a endpoint
        String base_url = getApplication().getResources().getString(R.string.base_url_service);
        String url = base_url + "contacts/chatlist";
        Request request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null, //no body for this get request
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
        request.setRetryPolicy(new DefaultRetryPolicy(
                10_000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //Instantiate the RequestQueue and add the request to the queue
        Volley.newRequestQueue(getApplication().getApplicationContext())
                .add(request);
    }

    /**
     * Connect to webservice to delete a chatroom for the user
     * @param jwt
     * @param chatID
     * @param email
     */
    public void connectDelete(String jwt, int chatID, String email){
        String base_url = getApplication().getResources().getString(R.string.base_url_service);
        String url = base_url + "chats/" + chatID + "/" + email;

        Request request = new JsonObjectRequest(
                Request.Method.DELETE,
                url,
                null, //no body for this get request
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
}
