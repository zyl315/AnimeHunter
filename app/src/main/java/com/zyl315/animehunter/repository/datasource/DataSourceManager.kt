package com.zyl315.animehunter.repository.datasource

object DataSourceManager {
    lateinit var dataSourceFactory: DataSourceFactory<AbstractDataSource>

    fun getDataSource(): AbstractDataSource {
        return dataSourceFactory.createDataSource()
    }
}