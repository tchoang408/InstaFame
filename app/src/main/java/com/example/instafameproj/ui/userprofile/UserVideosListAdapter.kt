package com.example.instafameproj.ui.userprofile

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.VideoView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.instafameproj.R
import com.example.instafameproj.ui.Model.VideoModel
import com.example.instafameproj.databinding.UserVideoRowBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem


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

    inner class ViewHolder(private val videoRowBinding : UserVideoRowBinding)
        : RecyclerView.ViewHolder(videoRowBinding.root) {
        init {
            itemView.setOnClickListener {

                val playerView = it.findViewById<com.google.android.exoplayer2.ui.PlayerView>(R.id.videoView1)
                val player = playerView.player
                if( player != null){
                    if(player.isPlaying){
                        player.pause()
                    }
                    else{
                        player.play()
                    }
                }
            }
        }

        fun bind(holder: ViewHolder, position: Int) {
            val rowBinding = holder.videoRowBinding

            val uri1 = Uri.parse(viewModel.getUserMeta().videoUrl[position])
            Log.d("video_url", uri1.toString())
            /*
            Log.d("url", viewModel.getUserMeta().videoUrl[position])
            rowBinding.videoView1.setVideoPath(viewModel.getUserMeta().videoUrl[position])
            rowBinding.videoView1.seekTo(1)
            */


            val player = ExoPlayer.Builder(context).build()

            rowBinding.videoView1.player = player
            val mediaItem: MediaItem = MediaItem.fromUri(uri1)
            player.setMediaItem(mediaItem)

            player.prepare()
            player.playWhenReady = false


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


}