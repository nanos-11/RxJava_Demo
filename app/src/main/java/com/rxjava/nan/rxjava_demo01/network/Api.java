package com.rxjava.nan.rxjava_demo01.network;

import com.rxjava.nan.rxjava_demo01.request.UserBaseInfoResponse;
import com.rxjava.nan.rxjava_demo01.request.UserExtraInfoResponse;
import com.rxjava.nan.rxjava_demo01.response.LoginResponse;
import com.rxjava.nan.rxjava_demo01.response.RegisterResponse;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Api接口
 * Created by Nan on 2017/5/4.
 */

public interface Api {
    @GET("/")
    Observable<LoginResponse> login(@QueryMap Map<String, String> content);

    @GET("/")
    Observable<RegisterResponse> register(@QueryMap Map<String, String> content);

    @GET("/")
    Observable<UserBaseInfoResponse> getUserBaseInfo(@QueryMap Map<String, String> content);

    @GET("/")
    Observable<UserExtraInfoResponse> getUserExtraInfo(@QueryMap Map<String, String> content);

    @GET("v2/movie/top250")
    Observable<Response<ResponseBody>> getTop250(@Query("start") int start, @Query("count") int count);
}
