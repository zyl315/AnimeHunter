package com.zyl315.animehunter.api

enum class SearchStatus {
    Normal, Failed, Success, Refresh
}

enum class PlayStatus {
    GET_PLAY_SOURCE_FAILED,
    GET_PLAY_SOURCE_SUCCESS,
    GET_PLAY_URL_FAILED,
    GET_PLAY_URL_SUCCESS
}
