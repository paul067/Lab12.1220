package com.example.user.myapplication;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Interpolator;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;



import com.google.gson.Gson;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    class Data{
        Result result;
        class Result{
            Results[] results;
            class Results{
                String Station;
                String Destination;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                OkHttpClient mOkHttpClient = new OkHttpClient();

                Request request = new Request.Builder()
                        .url("http://data.taipei/opendata/datalist/apiAccess"+
                                "?scope=resourceAquire&rid=55ec6d6e-dc5c-4268-a725-d04cc262172b")
                        .build();

                Call call = mOkHttpClient.newCall(request);

                call.enqueue(new Callback()
                {
                    @Override
                    public void onFailure(Request request, IOException e)
                    {

                    }

                    @Override
                    public void onResponse(final Response response) throws IOException
                    {
                        Intent i = new Intent("MyMessage");
                        i.putExtra("json", response.body().string());
                        sendBroadcast(i);
                    }
                });
            }
        });

        BroadcastReceiver myBroadcasReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String myJson = intent.getExtras().getString("json");

                Gson gson = new Gson();
                Data data = gson.fromJson(myJson,Data.class);

                String[] list_item = new String[data.result.results.length];
                for (int i=0;i<data.result.results.length;i++){
                    list_item[i]=new String();
                    list_item[i]+="\n列車即將進入:"+data.result.results[i].Station;
                    list_item[i]+="\n列車行駛目的地為:"+data.result.results[i].Destination;
                }
                AlertDialog.Builder dialog_list = new AlertDialog.Builder(MainActivity.this);
                dialog_list.setTitle("臺北捷運列車到站站名");
                dialog_list.setItems(list_item, null);
                dialog_list.show();
            }
        };
        IntentFilter intentFilter =new IntentFilter("MyMessage");
        registerReceiver(myBroadcasReceiver,intentFilter);
    }
}
