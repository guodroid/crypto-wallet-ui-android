package org.trustnote.superwallet.biz.wallet

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import org.trustnote.superwallet.R
import org.trustnote.superwallet.TApp
import org.trustnote.superwallet.TApplicationComponent
import org.trustnote.superwallet.uiframework.ActivityBase

class SimpleFragmentActivityBase : ActivityBase() {

    override fun injectDependencies(graph: TApplicationComponent) {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_simple_fragment)

        val fragmentClzName = intent.getStringExtra(KEY_FRAGMENT_CLZ_NAME)

        val fragment = Class.forName(fragmentClzName).newInstance() as Fragment
        supportFragmentManager.beginTransaction().replace(
                R.id.fragment_container, fragment)
                .commit()
    }

    override fun onPause() {
        super.onPause()
    }

    companion object {
        const val KEY_FRAGMENT_CLZ_NAME = "KEY_FRAGMENT_CLZ_NAME"

        //TODO: It is better to use class instead class name.
        @JvmStatic fun startMe(clazzName: String) {
            val intent = Intent(TApp.context, SimpleFragmentActivityBase::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra(KEY_FRAGMENT_CLZ_NAME, clazzName)
            TApp.context.startActivity(intent)
        }
    }
}

