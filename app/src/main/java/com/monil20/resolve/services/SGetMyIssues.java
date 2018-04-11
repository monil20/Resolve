package com.monil20.resolve.services;

/**
 * Created by monil20 on 2/23/18.
 */

import retrofit2.Call;
import retrofit2.http.POST;

public interface SGetMyIssues {

    @POST("/CodeTechnica/getMyIssues.php")
    Call<String> getData();
}
