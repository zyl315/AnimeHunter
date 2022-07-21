package com.zyl315.animehunter.repository.datasource

import com.zyl315.animehunter.repository.impls.agefans.AgeFansDataSourceFactory
import com.zyl315.animehunter.repository.impls.kudm.KudmDataSourceFactory
import com.zyl315.animehunter.repository.impls.ysjdm.YsjdmDataSourceFactory

object DataSourceManager {
    enum class DataSource {
        AGEFANS, KUDM, YSJDM, UNKNOWN
    }

    var dataSource: AbstractDataSource = AgeFansDataSourceFactory().createDataSource()

    fun getDataSource(dataSourceName: DataSource? = null): AbstractDataSource {
        return when (dataSourceName) {
            DataSource.AGEFANS -> AgeFansDataSourceFactory().createDataSource()
            DataSource.KUDM -> KudmDataSourceFactory().createDataSource()
            DataSource.YSJDM -> YsjdmDataSourceFactory().createDataSource()
            else -> dataSource
        }
    }

    fun getAllDataSource(): Map<String, DataSource> {
        return mapOf("AGEFANS" to DataSource.AGEFANS, "KUDM" to DataSource.KUDM, "YSJDM" to DataSource.YSJDM)
    }
}