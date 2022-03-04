package com.zyl315.animehunter.repository.datasource

import com.zyl315.animehunter.repository.interfaces.IHomeParser
import com.zyl315.animehunter.repository.interfaces.IPlayParser
import com.zyl315.animehunter.repository.interfaces.ISearchParser

abstract class AbstractDataSource : ISearchParser, IPlayParser,IHomeParser {
    abstract fun getHost(): String

    abstract fun getDefaultCatalogUrl(): String

    abstract suspend fun checkHost()
}