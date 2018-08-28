package org.trustnote.superwallet.network.pojo


import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.trustnote.superwallet.network.HubModel
import org.trustnote.superwallet.util.Utils

open class HubMsg {

    var msgSource = MSG_SOURCE.hub
    var msgType: MSG_TYPE = MSG_TYPE.empty
    var msgString: String = ""
    var msgJson: JsonObject = Utils.emptyJsonObject
    var textFromHub: String = ""
    var lastSentTime = 0L
    var shouldRetry = false

    var actualHubAddress: String = ""

    var targetHubAddress: String = HubModel.instance.mDefaultHubAddress
    var failHubAddress: MutableList<String> = mutableListOf()

    constructor(msgType: MSG_TYPE = MSG_TYPE.empty) {
        this.msgType = msgType
    }

    constructor(textFromHub: String) {
        this.textFromHub = textFromHub

        val index = textFromHub.indexOf(',')

        if (index < 3) {
            msgType = MSG_TYPE.ERROR
            return
        } else {
            msgType = MSG_TYPE.valueOf(textFromHub.substring(2, index - 1))
            msgString = textFromHub.substring(index + 1, textFromHub.length - 1)
            msgJson = JsonParser().parse(msgString).asJsonObject
        }
    }

    fun toHubString(): String {
        return """["${msgType}",${msgJson}]"""
    }

    fun networkErr() {
        (this as? HubRequest)?.setResponse(HubResponse(MSG_TYPE.networkerr))
    }

    fun timeout() {
        (this as? HubRequest)?.setResponse(HubResponse(MSG_TYPE.timeout))
    }

    fun shouldSendWithThisHub(hubAddress: String): Boolean {
        return hubAddress == targetHubAddress
    }

}


enum class MSG_TYPE {
    empty,
    timeout,
    networkerr,
    request,
    response,
    justsaying,
    CONNECTED,
    CLOSED,
    unknown,
    ERROR
}


enum class MSG_SOURCE {
    wallet,
    hub
}
