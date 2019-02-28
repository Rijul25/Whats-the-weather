package com.example.dell.whatstheweather;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {
    EditText cityName;
    TextView resultTextView;
    public void findWeather(View view){
        Log.i("city name",cityName.getText().toString());

        try {
            String EncodedCityName= URLEncoder.encode(cityName.getText().toString(),"UTF-8");
            DownloadTask task = new DownloadTask();
            task.execute("http://api.openweathermap.org/data/2.5/weather?q=" + EncodedCityName  + "&appid=90ebdc57172a838d0fce0abbc044df8e");
        } catch (UnsupportedEncodingException e) {
            Toast.makeText(getApplicationContext(),"Could not find weather",Toast.LENGTH_LONG);
        }

        InputMethodManager mgr= (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(cityName.getWindowToken(),0);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cityName=findViewById(R.id.cityName);
        resultTextView=findViewById(R.id.resultTextView);
    }
    public class DownloadTask extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;
            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                int data = inputStreamReader.read();
                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = inputStreamReader.read();
                }
                return result;

            } catch (Exception e) {
                Toast.makeText(getApplicationContext(),"Could not find weather",Toast.LENGTH_LONG);
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) { //called when the doin background method has completed. it will be passed the return of the doin background.
            super.onPostExecute(result);



            try {
                String message="";
                JSONObject jsonObject = new JSONObject(result);
                String WeatherInfo = jsonObject.getString("weather");
                Log.i("weather content", WeatherInfo);
                JSONArray jsonArray=new JSONArray(WeatherInfo);
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject jsonPart=jsonArray.getJSONObject(i);
                    String main="";
                    String description="";
                    main=jsonPart.getString("main");
                    description=jsonPart.getString("description");
                    if(main!="" && description!=""){
                        message+=main + ": " + description + "\r\n";
                    }

                }
                if(message!=""){
                  resultTextView.setText(message);

                }
                else{
                    Toast.makeText(getApplicationContext(),"Could not find weather",Toast.LENGTH_LONG);
                }

            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(),"Could not find weather",Toast.LENGTH_LONG);
            }

        }


    }
}
