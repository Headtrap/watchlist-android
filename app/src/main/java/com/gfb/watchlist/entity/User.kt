package com.gfb.watchlist.entity

/**
 * Created by Gustavo on 12/26/2017.
 */
class User(
        var id: String?,
        var email: String,
        var password: String,
        var watchList: List<Content>?,
        var archive: List<Content>?)