package com.example.instafameproj.ui.home

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
import com.example.instafameproj.ui.userprofile.UserProfileViewModel
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
            ::followListener,
            ::likeListener)
        binding.viewPager.adapter = adapter
        viewModel.observeAuthUser().observe(viewLifecycleOwner){
            setupViewPager()
        }

        binding.endViewrefresh.setOnClickListener {
            val db: FirebaseFirestore = FirebaseFirestore.getInstance()
            val query = db.collection("Videos")
                .whereNotEqualTo("uuid", viewModel.getCurrentAuthUser()?.uid)
                .orderBy("createdTime", Query.Direction.DESCENDING)
                .orderBy("title")
                .limit(10)

            val options = FirestoreRecyclerOptions.Builder<VideoModel>()
                .setQuery(query, VideoModel::class.java)
                .build()


            adapter.updateOptions(options)
        }
    }


    private fun setupViewPager(){
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()
        val query = db.collection("Videos")
            .whereNotEqualTo("uuid", viewModel.getCurrentAuthUser()?.uid)
            .orderBy("title")
            .orderBy("createdTime", Query.Direction.DESCENDING)
        val options = FirestoreRecyclerOptions.Builder<VideoModel>()
            .setQuery(query, VideoModel::class.java)
            .build()


        adapter = HomeVideoListAdapter(options,
            viewModel,
            ::list,
            ::followListener,
            ::likeListener)
        binding.viewPager.adapter = adapter

        adapter.startListening()
        /*
        binding.homeRV.adapter = adapter
        val layoutManager = LinearLayoutManager(binding.homeRV.context)
        binding.homeRV.layoutManager = layoutManager
        */
    }

    override fun onStart() {
        super.onStart()

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
                val db: FirebaseFirestore = FirebaseFirestore.getInstance()
                val query = db.collection("Videos")
                    .whereNotEqualTo("uuid", viewModel.getCurrentAuthUser()?.uid)
                    .orderBy("createdTime", Query.Direction.DESCENDING)
                    .orderBy("title")
                    .limit(10)

                val options = FirestoreRecyclerOptions.Builder<VideoModel>()
                    .setQuery(query, VideoModel::class.java)
                    .build()

                adapter.updateOptions(options)
                swipe.isRefreshing = false

                Handler().postDelayed({
                    // Once the operation is complete, call setRefreshing(false) to stop the refreshing animation
                    swipe.isRefreshing = false
                }, 1000) // Delay in milliseconds
            }
        }
    }

    fun list(s:Boolean){
        if(s)
            binding.endViewrefresh.visibility = View.VISIBLE
        else
            binding.endViewrefresh.visibility = View.GONE
    }

    fun followListener(uid:String, isFollow:Boolean){

        if(isFollow){
            viewModel.addUserFollower(uid){

            }
        }
        else{
            viewModel.removeUserFollower(uid){

            }
        }
    }

    fun likeListener(videoId:String, videoUid: String,isLikes:Boolean){
        if(isLikes){
            viewModel.addUserLikes(videoId,videoUid){

            }
        }
        else{
            viewModel.removeUserLikes(videoId){

            }
        }
    }

}