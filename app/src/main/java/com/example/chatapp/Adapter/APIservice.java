package com.example.chatapp.Adapter;

import com.example.chatapp.Notification.MyRespone;
import com.example.chatapp.Notification.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIservice {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAlZfgKc4:APA91bFf97kSz5q_yuuxb34Bt1ooDx_fOsrM-08lQjrizHHHZYXyoOGIHD70IMj0kmvgUsvcdpQcXx3nLsWRsR0Fwfjt-zII9YyToR5t-VoaJAjQ29mgCXbQdmbfY73vConkd-10ev1f"
            }
    )
    @POST("fcm/send")
    Call<MyRespone>sendNotification(@Body Sender body);
}
