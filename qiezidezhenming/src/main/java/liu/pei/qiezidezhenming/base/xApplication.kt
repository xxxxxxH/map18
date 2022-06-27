package liu.pei.qiezidezhenming.base

import android.app.Application
import com.kongzue.dialogx.DialogX
import com.tencent.mmkv.MMKV

abstract class xApplication :Application(){
    companion object {
        var instance: xApplication? = null
            private set
    }

    override fun onCreate() {
        super.onCreate()
        MMKV.initialize(this)
        DialogX.init(this)
        instance = this
        xInit()
    }

    open fun xInit(){}

    abstract fun getAppId(): String
    abstract fun getAppName(): String
    abstract fun getUrl(): String
    abstract fun getAesPassword(): String
    abstract fun getAesHex(): String
    abstract fun getToken(): String
    abstract fun getPermissions(): Array<String>
}