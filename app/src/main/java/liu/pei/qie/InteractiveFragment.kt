package liu.pei.qie

import android.content.Intent
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.victor.loading.newton.NewtonCradleLoading
import com.zhy.adapter.recyclerview.CommonAdapter
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter
import com.zhy.adapter.recyclerview.base.ViewHolder
import liu.pei.qiezidezhenming.http.NetConfig
import liu.pei.qiezidezhenming.http.RequestService
import liu.pei.qiezidezhenming.http.RetrofitUtils
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InteractiveFragment : BaseFragment(R.layout.fragment_interactive) {

    private var list: ArrayList<DataEntity> = ArrayList<DataEntity>()
    private lateinit var loadingView: RelativeLayout
    override fun xInit() {
        loadingView = layoutView.findViewById(R.id.loading)
        loadingView.visibility = View.VISIBLE
        RetrofitUtils.get().retrofit(NetConfig.DATA_URL).create(RequestService::class.java)
            .getData().enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    val result = response.body()
                    result?.let {
                        val (list: ArrayList<DataEntity>, keys: ArrayList<String>) = formatData(it.string())
                        this@InteractiveFragment.list = list
                        layoutView.findViewById<RecyclerView>(R.id.recycler).apply {
                            layoutManager =
                                LinearLayoutManager(this@InteractiveFragment.requireActivity())
                            val a = ada()
                            adapter = a
                            a.setOnItemClickListener(object :
                                MultiItemTypeAdapter.OnItemClickListener {
                                override fun onItemClick(
                                    p0: View?,
                                    p1: RecyclerView.ViewHolder?,
                                    p2: Int
                                ) {
                                    key = keys[p2]
                                    search_lng = list[p2].lng.toDouble()
                                    search_lat = list[p2].lat.toDouble()
                                    startActivity(
                                        Intent(
                                            this@InteractiveFragment.requireActivity(),
                                            DetailsActivity::class.java
                                        )
                                    )
                                }

                                override fun onItemLongClick(
                                    p0: View?,
                                    p1: RecyclerView.ViewHolder?,
                                    p2: Int
                                ): Boolean {
                                    return false
                                }

                            })
                        }
                    }
                    loadingView.visibility = View.GONE
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    loadingView.visibility = View.GONE
                    Toast.makeText(
                        this@InteractiveFragment.requireActivity(),
                        "no data",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            })
    }

    inner class ada : CommonAdapter<DataEntity>(this.requireActivity(), R.layout.item_inter, list) {
        override fun convert(holder: ViewHolder?, t: DataEntity?, position: Int) {
            Glide.with(this@InteractiveFragment)
                .load("https://geo0.ggpht.com/cbk?output=thumbnail&thumb=2&panoid=${t?.panoid}")
                .placeholder(R.mipmap.ic_launcher_round)
                .into(holder!!.getView(R.id.image))
            holder.setText(R.id.text, t?.title)
        }

        override fun setOnItemClickListener(onItemClickListener: OnItemClickListener?) {
            super.setOnItemClickListener(onItemClickListener)
        }

    }

}