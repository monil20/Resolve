package com.monil20.resolve.login;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.monil20.resolve.R;
import com.monil20.resolve.services.SRegister;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class SignUpFragment extends Fragment implements OnSignUpListener{
    EditText fname, lname, uname, phone, pwd;
    SRegister service;

    private static final String TAG = "SignUpFragment";
    public SignUpFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_signup, container, false);

        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(),"fonts/Raleway-Regular.ttf");

        fname = inflate.findViewById(R.id.fname);
        lname = inflate.findViewById(R.id.lname);
        uname = inflate.findViewById(R.id.uname);
        phone = inflate.findViewById(R.id.mob);
        pwd = inflate.findViewById(R.id.pwd);

        fname.setTypeface(typeface);
        lname.setTypeface(typeface);
        uname.setTypeface(typeface);
        phone.setTypeface(typeface);
        pwd.setTypeface(typeface);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.43.16/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        service = retrofit.create(SRegister.class);
        return inflate;
    }

    @Override
    public void signUp() {
            Call<String> data = service.sendData(fname.getText().toString().trim(),lname.getText().toString().trim(),phone.getText().toString().trim(),uname.getText().toString().trim(),pwd.getText().toString().trim());
            data.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    Log.d("XXX",response.body());
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.d("XXX",call.toString());
                    Log.d("XXX",data.toString());
                }
            });
        Toast.makeText(getContext(),"Registration Successful",Toast.LENGTH_SHORT).show();
    }
}
