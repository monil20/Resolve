package com.monil20.resolve.services;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by monil20 on 2/23/18.
 */

public interface SRegister {
    @FormUrlEncoded
    @POST("/CodeTechnica/signUp.php")
    Call<String> sendData(@Field("fname") String fname, @Field("lname") String lname, @Field("phone") String phone, @Field("uname") String uname, @Field("pwd") String pwd);
}
