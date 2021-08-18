package com.api.slotstracker.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.api.slotstracker.R;
import com.api.slotstracker.data.DistSp;
import com.api.slotstracker.data.State;
import com.api.slotstracker.data.StateSp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class DashboardActivity extends AppCompatActivity {

    private Button go, all_state, by_dist;
    private EditText et_pincode, edittext, dist_date;
    private TextView total_case_i, total_case_w, total_death_i, total_death_w, total_recovery_i, total_recovery_w , tvKW;
    private Spinner sp_state, sp_dist;

    private RequestQueue queue;

    final Calendar myCalendar = Calendar.getInstance();
    private DatePickerDialog.OnDateSetListener date, dis_date;
    private DistSp dsp;
    public static final String LOG_TAG = SlotsActivity.class.getSimpleName();
    private static final String WORLD_RECORD =
            "https://disease.sh/v3/covid-19/all";
    private static final String IN_RECORD =
            "https://api.rootnet.in/covid19-in/stats/latest";
    private static final String STATE_ALL = "https://cdn-api.co-vin.in/api/v2/admin/location/states";
    private static final String DIST_ALL = "https://cdn-api.co-vin.in/api/v2/admin/location/districts/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        setContentView(R.layout.activity_dashboard);
        go = findViewById(R.id.pincode);
        et_pincode = findViewById(R.id.et_pincode);
        edittext = (EditText) findViewById(R.id.Birthday);
        total_case_i = findViewById(R.id.tc_ind);
        total_case_w = findViewById(R.id.tc_w);
        total_death_i = findViewById(R.id.td_ind);
        total_death_w = findViewById(R.id.td_w);
        total_recovery_i = findViewById(R.id.tr_ind);
        total_recovery_w = findViewById(R.id.tr_w);
        all_state = findViewById(R.id.all_state);
        sp_state = findViewById(R.id.spin_state);
        sp_dist = findViewById(R.id.spin_districts);
        dist_date = findViewById(R.id.dist_date);
        by_dist = findViewById(R.id.by_dist);
        tvKW = findViewById(R.id.tv_kw);

        queue = Volley.newRequestQueue(DashboardActivity.this);
        getData();
        datePicker();
        disDatePicker();
        populateStateSpin();

        edittext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(DashboardActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        dist_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(DashboardActivity.this, dis_date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity.this, SlotsActivity.class);
                if (!et_pincode.getText().toString().isEmpty() && !edittext.getText().toString().isEmpty()) {
                    intent.putExtra(getString(R.string.type), getString(R.string.by_pincode));
                    intent.putExtra(getString(R.string.pincode), et_pincode.getText().toString());
                    intent.putExtra(getString(R.string.date), edittext.getText().toString());
                    startActivity(intent);
                } else {
                    Toast.makeText(DashboardActivity.this, "ENTER PINCODE and DATE", Toast.LENGTH_SHORT).show();
                }

            }
        });

        by_dist.setOnClickListener(view -> {
            Intent intent = new Intent(DashboardActivity.this, SlotsActivity.class);
            if (dsp != null && !dist_date.getText().toString().isEmpty()) {
                intent.putExtra(getString(R.string.type), getString(R.string.by_dist));
                intent.putExtra(getString(R.string.dsp_id), dsp.getId());
                intent.putExtra(getString(R.string.date), dist_date.getText().toString());
                startActivity(intent);
            } else {
                Toast.makeText(DashboardActivity.this, "Enter Date and Select District", Toast.LENGTH_SHORT).show();
            }
        });

        all_state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DashboardActivity.this, StateActivity.class));
            }
        });

        sp_state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                StateSp sp = (StateSp) adapterView.getSelectedItem();
                populateDistSpin(sp.getId() + "");

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        sp_dist.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                dsp = (DistSp) adapterView.getSelectedItem();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        tvKW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DashboardActivity.this, KnowYourApp.class));
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.threedot, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.exit:
                finish();
                System.exit(0);
                break;
            case R.id.kwapp:
                startActivity(new Intent(DashboardActivity.this, KnowYourApp.class));
                break;
            default:

        }
        return super.onOptionsItemSelected(item);
    }

    private void updateUI(State state, String ask) {
        if (state != null) {
            if (ask.equalsIgnoreCase("world")) {
                total_case_w.setText(state.getTotalConfirmed() + "");
                total_recovery_w.setText(state.getDischarged() + "");
                total_death_w.setText(state.getDeaths() + "");
            } else if (ask.equalsIgnoreCase("india")) {
                total_case_i.setText(state.getTotalConfirmed() + "");
                total_recovery_i.setText(state.getDischarged() + "");
                total_death_i.setText(state.getDeaths() + "");
            }
        }
    }

    private void updateLabel(String key) {
        String myFormat = "dd-MM-yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        if (key.equalsIgnoreCase("1"))
            edittext.setText(sdf.format(myCalendar.getTime()));
        else if (key.equalsIgnoreCase("2"))
            dist_date.setText(sdf.format(myCalendar.getTime()));
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
                        updateUI(state, "india");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(DashboardActivity.this, "Fail to get the data..", Toast.LENGTH_SHORT).show());
        JsonObjectRequest jsonObjectRequest1 = new JsonObjectRequest(Request.Method.GET, WORLD_RECORD, null,
                response -> {
                    try {
                        String totalConfirmed = response.getString("cases");
                        String deaths = response.getString("deaths");
                        String recovered = response.getString("recovered");

                        State state = new State(totalConfirmed, deaths, recovered);
                        updateUI(state, "world");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(DashboardActivity.this, "Fail to get the data..", Toast.LENGTH_SHORT).show());
        queue.add(jsonObjectRequest);
        queue.add(jsonObjectRequest1);
    }

    void datePicker() {
        date = (view, year, monthOfYear, dayOfMonth) -> {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel("1");
        };
    }

    void disDatePicker() {
        dis_date = (view, year, monthOfYear, dayOfMonth) -> {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel("2");
        };
    }

    void populateStateSpin() {
        queue = Volley.newRequestQueue(DashboardActivity.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, STATE_ALL, null, response -> {
            try {
                JSONArray jsonArray = response.getJSONArray("states");
                ArrayList<StateSp> das = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject js = jsonArray.getJSONObject(i);
                    das.add(new StateSp("state", js.getString("state_name") + "", js.getString("state_id") + ""));
                }
                ArrayAdapter<StateSp> adapter = new ArrayAdapter<StateSp>(DashboardActivity.this, android.R.layout.simple_spinner_item,
                        das);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sp_state.setAdapter(adapter);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> Toast.makeText(DashboardActivity.this, "Fail to get the data..", Toast.LENGTH_SHORT).show());
        queue.add(jsonObjectRequest);
    }

    void populateDistSpin(String sid) {
        queue = Volley.newRequestQueue(DashboardActivity.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, DIST_ALL + sid, null, response -> {
            try {
                JSONArray jsonArray = response.getJSONArray("districts");
                ArrayList<DistSp> das = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject js = jsonArray.getJSONObject(i);
                    das.add(new DistSp(js.getString("district_name") + "", js.getString("district_id") + ""));
                }
                ArrayAdapter<DistSp> adapter = new ArrayAdapter<>(DashboardActivity.this, android.R.layout.simple_spinner_item,
                        das);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sp_dist.setAdapter(adapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> Toast.makeText(DashboardActivity.this, "Fail to get the data..", Toast.LENGTH_SHORT).show());
        queue.add(jsonObjectRequest);
    }

}