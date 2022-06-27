package liu.pei.qie

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import androidx.fragment.app.Fragment
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.TypeReference
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.dsl.cameraOptions
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.search.MapboxSearchSdk

var search_lat = 0.0

var search_lng = 0.0

@SuppressLint("StaticFieldLeak")
lateinit var context: Context

val searchEngine by lazy {
    MapboxSearchSdk.getSearchEngine()
}

fun MapView.currentLocation() {
    val listener = object : OnIndicatorPositionChangedListener {
        override fun onIndicatorPositionChanged(point: Point) {
            setTag(R.id.appViewMyLocationId, point)
            getMapboxMap().setCamera(CameraOptions.Builder().center(point).build())
            gestures.focalPoint = getMapboxMap().pixelForCoordinate(point)
            location.removeOnIndicatorPositionChangedListener(this)
        }
    }
    location.addOnIndicatorPositionChangedListener(listener)
}

fun MapView.setCameraChangeListener(block: (Double, Double) -> Unit) {
    getMapboxMap().addOnCameraChangeListener {
        getMapboxMap().cameraState.center.let {
            block(it.longitude(), it.latitude())
        }
    }
}

fun MapView.setCamera(center: Point) {
    getMapboxMap().setCamera(
        cameraOptions {
            center(center)
            zoom(14.0)
        }
    )
}

fun fragmentCollections(): ArrayList<Fragment> {
    val list = ArrayList<Fragment>()
    list.add(HomeFragment())
    list.add(NearbyFragment())
    list.add(StreetviewFragment())
    list.add(InteractiveFragment())
    list.add(SettingsFragment())
    return list
}

fun nearbyDataCollections(): HashMap<String, Int> {
    val map = HashMap<String, Int>()
    map["airport"] = R.mipmap.airport
    map["atm"] = R.mipmap.atm
    map["bakery"] = R.mipmap.bakery
    map["bank"] = R.mipmap.bank
    map["bus"] = R.mipmap.bus
    map["cafe"] = R.mipmap.cafe
    map["church"] = R.mipmap.church
    map["cloth"] = R.mipmap.cloth
    map["dentist"] = R.mipmap.dentist
    map["doctor"] = R.mipmap.doctor
    map["fire station"] = R.mipmap.fire_station
    map["gas station"] = R.mipmap.gas_station
    map["hotel"] = R.mipmap.hotel
    map["jewelry"] = R.mipmap.jewelry
    map["mall"] = R.mipmap.mall
    map["map terrain"] = R.mipmap.map_terrain
    map["mosque"] = R.mipmap.mosque
    map["park"] = R.mipmap.park
    map["pet"] = R.mipmap.pet
    map["pharmacy"] = R.mipmap.pharmacy
    map["police"] = R.mipmap.police
    map["post office"] = R.mipmap.post_office
    map["salon"] = R.mipmap.salon
    map["school"] = R.mipmap.school
    map["shoe"] = R.mipmap.shoe
    map["stadium"] = R.mipmap.stadium
    map["university"] = R.mipmap.university
    map["zoo"] = R.mipmap.zoo
    return map
}

fun formatData(data: String): Pair<ArrayList<DataEntity>, ArrayList<String>> {
    val type = object : TypeToken<Map<String, DataEntity>>() {}.type
    val map: Map<String, DataEntity> = Gson().fromJson<Map<String, DataEntity>>(data, type)
    val list: ArrayList<DataEntity> = ArrayList<DataEntity>(map.values)
    val keys = ArrayList(map.keys)
    return Pair(list, keys)
}