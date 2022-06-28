package liu.pei.qie

import android.content.Intent
import com.victor.loading.newton.NewtonCradleLoading
import liu.pei.qiezidezhenming.base.BasePage

class FirstPage : BasePage(R.layout.activity_main) {
    private lateinit var loading: NewtonCradleLoading

    override fun initView() {
        loading = findViewById(R.id.loading)
        super.initView()
    }

    override fun jump() {
        startActivity(Intent(this, Homepage::class.java))
        finish()
    }

    override fun displayLoading() {
        loading.start()
    }

    override fun dismissLoading() {
        loading.stop()
    }
}