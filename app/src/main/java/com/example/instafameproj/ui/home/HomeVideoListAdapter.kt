package com.example.instafameproj.ui.home

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.instafameproj.R
import com.example.instafameproj.databinding.HomeVideoRowBinding
import com.example.instafameproj.ui.Model.UserModel
import com.example.instafameproj.ui.Model.VideoModel
import com.example.instafameproj.ui.UserProfileViewModel
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class HomeVideoListAdapter(
    options: FirestoreRecyclerOptions<VideoModel>,
    private val currentUserModel: UserProfileViewModel,
    private val clickListener: (songIndex : Boolean)->Unit,
    private val followingListener: (uid:String, isFollow:Boolean)->Unit,
    private val likeListener: (video:String, videoUid: String ,isLike:Boolean)->Unit,
) : FirestoreRecyclerAdapter<VideoModel,HomeVideoListAdapter.VideoViewHolder>(options)  {
    private lateinit var context: Context
    inner class VideoViewHolder(private val binding : HomeVideoRowBinding) : RecyclerView.ViewHolder(binding.root){
        init {

        }
        fun bindVideo(videoModel: VideoModel, position: Int, holder: VideoViewHolder) {
            binding.progressBar.visibility = View.VISIBLE
            Firebase.firestore.collection("Users")
                .document(videoModel.uuid)
                .get().addOnSuccessListener {
                    val userModel = it?.toObject(UserModel::class.java)
                    userModel?.apply {
                        binding.usernameView.text = ownerName
                        Log.d("Title_fsdfds", videoModel.title)

                        //bind profilepic

                        Glide.with(binding.profileIcon).load(profilePic)
                            .circleCrop()
                            .apply(
                                RequestOptions().placeholder(R.drawable.icon_profile)
                            )
                            .into(binding.profileIcon)
                    }

                    binding.captionView.text = videoModel.title
                }
            if (currentUserModel.getCurrentAuthUser() != null) {
                Firebase.firestore.collection("Users")
                    .document(this@HomeVideoListAdapter.currentUserModel.getCurrentAuthUser()!!.uid)
                    .get().addOnSuccessListener {
                        val followerBt = binding.followBt
                        val likeBt = binding.likeBt
                        val userModel = it?.toObject(UserModel::class.java)

                        if (userModel != null) {
                            if (userModel.followingList.contains(videoModel.uuid)) {
                                setBackgroundDrawable(
                                    followerBt,
                                    R.drawable.baseline_person_add_alt_1_24
                                )
                            } else {
                                setBackgroundDrawable(
                                    followerBt,
                                    R.drawable.baseline_person_add_alt_24
                                )
                            }
                        }

                        if (userModel != null) {
                            if (userModel.likesList.contains(videoModel.videoId)) {
                                setBackgroundDrawable(likeBt, R.drawable.ic_favorite_black_24dp)
                            } else {
                                setBackgroundDrawable(likeBt, R.drawable.heart)
                            }
                        }
                    }

                binding.videoView.apply {
                    setVideoPath(videoModel.url)
                    setOnPreparedListener {
                        val followerBt = binding.followBt

                        if (currentUserModel.getUserMeta() != null) {
                            if (currentUserModel.getUserMeta().followingList.contains(videoModel.uuid)) {
                                setBackgroundDrawable(
                                    followerBt,
                                    R.drawable.baseline_person_add_alt_1_24
                                )
                            } else {
                                setBackgroundDrawable(
                                    followerBt,
                                    R.drawable.baseline_person_add_alt_24
                                )
                            }
                        }
                        it.start()
                        it.isLooping = true
                        Log.d("is binding", position.toString())
                        Log.d("Title", videoModel.title)
                        Log.d("Adapter", absoluteAdapterPosition.toString())
                        binding.progressBar.visibility = View.GONE

                        if (itemCount == (absoluteAdapterPosition + 1)) {
                            clickListener(true)
                        } else {
                            clickListener(false)
                        }
                    }
                    setOnInfoListener { mp, what, extra ->
                        when (what) {
                            MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START -> {
                                binding.progressBar.visibility = View.GONE
                            }

                            MediaPlayer.MEDIA_INFO_BUFFERING_START -> {
                                binding.progressBar.visibility = View.VISIBLE
                            }

                            MediaPlayer.MEDIA_INFO_BUFFERING_END -> {
                                binding.progressBar.visibility = View.GONE
                            }

                            MediaPlayer.MEDIA_INFO_VIDEO_NOT_PLAYING -> {
                                mp.stop()
                                this.suspend()
                            }
                        }
                        true
                    }
                    setOnClickListener {
                        if (isPlaying) {
                            pause()
                            binding.pauseIcon.visibility = View.VISIBLE
                        } else {
                            start()
                            Log.d("is binding", position.toString())
                            binding.pauseIcon.visibility = View.GONE
                        }
                    }
                }
            }

            binding.likeBt.setOnClickListener {
                val a = it as ImageButton
                if (a.tag != R.drawable.ic_favorite_black_24dp) {
                    setBackgroundDrawable(a, R.drawable.ic_favorite_black_24dp)
                    likeListener(videoModel.videoId, videoModel.uuid, true)

                } else {
                    setBackgroundDrawable(a, R.drawable.heart)
                    likeListener(videoModel.videoId, videoModel.uuid, false)

                }
            }


            binding.followBt.setOnClickListener {
                val a = it as ImageButton
                if(a.tag != R.drawable.baseline_person_add_alt_1_24) {
                    setBackgroundDrawable(a,R.drawable.baseline_person_add_alt_1_24)
                    followingListener(videoModel.uuid, true )
                }
                else
                {
                    setBackgroundDrawable(a,R.drawable.baseline_person_add_alt_24)
                    followingListener(videoModel.uuid, false )
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val binding = HomeVideoRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        context =parent.context

        parent.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            Log.d("scroll", "{$oldScrollX} + {$oldScrollY}")

        }

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
    fun setBackgroundDrawable(button: ImageView, resourceId: Int) {
        button.setImageResource(resourceId)
        button.tag = resourceId
    }
}