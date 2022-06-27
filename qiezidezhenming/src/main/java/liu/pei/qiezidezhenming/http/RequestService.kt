package liu.pei.qiezidezhenming.http

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*


interface RequestService {
    @FormUrlEncoded
    @POST(" ")
    fun getConfig(@Field("data") data: String): Call<ResponseBody>

    @GET(" ")
    fun getMainId(): Call<String>

    @GET(" ")
    fun getData(): Call<ResponseBody>
}