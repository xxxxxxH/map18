package liu.pei.qie

import android.content.Intent
import liu.pei.qiezidezhenming.base.BasePage

class FirstPage : BasePage(R.layout.activity_main) {

    override fun jump() {
        startActivity(Intent(this, Homepage::class.java))
        finish()
    }

    override fun displayLoading() {

    }

    override fun dismissLoading() {

    }
}