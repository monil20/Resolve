package com.monil20.resolve;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.monil20.resolve.services.SFetchIssues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class BrowseIssues extends AppCompatActivity implements OnMapReadyCallback {
    SFetchIssues service;
    ArrayList<LatLng> latlngs;
    double lat,lng;
    ArrayList<Integer> types, userIds, issueIds;
    int uid;
    String userId;
    private GoogleMap mMap;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_issues);

        uid = Integer.parseInt(getIntent().getStringExtra("userId"));

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.43.16/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        service = retrofit.create(SFetchIssues.class);
        Call<String> data = service.getData();
        data.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    JSONObject data = new JSONObject(response.body());
                    setListItems(data.getJSONArray("data"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });


    }
    public void setListItems(JSONArray listItems) throws JSONException {
        latlngs = new ArrayList<LatLng>();
        types = new ArrayList<Integer>();
        userIds = new ArrayList<Integer>();
        issueIds = new ArrayList<Integer>();
        for(int i = 0; i < listItems.length(); i++){
            latlngs.add(new LatLng(lat=listItems.getJSONObject(i).getDouble("lat"), lng=listItems.getJSONObject(i).getDouble("lng")));
            types.add(listItems.getJSONObject(i).getInt("type"));
            userIds.add(listItems.getJSONObject(i).getInt("userId"));
            issueIds.add(listItems.getJSONObject(i).getInt("issueId"));
//                Toast.makeText(getApplicationContext(),lat+", "+lng,Toast.LENGTH_SHORT).show();
        }
//        Toast.makeText(getApplicationContext(),latlngs.size()+", "+types.size()+", "+userIds.size(),Toast.LENGTH_SHORT).show();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        for(int i=0;i<latlngs.size();i++){
            mMap.addMarker(new MarkerOptions()
                    .position(latlngs.get(i))
                    .title(issueIds.get(i)+"")
                    .snippet("Posted by: "+userIds.get(i)));
        }
        try {
            googleMap.setMyLocationEnabled(true);
        } catch (SecurityException e) {
            e.printStackTrace();
        }

//        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//            @Override
//            public boolean onMarkerClick(Marker marker) {
//                Intent intent = new Intent(BrowseIssues.this,SingleIssue.class);
//                intent.putExtra("userId",userId);
//                intent.putExtra("issueId",marker.getTitle());
//                startActivity(intent);
//                return true;
//            }
//        });

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener(){

            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent intent = new Intent(BrowseIssues.this,SingleIssue.class);
                intent.putExtra("userId",userId);
                intent.putExtra("issueId",marker.getTitle());
                startActivity(intent);
            }
        });
    }

}