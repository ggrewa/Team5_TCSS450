package edu.uw.tcss450.team5.holochat.ui.auth.changepass;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.Objects;

public class ChangePasswordViewModel extends AndroidViewModel {

    /** JSON response from server when user tries changing password. */
    private MutableLiveData<JSONObject> mResponse;

    /**
     * Constructor the initializes the change password view model.
     *
     * @param application application tied to the view model
     */
    public ChangePasswordViewModel(@NonNull Application application) {
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
                Log.e("Change password JSON PARSE", "JSON Parse Error in handleError");
            }
        }
    }

    /**
     * Send a post request to the server to register the user.
     *
     * @param password the password of the user
     * @param verifCode the verification code typed in by user
     */
    public void connect(final String email, final String verifCode, final String password) {
        String url = "https://team5-tcss450-holochat.herokuapp.com/changePassword";
        JSONObject body = new JSONObject();
        try {
            body.put("email", email);
            body.put("verifCode", verifCode);
            body.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Request request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                body,
                mResponse::setValue,
                this::handleError);
        request.setRetryPolicy(new DefaultRetryPolicy(
                10_000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //Instantiate the RequestQueue and add the request to the queue
        Volley.newRequestQueue(getApplication().getApplicationContext())
                .add(request);
    }
}
