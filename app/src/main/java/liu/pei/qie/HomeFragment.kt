package liu.pei.qie

import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.location
import com.tencent.mmkv.MMKV
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class HomeFragment : BaseFragment(R.layout.fragment_home) {
    private lateinit var map: MapView
    private val positionChangedListener by lazy {
        map.getIndicatorListener()
    }

    private val onMoveListener = object : OnMoveListener {
        override fun onMove(detector: MoveGestureDetector): Boolean {
            return false
        }

        override fun onMoveBegin(detector: MoveGestureDetector) {

        }

        override fun onMoveEnd(detector: MoveGestureDetector) {

        }

    }
    override fun xInit() {
        EventBus.getDefault().register(this)
        map = layoutView.findViewById(R.id.map)
        map.getMapboxMap().setCamera(CameraOptions.Builder().zoom(14.0).build())
        when (MMKV.defaultMMKV().decodeInt("mapStyle", 0)) {
            0 -> {
                map.getMapboxMap().loadStyleUri(Style.OUTDOORS){
                    map.initLocationComponent(positionChangedListener)
                    map.addMoveListener(onMoveListener)
                }
            }
            1 -> {
                map.getMapboxMap().loadStyleUri(Style.SATELLITE_STREETS){
                    map.initLocationComponent(positionChangedListener)
                    map.addMoveListener(onMoveListener)
                }
            }
            2 -> {
                map.getMapboxMap().loadStyleUri(Style.TRAFFIC_DAY){
                    map.initLocationComponent(positionChangedListener)
                    map.addMoveListener(onMoveListener)
                }
            }
            3 -> {
                map.getMapboxMap().loadStyleUri(Style.LIGHT){
                    map.initLocationComponent(positionChangedListener)
                    map.addMoveListener(onMoveListener)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(e:Event){
        if(e.getMessage()[0] == "styleChange"){
            when (MMKV.defaultMMKV().decodeInt("mapStyle", 0)) {
                0 -> {
                    map.getMapboxMap().loadStyleUri(Style.OUTDOORS)
                }
                1 -> {
                    map.getMapboxMap().loadStyleUri(Style.SATELLITE_STREETS)
                }
                2 -> {
                    map.getMapboxMap().loadStyleUri(Style.TRAFFIC_DAY)
                }
                3 -> {
                    map.getMapboxMap().loadStyleUri(Style.LIGHT)
                }
            }
        }
    }

}