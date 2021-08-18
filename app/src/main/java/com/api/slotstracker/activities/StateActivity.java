package com.api.slotstracker.activities;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.api.slotstracker.R;
import com.api.slotstracker.adapters.StateAdapter;
import com.api.slotstracker.data.Slots;
import com.api.slotstracker.data.State;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class StateActivity extends AppCompatActivity {
    /**
     * Tag for the log messages
     */
    // Create the object of TextView and PieChart class
    TextView tvCase, tvDeath, tvReco;
    PieChart pieChart;

    public static final String LOG_TAG = StateActivity.class.getSimpleName();
    private ListView listView;
    private RequestQueue queue;
    /**
     * URL to query the USGS dataset for earthquake information
     */
    private static final String IN_RECORD =
            "https://api.rootnet.in/covid19-in/stats/latest";
    private static final String STATE_REQUEST_URL =
            "https://api.rootnet.in/covid19-in/stats/latest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_state_data);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        queue = Volley.newRequestQueue(StateActivity.this);

        listView = findViewById(R.id.state_view);
        tvCase = findViewById(R.id.tvCase);
        tvReco = findViewById(R.id.tvReco);
        tvDeath = findViewById(R.id.tvDeath);
        pieChart = findViewById(R.id.piechart);

        getData();
        StateAsyncTask stateAsyncTask = new StateAsyncTask();
        stateAsyncTask.execute();
    }

    void getData() {
        // creating a new variable for our request queue
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, IN_RECORD, null,
                response -> {
                    try {
                        JSONObject jsonObject = response.getJSONObject("data");
                        JSONObject responseObj = jsonObject.getJSONObject("summary");
                        String totalConfirmed = responseObj.getString("total");
                        String deaths = responseObj.getString("deaths");
                        String discharged = responseObj.getString("discharged");

                        State state = new State(totalConfirmed, deaths, discharged);
                        setData(state);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(StateActivity.this, "Fail to get the data..", Toast.LENGTH_SHORT).show());
        queue.add(jsonObjectRequest);
    }

    private void setData(State state)
    {
        // Set the percentage of language used
        tvReco.setText(state.getDischarged());
        tvDeath.setText(state.getDeaths());
        tvCase.setText(state.getTotalConfirmed());
        // Set the data and color to the pie chart
        pieChart.addPieSlice(
                new PieModel(
                        "Recovered",
                        Integer.parseInt(tvReco.getText().toString()),
                        Color.parseColor("#66BB6A")));
        pieChart.addPieSlice(
                new PieModel(
                        "Deaths",
                        Integer.parseInt(tvDeath.getText().toString()),
                        Color.parseColor("#EF5350")));

        // To animate the pie chart
        pieChart.startAnimation();
    }
    public class StateAsyncTask extends AsyncTask<URL, Void, ArrayList<State>> {

        @Override
        protected ArrayList<State> doInBackground(URL... urls) {
            URL url = createUrl(STATE_REQUEST_URL);
            String jasonResponse = "";
            try {
                jasonResponse = makeHttpRequest(url);
            } catch (IOException e) {
                Log.e(LOG_TAG, "DoInBackGround" + e);
            }

            final ArrayList<State> states = extractFeatureFromJson(jasonResponse);
            return states;
        }

        /**
         * Returns new URL object from the given string URL.
         */
        private URL createUrl(String stringUrl) {
            URL url = null;
            try {
                url = new URL(stringUrl);
            } catch (MalformedURLException exception) {
                Log.e(LOG_TAG, "Error with creating URL", exception);
                return null;
            }
            return url;
        }

        @Override
        protected void onPostExecute(ArrayList<State> states) {
            if (states == null) {
                return;
            }
            StateAdapter stateAdapter = new StateAdapter(StateActivity.this, states);
            listView.setAdapter(stateAdapter);
        }

        /**
         * Make an HTTP request to the given URL and return a String as the response.
         */
        private String makeHttpRequest(URL url) throws IOException {
            String jsonResponse = "";
            //Checkig URL
            if (url == null) {
                return jsonResponse;
            }

            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(10000 /* milliseconds */);
                urlConnection.setConnectTimeout(15000 /* milliseconds */);
                urlConnection.connect();
                //Checking Response Code
                int x = urlConnection.getResponseCode();
                if (x == 200) {
                    inputStream = urlConnection.getInputStream();
                    jsonResponse = readFromStream(inputStream);
                } else {
                    Log.e(LOG_TAG, "makeHttpRequest: " + x);
                }
            } catch (IOException e) {
                // TODO: Handle the exception
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (inputStream != null) {
                    // function must handle java.io.IOException here
                    inputStream.close();
                }
            }
            return jsonResponse;
        }

        /**
         * Convert the {@link InputStream} into a String which contains the
         * whole JSON response from the server.
         */
        private String readFromStream(InputStream inputStream) throws IOException {
            StringBuilder output = new StringBuilder();
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            }
            return output.toString();
        }

        /**
         * Return an {@link Slots} object by parsing out information
         * about the first earthquake from the input earthquakeJSON string.
         */
        private ArrayList<State> extractFeatureFromJson(String earthquakeJSON) {
            //Checking String Earthquake
            if (TextUtils.isEmpty(earthquakeJSON)) {
                return null;
            }
            try {
                JSONObject baseJsonResponse = new JSONObject(earthquakeJSON);
                JSONObject featureArray = baseJsonResponse.getJSONObject("data");
                JSONArray secondArray = featureArray.getJSONArray("regional");
                // If there are results in the features array
                if (secondArray.length() > 0) {
                    ArrayList<State> states = new ArrayList<>();
                    for (int i = 0; i < secondArray.length(); i++) {
                        // Extract out the first feature (which is an earthquake)
                        JSONObject firstFeature = secondArray.getJSONObject(i);

                        // Extract out the title, time, and tsunami values
                        String loc = firstFeature.getString("loc");
                        String totalConfirmed = firstFeature.getString("totalConfirmed");
                        String deaths = firstFeature.getString("deaths");
                        String discharged = firstFeature.getString("discharged");

                        states.add(new State(loc, totalConfirmed, deaths, discharged));
                    }
                    // Create a new {@link Event} object
                    return states;
                }
            } catch (JSONException e) {
                Log.e(SlotsActivity.LOG_TAG, "Problem parsing the earthquake JSON results", e);
            }
            return null;
        }
    }
}