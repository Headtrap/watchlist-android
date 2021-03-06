@file:Suppress("DEPRECATION")

package com.gfb.watchlist.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.crashlytics.android.Crashlytics
import com.gfb.watchlist.R
import com.gfb.watchlist.ui.addToList.impl.AddToListViewImpl
import com.gfb.watchlist.ui.main.impl.MainViewImpl
import com.gfb.watchlist.widget.Progress
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.*

/**
 * Created by Gustavo on 12/4/2017.
 */


@SuppressLint("Registered")
open class BaseView : AppCompatActivity() {

    private val tag = "BaseView"
    private var progress: Progress? = null


    fun <T> Observable<T>.applySchedulers(): Observable<T> {
        return subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread())
    }

    fun setupToolbar(title: Int) {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        when {
            toolbar != null -> {
                toolbar.setTitle(title)
                setSupportActionBar(toolbar)
            }
            else -> Log.d(tag, "Toolbar not found, check if you have it in your layout " +
                    "or if setContentView() was called.")
        }
    }

    fun setupActionBar() {
        val ab = supportActionBar
        ab?.setHomeButtonEnabled(true)
        ab?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun handleException(exception: Throwable) {
        exception.printStackTrace()
        Crashlytics.logException(exception)
        exception.message?.let {
            alert(it, getString(R.string.error_title))
            {
                yesButton {
                    when {
                        getActivity() !is MainViewImpl
                                && getActivity() !is AddToListViewImpl -> finish()
                    }
                }
            }.show()
        }
    }

    fun showWarning(message: Int) {
        showWarning(getString(message))
    }

    fun showWarning(message: String) {
        alert(message, getString(R.string.error_title)) { yesButton { } }.show()
    }

    fun showMessage(message: String) {
        alert(message, getString(R.string.title_success)) { yesButton { } }.show()
    }

    fun Activity.hideKeyboard() {
        hideKeyboard(if (currentFocus == null) View(this) else currentFocus)
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    protected fun showProgress() {
        try {
            if (progress == null) {
                progress = Progress(this)
            }
            progress!!.show()
        } catch (e: Exception) {
            Crashlytics.logException(e)
        }
    }

    protected fun closeProgress() {
        try {
            if (progress != null && progress!!.isShowing) {
                progress!!.dismiss()
            }
        } catch (e: Exception) {
            Crashlytics.logException(e)
        }
    }

    fun getActivity() = this

}