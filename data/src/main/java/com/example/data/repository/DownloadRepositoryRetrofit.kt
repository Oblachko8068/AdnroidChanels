package com.example.data.repository

import com.example.data.model.ChannelsApi
import com.example.data.model.ChannelsJson
import com.example.data.model.EpgDbEntity
import com.example.data.model.fromChannelJsonToChannelDbEntity
import com.example.data.model.fromChannelJsonToEpgDbEntity
import com.example.data.room.ChannelDao
import com.example.data.room.EpgDao
import com.example.domain.repository.DownloadRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import javax.inject.Inject

class DownloadRepositoryRetrofit @Inject constructor (
    private val channelDao: ChannelDao,
    private val epgDao: EpgDao,
    private val retrofit: Retrofit
) : DownloadRepository {

    //сделать suspend
    //переписать на result api coroutine https://medium.com/10ms-technology-blog/android-kotlin-coroutine-with-retrofit-beginner-a34e6d3d4244
    //узнать что такое CoroutineExceptionHandler https://habr.com/ru/articles/689256/ и добавить его в контекст корутины(), вынести
    override fun fetchChannels() {
        retrofit
            .create(ChannelsApi::class.java)
            .getChannelList().enqueue(object : Callback<ChannelsJson> {
                override fun onFailure(call: Call<ChannelsJson>, t: Throwable) {}

                override fun onResponse(
                    call: Call<ChannelsJson>,
                    response: Response<ChannelsJson>
                ) {
                    val channelList = response.body()?.channels ?: emptyList()
                    val channelDbEntityList =
                        channelList.map { it.fromChannelJsonToChannelDbEntity() }
                    val epgDBEntityList: ArrayList<EpgDbEntity> = arrayListOf()
                    channelList.forEach { epgDBEntityList.addAll(it.fromChannelJsonToEpgDbEntity()) }
                    CoroutineScope(Dispatchers.IO).launch {
                        channelDao.createChannel(channelDbEntityList)
                    }
                    CoroutineScope(Dispatchers.IO).launch {
                        epgDao.createEpg(epgDBEntityList)
                    }
                }
            })
    }
}