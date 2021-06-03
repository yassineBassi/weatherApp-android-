package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private RequestQueue requestQueue;
    private TextView date;
    private TextView cityName;
    private TextView tempVal;
    private TextView minTempVal;
    private TextView maxTempVal;
    private TextView pressureVal;
    private TextView humidityVal;
    private ImageView weatherIcon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestQueue = Volley.newRequestQueue(this);
        setContentView(R.layout.activity_main);

        this.cityName = findViewById(R.id.cityName);
        this.tempVal = findViewById(R.id.tempVal);
        this.minTempVal = findViewById(R.id.minTempVal);
        this.maxTempVal = findViewById(R.id.maxTempVal);
        this.pressureVal = findViewById(R.id.pressureVal);
        this.humidityVal = findViewById(R.id.humidityVal);
        this.date = findViewById(R.id.date);
        this.weatherIcon = findViewById(R.id.weatherIcon);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem searchItem = menu.findItem(R.id.searchItem);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Enter city name");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String city) {
                loadCityWeather(city);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String city) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void loadCityWeather(String city) {
        String url = "http://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=3bce0fa7dff29eca7969012bf9712b21";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, (Response.Listener<String>) response -> {
            try {
                Weather weather = getCityWeather(response);
                displayWeather(weather, city);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Toast.makeText(getApplicationContext(), "impossible de se connecter à l'API, veuillez vérifier votre connexion Internet", Toast.LENGTH_LONG).show();
            System.out.println(error);
        });

        requestQueue.add(stringRequest);
    }

    @SuppressLint("SetTextI18n")
    private void displayWeather(Weather weather, String city){
        Picasso.get().load("http://openweathermap.org/img/wn/" + weather.getIcon() + "@4x.png").into(weatherIcon);
        @SuppressLint("SimpleDateFormat") String currentDate = new SimpleDateFormat("dd MMM yyyy 'T' HH:mm").format(new Date());
        cityName.setText(city);
        date.setText(currentDate);
        tempVal.setText(convertToCelsius(weather.getTemp()) + "°C");
        minTempVal.setText(convertToCelsius(weather.getTempMin()) + "°C");
        maxTempVal.setText(convertToCelsius(weather.getTempMax()) + "°C");
        pressureVal.setText(convertToCelsius(weather.getPressure()) + " hPa");
        humidityVal.setText(convertToCelsius(weather.getHumidity()) + "%");
    }

    private Weather getCityWeather(String response) throws JSONException {
        JSONObject responseParser = new JSONObject(response);
        JSONObject weatherParser = (JSONObject) responseParser.getJSONArray("weather").get(0);
        JSONObject mainParser = responseParser.getJSONObject("main");
        Weather weather = new Weather();
        weather.setTemp(mainParser.getDouble("temp"));
        weather.setTempMin(mainParser.getDouble("temp_min"));
        weather.setTempMax(mainParser.getDouble("temp_max"));
        weather.setPressure(mainParser.getDouble("pressure"));
        weather.setHumidity(mainParser.getDouble("humidity"));
        weather.setIcon(weatherParser.getString("icon"));

        return weather;
    }

    @SuppressLint("DefaultLocale")
    public String convertToCelsius(double kelvinTemp){
        return String.format("%.1f", kelvinTemp - 273.15);
    }
}