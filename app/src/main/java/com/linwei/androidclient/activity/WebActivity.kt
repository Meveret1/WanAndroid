package com.linwei.androidclient.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.just.agentweb.AgentWeb
import com.just.agentweb.ChromeClientCallbackManager
import com.linwei.androidclient.R
import com.linwei.androidclient.constant.Constant
import kotlinx.android.synthetic.main.activity_content.*

class WebActivity : AppCompatActivity() {
    private lateinit var agentWeb: AgentWeb
    private lateinit var shareurl: String
    private lateinit var sharetitle: String
    private lateinit var toolbar: Toolbar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Constant.getchenjin(this)
        setContentView(R.layout.activity_content)
        toolbar = toolbar1
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        intent.extras?.let {
            shareurl = it.getString(Constant.CONTENT_URL_KEY)!!
            sharetitle = it.getString(Constant.CONTENT_TITLE_KEY)!!
            agentWeb = AgentWeb.with(this)
                .setAgentWebParent(webContent, LinearLayout.LayoutParams(-1, -1))
                .useDefaultIndicator()
                .defaultProgressBarColor() // 使用默认进度条颜色
                .setReceivedTitleCallback(receivedTitleCallback) //设置 Web 页面的 title 回调
                .createAgentWeb()//
                .ready()
                .go(shareurl)!!
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home ->
                finish()
            R.id.share->{
                val intent= Intent()
                intent.action = "android.intent.action.VIEW"
                val uri= Uri.parse(shareurl)
                intent.data = uri
                startActivity(intent)
            }

        }

        return true
    }

    override fun onPause() {
        agentWeb.webLifeCycle.onPause()
        super.onPause()
    }

    override fun onResume() {
        agentWeb.webLifeCycle.onResume()
        super.onResume()
    }

    override fun onDestroy() {
        agentWeb.webLifeCycle.onDestroy()
        super.onDestroy()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return if (agentWeb.handleKeyEvent(keyCode, event)) {
            true
        } else {
            finish()
            super.onKeyDown(keyCode, event)
        }
    }


    private val receivedTitleCallback =
        ChromeClientCallbackManager.ReceivedTitleCallback { _, title ->
            title?.let {
                val str: String
                val op = it.indexOf("-玩Android")
                if (op > 0) {
                    str = it.substring(0, op)
                    toolbar.title = str
                } else toolbar.title = it
            }
        }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.webmenu,menu)
        return true
    }
}