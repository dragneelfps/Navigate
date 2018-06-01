package com.example.sourabh.navigate.data.network.base

import android.content.Context

interface Request {
    fun toQueryUrl(context: Context): String
}