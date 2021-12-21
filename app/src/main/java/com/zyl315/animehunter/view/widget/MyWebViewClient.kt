package com.zyl315.animehunter.view.widget

import android.webkit.*

class MyWebViewClient : WebViewClient() {
    inner class JsObject {
        @JavascriptInterface
        fun processHtml(html: String) {
            onComplete?.let {
                it(html)
            }
        }
    }

    private val jsObject = JsObject()

    var onComplete: ((String) -> Unit)? = null
    var onError: (() -> Unit)? = null

    override fun shouldInterceptRequest(
        view: WebView?,
        request: WebResourceRequest?
    ): WebResourceResponse? {
        if (request?.url.toString().contains("\\.((css|png|jp.?g))+".toRegex())) {
            return null
        }
        return super.shouldInterceptRequest(view, request)
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        view?.loadUrl("javascript:window.JAVA_OBJ.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');")
    }

    override fun onReceivedError(
        view: WebView?,
        request: WebResourceRequest?,
        error: WebResourceError?
    ) {
        super.onReceivedError(view, request, error)
        onError?.let {
            it()
        }
        onComplete = null
        onError = null
    }

    fun addJavascriptInterface(webView: WebView?) {
        webView?.addJavascriptInterface(jsObject, JAVA_OBJ)
    }

    fun removeJavascriptInterface(webView: WebView?) {
        webView?.removeJavascriptInterface(JAVA_OBJ)
    }


    companion object {
        const val JAVA_OBJ = "JAVA_OBJ"
    }
}