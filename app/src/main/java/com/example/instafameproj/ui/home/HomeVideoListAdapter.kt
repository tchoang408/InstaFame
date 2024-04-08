package com.example.instafameproj.ui.home

import android.media.MediaPlayer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import com.example.instafameproj.databinding.HomeVideoRowBinding
import com.example.instafameproj.ui.Model.UserModel
import com.example.instafameproj.ui.Model.VideoModel
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeVideoListAdapter(
    options: FirestoreRecyclerOptions<VideoModel>
) : FirestoreRecyclerAdapter<VideoModel,HomeVideoListAdapter.VideoViewHolder>(options)  {

    private lateinit var prevView: MediaPlayer
    inner class VideoViewHolder(private val binding : HomeVideoRowBinding) : RecyclerView.ViewHolder(binding.root){
        init {
            itemView.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                Log.d("Scrol_area", scrollY.toString())
                Log.d("Scrol_area_ab", absoluteAdapterPosition.toString())
            }


        }
        fun bindVideo(videoModel: VideoModel){
            Log.d("Binding_process", "process")
            //bindUserData
            Firebase.firestore.collection("Users")
                .document(videoModel.uuid)
                .get().addOnSuccessListener {
                    val userModel = it?.toObject(UserModel::class.java)
                    userModel?.apply {
                        binding.usernameView.text = userName
                        //bind profilepic
                        /*
                        Glide.with(binding.profileIcon).load(profilePic)
                            .circleCrop()
                            .apply(
                                RequestOptions().placeholder(R.drawable.icon_profile)
                            )
                            .into(binding.profileIcon)

                         */
                    }
                }

            binding.captionView.text = videoModel.title
            binding.progressBar.visibility = View.VISIBLE


            //bindVideo
            binding.videoView.apply {
                setVideoPath(videoModel.url)
                setOnPreparedListener {
                    binding.progressBar.visibility = View.GONE
                    Log.d("video_start", videoModel.url.toString())
                    if(absoluteAdapterPosition != 0)
                        prevView.stop()
                    it.start()
                    it.isLooping = true
                        prevView = it

                    Log.d("Scrol_area_ab", absoluteAdapterPosition.toString())
                }
                //play pause
                setOnClickListener {
                    if(isPlaying){
                        pause()
                        binding.pauseIcon.visibility = View.VISIBLE
                    }else{
                        start()
                        binding.pauseIcon.visibility = View.GONE
                    }
                }
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val binding = HomeVideoRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return VideoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int, model: VideoModel) {
        holder.bindVideo(model)
    }

}