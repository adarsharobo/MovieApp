package com.robosoftin.movieapp.ui.feature.detail

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.leanback.app.DetailsSupportFragment
import androidx.leanback.widget.*
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.imageLoader
import coil.request.ImageRequest
import com.robosoftin.movieapp.R
import com.robosoftin.movieapp.data.remote.Movie
import com.robosoftin.movieapp.ui.feature.home.PosterPresenter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailFragment : DetailsSupportFragment() {

    companion object {
        private const val ACTION_PLAY = 1L
    }

    val viewModel: DetailViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.args.asLiveData().observe(viewLifecycleOwner) { _args ->
            _args?.let { args ->
                bindArgs(args)
            }
        }

        viewModel.toast.asLiveData().observe(viewLifecycleOwner) { stringRes ->
            Toast.makeText(requireContext(), stringRes, Toast.LENGTH_SHORT).show()
        }

        viewModel.navigateToDetail.asLiveData().observe(viewLifecycleOwner) {
            findNavController().navigate(
                DetailFragmentDirections.actionDetailToDetail(
                    it.category,
                    it.movie
                )
            )
        }

        setOnItemViewClickedListener { _, item, _, _ ->
            when (item) {
                is Action -> {
                    when (item.id) {
                        ACTION_PLAY -> {
                            viewModel.onPlayClicked()
                        }
                        else -> error("Undefined action")
                    }
                }

                is Movie -> {
                    viewModel.onMovieClicked(item)
                }
            }
        }
    }

    private fun bindArgs(args: DetailFragmentArgs) {
        // Bind to detail
        val movie = args.movie
        title = movie.name

        // Meta
        val rowsAdapter = createRowsAdapter()
        val detailsOverviewRow = DetailsOverviewRow(movie).apply {
            lifecycleScope.launch {
                val imageRequest = ImageRequest.Builder(requireContext())
                    .data(movie.imageUrl)
                    .build()

                requireContext()
                    .imageLoader
                    .execute(imageRequest)
                    .drawable
                    ?.let {
                        this@apply.imageDrawable = it
                    }
            }
        }

        // Actions
        val actionAdapter = ArrayObjectAdapter()
        actionAdapter.add(
            Action(
                ACTION_PLAY,
                getString(R.string.detail_action_play)
            )
        )
        detailsOverviewRow.actionsAdapter = actionAdapter
        rowsAdapter.add(detailsOverviewRow)

        // More like this
        val moreLikeThisAdapter = ArrayObjectAdapter(PosterPresenter())
        for (relatedMovie in args.category.movies) {
            moreLikeThisAdapter.add(relatedMovie)
        }
        val moreLikeThisHeader =
            HeaderItem(getString(R.string.detail_header_more_like_this))
        rowsAdapter.add(ListRow(moreLikeThisHeader, moreLikeThisAdapter))


        // Finally, set the adapter
        this.adapter = rowsAdapter
    }

    private fun createRowsAdapter(): ArrayObjectAdapter {
        val rowPresenterSelector = ClassPresenterSelector()
        val detailsOverviewRowPresenter = FullWidthDetailsOverviewRowPresenter(DetailPresenter())
        rowPresenterSelector.addClassPresenter(
            DetailsOverviewRow::class.java,
            detailsOverviewRowPresenter
        )
        rowPresenterSelector.addClassPresenter(ListRow::class.java, ListRowPresenter())
        return ArrayObjectAdapter(rowPresenterSelector)
    }
}