package com.example.instafameproj.ui.home

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import com.example.instafameproj.R
import com.example.instafameproj.databinding.HomeVideoRowBinding
import com.example.instafameproj.ui.Model.UserModel
import com.example.instafameproj.ui.Model.VideoModel
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class HomeVideoListAdapter(
    options: FirestoreRecyclerOptions<VideoModel>
) : FirestoreRecyclerAdapter<VideoModel,HomeVideoListAdapter.VideoViewHolder>(options)  {
    private lateinit var context: Context

    private lateinit var prevView: MediaPlayer
    private lateinit var listVideoView: MutableList<VideoView>
    inner class VideoViewHolder(private val binding : HomeVideoRowBinding) : RecyclerView.ViewHolder(binding.root),
        Player.Listener {
        init {


            itemView.setOnClickListener {

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
                        //binding.usernameView.text = userName
                        binding.usernameView.text = "yolo408"

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

                    //binding.captionView.text = videoModel.title
                    binding.captionView.text = "Testing my caption"
                    binding.progressBar.visibility = View.GONE
                    val uri1 = Uri.parse(videoModel.url)

                    val player = ExoPlayer.Builder(context).build()
                    player.addListener(object : Player.Listener {
                        override fun onPlayerStateChanged(
                            playWhenReady: Boolean,
                            playbackState: Int
                        ) {
                            when (playbackState) {
                                ExoPlayer.STATE_BUFFERING -> {   }
                                ExoPlayer.STATE_ENDED -> {player.play()}
                                ExoPlayer.STATE_IDLE -> {}
                                ExoPlayer.STATE_READY -> { Log.d("player_ready", position.toString())}
                                else -> {}
                            }
                        }

                        fun onPlayWhenReadyCommitted() {}
                        fun onPlayerError(error: ExoPlaybackException?) {}
                    })

                    //  player.repeatMode = Player.REPEAT_MODE_ALL
                    binding.videoView.player = player
                    Log.d("is binding", position.toString())

                    val mediaItem: MediaItem = MediaItem.fromUri(uri1)
                    player.setMediaItem(mediaItem)
                    player.prepare()
                    //player.playWhenReady = true
                    if(player.isLoading){
                        binding.progressBar.visibility = View.VISIBLE
                    }
                }



            holder.itemView.setOnClickListener {
                val playerView = it.findViewById<com.google.android.exoplayer2.ui.PlayerView>(R.id.video_view)
                val player = playerView.player
                if (player != null) {
                    player.prepare()
                }
                if( player != null){
                    if(player.isPlaying){
                        player.pause()
                        Log.d("Item_click", "fsdfsd")
                    }
                    else{
                        player.play()
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




}