package com.example.mediaapp

import android.content.res.AssetManager
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaItem.SubtitleConfiguration
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.Util
import org.json.JSONObject
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException
import kotlin.random.Random

object MediaList {
    val mediaItems : List<MediaItem> get() = _mediaItems
    private var _mediaItems : MutableList<MediaItem> = mutableListOf()
    private var isInitialized = false

    fun initialize(assets: AssetManager) {
        if (isInitialized) return
        isInitialized = true

        val jsonObject = JSONObject(readJSON(assets))
        val mediaList = jsonObject.getJSONArray("items")

        for (i in 0 until mediaList.length()) {
            val mediaItem = parseMediaItemFromJson(mediaList.getJSONObject(i))
            _mediaItems.add(mediaItem)
        }
    }

    private fun parseMediaItemFromJson(jMedia: JSONObject) : MediaItem {
        val id = jMedia.getString("id")
        val title  = jMedia.getString("title")
        val artist = jMedia.getString("artist")
        val genre = jMedia.getString("genre")
        val subtitleConfigurations = getSubtitleConfigurations(jMedia)
        val sourceUri = Uri.parse(jMedia.getString("source"))
        val imageUri = Uri.parse(jMedia.getString("image"))

        return buildMediaItem(
            title = title,
            mediaId = id,
            subtitleConfigurations,
            artist = artist,
            genre = genre,
            sourceUri = sourceUri,
            imageUri = imageUri
        )
    }

    private fun getSubtitleConfigurations(jMedia: JSONObject) : MutableList<SubtitleConfiguration> {
        val subtitleConfigurations: MutableList<SubtitleConfiguration> = mutableListOf()

        if (!jMedia.has("subtitles")) return subtitleConfigurations

        val jSubtitles = jMedia.getJSONArray("subtitles")
        for (i in 0 until jSubtitles.length()) {
            val jSubtitle = jSubtitles.getJSONObject(i)
            val subtitle = SubtitleConfiguration.Builder(Uri.parse(jSubtitle.getString("subtitle_uri")))
                .setMimeType(jSubtitle.getString("subtitle_mime_type"))
                .setLanguage(jSubtitle.getString("subtitle_lang"))
                .build()
            subtitleConfigurations.add(subtitle)
        }

        return subtitleConfigurations
    }

    private fun buildMediaItem(
        title: String,
        mediaId: String,
        subtitleConfigurations: MutableList<SubtitleConfiguration>,
        artist: String,
        genre: String,
        sourceUri: Uri?,
        imageUri: Uri?
    ) : MediaItem {

        val metadata = MediaMetadata.Builder()
            .setTitle(title)
            .setArtist(artist)
            .setGenre(genre)
            .setArtworkUri(imageUri)
            .build()

        return MediaItem.Builder()
            .setMediaId(mediaId)
            .setSubtitleConfigurations(subtitleConfigurations)
            .setMediaMetadata(metadata)
            .setUri(sourceUri)
            .build()
    }

    private fun readJSON(assets: AssetManager): String {
        val buffer = assets.open("media.json").use { Util.toByteArray(it) }
        return String(buffer, Charsets.UTF_8)
    }
}