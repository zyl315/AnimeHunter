package com.zyl315.animehunter.repository.datasource

abstract class DataSourceFactory<out T : AbstractDataSource> {

    abstract fun createDataSource(): T
}

