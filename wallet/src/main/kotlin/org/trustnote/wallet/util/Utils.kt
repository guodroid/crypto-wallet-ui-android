package org.trustnote.wallet.util


import android.net.NetworkInfo
import android.widget.Toast
import com.github.pwittchen.reactivenetwork.library.rx2.Connectivity
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

import org.trustnote.wallet.TApp
import org.trustnote.wallet.network.hubapi.HubClient

import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit

object Utils {

    val emptyJsonObject = JsonObject()

    fun debugLog(s: String) {
        android.util.Log.e("GUO", s)
    }

    fun debugJS(s: String) {
        android.util.Log.e("JSAPI", s)
    }

    fun crash(s: String) {
        //TODO:
        android.util.Log.e("CRASH", s)
        throw RuntimeException(s)
    }

    fun debugToast(s: String) {
        Toast.makeText(TApp.context, s, Toast.LENGTH_SHORT).show()
    }

    fun toastMsg(s: String) {
        Toast.makeText(TApp.context, s, Toast.LENGTH_SHORT).show()
    }

    fun toastMsg(stringResId: Int) {
        Toast.makeText(TApp.context, stringResId, Toast.LENGTH_SHORT).show()
    }

    fun d(clz: Class<*>, msg: String) {
        Timber.d(clz.simpleName + msg)
    }

    //TODO: Bug?? Thread Manager
    fun computeThread(action: () -> Any) {
        Thread {
            action
        }.start()
    }

    //TODO:
    fun runInbackground(runnable: Runnable) {
        Thread { runnable.run() }.start()
    }

    fun generateRandomString(length:Int): String {
        //TODO: USE crypto alg.
        return "RANDOM:" + Random().nextInt()
    }


    fun getGson(): Gson {
        return GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create()
    }

    fun debugHub(s:String) {
        d(HubClient::class.java, s)
        //android.util.Log.e(HubClient::class.java.simpleName, s)
    }

    fun connectedEvent(): Observable<Connectivity> {
        return ReactiveNetwork.observeNetworkConnectivity(TApp.context)
                .filter {
                    it.state == NetworkInfo.State.CONNECTED
                }.take(1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun <T> throttleDbEvent(orig: Observable<T>, intervalSecs: Long): Observable<T> {
        return orig.throttleFirst(intervalSecs, TimeUnit.SECONDS)
    }

}
