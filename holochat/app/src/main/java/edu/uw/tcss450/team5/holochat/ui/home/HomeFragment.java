/*
 * TCSS450
 * Mobile Application Programming
 * Spring 2022
 */
package edu.uw.tcss450.team5.holochat.ui.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.auth0.android.jwt.JWT;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

import edu.uw.tcss450.team5.holochat.R;
import edu.uw.tcss450.team5.holochat.databinding.FragmentHomeBinding;
import edu.uw.tcss450.team5.holochat.model.UserInfoViewModel;
import edu.uw.tcss450.team5.holochat.ui.chats.chatroom.HomeRecentMessagesRecyclerViewAdapter;
import edu.uw.tcss450.team5.holochat.ui.chats.chatroom_list.MessageListViewModel;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private UserInfoViewModel mUserModel;

    private MessageListViewModel mMessagesModel;
    private HomeRecentMessagesRecyclerViewAdapter mAdapter;

    //weather attributes
    TextView home_weather_city;
    ImageView home_weather_icon;
    TextView home_weather_description;
    TextView home_weather_temp;

    //will be used to format temperature data retrieved from json
    DecimalFormat df = new DecimalFormat("#.##");

    public HomeFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewModelProvider provider = new ViewModelProvider(getActivity());
        mMessagesModel = provider.get(MessageListViewModel.class);
        mUserModel = provider.get(UserInfoViewModel.class);

        mMessagesModel.connectGet(mUserModel.getJwt());


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //welcome the user
        ViewModelProvider provider = new ViewModelProvider(getActivity());
        mUserModel = provider.get(UserInfoViewModel.class);
        JWT jwt = new JWT( mUserModel.getJwt());
        binding.textHomeWelcomeUser.setText("Welcome, " + jwt.getClaim("first").asString() + " " +
                jwt.getClaim("last").asString() + "!");

        //initialize weather attributes
        home_weather_city = view.findViewById(R.id.home_weather_city);
        home_weather_icon = view.findViewById(R.id.home_weather_icon);
        home_weather_description = view.findViewById(R.id.home_weather_desciption);
        home_weather_temp = view.findViewById(R.id.home_weather_temp);

        //populate weather with tacoma data
        connect("98405");

        //Local access to the ViewBinding object. No need to create as Instance Var as it is only
        //used here.
        FragmentHomeBinding binding = FragmentHomeBinding.bind(getView());
        updateMessagesModel();
        //refresh the list with a swipe down
        binding.swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateMessagesModel();
            }
        });

    }

    private void updateMessagesModel() {
        //SetRefreshing shows the internal Swiper view progress bar. Show this until messages load
        binding.swipeContainer.setRefreshing(true);
        //may need to change this to be similar to chat fragment on view created.
        //add observer for getting messages
        mMessagesModel.addMessageListObserver(getViewLifecycleOwner(), messageList -> {
            mAdapter = new HomeRecentMessagesRecyclerViewAdapter(messageList, getActivity().getSupportFragmentManager());
            binding.recyclerMessages.setAdapter(mAdapter);
            binding.swipeContainer.setRefreshing(false);
        });
    }


    public void connect(String zip) {
        String webServiceUrl = getResources().getString(R.string.base_url_service) + "weather/" + zip;
        Request request = new JsonObjectRequest(
                Request.Method.GET,
                webServiceUrl,
                null, //no body needed!
                this::handleResult,
                this::handleError);
        request.setRetryPolicy(new DefaultRetryPolicy(
                10_000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //Instantiate the RequestQueue and add the request to the queue
        Volley.newRequestQueue(getActivity())
                .add(request);
    }

    public void handleResult(final JSONObject jsonResponse) {
        try {
            String city = "";
            try{
                city = jsonResponse.getString("city");
            }catch (JSONException e) {
                float lat = jsonResponse.getInt("latitude");
                float lon = jsonResponse.getInt("longitude");
                String latDir = "";
                String lonDir = "";
                if(lat > 0){
                    latDir = lat + "°N";
                } else {
                    latDir = lat + "°S";
                }
                if(lon > 0){
                    lonDir = lon + "°E";
                } else {
                    lonDir = lon + "°W";
                }
                city = latDir + ", " + lonDir;
                System.out.println("city not availible getting coord: " + lat + ", " + lon);
            }

            double tempf = jsonResponse.getDouble("tempF");

            //get the description and icon code of the current weather
            JSONArray description = jsonResponse.getJSONArray("description");
            JSONObject desZero = description.getJSONObject(0);
            String des = desZero.getString("description");
            String icon = desZero.getString("icon");

            home_weather_city.setText(city);
            home_weather_description.setText(des);
            home_weather_temp.setText(df.format(tempf) + "°F");
            String iconUrl = "https://openweathermap.org/img/wn/" + icon + ".png";
            Picasso.with(getContext()).load(iconUrl).into(home_weather_icon);
        } catch (JSONException e) {

        }
    }

    private void handleError(final VolleyError error) {
        System.out.println("handle error was triggered");
    }

}

