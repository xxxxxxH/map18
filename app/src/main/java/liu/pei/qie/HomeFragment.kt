package liu.pei.qie

import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.location

class HomeFragment : BaseFragment(R.layout.fragment_home) {
    private lateinit var map: MapView
    override fun xInit() {
        map = layoutView.findViewById(R.id.map)
        map.getMapboxMap().setCamera(CameraOptions.Builder().zoom(14.0).build())
        map.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS) {
            val plugin = map.location
            plugin.updateSettings {
                this.enabled = true
            }
            plugin.addOnIndicatorPositionChangedListener {
                map.getMapboxMap().setCamera(CameraOptions.Builder().center(it).build())
                map.gestures.focalPoint = map.getMapboxMap().pixelForCoordinate(it)
            }
            map.gestures.addOnMoveListener(object : OnMoveListener {
                override fun onMove(detector: MoveGestureDetector): Boolean {
                    return false
                }

                override fun onMoveBegin(detector: MoveGestureDetector) {

                }

                override fun onMoveEnd(detector: MoveGestureDetector) {

                }

            })
        }
    }

}