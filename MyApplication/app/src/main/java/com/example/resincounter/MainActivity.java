package com.example.resincounter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView resinDisplay,sanityDisplay,serumDisplay;
    TextView resinset,sanityset,serumset;
    Button btn_setresin,btn_setsanity,btn_setserum;
    int resin,sanity,serum;
    String regex = "\\d+";
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher;
    String stam;
    private static final String DBRequest_URL = "https://personalcounter-app.herokuapp.com/requestDB.php";
    private static final String DBUpdate_URL = "https://personalcounter-app.herokuapp.com/insertRequest.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resinset = findViewById(R.id.setresin);
        serumset = findViewById(R.id.setserum);
        sanityset = findViewById(R.id.setsanity);
        resinDisplay = findViewById(R.id.resinDisplay);
        serumDisplay = findViewById(R.id.serumDisplay);
        sanityDisplay = findViewById(R.id.sanityDisplay);
        btn_setresin = findViewById(R.id.btn_setresin);
        btn_setsanity = findViewById(R.id.btn_setsanity);
        btn_setserum = findViewById(R.id.btn_setserum);

        getDataFromDB();

        findViewById(R.id.btn_setresin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stam = resinset.getText().toString();
                //check if input is numeric
                matcher = pattern.matcher(stam);
                if (!matcher.matches()){
                    Toast.makeText(MainActivity.this, "Enter Numeric Value!", Toast.LENGTH_SHORT).show();
                    return;
                }
                updateDB("resin",stam);
            }
        });

        findViewById(R.id.btn_setsanity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stam = sanityset.getText().toString();
                //check if input is numeric
                matcher = pattern.matcher(stam);
                if (!matcher.matches()){
                    Toast.makeText(MainActivity.this, "Enter Numeric Value!", Toast.LENGTH_SHORT).show();
                    return;
                }
                updateDB("sanity",stam);
            }
        });

        findViewById(R.id.btn_setserum).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stam = serumset.getText().toString();
                //check if input is numeric
                matcher = pattern.matcher(stam);
                if (!matcher.matches()){
                    Toast.makeText(MainActivity.this, "Enter Numeric Value!", Toast.LENGTH_SHORT).show();
                    return;
                }
                updateDB("pgr",stam);
            }
        });

    }

    private void updateDB(String type,String stam) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                //Starting Write and Read data with URL
                //Creating array for parameters
                String[] field = new String[2];
                field[0] = "type";
                field[1] = "stam";
                //Creating array for data
                String[] data = new String[2];
                data[0] = type;
                data[1] = stam;
                PutData putData = new PutData(DBUpdate_URL, "POST", field, data);
                if (putData.startPut()) {
                    if (putData.onComplete()) {
                        String result = putData.getResult();
                        if(result.equals("Request DB Success")){
                            Toast.makeText(MainActivity.this, "Successfully updated.", Toast.LENGTH_SHORT).show();
                            getDataFromDB();

                        }else if(result.equals("Request DB Failed")){
                            Toast.makeText(MainActivity.this, "Failed to update DB.", Toast.LENGTH_SHORT).show();
                        }
                        //End ProgressBar (Set visibility to GONE)
                        Log.i("PutData", result);
                    }
                }
                //End Write and Read data with URL
            }
        });
    }

    private void getDataFromDB() {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, DBRequest_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            //converting the string to json array object
                            JSONArray array = new JSONArray(response);

                            //traversing through all the object
                            for (int i = 0; i < array.length(); i++) {

                                //getting product object from json array
                                JSONObject data = array.getJSONObject(i);


                                sanity = data.getInt("sanity");
                                serum = data.getInt("pgr");
                                resin = data.getInt("resin");

                            }
                            Log.d("Success", "Fetched from request database successfully!");
                            double resinTime = getTimeRemaining(resin,160,8);
                            double serumTime = getTimeRemaining(serum,160,6);
                            double sanityTime = getTimeRemaining(sanity,135,6);

                            resinDisplay.setText("Current Resin: " + resin + "/160 (" + resinTime + "h left)");
                            sanityDisplay.setText("Current Sanity: " + sanity + "/135 (" + sanityTime + "h left)");
                            serumDisplay.setText("Current Serum: " + serum + "/160 (" + serumTime + "h left)");


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("Fail", "Fetched from request database failed!");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        Volley.newRequestQueue(this).add(stringRequest);
    }

    private double getTimeRemaining(int current, int max, int refresh) {
        double timeRemaining;
        double leftover = max - current;
        double timeInMin = leftover*refresh;
        double timeInHour = timeInMin/60;
        timeRemaining = (double) Math.round(timeInHour*100)/100;
        return timeRemaining;
    }

    @Override
    public void onClick(View v) {
        String regex = "\\d+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher;
        String stam;
        switch (v.getId()) {
            /*case R.id.btn_setresin:
                Toast.makeText(MainActivity.this, "hi", Toast.LENGTH_SHORT).show();
                stam = resinset.getText().toString();
                //check if input is numeric
                matcher = pattern.matcher(stam);
                if (!matcher.matches()){
                    Toast.makeText(MainActivity.this, "Enter Numeric Value!", Toast.LENGTH_SHORT).show();
                    return;
                }
                updateDB("resin",stam);
                break;*/
            case R.id.btn_setsanity:
                stam = resinset.getText().toString();
                matcher = pattern.matcher(stam);
                if (!matcher.matches()){
                    Toast.makeText(MainActivity.this, "Enter Numeric Value!", Toast.LENGTH_SHORT).show();
                    return;
                }
                updateDB("sanity",stam);
                break;
            case R.id.btn_setserum:
                stam = resinset.getText().toString();
                matcher = pattern.matcher(stam);
                if (!matcher.matches()){
                    Toast.makeText(MainActivity.this, "Enter Numeric Value!", Toast.LENGTH_SHORT).show();
                    return;
                }
                updateDB("pgr",stam);
                break;
        }
    }
}