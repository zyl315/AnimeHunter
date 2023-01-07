package com.zyl315.animehunter.repository.datasource

import android.content.Context
import androidx.core.content.edit
import com.zyl315.animehunter.App
import com.zyl315.animehunter.repository.impls.agefans.AgeFansDataSourceFactory
import com.zyl315.animehunter.repository.impls.kudm.KudmDataSourceFactory
import com.zyl315.animehunter.repository.impls.ysjdm.YsjdmDataSourceFactory

object DataSourceManager {
    const val DATA_SOURCE = "data_source"
    const val CATALOG_DATA_SOURCE = "catalog_data_source"

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

    fun getCurrentCatalogDataSource(): AbstractDataSource {
        val catalogDataSource = App.context
            .getSharedPreferences(DATA_SOURCE, Context.MODE_PRIVATE)
            .getString(CATALOG_DATA_SOURCE, "")
        val source = getAllCatalogDataSource()[catalogDataSource]
        return getDataSource(source)
    }

    fun setCurrentCatalogDataSource(dataSource: DataSource) {
        App.context
            .getSharedPreferences(DATA_SOURCE, Context.MODE_PRIVATE)
            .edit()
            .putString(CATALOG_DATA_SOURCE, dataSource.toString())
            .apply()
    }

    fun getAllDataSource(): Map<String, DataSource> {
        return mapOf("AGEFANS" to DataSource.AGEFANS, "KUDM" to DataSource.KUDM, "YSJDM" to DataSource.YSJDM)
    }

    fun getAllCatalogDataSource(): Map<String, DataSource> {
        return mapOf("AGEFANS" to DataSource.AGEFANS, "YSJDM" to DataSource.YSJDM)
    }
}