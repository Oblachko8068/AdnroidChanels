package com.example.channels.model.retrofit

import retrofit2.Call
import retrofit2.http.*

interface ChannelsApi {
    @GET("rKkyTS")
    fun getChannelList(): Call<ChannelsJson>

}