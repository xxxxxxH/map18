package liu.pei.qie

import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.mingle.widget.LoadingView

class NearbyFragment : BaseFragment(R.layout.fragment_nearby), NearItemClickListener {

    private lateinit var loadingView: RelativeLayout

    override fun xInit() {
        loadingView = layoutView.findViewById(R.id.loading)
        layoutView.findViewById<LinearLayout>(R.id.content).apply {
            val data = nearbyDataCollections()
            data.forEach { (key, value) ->
                val item = NearItem(this@NearbyFragment.requireActivity())
                item.listener = this@NearbyFragment
                item.setText(key)
                item.setIcon(value)
                addView(item)
            }
        }
    }

    override fun startSearch() {
        loadingView.visibility = View.VISIBLE
    }

    override fun endSearch() {
        loadingView.visibility = View.GONE
    }

}