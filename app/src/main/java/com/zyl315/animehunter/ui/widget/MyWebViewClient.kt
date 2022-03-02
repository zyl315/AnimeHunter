package com.zyl315.animehunter.ui.widget

import android.webkit.*

class MyWebViewClient(private var webView: WebView) : WebViewClient() {
    private val jsObject = JsObject()

    var onComplete: ((String) -> Unit)? = null
    var onError: (() -> Unit)? = null

    init {
        webView.addJavascriptInterface(this, JAVA_OBJ)
    }

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
        webView.loadUrl("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');")
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


    fun removeJavascriptInterface() {
        webView.removeJavascriptInterface(JAVA_OBJ)
    }

    @JavascriptInterface
    fun processHtml(html: String) {
        onComplete?.let {
            it(html)
        }
    }


    companion object {
        const val JAVA_OBJ = "HTMLOUT"
    }

    inner class JsObject {
        @JavascriptInterface
        fun processHtml(html: String) {
            onComplete?.let {
                it(html)
            }
        }
    }
}