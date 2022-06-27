package liu.pei.qie

import android.widget.LinearLayout

class NearbyFragment : BaseFragment(R.layout.fragment_nearby) {
    override fun xInit() {
        layoutView.findViewById<LinearLayout>(R.id.content).apply {
            val data = nearbyDataCollections()
            data.forEach { (key, value) ->
                val item = NearItem(this@NearbyFragment.requireActivity())
                item.setText(key)
                item.setIcon(value)
                addView(item)
            }
        }
    }

}