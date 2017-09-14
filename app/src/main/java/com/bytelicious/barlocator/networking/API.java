package com.bytelicious.barlocator.networking;

import com.bytelicious.barlocator.model.BarsResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * @author ylyubenov
 */

public interface API {

    @GET("json?type=bar")
    Call<BarsResponse> getBars(@Query("location") String latitudeLongitude,
                               @Query("radius") int radius,
                               @Query("key") String key);

}
