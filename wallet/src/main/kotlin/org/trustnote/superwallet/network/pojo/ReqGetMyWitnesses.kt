package org.trustnote.superwallet.network.pojo

import org.trustnote.superwallet.network.HubModel
import org.trustnote.superwallet.network.HubMsgFactory.CMD_GET_WITNESSES

class ReqGetMyWitnesses(): HubRequest(CMD_GET_WITNESSES, tag = HubModel.instance.getRandomTag()) {

    override fun handleResponse(): Boolean {
        return true
    }

}