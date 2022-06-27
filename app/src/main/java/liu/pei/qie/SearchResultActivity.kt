package liu.pei.qie

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mapbox.geojson.Point
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.dsl.cameraOptions
import com.mapbox.maps.plugin.animation.flyTo
import com.mapbox.maps.plugin.annotation.AnnotationConfig
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager

class SearchResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_result)
        findViewById<MapView>(R.id.map).apply {
            getMapboxMap().apply {
                flyTo(cameraOptions {
                    center(Point.fromLngLat(search_lng, search_lat))
                    zoom(14.0)
                })
                loadStyleUri(Style.OUTDOORS)
            }
            annotations.apply {
                cleanup()
                createPointAnnotationManager(AnnotationConfig()).apply {
                    create(
                        PointAnnotationOptions()
                            .withPoint(Point.fromLngLat(search_lng, search_lat))
                            .withIconImage(
                                BitmapFactory.decodeResource(
                                    this@SearchResultActivity.resources,
                                    R.mipmap.red_marker
                                )
                            )
                    )
                }
            }

        }
    }
}