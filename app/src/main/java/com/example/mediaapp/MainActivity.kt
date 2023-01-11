package com.example.mediaapp

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.session.MediaBrowser
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.squareup.picasso.Picasso
import java.io.InputStream
import android.widget.AdapterView





class MainActivity : AppCompatActivity() {

    private lateinit var mediaListView : ListView
    private lateinit var mediaListAdapter : MediaItemsArrayAdapter
    private lateinit var browserFuture: ListenableFuture<MediaBrowser>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeListView()
    }

    private fun initializeListView(){
        mediaListView = findViewById(R.id.media_list_view)
        mediaListAdapter = MediaItemsArrayAdapter(this, R.layout.media_item_layout, MediaList.mediaItems)
        mediaListView.adapter = mediaListAdapter
        mediaListView.setOnItemClickListener { _, _, position, _ ->
            //val item = mediaListView.getItemAtPosition(position)
            val intent = Intent(this, PlayerActivity::class.java )
            intent.putExtra("index", position)
            startActivity(intent)
        }
    }

    private fun initializeBrowser() {
        browserFuture =
            MediaBrowser.Builder(
                this,
                SessionToken(this, ComponentName(this, PlaybackService::class.java))
            )
                .buildAsync()
        browserFuture.addListener({ }, ContextCompat.getMainExecutor(this))
    }

    override fun onStart() {
        super.onStart()
        initializeBrowser()
    }

    override fun onStop() {
        releaseBrowser()
        super.onStop()
    }

    private fun releaseBrowser() {
        MediaBrowser.releaseFuture(browserFuture)
    }

    private class MediaItemsArrayAdapter(
        context : Context,
        viewId: Int,
        mediaItemList: List<MediaItem>)
        : ArrayAdapter<MediaItem>(context, viewId, mediaItemList) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val mediaItem = getItem(position)!!
            val view =
                convertView ?: LayoutInflater.from(context).inflate(R.layout.media_item_layout, parent, false)
            val metaData = mediaItem.mediaMetadata
            view.findViewById<TextView>(R.id.title_view).text = metaData.title
            view.findViewById<TextView>(R.id.artist_view).text = metaData.artist
            val imageUri = metaData.artworkUri
            val imageView = view.findViewById<ImageView>(R.id.image_view)
            Picasso.get().load(imageUri).into(imageView)
            return view
        }
    }


}