package com.example.instafameproj.ui.home

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.instafameproj.MainActivity
import com.example.instafameproj.databinding.FragmentHomeBinding
import com.example.instafameproj.ui.Model.VideoModel
import com.example.instafameproj.ui.UserProfileViewModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var adapter : HomeVideoListAdapter
    private val viewModel: UserProfileViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(javaClass.simpleName, "onViewCreated")
        val mainActivity = (requireActivity() as MainActivity)
        initSwipeLayout(binding.swipeRefreshLayout)
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()
        val query = db.collection("Videos")
            .orderBy("title")
            .orderBy("createdTime", Query.Direction.DESCENDING)
        val options = FirestoreRecyclerOptions.Builder<VideoModel>()
            .setQuery(query, VideoModel::class.java)
            .build()


        adapter = HomeVideoListAdapter(options,
            viewModel,
            ::list,
            ::followingListener,
            ::likeListener,
        )
        binding.viewPager.adapter = adapter

        viewModel.observeAuthUser().observe(viewLifecycleOwner){
            val drawable: Drawable = binding.friendsBt.background
            drawable.setTint(Color.WHITE)
            binding.friendsBt.tag = "0"
            setupViewPager()
        }

        binding.friendsBt.setOnClickListener {
            if(it.tag == "0")
            {
                val drawable: Drawable = it.background
                drawable.setTint(Color.rgb(0x2e,0x8b, 0xC0))
                it.tag = "1"
                personalVideo(true)
            }
            else{
                val drawable: Drawable = it.background
                drawable.setTint(Color.WHITE)
                it.tag = "0"
                personalVideo(false)
            }
        }

        binding.endViewrefresh.setOnClickListener {
            getAllVideos()
            disableFriendView ()
            adapter.startListening()

        }
    }


    private fun setupViewPager(){
        getAllVideos()
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        if(adapter != null)
            adapter.stopListening()
    }

    private fun initSwipeLayout(swipe : SwipeRefreshLayout) {
        // XXX Write me
        swipe.setOnRefreshListener {
            if(swipe.isRefreshing){
                getAllVideos()
                disableFriendView ()

                swipe.isRefreshing = false

                Handler().postDelayed({
                    // Once the operation is complete, call setRefreshing(false) to stop the refreshing animation
                    swipe.isRefreshing = false
                }, 1000) // Delay in milliseconds
            }
        }
    }

    private fun list(s:Boolean){
        if(s)
            binding.endViewrefresh.visibility = View.VISIBLE
        else
            binding.endViewrefresh.visibility = View.GONE
    }

    private fun followingListener(followingUid:String, isFollow:Boolean){

        if(isFollow){
            viewModel.addUserFollowing(followingUid){

            }
        }
        else{
            viewModel.removeUserFollowing(followingUid){

            }
        }
    }

    private fun likeListener(videoId:String, videoUid: String, isLikes:Boolean){
        if(isLikes){
            viewModel.addUserLikes(videoId,videoUid){

            }
        }
        else{
            viewModel.removeUserLikes(videoId, videoUid){

            }
        }
    }

    private fun personalVideo(s:Boolean){
        if(s)
        {
            val followingList = viewModel.getUserMeta().followingList
            if(followingList.isEmpty())
            {
                followingList.add("-1")
                updatePersonalVideos(followingList)
                binding.showInfo.visibility = View.VISIBLE
            }
            else{
                binding.showInfo.visibility = View.GONE
                updatePersonalVideos(followingList)
            }

        }
        else
        {
            getAllVideos()
        }
    }


    private fun getAllVideos(){
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()
        val uuid =viewModel.getCurrentAuthUser()?.uid
        val query = db.collection("Videos")
            .whereNotIn("uuid", listOf(uuid))
            .orderBy("createdTime", Query.Direction.DESCENDING)
            .orderBy("title")

        val options = FirestoreRecyclerOptions.Builder<VideoModel>()
            .setQuery(query, VideoModel::class.java)
            .build()

        adapter.updateOptions(options)
    }

    private fun updatePersonalVideos(followList:List<String>){
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()
        val query = db.collection("Videos")
            .whereIn("uuid", followList)
            .orderBy("title")
            .orderBy("createdTime", Query.Direction.DESCENDING)
        val options = FirestoreRecyclerOptions.Builder<VideoModel>()
            .setQuery(query, VideoModel::class.java)
            .build()

        adapter.updateOptions(options)
    }

    private fun disableFriendView(){
        val drawable: Drawable = binding.friendsBt.background
        drawable.setTint(Color.WHITE)
        binding.friendsBt.tag = "0"
    }

}