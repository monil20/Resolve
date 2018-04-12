package com.monil20.resolve.services;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by hp on 11-04-2018.
 */

public interface SVotes {
    @FormUrlEncoded
    @POST("/CodeTechnica/SendVoteCount.php")
    Call<String> sendVotes(@Field("issueid") int issueid, @Field("vcount") int vcount);

    @POST("/CodeTechnica/GetVoteCount.php")
    Call<String> getVotes();
}
