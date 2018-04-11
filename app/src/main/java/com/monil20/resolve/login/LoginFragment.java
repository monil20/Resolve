package com.monil20.resolve.login;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.monil20.resolve.R;
import com.monil20.resolve.services.SLogin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class LoginFragment extends Fragment implements com.monil20.resolve.login.OnLoginListener {
    EditText editText, editText2;
    SLogin service;
    String uname, pwd;
    Intent intent;

    private static final String TAG = "LoginFragment";

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_login, container, false);
        inflate.findViewById(R.id.forgot_password).setOnClickListener(v ->
                Toast.makeText(getContext(), "Forgot password clicked", Toast.LENGTH_SHORT).show());
        editText = inflate.findViewById(R.id.uname);
        editText2 = inflate.findViewById(R.id.pwd);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.43.16/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        service = retrofit.create(SLogin.class);

        return inflate;
    }

    @Override
    public void login() {
        uname = editText.getText().toString().trim();
        pwd = editText2.getText().toString().trim();
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
        for(int i = 0; i < listItems.length(); i++){
            if(listItems.getJSONObject(i).getString("uname").equals(uname) && listItems.getJSONObject(i).getString("pwd").equals(pwd)){
                intent = new Intent( getContext() , com.monil20.resolve.Home.class);
                intent.putExtra("userId",listItems.getJSONObject(i).getInt("id")+"");
                startActivity(intent);
                return;
            }
        }
        editText.setText("");
        editText2.setText("");
        Toast.makeText(getContext(),"Enter valid credentials",Toast.LENGTH_SHORT).show();
    }
}
