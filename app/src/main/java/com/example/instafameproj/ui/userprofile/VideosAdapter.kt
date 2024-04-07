package com.example.instafameproj.ui.userprofile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.instafameproj.databinding.VideoRowBinding
import com.example.instafameproj.videoData

class VideosAdapter(private val viewModel: UserProfileViewModel,
                    private val clickListener: (songIndex : Int)->Unit)
// https://developer.android.com/reference/androidx/recyclerview/widget/ListAdapter
// Slick adapter that provides submitList, so you don't worry about how to update
// the list, you just submit a new one when you want to change the list and the
// Diff class computes the smallest set of changes that need to happen.
// NB: Both the old and new lists must both be in memory at the same time.
// So you can copy the old list, change it into a new list, then submit the new list.
    : ListAdapter<videoData,
        VideosAdapter.ViewHolder>(Diff())
{

    // ViewHolder pattern holds row binding
    inner class ViewHolder(val videoRowBinding : VideoRowBinding)
        : RecyclerView.ViewHolder(videoRowBinding.root) {
        init {


        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //XXX Write me.
        // TODO: copy and paste might be correct
        val rowBinding = VideoRowBinding.inflate(LayoutInflater.from(parent.context),
            parent, false)
        return ViewHolder(rowBinding)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //XXX Write me.
        //TODO: DONE ?
        val rowBinding = holder.videoRowBinding


    }

    class Diff : DiffUtil.ItemCallback<videoData>() {
        // Item identity
        override fun areItemsTheSame(oldItem: videoData, newItem: videoData): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
        // Item contents are the same, but the object might have changed
        override fun areContentsTheSame(oldItem: videoData, newItem: videoData): Boolean {
            return oldItem.uuid == newItem.uuid

        }
    }
}