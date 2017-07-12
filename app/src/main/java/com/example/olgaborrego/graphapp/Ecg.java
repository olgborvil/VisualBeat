package com.example.olgaborrego.graphapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class Ecg extends AppCompatActivity {
    SortedMap<String,Double> timeValue= new TreeMap<>();
    String url;
    GraphView graph;
    LineGraphSeries<DataPoint> series;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ecg);
        Bundle bundle = getIntent().getExtras();
         url=bundle.get("Url").toString();
        System.out.println("SEGUNDA "+url);

        new Thread(new Runnable() {
            @Override
            public void run() {
                doInBackgroundArray(url);

            }
        }).start();


    }
    public JSONArray doInBackgroundArray (String url){
      final JSONArray resp= new JSONArray();
        JsonArrayRequest jsArray = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

                    public void onResponse(JSONArray response) {

                        // looping through All the values
                        try {

                            for (int i = 0; i < response.length(); i++) {

                                JSONObject c = response.getJSONObject(i);
                                String time = c.getString("Time");
                                Double ecgValue=Double.valueOf(c.getString("EcgWaveform"));
                                //Filling a map with the time and the values of the ECG
                                timeValue.put(time, ecgValue);
                                                            }
                            createGraph(timeValue);



                        }catch(JSONException e){
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


        return resp;
    }
    void createGraph(Map<String, Double> map){
        graph = (GraphView) findViewById(R.id.graph);
        //hacer un for que recorra cada una de las posiciones del array grande
        //hacer un for clasico que vaya sumando en uno para sumar
        int i=0;
        DataPoint[] datapoints = new DataPoint[map.values().size()];


        for(Double d: map.values()) {
            datapoints[i] = new DataPoint(i, d);
            i++;

        }
        //Obtain date


            series = new LineGraphSeries<DataPoint>(datapoints);
          //  series.setTitle("ECG of the date"+url.split("=")[0]);
            graph.getGridLabelRenderer().setNumHorizontalLabels(2);
            graph.addSeries(series);
        graph.setTitle("ECG of the date: "+url.split("=")[1]);

        graph.setTitleColor(android.R.color.holo_blue_light);
        graph.setTitleTextSize(15f);


        }
    }

