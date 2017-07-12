package com.example.olgaborrego.graphapp;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import static com.example.olgaborrego.graphapp.R.id.button;
import static com.example.olgaborrego.graphapp.R.id.date;
import static com.example.olgaborrego.graphapp.R.id.menuSpinner;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private String TAG = MainActivity.class.getSimpleName();

    SortedMap<String, Double> timeValue=new TreeMap<String, Double>();;
    private String jsonResponse;
    SortedMap<String, Double> map=new TreeMap<String, Double>();
    String s="";
    EditText dateIn;
    Button bton;
    DatePickerDialog datePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String url1 = "http://api.androidhive.info/contacts/";
        String url2 = "http://134.103.176.168:8080/api/v1/ecg?date=21/06/2017";
        dateIn = (EditText) findViewById(date);
        bton = (Button) findViewById(button);

        // Spinner element
        Spinner spinner = (Spinner) findViewById(menuSpinner);
        spinner.setOnItemSelectedListener(this);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.menu_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);


    }

        void ButtonEventEcg() {
        bton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

              String s = getUrl("http://134.103.176.168:8080/api/v1/ecg?date=", dateIn.getText().toString());

                Intent i = new Intent(MainActivity.this, Ecg.class);
                i.putExtra("Url",s);
                startActivity(i);
            }
        });
        }

        /*

        new Thread(new Runnable() {

            public void run() {
             map=doInBackgroundArray("http://134.103.176.168:8080/api/v1/ecg?date=07/07/2017");
                //System.out.println("Values are:"+map.values());
            }
        }).start();
*/


    public String getUrl(String url, String date){
       return url+date;
    }




        public SortedMap<String, Double> doInBackgroundArray (String url){
            JsonArrayRequest jsArray = new JsonArrayRequest
                    (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

                        public void onResponse(JSONArray response) {
                            System.out.println("Response: " + response.toString());
                            // looping through All the values
                            try {
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject c = response.getJSONObject(i);
                                    String time = c.getString("Time");
                                    Double ecgValue=Double.valueOf(c.getString("EcgWaveform"));
                                    //Filling a map with the time and the values of the ECG
                                    timeValue.put(time, ecgValue);

                                }
                               // System.out.println("Size is:"+timeValue.size());


                            }catch(final JSONException e){
                                System.out.println("Error");
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            System.out.println("That didn't work");

                        }
                    });
            jsArray.setRetryPolicy(new DefaultRetryPolicy(500000000, 0, 1f));
            System.gc();
            MySingleton.getInstance(this.getApplicationContext()).addToRequestQueue(jsArray);
            System.out.println("La cadena es:"+s);

            return timeValue;


        }

    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();

        // Showing selected spinner item
        Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
        if (item.equals("ECG")) {
            ButtonEventEcg();

        } else {
            System.out.println("RR-INTERVAL");
            //Intent intent = new Intent(this,HeartRate.class);
            //startActivity(intent);


        }
    }



    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub

    }







}
