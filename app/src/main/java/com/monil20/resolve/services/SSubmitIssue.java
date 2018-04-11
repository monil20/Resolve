package com.monil20.resolve.services;

/**
 * Created by monil20 on 2/23/18.
 */

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface SSubmitIssue {
    @FormUrlEncoded
    @POST("/CodeTechnica/sendIssue.php")
    Call<String> sendData(@Field("itype") String itype,
                          @Field("lat") double lat,
                          @Field("lng") double lng,
                          @Field("dsc") String dsc,
                          @Field("userId") int userId,
                          @Field("img") String img,
                          @Field("title") String title);

    @POST("/CodeTechnica/getIssueTypes.php")
    Call<String> getData();
}
