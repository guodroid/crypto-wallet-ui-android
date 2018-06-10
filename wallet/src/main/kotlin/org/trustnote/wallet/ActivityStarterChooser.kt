package org.trustnote.wallet

import android.os.Bundle
import org.trustnote.wallet.biz.pwd.ActivityInputPwd
import org.trustnote.wallet.biz.startMainActivityWithMenuId
import org.trustnote.wallet.biz.init.ActivityInit
import org.trustnote.wallet.biz.init.CreateWalletModel
import org.trustnote.wallet.uiframework.ActivityBase
import org.trustnote.wallet.util.AndroidUtils
import org.trustnote.wallet.util.Prefs

class ActivityStarterChooser : ActivityBase() {

    override fun injectDependencies(graph: TApplicationComponent) {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!TApp.userAlreadyInputPwd
            && CreateWalletModel.readPwdHash().isNotEmpty()
            && Prefs.readEnablepwdForStartup()) {

            AndroidUtils.startActivity(ActivityInputPwd::class.java)
            finish()
            return
        }

        if (CreateWalletModel.isFinisheCreateOrRestore()) {
            startMainActivityWithMenuId(R.id.menu_wallet)
        } else {
            AndroidUtils.startActivity(ActivityInit::class.java)
        }

        finish()
    }
}

