package com.example.instafameproj.ui.home

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.instafameproj.R
import com.example.instafameproj.databinding.HomeVideoRowBinding
import com.example.instafameproj.ui.Model.UserModel
import com.example.instafameproj.ui.Model.VideoModel
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class HomeVideoListAdapter(
    options: FirestoreRecyclerOptions<VideoModel>
) : FirestoreRecyclerAdapter<VideoModel,HomeVideoListAdapter.VideoViewHolder>(options)  {
    private lateinit var context: Context

    private lateinit var listVideoView: MutableList<VideoView>

    private lateinit var player: ExoPlayer
    inner class VideoViewHolder(private val binding : HomeVideoRowBinding) : RecyclerView.ViewHolder(binding.root),
        Player.Listener {
        init {


            itemView.setOnClickListener {
                val d = it.findViewById<ViewPager2>(R.id.videosRV)
                d.setOnClickListener {
                    Log.d("page_click", "update fater")
                }
                val playerView = it.findViewById<com.google.android.exoplayer2.ui.PlayerView>(R.id.video_view)
                val player = playerView.player
                Log.d("Item_click", "fsdfsd")
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
        fun bindVideo(videoModel: VideoModel, position: Int, holder: VideoViewHolder){
            Log.d("Binding_process", "process")
            //bindUserData
            Firebase.firestore.collection("Users")
                .document(videoModel.uuid)
                .get().addOnSuccessListener {
                    val userModel = it?.toObject(UserModel::class.java)
                    userModel?.apply {
                        binding.usernameView.text = ownerName

                        //bind profilepic

                        Glide.with(binding.profileIcon).load(profilePic)
                            .circleCrop()
                            .apply(
                                RequestOptions().placeholder(R.drawable.icon_profile)
                            )
                            .into(binding.profileIcon)
                    }

                    //binding.captionView.text = videoModel.title
                    binding.captionView.text = "Testing my caption"
                    binding.progressBar.visibility = View.GONE

                }

            binding.videoView.apply {
                setVideoPath(videoModel.url)
                setOnPreparedListener {
                    binding.progressBar.visibility = View.GONE
                    it.start()
                    it.isLooping = true
                    Log.d("is binding", position.toString())
                }
                setOnCompletionListener{
                    Log.d("complete", "song complete")
                }


                //play pause
                setOnClickListener {
                    if(isPlaying){
                        pause()
                        binding.pauseIcon.visibility = View.VISIBLE
                    }else{
                        start()
                        Log.d("is binding", position.toString())
                        binding.pauseIcon.visibility = View.GONE
                    }
                }
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val binding = HomeVideoRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        context =parent.context
        return VideoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int, model: VideoModel) {
        holder.bindVideo(model, position, holder)
    }

    override fun onError(e: FirebaseFirestoreException) {
        Log.w("FirestoreRecycler", "onError", e)
    }


    override fun onDataChanged() {
        // Called each time there is a new query snapshot. You may want to use this method
        // to hide a loading spinner or check for the "no documents" state and update your UI.
        // ...
        Log.d("homeAdapter", "data changed")
    }
    fun erro(f:Boolean){

    }
}