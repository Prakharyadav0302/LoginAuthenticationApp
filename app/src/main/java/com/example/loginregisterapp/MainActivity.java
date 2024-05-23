package com.example.loginregisterapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;
import android.view.View;
import android.os.*;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    Button button;
    TextView textView;
    FirebaseUser user;

    TextView cityName;
    Button search;
    TextView show;
    String url;

    class getWeather extends AsyncTask<String,Void, String>{

        @Override
        protected  String doInBackground(String... urls){
            StringBuilder result = new StringBuilder();
            try{
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                String line = "";
                while ((line = reader.readLine()) != null){
                    result.append(line).append("\n");
                }
                return result.toString();
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected  void onPostExecute(String result){
            super.onPostExecute(result);
            try{
                JSONObject jsonObject = new JSONObject(result);
                String weatherInfo = jsonObject.getString("main");
                show.setText(weatherInfo);
                weatherInfo = weatherInfo.replace("temp","Temperature");
                weatherInfo= weatherInfo.replace("feels_like","Feels Like");
                weatherInfo = weatherInfo.replace("temp_max","Temperature Max");
                weatherInfo = weatherInfo.replace("temp_min","Temperature Min");
                weatherInfo = weatherInfo.replace("pressure","Pressure");
                weatherInfo = weatherInfo.replace("humidity","Humidity");
                show.setText(weatherInfo);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();
        button = findViewById(R.id.logout);
        textView = findViewById(R.id.user_details);
        user = auth.getCurrentUser();
        if(user == null){
            Intent intent = new Intent(getApplicationContext(),Login.class);
            startActivity(intent);
            finish();
        }else{
            textView.setText(user.getEmail());
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(),Login.class);
                startActivity(intent);
                finish();
            }
        });

        cityName = findViewById(R.id.cityname);
        search = findViewById(R.id.btnSearch);
        show = findViewById(R.id.weather);
        final String[] temp = {""};

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this,"Button Clicked",Toast.LENGTH_SHORT).show();
                String city = cityName.getText().toString();
                try {
                    if(city !=null)
                    url = "https://api.openweathermap.org/data/2.5/weather?q="+city+"&appid=412a0528e8a817b0b396e43542a3061e";
                    else{
                        Toast.makeText(MainActivity.this,"Enter City",Toast.LENGTH_SHORT).show();
                    }
                    getWeather task = new getWeather();
                    temp [0] = task.execute(url).get();
                }catch (ExecutionException e){
                    e.printStackTrace();
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
                if(temp[0] == null){
                    show.setText("cannot able to find weather");
                }
            }
        });
    }
}