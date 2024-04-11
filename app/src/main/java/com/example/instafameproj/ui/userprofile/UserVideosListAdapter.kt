package com.example.instafameproj.ui.userprofile

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.VideoView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.instafameproj.R
import com.example.instafameproj.databinding.UserVideoRowBinding
import com.example.instafameproj.ui.Model.VideoModel
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerView


class UserVideosListAdapter(private val viewModel: UserProfileViewModel,
                            private val clickListener: (songIndex : Int)->Unit)
// https://developer.android.com/reference/androidx/recyclerview/widget/ListAdapter
// Slick adapter that provides submitList, so you don't worry about how to update
// the list, you just submit a new one when you want to change the list and the
// Diff class computes the smallest set of changes that need to happen.
// NB: Both the old and new lists must both be in memory at the same time.
// So you can copy the old list, change it into a new list, then submit the new list.
    : ListAdapter<VideoModel,
        UserVideosListAdapter.ViewHolder>(Diff())
{
    private lateinit var context: Context
    private lateinit var player: Player
    inner class ViewHolder(private val videoRowBinding : UserVideoRowBinding)
        : RecyclerView.ViewHolder(videoRowBinding.root) {
        init {
            itemView.setOnClickListener {
                val playerView = it.findViewById<VideoView>(R.id.videoView1)
                val img = itemView.findViewById<ImageView>(R.id.ThumbnailView)

                if(playerView.isPlaying){
                        playerView.pause()
                        img.visibility = View.VISIBLE
                    } else{
                        val img = itemView.findViewById<ImageView>(R.id.ThumbnailView)
                        img.visibility = View.GONE
                        Log.d("item_click", "player is click")
                        val url = viewModel.getUserMeta().videoUrl[absoluteAdapterPosition]
                        Log.d("item_click", url.toString())
                        playerView.setVideoPath(url)
                        playerView.start()
                    }
            }
        }

        fun bind(holder: ViewHolder, position: Int) {
            val rowBinding = holder.videoRowBinding

            val uri1 = Uri.parse(viewModel.getUserMeta().videoUrl[position])
            Log.d("video_url", uri1.toString())
            Glide.with(rowBinding.ThumbnailView).load(uri1).into(rowBinding.ThumbnailView)
/*
            Log.d("url", viewModel.getUserMeta().videoUrl[position])
            rowBinding.videoView1.setVideoPath(viewModel.getUserMeta().videoUrl[position])
            rowBinding.videoView1.seekTo(1)
            val videoView = holder.itemView.findViewById<VideoView>(R.id.videoView1)

// Set aspect ratio (width:height) - For example, 16:9
            val aspectRatioWidth = 16
            val aspectRatioHeight = 9

// Calculate the height based on the desired aspect ratio

// Set layout parameters
            val playerView = holder.itemView.findViewById<CardView>(R.id.cardV)

            val layoutParams = videoView.layoutParams
            layoutParams.width = 1000
            layoutParams.height = 400
            videoView.layoutParams = layoutParams

 */
            /*
            val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(
                context, Util.getUserAgent(context, "RecyclerView VideoPlayer")
            )
            val videoSource: MediaDataSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri1)
*/
            /*
            player = ExoPlayer.Builder(context).build()

            rowBinding.videoView1.player = player
            val mediaItem: MediaItem = MediaItem.fromUri(uri1)
            player.setMediaItem(mediaItem)

            //player.prepare(videoSource)
            player.playWhenReady = false
            */
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val rowBinding = UserVideoRowBinding.inflate(LayoutInflater.from(parent.context),
            parent, false)
        context =parent.context

        return ViewHolder(rowBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bind(holder, position)
    }


    class Diff : DiffUtil.ItemCallback<VideoModel>() {
        // Item identity
        override fun areItemsTheSame(oldItem: VideoModel, newItem: VideoModel): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
        override fun areContentsTheSame(oldItem: VideoModel, newItem: VideoModel): Boolean {
            return oldItem.uuid == newItem.uuid
                    && oldItem.videoId == newItem.videoId
                    && oldItem.url == newItem.url
                    && oldItem.title == newItem.title
                    && oldItem.createdTime == newItem.createdTime

        }
    }

    private fun setVideoThumbnail(playerView: PlayerView, thumbnailImage: ImageView ) {
        val textureView = playerView.videoSurfaceView as TextureView
        val bitmap = textureView.bitmap
        thumbnailImage.setImageBitmap(bitmap)
        thumbnailImage.visibility = View.VISIBLE
    }

    fun releasePlayer(){
       // player.release()
    }

}