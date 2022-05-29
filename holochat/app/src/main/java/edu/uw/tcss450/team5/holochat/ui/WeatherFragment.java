package edu.uw.tcss450.team5.holochat.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayoutMediator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;
import java.util.TimeZone;

import edu.uw.tcss450.team5.holochat.R;
import edu.uw.tcss450.team5.holochat.ui.contacts.contact_tabs.ContactTabsAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class WeatherFragment extends Fragment {

    //textbox where the user can enter the city
    EditText etCity;
    //button to refresh results
    Button btnget;
    //text result of the weather
    TextView tvResult;

    private String lon = "";
    private String lat = "";

    private String tempUrl = "";
    private String tempUrl2 = "";
    private String tempUrl3 = "";

    private int requestCounter = 0;

    //why two api calls?
    //open-weather-api allows requests by city for current weather but not for one-call(returns hourly and daily forecast and more)
    //first request returns longitude and latitude which can be used for the second call
    //url for current weather api
    private final String url = "https://api.openweathermap.org/data/2.5/weather";
    //url for one-call weather api (returns forecasts)
    private final String url2 = "https://api.openweathermap.org/data/2.5/onecall";
    //api key linked to my account (masonh6)
    private final String appid = "5d35717e8f7700ac945b1abc468129d0";
    //hard coded request for tacoma washington
    private final String url3 = "https://api.openweathermap.org/data/2.5/onecall?lat=47.2529&lon=-122.4443&exclude=minutely,alerts&appid=";

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
        etCity = view.findViewById(R.id.etCity);
        //etCity.setText("Tacoma");
        btnget = view.findViewById(R.id.btnget);
        btnget.setOnClickListener(this::getWeatherDetails);
        tvResult = view.findViewById(R.id.tvResult);
    }

    public void getWeatherDetails(View view){



        //get city from user input and construct first temp url
        String city = etCity.getText().toString().trim();
        if(city.equals("")){
            tvResult.setText("bro you forgot to enter the city");
        } else {
            tempUrl = url + "?q=" + city + "&appid=" + appid;
            System.out.println(tempUrl);
            tempUrl3 = url3 + appid;
        }

        //request queue
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        //first string request to get lon and lat
        StringRequest stringRequest = new StringRequest(Request.Method.POST, tempUrl3, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("response", response);
                String output = "";
                try {

                    JSONObject jsonResponse = new JSONObject(response);

                    //grab current weather
                    JSONObject jsonObjectMain = jsonResponse.getJSONObject("current");
                    //use for °C
                    double temp = jsonObjectMain.getDouble("temp") - 273.15;
                    double feelsLike = jsonObjectMain.getDouble("feels_like") - 273.15;
                    //use for °F
                    double tempF = 1.8 * (jsonObjectMain.getDouble("temp") - 273.15) +32;
                    double feelsLikeF = 1.8 * (jsonObjectMain.getDouble("feels_like") - 273.15) +32;
                    float pressure = jsonObjectMain.getInt("pressure");
                    int humidity = jsonObjectMain.getInt("humidity");
                    int wind_speed = jsonObjectMain.getInt("wind_speed");
                    int clouds = jsonObjectMain.getInt("clouds");

                    output += "Current weather of " + "Tacoma" + " (" + "WA, USA" + ") "
                            + "\n Temp: " + df.format(tempF) + " °F"
                            + "\n Feels Like: " + df.format(feelsLikeF) + " °F"
                            + "\n Humidity: " + humidity + "%"
                            //+ "\n Description: " + description
                            + "\n Wind Speed: " + wind_speed + "m/s (meters per second)"
                            + "\n Cloudiness: " + clouds + "%"
                            + "\n Pressure: " + pressure + " hPa" + "\n\n";

                    //grab hourly forcast
                    output += "24 Hour Forcast: \n";

                    JSONArray hourlyArray = jsonResponse.getJSONArray("hourly");

                    for (int i = 1; i <=24; i++){
                        JSONObject curHourIndex = hourlyArray.getJSONObject(i);
                        //long curHour = (long) curHourIndex.getDouble("dt");
                        String curHourString = curHourIndex.getString("dt");
                        Date date = new Date(Long.parseLong(curHourString) * 1000);
                        DateFormat format = new SimpleDateFormat("ha");
                        format.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
                        String formattedDate = format.format(date);
                        //System.out.println(formattedDate);


                        double curTemp = curHourIndex.getDouble("temp");
                        double curTempF = 1.8 * (curHourIndex.getDouble("temp") - 273.15) +32;
                        JSONArray curHourWeatherArray = curHourIndex.getJSONArray("weather");
                        JSONObject curHourWeatherArrayIndex = curHourWeatherArray.getJSONObject(0);
                        String curHourWeatherDescription = curHourWeatherArrayIndex.getString("description");
                        if (i % 2 == 0){
                            output += formattedDate + " " + df.format(curTempF) + "°F, " + curHourWeatherDescription + "\n";
                        } else {
                            output += formattedDate + " " + df.format(curTempF) + "°F, " + curHourWeatherDescription + " - ";
                        }
                    }

                    //grab daily forecast
                    output += "\n5 Day Forecast\n";

                    JSONArray dailyArray = jsonResponse.getJSONArray("daily");
                    for(int i = 1; i <= 5; i++){
                        JSONObject curDayIndex = dailyArray.getJSONObject(i);
                        String curDayString = curDayIndex.getString("dt");
                        Date date = new Date(Long.parseLong(curDayString) * 1000);
                        DateFormat format = new SimpleDateFormat("EEEE");
                        String formattedDay = format.format(date);

                        JSONObject tempOb = curDayIndex.getJSONObject("temp");
                        double dayTempF = 1.8 * (tempOb.getDouble("day") - 273.15) + 32;

                        JSONArray dailyWeatherArray = curDayIndex.getJSONArray("weather");
                        JSONObject dailyWeatherArrayIndex = dailyWeatherArray.getJSONObject(0);
                        String dayDescription = dailyWeatherArrayIndex.getString("description");

                        output += formattedDay + " " + df.format(dayTempF) + "°F, " + dayDescription + "\n";

                    }
                    //print to textview
                    tvResult.setText(output);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener(){
                @Override
                public void onErrorResponse(VolleyError error){
                    Toast.makeText(getActivity(), error.toString().trim(), Toast.LENGTH_SHORT).show();
                    System.out.println("First request is failing");
                }
        });






        //add the first request to the first queue
        requestQueue.add(stringRequest);
    }

    /**
     * part of my attempt to send two requests
     */
    public void secondRequestHelper(){
        StringRequest stringRequest2 = new StringRequest(Request.Method.POST, tempUrl2, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.d("response", response);
                String output = "";
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    //was once jsonArray
                    //JSONArray jsonArray = jsonResponse.getJSONArray("weather");
                    //JSONObject jsonObjectWeather = jsonArray.getJSONObject(0);
                    //String description = jsonObjectWeather.getString("description");
                    JSONArray jsonArrayCurrent = jsonResponse.getJSONArray("current");


                    JSONObject jsonObjectMain = jsonResponse.getJSONObject("current");
                    //use for °C
                    double temp = jsonObjectMain.getDouble("temp") - 273.15;
                    double feelsLike = jsonObjectMain.getDouble("feels_like") - 273.15;
                    //use for °F
                    double tempF = 1.8 * (jsonObjectMain.getDouble("temp") - 273.15) +32;
                    double feelsLikeF = 1.8 * (jsonObjectMain.getDouble("feels_like") - 273.15) +32;
                    float pressure = jsonObjectMain.getInt("pressure");
                    int humidity = jsonObjectMain.getInt("humidity");
                    JSONObject jsonObjectWind = jsonResponse.getJSONObject("wind");
                    String wind = jsonObjectWind.getString("speed");
                    JSONObject jsonObjectClouds = jsonResponse.getJSONObject("clouds");
                    String clouds = jsonObjectClouds.getString("all");
                    JSONObject jsonObjectSys = jsonResponse.getJSONObject("sys");
                    String countryName = jsonObjectSys.getString("country");
                    String cityName = jsonResponse.getString("name");
                    System.out.println("lhjdfksakjlhfsdlhjkf");
                    output += "Current weather of " + cityName + " (" + countryName + ") " + "lon:" + lon + " lat:" + lat
                            + "\n Temp: " + df.format(tempF) + " °F"
                            + "\n Feels Like: " + df.format(feelsLikeF) + " °F"
                            + "\n Humidity: " + humidity + "%"
                            //+ "\n Description: " + description
                            + "\n Wind Speed: " + wind + "m/s (meters per second)"
                            + "\n Cloudiness: " + clouds + "%"
                            + "\n Pressure: " + pressure + " hPa";
                    tvResult.setText("testing");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                Toast.makeText(getActivity(), error.toString().trim(), Toast.LENGTH_SHORT).show();
                System.out.println("second request is failing");
            }
        });

        RequestQueue rq = Volley.newRequestQueue(getActivity());
        rq.add(stringRequest2);
    }

}