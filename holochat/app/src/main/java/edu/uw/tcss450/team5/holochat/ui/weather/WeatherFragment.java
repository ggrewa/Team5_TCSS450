package edu.uw.tcss450.team5.holochat.ui.weather;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;

import edu.uw.tcss450.team5.holochat.MainActivity;
import edu.uw.tcss450.team5.holochat.R;
import edu.uw.tcss450.team5.holochat.ui.chats.chatroom.ChatRoomFragmentDirections;

/**
 * Fragment that stores information about the weather
 *
 * @author Mason
 * @author Gurleen
 * @author Ken
 */
public class WeatherFragment extends Fragment {

    //keep track of view
    View globalView;

    //denote whether the user has selected c/f as their preference
    boolean celsius = false;

    //defining layout attributes to be linked to the existing xml attributes in fragment_weather.xml

    /**
     * defining layout attributes related to changing location
     */
    //button allow the user to select a search type
    View dropDown;
    //textbox where the user can enter the city/zip
    EditText inputBox;
    //button to allow the user to search the input they entered for city/zip
    Button searchbtn;

    /**
     * defining layout attributes related to current weather
     */
    //displays the day of the week as well as the city if it is gettable by the current search method
    //if not will display coordinates
    TextView dayAndCityText;
    //displays the weather current weather description to the user
    TextView descriptionText;
    //image view that shows the icon for the current weather conditions
    ImageView curIcon;
    //displays the current temperature
    TextView tempText;
    //displays the temp the current weather feels like
    TextView feelsLikeText;
    //displays the pressure of the current weather
    TextView pressureText;
    //displays the humidity of the current weather
    TextView humidityText;
    //displays the wind speed of the current weather
    TextView windText;


    //api key linked to my account (masonh6) i assume this is already stored in the webservice
    private final String appid = "5d35717e8f7700ac945b1abc468129d0";

    //will be used to format temperature data retrieved from json
    DecimalFormat df = new DecimalFormat("#.##");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /*
        View view = inflater.inflate(R.layout.fragment_weather, null, false);
        TextView tvResult = (TextView) view.findViewById(R.id.tvResult);
        return view;
        */
        return inflater.inflate(R.layout.fragment_weather, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        globalView = view;

        //set up search for zipcodes
        inputBox = view.findViewById(R.id.inputBox);
        inputBox.setVisibility(View.GONE);
        searchbtn = view.findViewById(R.id.searchbtn);
        searchbtn.setOnClickListener(this::getWeatherDetails);
        searchbtn.setVisibility(View.GONE);

        //testing geting images from internet into an imageview
        curIcon = view.findViewById(R.id.curIcon);
        String sampleIcon = "02d";
        String sampleIconUrl = "https://openweathermap.org/img/wn/" + sampleIcon + ".png";
        String sample2 = "https://media.geeksforgeeks.org/wp-content/cdn-uploads/logo-new-2.svg";
        Picasso.with(getContext()).load(sampleIconUrl).into(curIcon);

        //setup current weather attributes
        dayAndCityText = view.findViewById(R.id.dayAndCityText);
        descriptionText = view.findViewById(R.id.descriptionText);
        tempText = view.findViewById(R.id.tempText);
        feelsLikeText = view.findViewById(R.id.feelsLikeText);
        pressureText = view.findViewById(R.id.pressureText);
        humidityText = view.findViewById(R.id.humidityText);
        windText = view.findViewById(R.id.windText);

        //etCity.setText("Tacoma");
        //btnget = view.findViewById(R.id.btnget);
        //btnget.setOnClickListener(this::getWeatherDetails);
        //resultBox = view.findViewById(R.id.resultBox);
        //connect("seattle");

        dropDown = view.findViewById(R.id.changeLocationButton);
        dropDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(getActivity(), dropDown);

                popupMenu.getMenuInflater().inflate(R.menu.location_choice, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {

                        if(menuItem.getTitle().equals("Zipcode")){
                            //display serach bar and search button to user
                            inputBox.setVisibility(View.VISIBLE);
                            searchbtn.setVisibility(View.VISIBLE);

                        } else if (menuItem.getTitle().equals("Select On Map")){
                            navigateToSelectLocation();
                        } else if (menuItem.getTitle().equals("Current Location")){
                            //get current location and update weather no additional action required by user
                        }


                        return true;
                    }
                });
                popupMenu.show();
            }
        });
        /*
        //set up hourly forecast scroll view
        LinearLayout linearLayout = view.findViewById(R.id.hourlyForecast);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        for (int i = 0; i < 24; i++){
            View view2 = inflater.inflate(R.layout.hourly_forecast_item, linearLayout, false);
            TextView textView = view2.findViewById(R.id.tvHour);
            textView.setText("time am/pm\ndescription");

            ImageView imageView = view2.findViewById(R.id.ivHour);
            Picasso.with(getContext()).load(sampleIconUrl).into(imageView);

            linearLayout.addView(view2);
        }

         */

        /*
        //set up daily forecast scroll view
        LinearLayout linearLayout2 = view.findViewById(R.id.dailyForecast);
        LayoutInflater inflater2 = LayoutInflater.from(getContext());
        for(int i = 0; i < 5; i++){
            View view3 = inflater2.inflate(R.layout.daily_forecast_item, linearLayout2, false);
            TextView textView = view3.findViewById(R.id.tvDaily);
            textView.setText("Day, Description");

            ImageView imageView = view3.findViewById(R.id.ivDaily);
            Picasso.with(getContext()).load(sampleIconUrl).into(imageView);
            linearLayout2.addView(view3);
        }

         */

        //auto populate with tacoma
        connect("98405");

    }

    /**
     * Navigate to a google map
     */
    private void navigateToSelectLocation() {
        Navigation.findNavController(getView())
                .navigate(WeatherFragmentDirections.actionNavigationWeatherToNavigationLocation());
    }

    /**
     * Checks for valid user input and sends input to connect method
     * @param view
     */
    public void getWeatherDetails(View view){

        //get city from user input and construct first temp url
        String zip = inputBox.getText().toString().trim();
        if(zip.equals("")){
            //resultBox.setText("bro you forgot to enter the city");
            Toast.makeText(getActivity(), "You Must Enter a Zipcode", Toast.LENGTH_SHORT).show();
        } else {
            //call connect method with user input
            connect(zip);
            inputBox.setVisibility(View.GONE);
            searchbtn.setVisibility(View.GONE);
            System.out.println("look its the enterd zip" + zip);//works!
        }
    }


    /**
     * makes call to webservice, need to figure out how to put location/zip in body
     * @param zip
     */
    public void connect(String zip) {
        String webServiceUrl = "https://team5-tcss450-holochat.herokuapp.com/weather/" + zip;
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

    //get resulting json
    //needs to be updated based on new json structure from gurleen
    public void handleResult(final JSONObject jsonResponse){
        String output = "";
        try {
            //parse current weather details
            //get city name
            String city = jsonResponse.getString("city");
            //get temp in c
            double tempc = jsonResponse.getDouble("tempC");
            //get temp in f
            double tempf = jsonResponse.getDouble("tempF");
            //get feels like
            double feelsLike = jsonResponse.getDouble("feel");
            //get the pressure
            float pressure = jsonResponse.getInt("pressure");
            //get humidity
            int humidity = jsonResponse.getInt("humidity");
            //get wind speed
            int windSpeed = jsonResponse.getInt("windSpeed");

             //get the description and icon code of the current weather
             JSONArray description = jsonResponse.getJSONArray("description");
             JSONObject desZero = description.getJSONObject(0);
             String des = desZero.getString("description");
             String icon = desZero.getString("icon");

             //testing populating current weather ui
            dayAndCityText.setText(city);
            descriptionText.setText(des);
            String iconUrl = "https://openweathermap.org/img/wn/" + icon + ".png";
            Picasso.with(getContext()).load(iconUrl).into(curIcon);
            if(celsius){
                tempText.setText(df.format(tempc) + " °C");
            } else {
                tempText.setText(df.format(tempf) + " °F");
                feelsLike = 1.8 * (feelsLike - 273.15) + 32;
            }
            feelsLikeText.setText("Feels like:\n" + df.format(feelsLike) + "°F");
            pressureText.setText("Pressure:\n" + pressure + "hPa");
            humidityText.setText("Humidity:\n" + humidity + "%");
            windText.setText("Wind Speed:\n" + windSpeed + "m/s");

            //parse and set hourly details
            JSONArray hourlyArray = jsonResponse.getJSONArray("hourly");
            //set up hourly forecast scroll view
            LinearLayout linearLayout = globalView.findViewById(R.id.hourlyForecast);
            LayoutInflater inflater = LayoutInflater.from(getContext());
            linearLayout.removeAllViews();
            for(int i = 1; i <= 24; i++) {
                JSONObject curHourIndex = hourlyArray.getJSONObject(i);
                String curHourString = curHourIndex.getString("dt");
                Date date = new Date(Long.parseLong(curHourString) * 1000);
                DateFormat format = new SimpleDateFormat("ha");
                format.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
                String formattedDate = format.format(date);

                double curTemp = curHourIndex.getDouble("temp");
                double curTempF = 1.8 * (curHourIndex.getDouble("temp") - 273.15) + 32;
                JSONArray curHourWeatherArray = curHourIndex.getJSONArray("weather");
                JSONObject curHourWeatherArrayIndex = curHourWeatherArray.getJSONObject(0);
                String curHourWeatherDescription = curHourWeatherArrayIndex.getString("description");
                if (curHourWeatherDescription.equals("overcast clouds")){
                    curHourWeatherDescription = "overcast  clouds";
                }
                String curHourWeatherIcon = curHourWeatherArrayIndex.getString("icon");

                //System.out.println(formattedDate + ", " + df.format(curTempF) + "F" + ", " + curHourWeatherDescription + ", " + curHourWeatherIcon);



                    View view2 = inflater.inflate(R.layout.hourly_forecast_item, linearLayout, false);
                    TextView textView = view2.findViewById(R.id.tvHour);
                    textView.setText(formattedDate + " " + df.format(curTempF) + "°F" + "\n" + curHourWeatherDescription);
                    String hourlyIconUrl = "https://openweathermap.org/img/wn/" + curHourWeatherIcon + ".png";
                    ImageView imageView = view2.findViewById(R.id.ivHour);
                    Picasso.with(getContext()).load(hourlyIconUrl).into(imageView);

                    linearLayout.addView(view2);
            }

            //parse and set daily details
            JSONArray dailyArray = jsonResponse.getJSONArray("daily");
            //set up daily forecast scroll view
            LinearLayout linearLayout2 = globalView.findViewById(R.id.dailyForecast);
            LayoutInflater inflater2 = LayoutInflater.from(getContext());
            for(int i = 1; i <=5; i++) {
                JSONObject curDayIndex = dailyArray.getJSONObject(i);
                String curDayString = curDayIndex.getString("dt");
                Date date = new Date(Long.parseLong(curDayString) * 1000);
                DateFormat format = new SimpleDateFormat("EEEE");
                String formattedDay = format.format(date);

                JSONObject tempOb = curDayIndex.getJSONObject("temp");
                double dayTempF = 1.8 * (tempOb.getDouble("day") - 273.15) +32;

                JSONArray dailyWeatherArray = curDayIndex.getJSONArray("weather");
                JSONObject dailyWeatherArrayIndex = dailyWeatherArray.getJSONObject(0);
                String dayDescription = dailyWeatherArrayIndex.getString("description");
                String dayIcon = dailyWeatherArrayIndex.getString("icon");

                //System.out.println(formattedDay + ", " + df.format(dayTempF) + ", " + dayDescription + ", " + dayIcon);



                    View view3 = inflater2.inflate(R.layout.daily_forecast_item, linearLayout2, false);
                    TextView textView = view3.findViewById(R.id.tvDaily);
                    textView.setText(formattedDay + ", " + dayDescription + ", " + df.format(dayTempF) + "°F");
                    String dailyIconUrl = "https://openweathermap.org/img/wn/" + dayIcon + ".png";
                    ImageView imageView = view3.findViewById(R.id.ivDaily);
                    Picasso.with(getContext()).load(dailyIconUrl).into(imageView);
                    linearLayout2.addView(view3);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * Error handling when a call to the server fails.
     * Don't understand this and will properly implement as time allows
     *
     * @param error exception error that is returned when server call fails
     */
    private void handleError(final VolleyError error) {
        System.out.println("handle error was triggered");
        /*
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
                //mResponse.setValue(response);
            } catch (JSONException e) {
                Log.e("JSON PARSE", "JSON Parse Error in handleError");
            }


        }
        */
    }

}