package com.example.channels.fragments.ListFragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.example.channels.Di
import com.example.channels.R
import com.example.channels.RecyclerAdapter
import com.example.channels.databinding.FragmentFavoritesBinding
import com.example.channels.fragments.navigator
import com.example.channels.model.retrofit.Channel
import com.example.channels.model.retrofit.Epg
import com.google.gson.Gson


class FavoritesFragment : Fragment(), RecyclerAdapter.OnChannelItemClickListener {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    var searchQuery: String? = null
    lateinit var adapter: RecyclerAdapter
    lateinit var channel: List<Channel>
    lateinit var epg: List<Epg>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val channelViewModel = Di.channelViewModel
        val channelList = channelViewModel.getChannelListLiveData()
        val epgList = channelViewModel.getEpgListLiveData()

        epg = epgList.value ?: emptyList()
        channel = channelList.value ?: emptyList()
        channelList.observe(viewLifecycleOwner, Observer { channelList ->
            channel = channelList
            updateChannelsAndEpg()
        })

        epgList.observe(viewLifecycleOwner, Observer { epgList ->
            epg = epgList
            updateChannelsAndEpg()
        })


        binding.recyclerView4.setHasFixedSize(true)
        binding.recyclerView4.layoutManager = LinearLayoutManager(requireContext())

        val viewPager = requireActivity().findViewById<ViewPager>(R.id.viewpagerForTabs)
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                if (position == 1) {
                    updateChannelsAndEpg()
                    if (!searchQuery.isNullOrEmpty()) {
                        filterChannels(searchQuery)
                    }
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })

    }

    private fun updateChannelsAndEpg() {
        if (::channel.isInitialized && ::epg.isInitialized) {
            getAllChannelsList(channel, epg)
        }
    }

    private fun getAllChannelsList(channelList: List<Channel>, epg: List<Epg>) {
        val intArray = getSavedNewIntArray(requireContext())
        val favoriteChannels = channelList.filter { it.id in intArray }
        adapter = RecyclerAdapter(requireContext(), favoriteChannels, epg, this)
        binding.recyclerView4.adapter = adapter
        if (!searchQuery.isNullOrEmpty()) {
            filterChannels(searchQuery)
        }
    }

    fun filterChannels(searchQuery: String?) {
        val filteredList: List<Channel> = if (!searchQuery.isNullOrEmpty()) {
            channel.filter { channel ->
                channel.name.contains(searchQuery, ignoreCase = true)
            }
        } else {
            channel
        }
        val intArray = getSavedNewIntArray(requireContext())
        val favoriteChannels = filteredList.filter { it.id in intArray }
        val adapter = binding.recyclerView4.adapter as? RecyclerAdapter
        adapter?.setData(favoriteChannels)
    }

    private fun getSavedNewIntArray(context: Context): IntArray {
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


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onChannelItemClicked(channel: Channel) {
        val epgDbList = epg
        val selectedEpgDb = epgDbList.find { it.channelID == channel.id }

        navigator().showVideoPlayerFragment(channel, selectedEpgDb)
    }
}