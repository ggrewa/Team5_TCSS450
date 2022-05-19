/*
 * TCSS450
 * Mobile Application Programming
 * Spring 2022
 */
package edu.uw.tcss450.team5.holochat.ui.auth.forgotpass;

import android.app.Application;
import android.util.Base64;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import edu.uw.tcss450.team5.holochat.io.RequestQueueSingleton;

/*
 * Class for the Forgot Password view model.
 *
 * @author Charles Bryan
 * @author Gurleen Grewal
 * @version Spring 2022
 */
public class ForgotPasswordViewModel extends AndroidViewModel {

    /** JSON response from server when user tries changing password. */
    private MutableLiveData<JSONObject> mResponse;

    /**
     * Constructor the initializes the forgot password view model.
     *
     * @param application application tied to the view model
     */
    public ForgotPasswordViewModel(@NonNull Application application) {
        super(application);
        mResponse = new MutableLiveData<>();
        mResponse.setValue(new JSONObject());
    }

    /**
     * Add observer to the JSON live data.
     * @param owner the lifecycle owner
     * @param observer the observer that is to be added
     */
    public void addResponseObserver(@NonNull LifecycleOwner owner,
                                    @NonNull Observer<? super JSONObject> observer) {
        mResponse.observe(owner, observer);
    }

    /**
     * Error handling when a call to the server fails.
     *
     * @param error exception error that is returned when server call fails
     */
    private void handleError(final VolleyError error) {
        if (Objects.isNull(error.networkResponse)) {
            try {
                mResponse.setValue(new JSONObject("{" +
                        "error:\"" + error.getMessage() +
                        "\"}"));
            } catch (JSONException e) {
                Log.e("JSON PARSE", "JSON Parse Error in handleError");
            }
        }
        else {
            String data = new String(error.networkResponse.data, Charset.defaultCharset())
                    .replace('\"', '\'');
            try {
                JSONObject response = new JSONObject();
                response.put("code", error.networkResponse.statusCode);
                response.put("data", new JSONObject(data));
                mResponse.setValue(response);
            } catch (JSONException e) {
                Log.e("Forgot password JSON PARSE", "JSON Parse Error in handleError");
            }
        }
    }

    /**
     * Send a get request to the server to signin the user.
     *
     * @param email the email of the user
     */
    public void connect(final String email) {
        String url = "https://team5-tcss450-holochat.herokuapp.com/changePassword";
        Request request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null, //no body for this get request
                mResponse::setValue,
                this::handleError) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                // add headers <key,value>
                String credentials = email +": ";
                String auth = "Basic "
                        + Base64.encodeToString(credentials.getBytes(),
                        Base64.NO_WRAP);
                headers.put("Authorization", auth);
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
}
