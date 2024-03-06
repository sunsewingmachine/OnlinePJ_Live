package com.onlinepjliveapp.ui

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import android.widget.FrameLayout
import com.google.gson.JsonElement
import com.onlinepjliveapp.R
import com.onlinepjliveapp.api.ApiClient
import com.onlinepjliveapp.databinding.FragAllvideosBinding
import com.onlinepjliveapp.databinding.FragVideoPlayerBinding
import com.onlinepjliveapp.util.Util
import com.onlinepjliveapp.util.Util.noInternet
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FragAllvideos : Fragment() {

    private val uri = "https://www.youtube.com/embed/"
    private lateinit var binding: FragAllvideosBinding
    private var loaded = false
    private var id = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {

        if (loaded) {
            return binding.root
        }

        loaded = true
        binding = FragAllvideosBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*
        id = arguments?.getString("id") ?: ""
        if (id.isEmpty()) {
            // Util.showSnackBar(activity, "id is empty, loadData function called")
            loadData()
            return
        }*/

        loadVideo()
        // Util.showSnackBar(activity, "id not empty, loadVideo function called")
    }


    private fun loadData() {
        if (noInternet()) {
            Util.showSnackBar(activity, getString(R.string.no_internet))
            return
        }

        ApiClient.getClient().getRequest(
            "https://indiabeeps.com/allprojects/onlinepj/liveapp/live_info.json"
        ).enqueue(object: Callback<JsonElement> {
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()?.asJsonObject
                    if (body?.get("live-now")?.asBoolean == true) {
                        id = body.get("live-id")?.asString ?: "ooJE1Iqpo0c"
                        loadVideo()
                    } else {
                        binding.progress.visibility = View.GONE
                        binding.web.visibility = View.GONE
                        binding.message.visibility = View.VISIBLE
                        binding.message.text = body?.get("text-normal")?.asString
                    }
                }
            }

            override fun onFailure(call: Call<JsonElement>, t: Throwable) {}
        })

    }


    @SuppressLint("SetJavaScriptEnabled")
    private fun loadVideo() {

        if (noInternet()) {
            Util.showSnackBar(activity, getString(R.string.no_internet))
            return
        }

        binding.web.visibility = View.VISIBLE
        binding.message.visibility = View.GONE
        binding.progress.visibility = View.GONE
        binding.web.settings.javaScriptEnabled = true
        binding.web.settings.domStorageEnabled = true
        binding.web.settings.mediaPlaybackRequiresUserGesture = false
        binding.web.webChromeClient = MyChromeClient()
        binding.web.webViewClient = MyWebClient()
        binding.web.loadUrl("https://onlinepj.co.in/videos/")

        // binding.web.loadUrl("$uri$id?autoplay=1")
    }

    inner class MyWebClient: WebViewClient()

    inner class MyChromeClient : WebChromeClient() {

        private var customView: View? = null
        private var customCallback: CustomViewCallback? = null
        private var originalOrientation: Int? = 0
        private var originalVisibility: Int? = 0

        override fun onHideCustomView() {
            super.onHideCustomView()
            (activity?.window?.decorView as FrameLayout?)?.removeView(customView)
            originalVisibility?.let {
                activity?.window?.decorView?.systemUiVisibility = it
            }
            originalOrientation?.let {
                activity?.requestedOrientation = it
            }
            customCallback?.onCustomViewHidden()
            customView = null
            customCallback = null
            originalVisibility = null
            originalOrientation = null
        }

        override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
            super.onShowCustomView(view, callback)

            if (customView != null) {
                onHideCustomView()
                return
            }

            customView = view
            customCallback = callback
            originalOrientation = activity?.requestedOrientation
            originalVisibility = activity?.window?.decorView?.systemUiVisibility
            (activity?.window?.decorView as FrameLayout?)?.addView(customView, FrameLayout.LayoutParams(-1, -1))
            activity?.window?.decorView?.systemUiVisibility = 3846 or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE

        }
    }

    override fun onPause() {
        super.onPause()
        binding.web.onPause()
    }

    override fun onResume() {
        super.onResume()
        binding.web.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.web.destroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.web.saveState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState ?: return
        binding.web.restoreState(savedInstanceState)
    }

}