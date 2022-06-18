package com.zyl315.animehunter.repository.datasource

import com.zyl315.animehunter.repository.impls.agefans.AgeFansDataSourceFactory
import com.zyl315.animehunter.repository.impls.kudm.KudmDataSourceFactory
import com.zyl315.animehunter.repository.impls.ysjdm.YsjdmDataSourceFactory

object DataSourceManager {
    enum class DataSource {
        AGEFANS, KUDM, YSJDM, UNKNOWN
    }

    lateinit var dataSourceFactory: AbstractDataSourceFactory<AbstractDataSource>

    fun getDataSource(dataSource: DataSource? = null): AbstractDataSource {
        return when (dataSource) {
            DataSource.AGEFANS -> AgeFansDataSourceFactory().createDataSource()
            DataSource.KUDM -> KudmDataSourceFactory().createDataSource()
            DataSource.YSJDM -> YsjdmDataSourceFactory().createDataSource()
            else -> dataSourceFactory.createDataSource()
        }
    }

    fun getAllDataSource(): Map<String, DataSource> {
        return mapOf("AGE" to DataSource.AGEFANS, "KUDM" to DataSource.KUDM, "异世界动漫" to DataSource.YSJDM)
    }
}