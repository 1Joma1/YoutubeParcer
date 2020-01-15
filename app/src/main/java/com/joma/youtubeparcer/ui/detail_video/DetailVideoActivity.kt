package com.joma.youtubeparcer.ui.detail_video

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.SparseArray
import android.view.MenuItem
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import at.huber.youtubeExtractor.VideoMeta
import at.huber.youtubeExtractor.YouTubeExtractor
import at.huber.youtubeExtractor.YtFile
import com.google.android.exoplayer2.Player
import com.joma.youtubeparcer.R
import com.joma.youtubeparcer.adapter.DownloadDialogAdapter
import com.joma.youtubeparcer.model.DetailVideo
import com.joma.youtubeparcer.model.YtVideo
import com.joma.youtubeparcer.utils.CallBacks
import com.joma.youtubeparcer.utils.DownloadMaster
import com.joma.youtubeparcer.utils.InternetHelper
import com.joma.youtubeparcer.utils.PlayerManager
import kotlinx.android.synthetic.main.activity_detail_video.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class DetailVideoActivity : AppCompatActivity(), CallBacks.playerCallBack {

    private var viewModel: DetailVideoViewModel? = null

    private var videoId: String? = null
    private var playlistId: String? = null

    private val ITAG_FOR_AUDIO = 140

    private var writePermission = false
    private var selectedVideoQuality: String? = null
    private var selectedVideoExt: String? = null
    private var fileVideo: YtVideo? = null
    private var fileName: String? = null

    private lateinit var player: Player
    private lateinit var playerManager: PlayerManager

    private lateinit var dialogDownloadButton: Button
    private lateinit var dialogRecyclerView: RecyclerView

    private lateinit var dialogAdapter: DownloadDialogAdapter

    private var formatsToShowList: MutableList<YtVideo?>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_video)

        viewModel = ViewModelProviders.of(this).get(DetailVideoViewModel::class.java)
        playerManager = PlayerManager.getSharedInstance(this)
        player = playerManager.playerView.player
        initActionBar()
        getExtra()
        setupViews()

        getDetailVideo()
    }

    private fun getDetailVideo() {
        CoroutineScope(Dispatchers.Main).launch {
            val model = viewModel?.getDetailVideoData()
            if (model != null) {
                getExtraDetailVideoData(model)
            } else {
                if (InternetHelper().checkInternetConnection(this@DetailVideoActivity)) {
                    fetchDetailVideo()
                } else {
                    Toast.makeText(
                        this@DetailVideoActivity,
                        "No internet connection",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                }
            }
        }
    }

    private fun getExtraDetailVideoData(model: List<DetailVideo>?) {
        for (i in model!!.indices) {
            for (j in model[i].items!!) {
                if (j.id == videoId) {
                    setData(model[i])
                    return
                }
            }

        }
        if (InternetHelper().checkInternetConnection(this@DetailVideoActivity)) {
            fetchDetailVideo()
        } else {
            Toast.makeText(
                this@DetailVideoActivity,
                "No internet connection",
                Toast.LENGTH_LONG
            ).show()
            finish()
        }
    }

    private fun setupViews() {
        btn_download.setOnClickListener {
            checkRequestPermission()
            showDownloadDialog()
        }
    }

    private fun showDownloadDialog() {
        val builder = AlertDialog.Builder(this, R.style.DownloadDialog)

        val view = layoutInflater.inflate(R.layout.alert_download_dialog, null)
        builder.setView(view)
        dialogDownloadButton = view.findViewById(R.id.btn_alert_download)
        dialogRecyclerView = view.findViewById(R.id.alert_recycler_view)

        initDialogAdapter()
        dialogAdapter.updateData(formatsToShowList)
        val alert = builder.create()
        alert.show()
        downloadAction(alert)

    }

    private fun downloadAction(builder: AlertDialog) {
        dialogDownloadButton.setOnClickListener {
            var downloadName = fileName!!
            downloadName = downloadName.replace("[\\\\><\"|*?%:#/]".toRegex(), "")
            var downloadIds = ""

            try {
                if (fileVideo?.videoFile != null) {
                    downloadIds += DownloadMaster().downloadFile(
                        this,
                        fileVideo?.videoFile!!.url,
                        fileVideo?.videoFile!!.format.ext,
                        downloadName

                    )
                    downloadIds += "-"
                }
                if (fileVideo?.audioFile != null) {
                    downloadIds += DownloadMaster().downloadFile(
                        this,
                        fileVideo?.audioFile!!.url,
                        fileVideo?.audioFile!!.format.ext,
                        downloadName
                    )
                }

            } catch (e: Exception) {

            }
            builder.dismiss()
        }
    }


    private fun initDialogAdapter() {
        dialogAdapter = DownloadDialogAdapter { item: YtVideo -> downloadClickItem(item) }
        dialogRecyclerView.layoutManager = LinearLayoutManager(applicationContext)
        dialogRecyclerView.adapter = dialogAdapter
    }

    private fun downloadClickItem(item: YtVideo) {
        selectedVideoQuality = item.videoFile?.url
        selectedVideoExt = item.videoFile?.format?.ext
        fileVideo = item

    }

    private fun checkRequestPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                1024
            )
        } else {
            writePermission = true
        }
    }

    private fun initActionBar() {
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return true
    }

    private fun getExtra() {
        videoId = intent?.getStringExtra("videoId")
        playlistId = intent?.getStringExtra("playlistId")
    }

    private fun fetchDetailVideo() {
        val data = videoId?.let { viewModel?.getVideoData(it) }
        data?.observe(this, Observer<DetailVideo> {
            val model: DetailVideo? = data.value
            when {
                model != null -> {
                    setData(model)
                    actualLink(model.items?.get(0)?.id.toString())
                    viewModel?.insertDetailVideoData(model)
                }
            }
        })
    }

    private fun setData(model: DetailVideo) {
        tv_title.text = model.items?.get(0)?.snippet?.title
        fileName = model.items?.get(0)?.snippet?.title
        tv_description.text = model.items?.get(0)?.snippet?.description
    }

    @SuppressLint("StaticFieldLeak")
    private fun actualLink(link: String) {
        object : YouTubeExtractor(this) {
            public override fun onExtractionComplete(
                ytFiles: SparseArray<YtFile>?,
                vMeta: VideoMeta
            ) {
                formatsToShowList = mutableListOf()
                var i = 0
                var itag: Int
                if (ytFiles != null) {
                    while (i < ytFiles.size()) {
                        itag = ytFiles.keyAt(i)
                        val ytFile = ytFiles.get(itag)

                        if (ytFile.format.height >= 360) {
                            addFormatToList(ytFile, ytFiles)
                        }
                        i++
                    }
                }
                (formatsToShowList)?.sortWith(Comparator { lhs, rhs ->
                    lhs!!.height - rhs!!.height
                })
                try {
                    val yotutubeUrl: YtVideo? =
                        formatsToShowList?.get(formatsToShowList!!.lastIndex - 3)
                    playVideo(yotutubeUrl?.videoFile?.url!!)
                } catch (e: Exception) {
                    Toast.makeText(
                        this@DetailVideoActivity,
                        "Video can't be played",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                }
            }
        }.extract(link, true, true)
    }

    private fun addFormatToList(ytFile: YtFile, ytFiles: SparseArray<YtFile>) {
        val height = ytFile.format.height
        if (height != -1) {
            for (frVideo in this.formatsToShowList!!) {
                if (frVideo?.height == height && (frVideo.videoFile == null || frVideo.videoFile!!.format.fps == ytFile.format.fps)) {
                    return
                }
            }
        }
        val frVideo = YtVideo()
        frVideo.height = height
        if (ytFile.format.isDashContainer) {
            if (height > 0) {
                frVideo.videoFile = ytFile
                frVideo.audioFile = ytFiles.get(ITAG_FOR_AUDIO)
            } else {
                frVideo.audioFile = ytFile
            }
        } else {
            frVideo.videoFile = ytFile
        }
        formatsToShowList!!.add(frVideo)
    }

    private fun playVideo(url: String) {
        player_view?.player = player
        PlayerManager.getSharedInstance(this).playStream(url)
        PlayerManager.getSharedInstance(this).setPlayerListener(this)
    }

    override fun onPlayingEnd() {
    }

    override fun onItemClickOnItem(albumId: Int) {

    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            player_view.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
            supportActionBar?.hide()
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            player_view.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            supportActionBar?.show()
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
    }


}
