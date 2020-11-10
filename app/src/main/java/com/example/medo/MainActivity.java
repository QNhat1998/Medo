package com.example.medo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.android.volley.RequestQueue;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    private void Controls(){
        fab = findViewById(R.id.fab);
    }

    public void click(View v){
        Intent intent = new Intent(MainActivity.this,Scan_Qr_Activity.class);
        startActivityForResult(intent,0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 0){
            if(resultCode == CommonStatusCodes.SUCCESS)
            {
                if(data != null){
                    Barcode barcode = data.getParcelableExtra("barcode");
                    getFilePDF(barcode.displayValue.toString());
                }
                else{
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void getFilePDF(String path){

        // Instantiate the cache
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

        // Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());

        // Instantiate the RequestQueue with the cache and network.
        RequestQueue requestQueue = new RequestQueue(cache, network);

        // Start the queue
        requestQueue.start();
        System.out.println(path);
        final String token = path.substring(path.indexOf("auth=")+5);
        Log.v("Token: ",token);
        final String id = path.substring(path.indexOf("sign/")+5,path.indexOf("?")-1);
        Log.v("ID",id);

        String url = "https://wvs.foxit.co.jp:443/api/documents/doc/"+id;

        // Formulate the request and handle the response.
        StringRequest stringRequest = new StringRequest(StringRequest.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                System.out.println(response);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headerParams = new HashMap<String,String>();
                headerParams.put("Authorization","Bearer " + token); //replace the token here
                Log.v("header",headerParams.toString());
                return headerParams;
            }

        };
        Log.v("Lỗi",stringRequest.toString());
    // Add the request to the RequestQueue.
        requestQueue.add(stringRequest);
    }

}