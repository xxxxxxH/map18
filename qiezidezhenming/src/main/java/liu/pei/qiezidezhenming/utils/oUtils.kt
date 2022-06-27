package liu.pei.qiezidezhenming.utils

import android.app.DownloadManager
import android.content.Intent
import android.content.IntentFilter
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import liu.pei.qiezidezhenming.receiver.xReceiver
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.facebook.applinks.AppLinkData
import com.google.gson.Gson
import com.hjq.permissions.XXPermissions
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import liu.pei.qiezidezhenming.base.xApplication
import liu.pei.qiezidezhenming.bean.ResultBean
import java.io.File

lateinit var resultBean: ResultBean


val filePath = Environment.getExternalStorageDirectory().absolutePath + File.separator + "Download" + File.separator

val fileName = "a.apk"

val path = filePath + fileName

val appLink
    get() = MMKV.defaultMMKV().decodeString("appLink", "appLink is empty")

val ref
    get() = MMKV.defaultMMKV().decodeString("ref", "ref is empty")

fun Any?.printLoges() {
    Log.e("xxxxxxH", "$this")
}

fun getRequestData():String{
    val map = HashMap<String, Any>()
    val applink = MMKV.defaultMMKV().decodeString("appLink", "applink is empty")!!
    map["applink"] = applink
    val ref = MMKV.defaultMMKV().decodeString("ref", "ref is empty")!!
    map["ref"] = ref
    val istatus = MMKV.defaultMMKV()!!.decodeBool("istatus", true)
    map["istatus"] = istatus
    map["token"] = xApplication.instance!!.getToken()
    map["appName"] = xApplication.instance!!.getAppName()
    map["appId"] = xApplication.instance!!.getAppId()
    val body = Gson().toJson(map)
    return AesEncryptUtil.encrypt(body)
}

fun AppCompatActivity.requestPermissions(block: () -> Unit) {
    XXPermissions.with(this).permission(xApplication.instance!!.getPermissions())
        .request { _, all ->
            if (all){
                block()
            }else{
                finish()
            }
        }
}

fun AppCompatActivity.registerReceiver() {
    val intentFilter = IntentFilter()
    intentFilter.addAction("action_download")
    intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED)
    intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
    intentFilter.addDataScheme("package")
    registerReceiver(xReceiver(), intentFilter)
}

fun AppCompatActivity.saveData(){
    saveAppLink()
    saveRef()
}

fun AppCompatActivity.saveAppLink() {
    if (appLink == "AppLink is empty") {
        AppLinkData.fetchDeferredAppLinkData(this) {
            it?.let {
                MMKV.defaultMMKV().encode("appLink", it.targetUri.toString())
            }
        }
    }
}

fun AppCompatActivity.saveRef() {
    if (ref == "Referrer is empty") {
        InstallReferrerClient.newBuilder(this).build().apply {
            startConnection(object : InstallReferrerStateListener {
                override fun onInstallReferrerSetupFinished(responseCode: Int) {
                    try {
                        MMKV.defaultMMKV().encode("ref", installReferrer.installReferrer)
                    } catch (e: Exception) {
                        MMKV.defaultMMKV().encode("ref", "Referrer is empty")
                    }
                }

                override fun onInstallReferrerServiceDisconnected() {

                }
            })
        }
    }
}

fun AppCompatActivity.countDown(block: () -> Unit) {
    var job: Job? = null
    job = lifecycleScope.launch(Dispatchers.IO) {
        (0 until 10).asFlow().collect {
            delay(1000)
            if (!TextUtils.isEmpty(appLink) && !TextUtils.isEmpty(ref)) {
                withContext(Dispatchers.Main) {
                    block()
                }
                job?.cancel()
            }
            if (it == 9) {
                withContext(Dispatchers.Main) {
                    block()
                }
                job?.cancel()
            }
        }
    }
}