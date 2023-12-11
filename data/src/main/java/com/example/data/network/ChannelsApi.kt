package com.example.data.network

import com.example.data.model.ChannelsJson
import retrofit2.Response
import retrofit2.http.*

interface ChannelsApi {

    @GET("rKkyTS")
    fun getChannelList(): Response<ChannelsJson>
}