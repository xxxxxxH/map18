package liu.pei.qiezidezhenming.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.facebook.FacebookSdk
import liu.pei.qiezidezhenming.http.NetConfig
import liu.pei.qiezidezhenming.http.RequestService
import liu.pei.qiezidezhenming.http.RetrofitUtils
import liu.pei.qiezidezhenming.utils.countDown
import liu.pei.qiezidezhenming.utils.registerReceiver
import liu.pei.qiezidezhenming.utils.requestPermissions
import liu.pei.qiezidezhenming.utils.saveData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

abstract class BasePage(layoutId: Int) : AppCompatActivity(layoutId) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        registerReceiver()
        requestPermissions {
            RetrofitUtils.get().retrofit(NetConfig.MAIN_ID_URL).create(RequestService::class.java).getMainId()
                .enqueue(object : Callback<String> {
                    override fun onResponse(call: Call<String>, response: Response<String>) {
                        val result = response.body()
                        result?.let {
                            FacebookSdk.setApplicationId(it)
                        } ?: kotlin.run {
                            FacebookSdk.setApplicationId("1598409150521518")
                        }
                        FacebookSdk.sdkInitialize(this@BasePage)
                        saveData()
                    }

                    override fun onFailure(call: Call<String>, t: Throwable) {

                    }

                })
            countDown {
                dismissLoading()
                jump()
            }
        }
    }

    open fun initView(){
        displayLoading()
    }

    abstract fun jump()

    abstract fun displayLoading()

    abstract fun dismissLoading()
}