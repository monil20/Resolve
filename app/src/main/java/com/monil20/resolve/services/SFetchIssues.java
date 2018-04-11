package com.monil20.resolve.services;

import retrofit2.Call;
import retrofit2.http.POST;

/**
 * Created by monil20 on 2/24/18.
 */

public interface SFetchIssues {
    @POST("/CodeTechnica/getMarkers.php")
    Call<String> getData();
}
