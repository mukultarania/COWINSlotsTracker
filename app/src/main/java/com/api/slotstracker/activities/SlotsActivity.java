package com.api.slotstracker.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.api.slotstracker.R;
import com.api.slotstracker.adapters.SlotsAdapter;
import com.api.slotstracker.data.Slots;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SlotsActivity extends AppCompatActivity {

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = SlotsActivity.class.getSimpleName();
    private ListView listView;
    private TextView tv_slot;
    private SearchView searchView;
    private RequestQueue queue;
    private SlotsAdapter slotsAdapter;

    private static final String SLOTS_REQUEST_URL =
            "https://cdn-api.co-vin.in/api/v2/appointment/sessions/public/findByPin?pincode=";
    private static final String DIST_SLOTS_URL =
            "https://cdn-api.co-vin.in/api/v2/appointment/sessions/public/findByDistrict?district_id=";
    private static String PINCODE;
    private static String SEARCH_DATE;
    private static String TYPE;
    private static String D_ID;
    private static final String SLOTS_REQUEST_END = "&date=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slots);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listView = findViewById(R.id.slots_view);
        listView.setClickable(true);
        searchView = findViewById(R.id.searchView);
        tv_slot = findViewById(R.id.tv_slots);

        queue = Volley.newRequestQueue(SlotsActivity.this);

        Intent intent = getIntent();
        Bundle bundle = getIntent().getExtras();
        if (intent != null) {
            TYPE = bundle.getString(getString(R.string.type));
            if (TYPE != null && TYPE.equals(getString(R.string.by_pincode))) {
                PINCODE = bundle.getString(getString(R.string.pincode));
                SEARCH_DATE = bundle.getString(getString(R.string.date));
                getData(SLOTS_REQUEST_URL + PINCODE + SLOTS_REQUEST_END + SEARCH_DATE);
            } else if (TYPE != null && TYPE.equals(getString(R.string.by_dist))) {
                D_ID = bundle.getString(getString(R.string.dsp_id));
                SEARCH_DATE = bundle.getString(getString(R.string.date));
                getData(DIST_SLOTS_URL + D_ID + SLOTS_REQUEST_END + SEARCH_DATE);
            }
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Slots st = (Slots) adapterView.getItemAtPosition(i);
                lvDialouge(st);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.e("Main"," data search"+s);
                slotsAdapter.getFilter().filter(s);
                return false;
            }
        });

    }

    void getData(String code) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, code, null, response -> {
            try {
                JSONArray featureArray = response.getJSONArray("sessions");
                ArrayList<Slots> slots = new ArrayList<>();
                for (int i = 0; i < featureArray.length(); i++) {
                    // Extract out the first feature (which is an earthquake)
                    JSONObject firstFeature = featureArray.getJSONObject(i);
                    // Extract out the title, time, and tsunami values
                    String name = firstFeature.getString("name");
                    String add = firstFeature.getString("address");
                    String vaccin = firstFeature.getString("vaccine");
                    String minage = firstFeature.getString("min_age_limit");
                    String dose1 = firstFeature.getString("available_capacity_dose1");
                    String price = firstFeature.getString("fee");
                    String dose2 = firstFeature.getString("available_capacity_dose2");

                    slots.add(new Slots(name, add, vaccin, minage, dose1, dose2, price));
                }
                if(slots.size() != 0){
                    slotsAdapter = new SlotsAdapter(SlotsActivity.this, slots);
                    listView.setAdapter(slotsAdapter);
                }else{
                    tv_slot.setVisibility(View.VISIBLE);
                    listView.setVisibility(View.INVISIBLE);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {

        });
        queue.add(jsonObjectRequest);

    }

    void lvDialouge(Slots sl){

        new MaterialAlertDialogBuilder(SlotsActivity.this, R.style.AlertDialogTheme)
                .setTitle("Vaccine Slots Tracker")
                .setMessage("Open Map or Book Slots in Arogya Setu App (AD)")
                .setPositiveButton("OPEN MAP", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String url = "https://www.google.com/maps/search/"+sl.getName()+sl.getAddress();
                        Intent ix = new Intent(Intent.ACTION_VIEW);
                        ix.setData(Uri.parse(url));
                        startActivity(ix);
                    }
                })
                .setNeutralButton("BOOK A SLOT (AD)", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=nic.goi.aarogyasetu&hl=en_IN&gl=US"));
                        startActivity(intent);
                    }
                })
                .show();

    }

}