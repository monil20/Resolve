package com.monil20.resolve.services;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by hp on 12-04-2018.
 */

public interface SAddComment {
    @FormUrlEncoded
    @POST("/CodeTechnica/sendComment.php")
    Call<String> sendComment(@Field("comment") String comment, @Field("issueid") int issueid, @Field("userid") int userid);
}
