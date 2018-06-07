package org.trustnote.wallet.biz.me

import android.view.View
import android.webkit.WebView
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import org.trustnote.wallet.R
import org.trustnote.wallet.biz.init.CreateWalletModel
import org.trustnote.wallet.biz.init.FragmentInitSetupPwd
import org.trustnote.wallet.biz.wallet.WalletManager
import org.trustnote.wallet.util.AndroidUtils
import org.trustnote.wallet.widget.MyDialogFragment
import org.trustnote.wallet.widget.MyTextWatcher
import org.trustnote.wallet.widget.PasswordStrength
import org.trustnote.wallet.widget.PwdStrength

class FragmentMeChangePwd : FragmentInitSetupPwd() {


    lateinit var oldPwd: EditText
    override fun initFragment(view: View) {

        fromInitActivity = false
        super.initFragment(view)

        oldPwd = findViewById(R.id.pwd_old)
        oldPwd.visibility = View.VISIBLE

    }

    override fun savePwdAndForward() {

        if (!CreateWalletModel.verifyPwd(oldPwd.text.toString())) {
            MyDialogFragment.showMsg(activity, R.string.me_change_pwd_old_pwd_err)
        } else {
            val pwdString = pwd.text.toString()
            WalletManager.model.updatePassword(oldPwd.text.toString(), pwd.text.toString())
            CreateWalletModel.savePassphrase(pwdString)
            onBackPressed()
        }
    }

}