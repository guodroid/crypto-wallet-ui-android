package org.trustnote.superwallet.biz.js

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.webkit.ConsoleMessage
import android.webkit.CookieManager
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import org.trustnote.superwallet.util.AndroidUtils
import org.trustnote.superwallet.util.Utils
import java.util.concurrent.CountDownLatch

class TWebView : WebView {

    constructor(context: Context) : super(context) {
        initInternal()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initInternal()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initInternal()
    }


    val mHandler = Handler()
    private fun initInternal() {
        sInstance = this
        setupWebView()
        loadJS()
    }

    private fun loadJS() {
        val jsLib = AndroidUtils.readAssetFile("core.js")
        callJS(jsLib, ValueCallback {
            //Do nothing.
        })
    }

    inner class TWebChromeClient : WebChromeClient() {
        override fun onConsoleMessage(cm: ConsoleMessage): Boolean {
            Utils.debugJS(cm.message() + " -- From line "
                    + cm.lineNumber() + " of "
                    + cm.sourceId())
            return true
        }
    }

    @SuppressLint("SetJavaScriptEnabled", "AddJavascriptInterface")
    private fun setupWebView() {
        settings.javaScriptEnabled = true
        CookieManager.getInstance().setAcceptCookie(true)
        webChromeClient = TWebChromeClient()
    }

    @Synchronized fun callJSSync(jsCode: String): String {
        if (Thread.currentThread() == Looper.getMainLooper().thread) {
            Utils.crash("JS sync all should called from non UI thread")
            return ""
        }

        val latch = CountDownLatch(1)
        val jsResult = JSResult()
        Utils.debugJS("From outer non UI thread:Before")

        val jsAction = Runnable {

            Utils.debugJS("Log from mainUI::will call jscode = $jsCode")
            evaluateJavascript(jsCode) { valueFromJS ->
                jsResult.result = valueFromJS
                latch.countDown()
            }
        }

        mHandler.post(jsAction)

        latch.await()

        Utils.debugJS("From outer non UI thread::after" + jsResult.result)

        return Utils.decodeJsStr(jsResult.result)
    }

    fun callJS(jsCode: String, cb: ValueCallback<String>) {

        if (Thread.currentThread() != Looper.getMainLooper().thread) {
            Utils.debugJS("JS should called from UI thread")
            Utils.crash("JS should called from UI thread")
            return
        }

        evaluateJavascript(jsCode) { valueFromJS ->
            cb.onReceiveValue(Utils.decodeJsStr(valueFromJS))
        }
    }

    companion object {

        lateinit var sInstance: TWebView

        fun init(context: Context) {
            sInstance = TWebView(context)
        }
    }

}




