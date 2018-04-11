package com.monil20.resolve.services;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by monil20 on 2/8/18.
 */

public interface SLogin {
    @FormUrlEncoded
    @POST("/CodeTechnica/getData.php")
    Call<String> sendData(@Field("id") String id, @Field("name") String name);

    @POST("/CodeTechnica/sendData.php")
    Call<String> getData();
}
