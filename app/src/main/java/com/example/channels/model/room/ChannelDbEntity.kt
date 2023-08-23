package com.example.channels.model.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.channels.model.retrofit.ChannelDb

@Entity(
    tableName = "channels"
)
data class ChannelDbEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val image: String,
    val stream: String
)

fun ChannelDb.fromChannelDb(): ChannelDbEntity = ChannelDbEntity(
    id = this.id,
    name = this.name,
    image = this.image,
    stream = this.stream,
    category = this.category,
)
