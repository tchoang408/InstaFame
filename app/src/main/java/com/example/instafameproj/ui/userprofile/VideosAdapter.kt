package com.example.instafameproj.ui.userprofile

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.instafameproj.R
import com.example.instafameproj.VideoMeta
import com.example.instafameproj.databinding.VideoRowBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem


class VideosAdapter(private val viewModel: UserProfileViewModel,
                    private val clickListener: (songIndex : Int)->Unit)
// https://developer.android.com/reference/androidx/recyclerview/widget/ListAdapter
// Slick adapter that provides submitList, so you don't worry about how to update
// the list, you just submit a new one when you want to change the list and the
// Diff class computes the smallest set of changes that need to happen.
// NB: Both the old and new lists must both be in memory at the same time.
// So you can copy the old list, change it into a new list, then submit the new list.
    : ListAdapter<VideoMeta,
        VideosAdapter.ViewHolder>(Diff())
{
    private lateinit var context: Context


    inner class ViewHolder(val videoRowBinding : VideoRowBinding)
        : RecyclerView.ViewHolder(videoRowBinding.root) {
        init {
            itemView.setOnClickListener {

                val s = it.findViewById<com.google.android.exoplayer2.ui.PlayerView>(R.id.videoView1)
                val player = s.player
                if(player != null){
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

            val uri1 = Uri.parse(viewModel.getVideoList()[position].url)
            //val uri2 = Uri.parse(viewModel.getUserMeta().videoUrl[1])
            // val uri3 = Uri.parse(viewModel.getUserMeta().videoUrl[3])
            val player = ExoPlayer.Builder(context).build()

            rowBinding.videoView1.player = player
            val mediaItem: MediaItem = MediaItem.fromUri(uri1)
            player.setMediaItem(mediaItem)
            player.prepare()
            player.playWhenReady = false

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val rowBinding = VideoRowBinding.inflate(LayoutInflater.from(parent.context),
            parent, false)

/*
        val view: View =
            LayoutInflater.from(parent.context).inflate(com.example.instafameproj.R.layout.video_row, parent, false)
        //return RecyclerViewHolder(view)
        */
        context =parent.context
        return ViewHolder(rowBinding)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(holder, position)
    }

    class Diff : DiffUtil.ItemCallback<VideoMeta>() {
        // Item identity
        override fun areItemsTheSame(oldItem: VideoMeta, newItem: VideoMeta): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
        // Item contents are the same, but the object might have changed
        override fun areContentsTheSame(oldItem: VideoMeta, newItem: VideoMeta): Boolean {
            return oldItem.upload_uuid == newItem.upload_uuid

        }
    }

    fun onItemClick(v: View?, position: Int) {
    }

}