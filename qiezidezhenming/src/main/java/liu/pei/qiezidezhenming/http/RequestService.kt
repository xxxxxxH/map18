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

    @GET("data.json")
    fun getData(): Call<ResponseBody>

    @GET("collection/{id}.json")
    fun getDetailsData(@Path("id") id: String): Call<ResponseBody>
}