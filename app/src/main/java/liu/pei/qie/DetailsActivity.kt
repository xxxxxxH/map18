package liu.pei.qie

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mapbox.geojson.Point
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.dsl.cameraOptions
import com.mapbox.maps.plugin.animation.flyTo
import com.mapbox.maps.plugin.annotation.AnnotationConfig
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
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

class DetailsActivity : AppCompatActivity() {
    private var list: ArrayList<DataEntity> = ArrayList<DataEntity>()
    private lateinit var mapView: MapView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        mapView = findViewById<MapView>(R.id.map)
        mapView.let {
            it.getMapboxMap().apply {
                flyTo(cameraOptions {
                    center(Point.fromLngLat(search_lng, search_lat))
                    zoom(14.0)
                })
                loadStyleUri(Style.SATELLITE_STREETS)

            }
            it.annotations.apply {
                cleanup()
                createPointAnnotationManager(AnnotationConfig()).create(
                    PointAnnotationOptions()
                        .withPoint(Point.fromLngLat(search_lng, search_lat))
                        .withIconImage(BitmapFactory.decodeResource(resources, R.mipmap.red_marker))
                )
            }
        }
        RetrofitUtils.get().retrofit(NetConfig.DATA_URL).create(RequestService::class.java).getDetailsData(key)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    val result = response.body()
                    result?.let {
                        val (list: ArrayList<DataEntity>, keys: ArrayList<String>) = formatData(it.string())
                        this@DetailsActivity.list = list
                        findViewById<RecyclerView>(R.id.recycler).apply {
                            layoutManager = GridLayoutManager(this@DetailsActivity, 2)
                            val ada = ada()
                            adapter = ada
                            ada.setOnItemClickListener(object : MultiItemTypeAdapter.OnItemClickListener {
                                override fun onItemClick(p0: View?, p1: RecyclerView.ViewHolder?, p2: Int) {
                                    mapView.let { m ->
                                        m.getMapboxMap().apply {
                                            flyTo(cameraOptions {
                                                center(
                                                    Point.fromLngLat(
                                                        list[p2].lng.toDouble(),
                                                        list[p2].lat.toDouble()
                                                    )
                                                )
                                                zoom(14.0)
                                            })
                                            loadStyleUri(Style.SATELLITE_STREETS)

                                        }
                                        m.annotations.apply {
                                            cleanup()
                                            createPointAnnotationManager(AnnotationConfig()).create(
                                                PointAnnotationOptions()
                                                    .withPoint(
                                                        Point.fromLngLat(
                                                            list[p2].lng.toDouble(),
                                                            list[p2].lat.toDouble()
                                                        )
                                                    )
                                                    .withIconImage(
                                                        BitmapFactory.decodeResource(
                                                            resources,
                                                            R.mipmap.red_marker
                                                        )
                                                    )
                                            )
                                        }
                                    }
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
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(
                        this@DetailsActivity,
                        "no data",
                        Toast.LENGTH_SHORT
                    ).show()
                    this@DetailsActivity.finish()
                }

            })
    }

    inner class ada : CommonAdapter<DataEntity>(this, R.layout.item_inter, list) {
        override fun convert(holder: ViewHolder?, t: DataEntity?, position: Int) {
            Glide.with(this@DetailsActivity)
                .load("https://geo0.ggpht.com/cbk?output=thumbnail&thumb=2&panoid=${t?.panoid}")
                .placeholder(R.mipmap.ic_launcher_round)
                .into(holder!!.getView(R.id.image))
            holder.setText(R.id.text, t?.title)
        }
    }
}