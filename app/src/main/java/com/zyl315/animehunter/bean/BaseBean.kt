package com.zyl315.animehunter.bean

import java.io.Serializable

interface BaseBean : Serializable {
    var type: String
    var actionUrl: String
}