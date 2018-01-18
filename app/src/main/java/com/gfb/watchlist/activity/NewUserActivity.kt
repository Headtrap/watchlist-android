package com.gfb.watchlist.activity

import android.os.Bundle
import com.gfb.watchlist.R
import com.gfb.watchlist.entity.User
import com.gfb.watchlist.entity.UserInfo
import com.gfb.watchlist.entity.dto.UserDTO
import com.gfb.watchlist.service.UserService
import kotlinx.android.synthetic.main.activity_new_user.*
import org.jetbrains.anko.startActivity

class NewUserActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_user)
        btEnter.setOnClickListener({ addUser() })

    }
    private fun addUser() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()
        val user = UserDTO(email, password, mutableListOf(), mutableListOf())

        when {
            !checkEmpty() -> {
                showProgress()
                UserService.addUser(user).applySchedulers()
                        .subscribe(
                                {
                                    saveUserLocally(it)
                                    closeProgress()
                                },
                                { error ->
                                    handleException(error)
                                    closeProgress()
                                }
                        )
            }
            else -> showWarning(R.string.warning_empty_fields)
        }
    }

    private fun saveUserLocally(user: User) {
        UserInfo.userId = user.id
        UserInfo.email = user.email
        startActivity<MainActivity>()
    }

    private fun checkEmpty(): Boolean {
        return etPassword.text.isNullOrEmpty() || etEmail.text.isNullOrEmpty()
    }
}