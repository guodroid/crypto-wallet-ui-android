package org.trustnote.superwallet.biz.me

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import org.trustnote.superwallet.R
import org.trustnote.superwallet.biz.TTT
import org.trustnote.superwallet.biz.ActivityMain
import org.trustnote.superwallet.biz.home.CredentialAdapter
import org.trustnote.superwallet.biz.home.FragmentMainCreateWallet
import org.trustnote.superwallet.biz.home.FragmentMainCreateWalletNormal
import org.trustnote.superwallet.biz.wallet.FragmentWalletBase
import org.trustnote.superwallet.biz.wallet.WalletManager
import org.trustnote.superwallet.biz.wallet.WalletModel
import org.trustnote.superwallet.util.AndroidUtils
import org.trustnote.superwallet.widget.MyDialogFragment

class FragmentMeWalletManager : FragmentWalletBase() {

    override fun getLayoutId(): Int {
        return R.layout.f_me_wallet_manager
    }

    lateinit var mRecyclerView: RecyclerView

    override fun initFragment(view: View) {
        super.initFragment(view)
        mRecyclerView = view.findViewById(R.id.wallet_list)
        mRecyclerView.layoutManager = LinearLayoutManager(activity)

        findViewById<View>(R.id.me_wallet_add).setOnClickListener {
            createNewWallet(activityMain = activity as ActivityMain)
        }

    }

    override fun setupToolbar() {
        super.setupToolbar()
        mToolbar.setBackgroundResource(R.color.tx_list_bg)
    }

    override fun updateUI() {
        super.updateUI()

        val myAllWallets = WalletManager.model.getAvaiableWalletsForUser()

        val adapter = CredentialAdapter(myAllWallets, R.layout.item_wallet_manager)

        mRecyclerView.adapter = adapter

        AndroidUtils.addItemClickListenerForRecycleView(mRecyclerView) {

            val bundle = Bundle()
            val insideAdapter = mRecyclerView.adapter as CredentialAdapter
            bundle.putString(TTT.KEY_WALLET_ID, insideAdapter.myDataset[it].walletId)
            val f = FragmentMeWalletDetail()
            f.arguments = bundle
            addL2Fragment(f)

        }

    }
}

fun createNewWallet(activityMain: ActivityMain) {

    activityMain.addL2Fragment(FragmentMainCreateWalletNormal())

}