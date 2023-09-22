package com.mc7.mybookmarklistapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mc7.mybookmarklistapp.databinding.FragmentNewsBinding
import com.mc7.mybookmarklistapp.data.Result
import com.mc7.mybookmarklistapp.ui.viewmodel.NewsViewModel
import com.mc7.mybookmarklistapp.ui.viewmodel.ViewModelFactory

class NewsFragment : Fragment() {

    private var tabName: String? = null
    private lateinit var binding : FragmentNewsBinding

    companion object {
        const val ARG_TAB = "tab_name"
        const val TAB_NEWS = "news"
        const val TAB_BOOKMARK = "bookmark"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        binding = FragmentNewsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tabName = arguments?.getString(ARG_TAB)

        val factory: ViewModelFactory = ViewModelFactory.getInstance(requireActivity())
        val viewModel: NewsViewModel by viewModels { factory }

        val newsAdapter = NewsAdapter { news ->
            if (news.isBookmarked){
                viewModel.deleteNews(news)
            } else {
                viewModel.saveNews(news)
            }
        }

        if (tabName == TAB_NEWS) {
            viewModel.getHeadlineNews().observe(viewLifecycleOwner) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }
                        is Result.Success -> {
                            binding.progressBar.visibility = View.GONE
                            val newsData = result.data
                            newsAdapter.submitList(newsData)
                        }
                        is Result.Error -> {
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(
                                context,
                                "Terjadi kesalahan" + result.error,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }else if (tabName == TAB_BOOKMARK) {
            viewModel.getBookmarkedNews().observe(viewLifecycleOwner) { bookmarkedNews ->
                binding.progressBar.visibility = View.GONE
                newsAdapter.submitList(bookmarkedNews)
            }
        }

        binding.rvNews.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = newsAdapter
        }
    }
}