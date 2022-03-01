package com.zyl315.animehunter.ui.fragment

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import android.webkit.JavascriptInterface

import android.webkit.WebViewClient

import android.os.Bundle
import android.webkit.WebView


class RunJsFragment : Fragment() {
    lateinit var webView: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        webView = WebView(requireContext())
        webView.settings.javaScriptEnabled = true
        webView.addJavascriptInterface(MyJavaScriptInterface(), "HTMLOUT")
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                webView.loadUrl("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');")
            }
        }
    }

    internal class MyJavaScriptInterface {
        @JavascriptInterface
        fun processHTML(html: String?) {
            // 注意啦，此处就是执行了js以后 的网页源码
        }
    }
}