package com.iota.mohammad.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;

public class MainActivity extends AppCompatActivity {

    Spinner spinner_led, spinner_fan;
    Button btnsave, btnopen, btnregister, btndelete;
    ToggleButton tgbled, tgbfan, tgbpir, tgbdoor_alert;
    EditText keyedit, chanedit, fanedit, uidedit;
    TextView gasstxt;

    String led_spinner, fan_type;
    String fanStatus;
    String mq2Status;
    String tempStatus;
    String humidityStatus;
    String doorStatus;
    String led_status="0";
    int counter = 0;

    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent notify=new Intent(getApplicationContext(), AlarmService.class);
        startService(notify);


        queue = Volley.newRequestQueue(getApplicationContext());  // this = context
        Button updateHumidChartButton = (Button) findViewById(R.id.updateHumidChartButton);
        Button updateTempChartButton = (Button) findViewById(R.id.updateTempChartButton);
        final LineChartView humiditychart = (LineChartView) findViewById(R.id.humiditychart);
        final LineChartView tempchart = (LineChartView) findViewById(R.id.tempchart);
        humiditychart.setInteractive(true);
        humiditychart.setZoomType(ZoomType.HORIZONTAL_AND_VERTICAL);
        humiditychart.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        tempchart.setInteractive(true);
        tempchart.setZoomType(ZoomType.HORIZONTAL_AND_VERTICAL);
        tempchart.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);


        spinner_led = (Spinner) findViewById(R.id.led_spinner);
        spinner_fan = (Spinner) findViewById(R.id.fan_spinner);

        btnsave = (Button) findViewById(R.id.btnsave);
        btnopen = (Button) findViewById(R.id.btnopen);
        btnregister = (Button) findViewById(R.id.btnregister);
        btndelete = (Button) findViewById(R.id.btndelete);

        tgbled = (ToggleButton) findViewById(R.id.tgbled);
        tgbfan = (ToggleButton) findViewById(R.id.tgbfan);
        tgbpir = (ToggleButton) findViewById(R.id.tgbpir);
        tgbdoor_alert = (ToggleButton) findViewById(R.id.tgbdoor_alert);

        keyedit = (EditText) findViewById(R.id.key_edit);
        chanedit = (EditText) findViewById(R.id.chan_edit);
        fanedit = (EditText) findViewById(R.id.fan_edit);
        uidedit = (EditText) findViewById(R.id.uid_edit);

        gasstxt = (TextView) findViewById(R.id.gass_txt);

/*
        TabHost tabHost = (TabHost) findViewById(R.id.tabhost);
        tabHost.setup();

        TabHost.TabSpec tab1 = tabHost.newTabSpec("publick");
        TabHost.TabSpec tab2 = tabHost.newTabSpec("publick");

        tab1.setContent(R.id.tab1);
        tab2.setContent(R.id.tab2);

        tab1.setIndicator("tempereture");
        tab2.setIndicator("humidity");

        tabHost.addTab(tab1);
        tabHost.addTab(tab2);
        */

        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        spinner_led.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                led_spinner = spinner_led.getSelectedItem().toString();
                Toast.makeText(MainActivity.this, led_spinner, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner_fan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fan_type = spinner_fan.getSelectedItem().toString();
                Toast.makeText(MainActivity.this, fan_type, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        tgbled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("spinner value", led_spinner);
                if (led_spinner.equals("Red"))
                    led_status = "1";
                if (led_spinner.equals("Blue"))
                    led_status = String.valueOf(2);
                if (led_spinner.equals("Green"))
                    led_status = String.valueOf(3);

                if(tgbled.isChecked()) {

                    final String url = "http://thingtalk.ir/channels/637/feed.json?key=17F82B8WX1YAD6OO&results=1";
                    JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    JSONArray arr = new JSONArray();
                                    try {
                                        arr = response.getJSONArray("feeds");
                                        JSONObject oneEntry = (JSONObject) arr.get(0);

                                        fanStatus = oneEntry.get("field2").toString();
                                        mq2Status = oneEntry.get("field3").toString();
                                        tempStatus = oneEntry.get("field4").toString();
                                        humidityStatus = oneEntry.get("field5").toString();
                                        doorStatus = oneEntry.get("field6").toString();

                                        final String postUrl = "http://thingtalk.ir/update?key=17F82B8WX1YAD6OO&field1="+ led_status + "&field2=" + fanStatus + "&field3=" + mq2Status + "&field4=" + tempStatus + "&field5=" + humidityStatus + "&field6=" + doorStatus;
                                        StringRequest postRequest = new StringRequest(Request.Method.POST, postUrl,
                                                new Response.Listener<String>() {
                                                    @Override
                                                    public void onResponse(String response) {
                                                        Toast.makeText(getApplicationContext(), "Led is On", Toast.LENGTH_SHORT).show();
                                                    }
                                                },
                                                new Response.ErrorListener() {
                                                    @Override
                                                    public void onErrorResponse(VolleyError error) {
                                                        // error
                                                        //    Log.d("Error.Response", response);
                                                    }
                                                }
                                        );
                                        queue.add(postRequest);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    //Log.d("Error.Response", response);
                                }
                            }
                    );
                    queue.add(getRequest);
                }

                else{
                    final String url = "http://thingtalk.ir/channels/637/feed.json?key=17F82B8WX1YAD6OO&results=1";
                    JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    //....
                                    JSONArray arr = new JSONArray();
                                    try {
                                        arr = response.getJSONArray("feeds");
                                        JSONObject oneEntry = (JSONObject) arr.get(0);
                                        fanStatus = oneEntry.get("field2").toString();
                                        mq2Status = oneEntry.get("field3").toString();
                                        tempStatus = oneEntry.get("field4").toString();
                                        humidityStatus = oneEntry.get("field5").toString();
                                        doorStatus = oneEntry.get("field6").toString();
                                        final String postUrl = "http://thingtalk.ir/update?key=17F82B8WX1YAD6OO&field1=0"+"&field2=" + fanStatus + "&field3=" + mq2Status + "&field4=" + tempStatus + "&field5=" + humidityStatus + "&field6=" + doorStatus;
                                        StringRequest postRequest = new StringRequest(Request.Method.POST, postUrl,
                                                new Response.Listener<String>() {
                                                    @Override
                                                    public void onResponse(String response) {
                                                        // response
                                                        Toast.makeText(getApplicationContext(), "Led is Off", Toast.LENGTH_SHORT).show();
                                                    }
                                                },
                                                new Response.ErrorListener() {
                                                    @Override
                                                    public void onErrorResponse(VolleyError error) {
                                                        // error
                                                        //    Log.d("Error.Response", response);
                                                    }
                                                }
                                        );
                                        queue.add(postRequest);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    //Log.d("Error.Response", response);
                                }
                            }
                    );
                    queue.add(getRequest);
                }

            }
        });

        tgbfan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (fan_type.equals("Automatic"))
                    fanStatus = String.valueOf('a');
                if (fan_type.equals("Manual"))
                    fanStatus = String.valueOf(1);

                if(tgbfan.isChecked()) {

                    final String url = "http://thingtalk.ir/channels/637/feed.json?key=17F82B8WX1YAD6OO&results=1";
                    JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    JSONArray arr = new JSONArray();
                                    try {
                                        arr = response.getJSONArray("feeds");
                                        JSONObject oneEntry = (JSONObject) arr.get(0);

                                        led_status = oneEntry.get("field1").toString();
                                        mq2Status = oneEntry.get("field3").toString();
                                        tempStatus = oneEntry.get("field4").toString();
                                        humidityStatus = oneEntry.get("field5").toString();
                                        doorStatus = oneEntry.get("field6").toString();

                                        final String postUrl = "http://thingtalk.ir/update?key=17F82B8WX1YAD6OO&field1="+ led_status + "&field2=" + fanStatus + "&field3=" + mq2Status + "&field4=" + tempStatus + "&field5=" + humidityStatus + "&field6=" + doorStatus;
                                        StringRequest postRequest = new StringRequest(Request.Method.POST, postUrl,
                                                new Response.Listener<String>() {
                                                    @Override
                                                    public void onResponse(String response) {
                                                        Toast.makeText(getApplicationContext(), "fan is On", Toast.LENGTH_SHORT).show();
                                                    }
                                                },
                                                new Response.ErrorListener() {
                                                    @Override
                                                    public void onErrorResponse(VolleyError error) {
                                                        // error
                                                        //    Log.d("Error.Response", response);
                                                    }
                                                }
                                        );
                                        queue.add(postRequest);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    //Log.d("Error.Response", response);
                                }
                            }
                    );
                    queue.add(getRequest);
                }

                else{
                    final String url = "http://thingtalk.ir/channels/637/feed.json?key=17F82B8WX1YAD6OO&results=1";
                    JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    //....
                                    JSONArray arr = new JSONArray();
                                    try {
                                        arr = response.getJSONArray("feeds");
                                        JSONObject oneEntry = (JSONObject) arr.get(0);
                                        led_status = oneEntry.get("field1").toString();
                                        mq2Status = oneEntry.get("field3").toString();
                                        tempStatus = oneEntry.get("field4").toString();
                                        humidityStatus = oneEntry.get("field5").toString();
                                        doorStatus = oneEntry.get("field6").toString();
                                        final String postUrl = "http://thingtalk.ir/update?key=17F82B8WX1YAD6OO&field1="+led_status+"&field2=0" + "&field3=" + mq2Status + "&field4=" + tempStatus + "&field5=" + humidityStatus + "&field6=" + doorStatus;
                                        StringRequest postRequest = new StringRequest(Request.Method.POST, postUrl,
                                                new Response.Listener<String>() {
                                                    @Override
                                                    public void onResponse(String response) {
                                                        // response
                                                        Toast.makeText(getApplicationContext(), "fan is Off", Toast.LENGTH_SHORT).show();
                                                    }
                                                },
                                                new Response.ErrorListener() {
                                                    @Override
                                                    public void onErrorResponse(VolleyError error) {
                                                        // error
                                                        //    Log.d("Error.Response", response);
                                                    }
                                                }
                                        );
                                        queue.add(postRequest);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    //Log.d("Error.Response", response);
                                }
                            }
                    );
                    queue.add(getRequest);
                }

            }
        });

        btnopen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    MqttClient client = new MqttClient("tcp://thingtalk.ir:1883", "androidClienbvclkmktOpenTheDoor", new MemoryPersistence());
                    MqttConnectOptions options = new MqttConnectOptions();
                    options.setCleanSession(true);
                    client.connect(options);
                    client.getTopic("smarties").publish("open".getBytes(), 0, false);
                    Toast.makeText(getApplication(), "در باز شد!", Toast.LENGTH_SHORT).show();
                } catch (MqttException e) {
                    e.printStackTrace();
                }

            }
        });

        tgbpir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        tgbdoor_alert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btndelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });



        updateHumidChartButton.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final List<PointValue> values = new ArrayList<PointValue>();
                final String url = "http://thingtalk.ir/channels/637/feed.json?key=17F82B8WX1YAD6OO&results=100";
                counter = 0;
                // prepare the Request
                JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                        new Response.Listener<JSONObject>()
                        {
                            @Override
                            public void onResponse(JSONObject response) {
                                JSONArray arr = new JSONArray();
                                values.add(new PointValue(0, 0));
                                try{
                                    arr = response.getJSONArray("feeds");
                                    for(int i=0;i < arr.length();i++) {
                                        JSONObject oneEntry = (JSONObject) arr.get(i);
                                        if(!oneEntry.get("field5").toString().equals("null")){
                                            if(!oneEntry.get("field5").equals("0")) {
                                                values.add(new PointValue(counter, Float.parseFloat(oneEntry.get("field5").toString())));
                                                counter = counter + 1;
                                            }
                                        }
                                    }
                                    Log.d("values: ", values.toString());

                                    Line line = new Line(values).setColor(Color.rgb(255, 138, 0)).setCubic(true);
                                    List<Line> lines = new ArrayList<Line>(1);
                                    lines.add(line);

                                    LineChartData data = new LineChartData();
                                    data.setLines(lines);

                                    Axis axisX = new Axis().setName("Time");
                                    Axis axisY = new Axis().setHasLines(true).setName("Humidity");
                                    data.setAxisXBottom(axisX);
                                    data.setAxisYLeft(axisY);
                                    data.setBaseValue(Float.NEGATIVE_INFINITY);
                                    humiditychart.setLineChartData(data);

//                                    Log.d("Response", arr.toString());
                                }catch (JSONException error){
                                    error.printStackTrace();

                                }
                                // display response
                            }
                        },
                        new Response.ErrorListener()
                        {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                //Log.d("Error.Response", response);
                            }
                        }
                );
                queue.add(getRequest);
            }

        }));


        updateTempChartButton.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final List<PointValue> values = new ArrayList<PointValue>();
                final String url = "http://thingtalk.ir/channels/637/feed.json?key=17F82B8WX1YAD6OO&results=100";
                counter = 0;
                // prepare the Request
                JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                        new Response.Listener<JSONObject>()
                        {
                            @Override
                            public void onResponse(JSONObject response) {
                                JSONArray arr = new JSONArray();
                                values.add(new PointValue(0, 0));
                                try{
                                    arr = response.getJSONArray("feeds");
                                    for(int i=0;i < arr.length();i++) {
                                        JSONObject oneEntry = (JSONObject) arr.get(i);
                                        if(!oneEntry.get("field4").toString().equals("null")){
                                            if(!oneEntry.get("field4").equals("0")) {
                                                StringBuilder sb = new StringBuilder(oneEntry.get("field4").toString());
                                                sb.deleteCharAt(oneEntry.get("field4").toString().length()-1);
                                                String resultString = sb.toString();
                                                Log.d("azazaaz", resultString);
                                                values.add(new PointValue(counter, Float.parseFloat(resultString)));
                                                counter = counter + 1;

                                            }
                                        }

                                    }
                                    Log.d("values: ", values.toString());

                                    Line line = new Line(values).setColor(Color.rgb(186, 3, 213)).setCubic(true);
                                    List<Line> lines = new ArrayList<Line>(1);
                                    lines.add(line);

                                    LineChartData data = new LineChartData();
                                    data.setLines(lines);

                                    Axis axisX = new Axis().setName("Time");
                                    Axis axisY = new Axis().setHasLines(true).setName("Temperature °C");
                                    data.setAxisXBottom(axisX);
                                    data.setAxisYLeft(axisY);
                                    data.setBaseValue(Float.NEGATIVE_INFINITY);
                                    tempchart.setLineChartData(data);

                                    //Log.d("Response", arr.toString());
                                }catch (JSONException error){
                                    error.printStackTrace();
                                }
                                // display response
                                // Log.d("Response", response.toString());
                            }
                        },
                        new Response.ErrorListener()
                        {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                //Log.d("Error.Response", response);
                            }
                        }
                );

                queue.add(getRequest);
            }

        }));

    }


}
