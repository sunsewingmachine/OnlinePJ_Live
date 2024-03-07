package com.onlinepjliveapp.ui

// import android.R
import android.content.Intent
import android.net.Uri
import android.opengl.Visibility
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.onlinepjliveapp.R
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


        setSupportActionBar(binding.toolbar)
// Set the toolbar title text color
        // binding.toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.blue))


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

            binding.toolbar.visibility = View.GONE
            navigateTo(FragAllvideos())
        }

        binding.player.setOnClickListener {
            binding.playerIcon.imageTintList = getColorStateList(com.onlinepjliveapp.R.color.selected_icon)
            binding.playerIcon.imageTintList = getColorStateList(com.onlinepjliveapp.R.color.white)
            binding.videoIcon.imageTintList = getColorStateList(com.onlinepjliveapp.R.color.selected_icon)
            binding.toolbar.visibility = View.VISIBLE
            navigateTo(FragVideoPlayer())
        }


        navigateTo(FragVideoPlayer())
        // navigateTo(fragVideos)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.action_share -> {
                shareApp()
                true
            }
            R.id.action_rate -> {
                rateApp()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun shareApp() {
        val appPackageName = packageName // getPackageName() from Context or Activity object
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        val shareMessage = "Check out this cool app at: https://play.google.com/store/apps/details?id=$appPackageName"
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
        startActivity(Intent.createChooser(shareIntent, "Share via"))
    }


    private fun rateApp() {
        // Direct the user to the app's Play Store page
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
        } catch (anfe: android.content.ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName")))
        }
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