package liu.pei.qie

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.flyco.tablayout.SlidingTabLayout
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style


class Homepage : AppCompatActivity() {
    private lateinit var tabLayout: SlidingTabLayout
    private lateinit var viewPager: ViewPager
    private lateinit var map: MapView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_home)
        context = this
//        LiuPeiQie.liuPeiQie(this)
        tabLayout = findViewById(R.id.tabLayout)
        viewPager = findViewById(R.id.viewpager)
        tabLayout.setViewPager(viewPager, ConstVal.title, this, fragmentCollections())
    }
}