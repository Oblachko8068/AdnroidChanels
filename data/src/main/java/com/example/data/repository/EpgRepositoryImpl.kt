package com.example.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.data.room.EpgDao
import com.example.domain.model.Epg
import javax.inject.Inject

class EpgRepositoryImpl @Inject constructor(
    private val epgDao: EpgDao
) : com.example.domain.repository.EpgRepository {

    override fun getEpgListLiveData(): LiveData<List<Epg>> {
        //два it - может привести к ерунде при компиляции, да и не особо понятно
        return epgDao.getEpgListAll().map { it.map { it.toEpgDb() } }
    }

    override fun getCurrentEpgByChannelId(channelID: Int): LiveData<Epg> {
        return epgDao.getEpgByChannelID(channelID).map { it.toEpgDb() }
    }

}
