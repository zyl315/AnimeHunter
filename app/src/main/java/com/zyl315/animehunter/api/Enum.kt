package com.zyl315.animehunter.api

enum class SearchStatus {
    FAILED, SUCCESS, REFRESH_SUCCESS, REFRESH_FAILED, NO_MORE_DATA, LOAD_MORE_SUCCESS, LOAD_MORE_FAILED
}

enum class PlayStatus {
    GET_PLAY_SOURCE_FAILED,
    GET_PLAY_SOURCE_SUCCESS,
    GET_PLAY_URL_FAILED,
    GET_PLAY_URL_SUCCESS
}
