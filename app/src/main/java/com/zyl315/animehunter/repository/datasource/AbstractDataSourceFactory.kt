package com.zyl315.animehunter.repository.datasource

abstract class AbstractDataSourceFactory<out T : AbstractDataSource> {

    abstract fun createDataSource(): T
}

