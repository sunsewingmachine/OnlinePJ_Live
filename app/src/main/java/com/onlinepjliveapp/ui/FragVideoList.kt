package com.onlinepjliveapp.ui

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.JsonElement
import com.onlinepjliveapp.R
import com.onlinepjliveapp.api.ApiClient
import com.onlinepjliveapp.databinding.FragVideoListBinding
import com.onlinepjliveapp.databinding.ItemVideoBinding
import com.onlinepjliveapp.model.VideoModel
import com.onlinepjliveapp.util.Util
import com.onlinepjliveapp.util.Util.noInternet
import com.onlinepjliveapp.util.Util.showSnackBar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

@SuppressLint("NotifyDataSetChanged")
class FragVideoList : Fragment() {

    private var loaded = false
    private lateinit var binding: FragVideoListBinding
    private lateinit var adapter: MyAdapter
    private val videos = mutableListOf<VideoModel>()
    private var nextPageKey = ""
    private var isLoadingMore = false
    private lateinit var layoutManager: LinearLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        if (loaded) {
            adapter.notifyDataSetChanged()
            return binding.root
        }

        loaded = true
        binding = FragVideoListBinding.inflate(inflater, container, false)
        setup()

        return binding.root
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        nextPageKey = savedInstanceState?.getString("nextPageKey") ?: ""
        super.onViewStateRestored(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("nextPageKey", nextPageKey)
        super.onSaveInstanceState(outState)
    }

    private fun setup() {
        adapter = MyAdapter()
        layoutManager = LinearLayoutManager(context)
        binding.list.layoutManager = layoutManager
        binding.list.adapter = adapter
        getList()

        binding.list.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (isLoadingMore) return
                if (dy > 0 && layoutManager.findLastVisibleItemPosition() >= videos.size - 3) {
                    isLoadingMore = true
                    getList()
                }
            }
        })

        binding.root.setOnRefreshListener {
            nextPageKey = ""
            getList()
        }
    }

    private fun getList() {

        if (noInternet()) {
            showSnackBar(activity, getString(R.string.no_internet))
            binding.message.visibility = if (videos.isEmpty()) View.VISIBLE else View.GONE
            binding.root.isRefreshing = false
            return
        }

        binding.message.visibility = View.GONE
        ApiClient.getClient().getRequest(Util.generateUrl(pageToken = nextPageKey))
            .enqueue(object : Callback<JsonElement> {
                override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                    isLoadingMore = false
                    binding.root.isRefreshing = false
                    if (response.isSuccessful && response.body() != null) {
                        val items = Util.parseData(response.body()!!)
                        val key = Util.parseNextPageToken(response.body()!!)
                        if (nextPageKey.isBlank()) {
                            videos.clear()
                            videos.addAll(items)
                            adapter.notifyDataSetChanged()
                        } else {
                            val size = videos.size
                            videos.addAll(items)
                            adapter.notifyItemRangeInserted(size, items.size)
                        }
                        nextPageKey = key
                    }
                }
                override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                    isLoadingMore = false
                    binding.root.isRefreshing = false
                    binding.message.visibility = if (videos.isEmpty()) View.VISIBLE else View.GONE
                }
            })
    }

    private fun formattedDate(i: String): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault())
        val date = dateFormat.parse(i)
        return SimpleDateFormat("dd MMM yy hh:mm a", Locale.getDefault()).format(date)
    }

    inner class MyAdapter: RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

        inner class MyViewHolder(val binding: ItemVideoBinding): RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            return MyViewHolder(
                ItemVideoBinding.inflate(
                    LayoutInflater.from(context), parent, false
                )
            )
        }

        override fun getItemCount(): Int {
            return videos.size
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val binding = holder.binding
            val video = videos[holder.adapterPosition]

            binding.title.text = video.title
            binding.description.text = video.description
            binding.createdAt.text = formattedDate(video.createdAt)

            Glide.with(this@FragVideoList)
                .load(video.thumb)
                .into(binding.thumb)


            binding.root.setOnClickListener {
                (activity as ActHome).navigateToPlayerPage(video.id)
            }

        }

    }

}