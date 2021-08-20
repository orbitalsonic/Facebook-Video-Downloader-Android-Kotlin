package com.thanos.romidownloader.activity

import android.app.Dialog
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.*
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.*
import com.google.android.exoplayer2.util.Util
import com.thanos.romidownloader.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class VideoPlayerFB : AppCompatActivity(), Player.EventListener {
    var videoFullScreenPlayer: PlayerView? = null
    var spinnerVideoDetails: ProgressBar? = null
    var imageDownload: ImageView? = null
    private val TAG = "ExoPlayerActivity"
    private val KEY_VIDEO_URI = "video_uri"
    var videoUri: String? = null
    var player: SimpleExoPlayer? = null
    var mHandler: Handler? = null
    var mRunnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player_f_b)

        videoFullScreenPlayer = findViewById(R.id.videoFullScreenPlayer)
        spinnerVideoDetails = findViewById(R.id.spinnerVideoDetails)

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        if (intent.hasExtra(KEY_VIDEO_URI)) {
            videoUri =
                intent.getStringExtra(KEY_VIDEO_URI)
        }
        setUp()

        imageDownload?.setOnClickListener(View.OnClickListener {
            try {
//                val mBaseFolderPath = (Environment
//                    .getExternalStorageDirectory()
//                    .toString() + File.separator
//                        + "FacebookDownloader" + File.separator)
//                if (!File(mBaseFolderPath).exists()) {
//                    File(mBaseFolderPath).mkdir()
//                }
//                val mFilePath =
//                    "file://" + mBaseFolderPath + "/" + getDateTime() + ".mp4"
                val downloadUri = Uri.parse(videoUri)
                val req = DownloadManager.Request(downloadUri)
//                req.setDestinationUri(Uri.parse(mFilePath))
                req.addRequestHeader("Accept", "application/mp4")
                req.setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    File.separator
                            + "FBDownloader" + File.separator + "facebook_" + getDateTime() + ".mp4"
                )
                req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                val dm =
                    getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                dm.enqueue(req)
                showDownloadDialog()
            } catch (e: Exception) {
                Toast.makeText(
                    this@VideoPlayerFB,
                    "Video Can't be downloaded! Try Again",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun setUp() {
        initializePlayer()
        if (videoUri == null) {
            return
        }
        buildMediaSource(Uri.parse(videoUri))
    }

    private fun initializePlayer() {
        if (player == null) {
            // 1. Create a default TrackSelector
            val loadControl: LoadControl = DefaultLoadControl(
                DefaultAllocator(true, 16),
                3000,
                5000,
                1500,
                5000,
                -1,
                true
            )
            val bandwidthMeter: BandwidthMeter = DefaultBandwidthMeter()
            val videoTrackSelectionFactory: TrackSelection.Factory =
                AdaptiveTrackSelection.Factory(bandwidthMeter)
            val trackSelector: TrackSelector = DefaultTrackSelector(videoTrackSelectionFactory)
            // 2. Create the player
            player = ExoPlayerFactory.newSimpleInstance(
                DefaultRenderersFactory(this), trackSelector,
                loadControl
            )
            videoFullScreenPlayer!!.player = player
        }
    }

    private fun buildMediaSource(mUri: Uri) {
        // Measures bandwidth during playback. Can be null if not required.
        val bandwidthMeter = DefaultBandwidthMeter()
        // Produces DataSource instances through which media data is loaded.
        val dataSourceFactory: DataSource.Factory =
            DefaultDataSourceFactory(
                this,
                Util.getUserAgent(
                    this,
                    getString(R.string.app_name)
                ), bandwidthMeter
            )
        // This is the MediaSource representing the media to be played.
        val videoSource: MediaSource = ExtractorMediaSource.Factory(dataSourceFactory)
            .createMediaSource(mUri)
        // Prepare the player with the source.
        player!!.prepare(videoSource)
        player!!.playWhenReady = true
        player!!.addListener(this)
    }

    private fun releasePlayer() {
        if (player != null) {
            player!!.release()
            player = null
        }
    }

    private fun pausePlayer() {
        if (player != null) {
            player!!.playWhenReady = false
            player!!.playbackState
        }
    }

    private fun resumePlayer() {
        if (player != null) {
            player!!.playWhenReady = true
            player!!.playbackState
        }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
        if (mRunnable != null) {
            mHandler!!.removeCallbacks(mRunnable!!)
        }
    }

    override fun onRestart() {
        super.onRestart()
        resumePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
    }

    override fun onTimelineChanged(
        timeline: Timeline?,
        manifest: Any?,
        reason: Int
    ) {
    }

    override fun onTracksChanged(
        trackGroups: TrackGroupArray?,
        trackSelections: TrackSelectionArray?
    ) {
    }

    override fun onLoadingChanged(isLoading: Boolean) {}
    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        when (playbackState) {
            Player.STATE_BUFFERING -> spinnerVideoDetails!!.visibility = View.VISIBLE
            Player.STATE_ENDED -> {
            }
            Player.STATE_IDLE -> {
            }
            Player.STATE_READY -> spinnerVideoDetails!!.visibility = View.GONE
            else -> {
            }
        }
    }

    override fun onRepeatModeChanged(repeatMode: Int) {}
    override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {}
    override fun onPlayerError(error: ExoPlaybackException?) {}
    override fun onPositionDiscontinuity(reason: Int) {}
    override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {}
    override fun onSeekProcessed() {}

    private fun getDateTime(): String? {
        val sdf = SimpleDateFormat("yyyyMMdd_HHmmss")
        return sdf.format(Date())
    }

    private fun showDownloadDialog() {
        val adDialog = Dialog(this)
        adDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        adDialog.setCanceledOnTouchOutside(true)
        adDialog.setContentView(R.layout.dialog_download)
        adDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val _gotit = adDialog.findViewById<TextView>(R.id._gotit)
        _gotit.setOnClickListener { adDialog.dismiss() }
        adDialog.show()
    }

}