package com.zyl315.animehunter.repository.impls.agefans

import com.zyl315.animehunter.repository.datasource.AbstractDataSourceFactory

class AgeFansDataSourceFactory : AbstractDataSourceFactory<AgeFansDataSource>() {

    override fun createDataSource(): AgeFansDataSource = AgeFansDataSource()
}