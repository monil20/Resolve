package com.monil20.resolve;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.Toast;

import com.monil20.resolve.adapters.MyIssuesAdapter;
import com.monil20.resolve.services.SGetMyIssues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class MyCases extends AppCompatActivity {

    SGetMyIssues service;
    int userId;
    ArrayList<String> img, title, status, date;
    ListView listView;
    MyIssuesAdapter adapter;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_cases);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));

        initComponents();

        userId = Integer.parseInt(getIntent().getStringExtra("userId"));

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.43.16/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        service = retrofit.create(SGetMyIssues.class);

        Call<String> data = service.getData();
        data.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    JSONObject data = new JSONObject(response.body());
                    Toast.makeText(getApplicationContext(),data.getJSONArray("data").length()+"",Toast.LENGTH_SHORT);
                    setIssueList(data.getJSONArray("data"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),SubmitIssue.class);
                intent.putExtra("userId",userId+"");
                startActivity(intent    );
            }
        });
    }

    private void initComponents() {
        listView = findViewById(R.id.listView_myIssues);
    }

    private void setIssueList(JSONArray listItems) {
        img = new ArrayList<>();
        title = new ArrayList<>();
        status = new ArrayList<>();
        date = new ArrayList<>();
        try {
            Toast.makeText(getApplicationContext(),String.valueOf(listItems.length()),Toast.LENGTH_SHORT).show();
            for(int i = 0; i < listItems.length(); i++){
                if(listItems.getJSONObject(i).getInt("userId") == userId){
                    img.add(listItems.getJSONObject(i).getString("img"));
                    title.add(listItems.getJSONObject(i).getString("title"));
                    status.add(listItems.getJSONObject(i).getString("status"));
                    date.add(listItems.getJSONObject(i).getString("submitTime"));
                }
            }
            adapter = new MyIssuesAdapter(this, img, title, status, date);
            listView.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
