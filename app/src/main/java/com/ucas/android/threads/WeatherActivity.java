package com.ucas.android.threads;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherActivity extends Activity {

    TextView tvWeatherJson;
    Button btnFetchWeather;
    String apiKey = "deb36c3236d1bf5dd6b0d5e631a02f15";
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        tvWeatherJson = (TextView) findViewById(R.id.tv_weather_json);
        btnFetchWeather = (Button) findViewById(R.id.btn_fetch_weather);
        btnFetchWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FetchWeatherData().execute();
            }
        });
    }


    private class FetchWeatherData extends AsyncTask<Void, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create progress dialog
                mProgressDialog = new ProgressDialog(WeatherActivity.this);
                // Set your progress dialog Title
                mProgressDialog.setTitle("Downloading");
                // Set your progress dialog Message
                mProgressDialog.setMessage("Downloading, Please Wait!");
                mProgressDialog.setIndeterminate(false);
                mProgressDialog.setMax(100);
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                // Show progress dialog
                mProgressDialog.show();

        }

        @Override
        protected String doInBackground(Void... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;
            String buffer = "";

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                URL url = new URL("http://api.openweathermap.org/data/2.5/weather?q=London,uk&APPID=" + apiKey);

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                for (String linee; (linee = reader.readLine()) != null; buffer += linee) ;
                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                Log.d("buffer", buffer + "");
                JSONObject jsonObject = new JSONObject(buffer);
                JSONObject main = jsonObject.getJSONObject("main");
                double temp = main.getDouble("temp");
                Log.d("temp", temp + "");

                return temp + "";
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            Log.d("onProgressUpdate", values[0] + "");
            mProgressDialog.setProgress((values[0]));
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s == null)
                return;
            Log.d("json", s + "");
            mProgressDialog.dismiss();
            tvWeatherJson.setText(s);
        }
    }
}
