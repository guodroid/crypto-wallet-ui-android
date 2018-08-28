package org.trustnote.superwallet.biz

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.view.MenuItem
import org.trustnote.superwallet.R
import org.trustnote.superwallet.TApp
import org.trustnote.superwallet.TApplicationComponent
import org.trustnote.superwallet.biz.home.FragmentMainCreateWallet
import org.trustnote.superwallet.biz.home.FragmentMainCreateWalletNormal
import org.trustnote.superwallet.biz.home.FragmentMainWallet
import org.trustnote.superwallet.biz.me.FragmentMeMain
import org.trustnote.superwallet.biz.me.SettingItem
import org.trustnote.superwallet.biz.me.SettingItemsGroup
import org.trustnote.superwallet.biz.me.createNewWallet
import org.trustnote.superwallet.biz.msgs.FragmentMsgMyPairId
import org.trustnote.superwallet.biz.msgs.FragmentMsgsChat
import org.trustnote.superwallet.biz.msgs.FragmentMsgsContactsList
import org.trustnote.superwallet.biz.msgs.MessageModel
import org.trustnote.superwallet.biz.wallet.WalletManager
import org.trustnote.superwallet.uiframework.ActivityBase
import org.trustnote.superwallet.uiframework.EmptyFragment
import org.trustnote.superwallet.util.AndroidBug5497Workaround
import org.trustnote.superwallet.util.AndroidUtils

class ActivityMain : ActivityBase() {

    override fun injectDependencies(graph: TApplicationComponent) {

    }

    lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        bottomNavigationView = findViewById(R.id.bottom_navigation)

        AndroidUtils.changeIconSizeForBottomNavigation(bottomNavigationView)

        disableShiftMode(bottomNavigationView)
        bottomNavigationView.setItemIconTintList(null)

        WalletManager.model.refreshExistWallet()

        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount == 0) {
            }
        }

        //AndroidBug5497Workaround.assistActivity(this)

    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    fun showPopupmenu(scanLogic: () -> Unit) {
        val f = FragmentPopupmenu()
        f.scanLogic = scanLogic
        addFragment(f, R.id.fragment_popmenu, isUseAnimation = false)
    }

    fun showPopupmenuForMsgChat(currentF: FragmentMsgsChat) {
        val f = FragmentPopupmenu()
        f.currentChatRef = currentF
        f.popLayoutId = R.layout.l_quick_action_chat
        addFragment(f, R.id.fragment_popmenu, isUseAnimation = false)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.action_create_wallet -> {
                createNewWallet(this)
                return true
            }
            R.id.action_my_pair_id -> {
                addL2Fragment(FragmentMsgMyPairId())
                return true
            }

        }
        return false
    }

    fun setToolbarTitle(s: String) {
        supportActionBar!!.title = s
    }

    override fun onDestroy() {
        super.onDestroy()
        TApp.userAlreadyInputPwd = false
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        selectPageByIntent(intent)
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            changeFragment(item.itemId)
            true
        }

        selectPageByIntent(intent)

        listener(MessageModel.instance.mMessagesEventCenter) {
            updateMsgIconInBottomNavigation()
        }

        showUpgradeInfoFromPrefs()

    }

    private fun selectPageByIntent(intent: Intent) {

        val menuId = intent.getIntExtra(MAINACTIVITY_KEY_MENU_ID, 0)
        //TODO: should default to first page.
        if (menuId != 0) {
            bottomNavigationView.selectedItemId = if (menuId == 0) R.id.menu_me else menuId
            intent.removeExtra(MAINACTIVITY_KEY_MENU_ID)
        }

        val isFromLanguageChange = intent.getBooleanExtra(AndroidUtils.KEY_FROM_CHANGE_LANGUAGE, false)
        if (isFromLanguageChange) {
            intent.removeExtra(AndroidUtils.KEY_FROM_CHANGE_LANGUAGE)
            SettingItem.openSubSetting(this, SettingItemsGroup.LANGUAGE, R.string.setting_system)
            SettingItem.selectLanguageUI(this)
        }

    }

    fun changeFragment(menuItemId: Int) {
        //TODO: can we do cache?
        var newFragment: Fragment = EmptyFragment()
        when (menuItemId) {
            R.id.menu_me -> newFragment = FragmentMeMain()
            R.id.menu_wallet -> newFragment = FragmentMainWallet()
            R.id.menu_msg -> newFragment = FragmentMsgsContactsList()
        }

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, newFragment)
        transaction.commit()

    }

    private fun updateMsgIconInBottomNavigation() {
        val menu = bottomNavigationView.menu
        val isUnread = MessageModel.instance.hasUnreadMessage()
        menu.findItem(R.id.menu_msg).setIcon(
                if (isUnread) R.drawable.ic_menu_message_unread
                else R.drawable.ic_menu_message)
    }
}

const val MAINACTIVITY_KEY_MENU_ID = "KEY_MENU_ID"
fun startMainActivityWithMenuId(menuId: Int = 0) {
    val intent = Intent(TApp.context, ActivityMain::class.java)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    intent.putExtra(MAINACTIVITY_KEY_MENU_ID, menuId)
    TApp.context.startActivity(intent)
}

fun startMainActivityAfterLanguageChanged() {
    val intent = Intent(TApp.context, ActivityMain::class.java)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    intent.putExtra(MAINACTIVITY_KEY_MENU_ID, R.id.menu_wallet)
    intent.putExtra(AndroidUtils.KEY_FROM_CHANGE_LANGUAGE, true)
    TApp.context.startActivity(intent)
}