package org.trustnote.superwallet.biz.init

import org.trustnote.superwallet.R
import org.trustnote.superwallet.biz.wallet.CREATE_WALLET_STATUS
import org.trustnote.superwallet.biz.wallet.WalletManager
import org.trustnote.superwallet.util.AndroidUtils
import org.trustnote.superwallet.util.Prefs

object CreateWalletModel {

    //TODO: should keep this in ram at most N minutes.
    private var passphraseInRam: String = ""
    var tmpMnemonic: String = ""

    fun getCreationProgress(): CREATE_WALLET_STATUS {
        if (Prefs.profileExist()) {
            return CREATE_WALLET_STATUS.FINISHED
        }

        if (Prefs.pwdExist()) {
            return CREATE_WALLET_STATUS.PASSWORD_READY
        }

        return CREATE_WALLET_STATUS.SELECT_CREATE_OR_RESTORE
    }

    fun savePassphraseInRam(passphrase: String) {
        passphraseInRam = passphrase
    }

    fun getPassphraseInRam(): String {
        return passphraseInRam
    }

    fun clearPassphraseInRam() {
        passphraseInRam = ""
    }

    fun savePassphraseInDisk(passphrase: String) {
        Prefs.writePwdHash(hash(passphrase))
    }

    fun readPwdHash(): String {
        return Prefs.readPwdHash()
    }

    fun hash(passphrase: String): String {
        return AndroidUtils.md5(passphrase)
    }

    fun userAgree() {
        Prefs.saveUserAgree()
    }

    fun isUserAgree(): Boolean {
        return Prefs.isUserAgree()
    }

    //TODO: R is not model logic.
    fun getStartPageLayoutId(): Int {
        if (!Prefs.isUserAgree()) {
            return R.layout.f_init_disclaimer
        }

        if (Prefs.readDeviceName().isEmpty()) {
            return R.layout.f_init_devicename
        }

        return R.layout.f_init_create_or_restore

    }

    fun saveDeviceName(deviceName: String) {
        Prefs.writeDeviceName(deviceName)
    }

    fun readDeviceName(): String {
        val deviceName = Prefs.readDeviceName()
        return if (deviceName.isBlank()) android.os.Build.MODEL else deviceName
    }

    fun finishedCreateOrRestore() {
        Prefs.writeFinisheCreateOrRestore()
    }

    fun isFinisheCreateOrRestore():Boolean {
        return Prefs.profileExist()
    }

    fun iamDone(mnemonic: String, isRemove: Boolean) {
        finishedCreateOrRestore()
        WalletManager.initWithMnemonic(passphraseInRam, mnemonic, isRemove)
    }

    fun iamDone() {
        finishedCreateOrRestore()
        WalletManager.initWithMnemonic(passphraseInRam, tmpMnemonic, false)
    }

    fun verifyPwd(pwd: String): Boolean {
        return Prefs.readPwdHash() == hash(pwd)
    }
}

