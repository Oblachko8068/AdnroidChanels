package com.example.channels.retrofit

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.channels.ChannelPlayer
import com.example.channels.R
import com.google.gson.Gson
import com.squareup.picasso.Picasso

class RecyclerAdapter (private val context: Context, private var channelList: List<Channel>):
    RecyclerView.Adapter<RecyclerAdapter.MyViewHolder>() {

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newChannels: List<Channel>) {
        channelList = newChannels
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.channel_block, parent, false)
        return MyViewHolder(itemView)
    }
    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.channelIcon)
        val txt_name: TextView = itemView.findViewById(R.id.channelName)
        val txt_team: TextView = itemView.findViewById(R.id.channelDesc)
        val icon_fav: ImageView = itemView.findViewById(R.id.icon_fav)

        fun bind(listItem: Channel) {
            image.setOnClickListener {
                Toast.makeText(it.context, "нажал на ", Toast.LENGTH_SHORT)
                    .show()
            }
            itemView.setOnClickListener {
                Toast.makeText(it.context, "нажал на ", Toast.LENGTH_SHORT).show()
            }
        }
    }




    override fun getItemCount() = channelList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val listItem = channelList[position]
        holder.bind(listItem)
        Picasso.get().load(channelList[position].image).into(holder.image)
        holder.txt_name.text = channelList[position].name
        holder.txt_team.text = channelList[position].epg[0].title
        holder.icon_fav.setImageResource(R.drawable.baseline_star_24)
        holder.icon_fav.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.icon_disable))
        val intArray1 = getSavedNewIntArray(context)
        if (channelList[position].id in intArray1){
            holder.icon_fav.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.icon_enable))
        }
        holder.icon_fav.setOnClickListener{
            var intArray = getSavedNewIntArray(context)
            if (channelList[position].id in intArray){
                holder.icon_fav.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.icon_disable))
                for (i in intArray.indices){
                    if (intArray[i] == channelList[position].id){
                        intArray = removeElementFromArray(intArray, i)
                        break
                    }
                }
            } else {
                intArray = addElementToArray(intArray, channelList[position].id)
                holder.icon_fav.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.icon_enable))
            }
            saveNewIntArray(context, intArray)
        }
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, ChannelPlayer::class.java)

            // Создаем Bundle и помещаем в него данные
            val bundle = Bundle()
            bundle.putString("channel_name", listItem.name)
            bundle.putString("channel_description", listItem.epg[0].title)
            //bundle.putString("channel_icon_resource", listItem.image)

            // Устанавливаем Bundle как аргумент Intent
            intent.putExtras(bundle)

            context.startActivity(intent)
        }
    }
    fun addElementToArray(array: IntArray, element: Int): IntArray {
        val newArray = IntArray(array.size + 1)
        array.copyInto(newArray)
        newArray[array.size] = element
        return newArray
    }
    fun removeElementFromArray(array: IntArray, indexToRemove: Int): IntArray {
        return array.filterIndexed { index, _ -> index != indexToRemove }.toIntArray()
    }
    fun getSavedNewIntArray(context: Context): IntArray {
        val sharedPref = context.getSharedPreferences("new_array_preferences", Context.MODE_PRIVATE)
        val jsonString = sharedPref.getString("new_int_array_data", null)

        return try {
            if (jsonString != null) {
                Gson().fromJson(jsonString, IntArray::class.java)
            } else {
                IntArray(0)
            }
        } catch (e: Exception) {
            IntArray(0)  // Возвращаем пустой (нулевой) массив в случае ошибки
        }
    }

    // Добавляем функцию для сохранения массива целочисленных значений в SharedPreferences
    fun saveNewIntArray(context: Context, intArray: IntArray) {
        val sharedPref = context.getSharedPreferences("new_array_preferences", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        val jsonString = Gson().toJson(intArray)
        editor.putString("new_int_array_data", jsonString)
        editor.apply()
    }

}