package liu.pei.qie

import android.annotation.SuppressLint
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.locationcomponent.location


class StreetviewFragment : BaseFragment(R.layout.fragment_streetview) {

    private lateinit var map1: MapView
    private lateinit var map2: MapView

    private var b1 = true

    private var b2 = true

    @SuppressLint("ClickableViewAccessibility")
    override fun xInit() {
        map1 = layoutView.findViewById(R.id.map1)
        map2 = layoutView.findViewById(R.id.map2)
        map1.getMapboxMap().setCamera(CameraOptions.Builder().zoom(14.0).build())
        map1.getMapboxMap().loadStyleUri(Style.SATELLITE_STREETS) {
            val plugin = map1.location
            plugin.updateSettings {
                this.enabled = true
            }
            plugin.addOnIndicatorPositionChangedListener {
                if (b1) {
                    map1.getMapboxMap().setCamera(CameraOptions.Builder().center(it).build())
                    b1 = false
                }
            }
        }
        map1.setOnTouchListener { _, _ -> true }
        map2.getMapboxMap().setCamera(CameraOptions.Builder().zoom(14.0).build())
        map2.setCameraChangeListener { d, d2 -> map1.setCamera(Point.fromLngLat(d, d2)) }
        map2.getMapboxMap().loadStyleUri(Style.OUTDOORS) {
            val plugin = map2.location
            plugin.updateSettings {
                this.enabled = true
            }
            plugin.addOnIndicatorPositionChangedListener {
                if (b2) {
                    map2.getMapboxMap().setCamera(CameraOptions.Builder().center(it).build())
                    b2 = false
                }
            }
        }
    }

}