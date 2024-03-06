package com.onlinepjliveapp.ui

// import android.R
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
// import com.onlinepjliveapp.R
import com.onlinepjliveapp.databinding.ActHomeBinding


class ActHome : AppCompatActivity() {

    private lateinit var binding: ActHomeBinding
    private val fragVideos: FragVideoList by lazy {
        FragVideoList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = ContextCompat.getColor(this, com.onlinepjliveapp.R.color.black)
        }

        binding.video.setOnClickListener {
            binding.videoIcon.imageTintList = getColorStateList(com.onlinepjliveapp.R.color.selected_icon)
            binding.videoIcon.imageTintList = getColorStateList(com.onlinepjliveapp.R.color.white)
            binding.playerIcon.imageTintList = getColorStateList(com.onlinepjliveapp.R.color.selected_icon)
            // navigateTo(fragVideos)
            navigateTo(FragAllvideos())
        }

        binding.player.setOnClickListener {
            binding.playerIcon.imageTintList = getColorStateList(com.onlinepjliveapp.R.color.selected_icon)
            binding.playerIcon.imageTintList = getColorStateList(com.onlinepjliveapp.R.color.white)
            binding.videoIcon.imageTintList = getColorStateList(com.onlinepjliveapp.R.color.selected_icon)
            navigateTo(FragVideoPlayer())
        }

        navigateTo(FragVideoPlayer())
        // navigateTo(fragVideos)
    }


    fun navigateToPlayerPage(id: String) {
        navigateTo(
            FragVideoPlayer().apply {
                arguments = Bundle().apply {
                    putString("id", id)
                }
            }, true
        )
    }


    private fun navigateTo(frag: Fragment, backStack: Boolean = false) {
        val transaction = supportFragmentManager
            .beginTransaction()
            .replace(com.onlinepjliveapp.R.id.frame, frag)

        if (backStack)
            transaction.addToBackStack(frag.javaClass.simpleName)

        transaction.commit()

    }


}