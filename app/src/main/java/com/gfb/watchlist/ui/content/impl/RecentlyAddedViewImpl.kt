package com.gfb.watchlist.ui.content.impl


import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gfb.watchlist.R
import com.gfb.watchlist.activity.ContentDetailsActivity
import com.gfb.watchlist.adapter.ContentAdapter
import com.gfb.watchlist.entity.Content
import com.gfb.watchlist.entity.ContentContainer
import com.gfb.watchlist.entity.Result
import com.gfb.watchlist.fragment.BaseFragment
import com.gfb.watchlist.prefs
import com.gfb.watchlist.ui.content.ContentView
import com.gfb.watchlist.util.Constants
import io.reactivex.rxkotlin.subscribeBy
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.yesButton


class RecentlyAddedViewImpl : BaseFragment(), ContentView {
    private lateinit var recyclerViewContent: RecyclerView
    private var inflated = false
    private val presenter = ContentPresenterImpl()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_recently_added, container, false)
        recyclerViewContent = view.findViewById(R.id.recyclerViewContent)
        recyclerViewContent.layoutManager = LinearLayoutManager(view.context)
        createAdapter()
        return view
    }

    companion object {
        fun newInstance(): RecentlyAddedViewImpl {
            return RecentlyAddedViewImpl()
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        when {
            inflated && isVisibleToUser -> createAdapter()
        }
    }

    private fun createAdapter() {
        val adapter = ContentAdapter(ContentContainer.content,
                { content -> callActivity(content) },
                { content -> confirmationArchive(content) })
        recyclerViewContent.adapter = adapter
        inflated = true
    }

    private fun callActivity(content: Content) {
        val intent = Intent(context, ContentDetailsActivity::class.java)
        intent.putExtra(Constants.TRANSITION_KEY_CONTENT, content)
        startActivity(intent)
    }

    private fun confirmationArchive(content: Content) {
        alert(String.format(getString(R.string.message_confirmation_archive_content), content.title), getString(R.string.title_archive_content)) {
            positiveButton(R.string.yes) { archiveContent(content) }
            negativeButton(R.string.no) {}
        }.show()
    }

    override fun archiveContent(content: Content) {
        showProgress()
        presenter.archiveContent(prefs.userId, content).applySchedulers()
                .subscribeBy(
                        onNext = {
                            deleteContent(it, content)
                        },
                        onError = {
                            handleException(it)
                            closeProgress()
                        },
                        onComplete = {
                            closeProgress()
                        }
                )
    }

    override fun deleteContent(result: Result, content: Content) {
        alert(result.message, getString(R.string.title_success)) {
            yesButton {
                ContentContainer.content.remove(content)
                createAdapter()
            }
        }.show()
    }

}