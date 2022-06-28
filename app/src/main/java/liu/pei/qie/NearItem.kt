package liu.pei.qie

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.mapbox.search.ResponseInfo
import com.mapbox.search.SearchMultipleSelectionCallback
import com.mapbox.search.SearchOptions
import com.mapbox.search.SearchSuggestionsCallback
import com.mapbox.search.result.SearchResult
import com.mapbox.search.result.SearchSuggestion

class NearItem : LinearLayout, SearchSuggestionsCallback, SearchMultipleSelectionCallback {
    constructor(context: Context) : super(context) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView(context)
    }

    private lateinit var imageView: ImageView
    private lateinit var textView: TextView
    private lateinit var root: RelativeLayout
    lateinit var listener: NearItemClickListener

    private fun initView(context: Context): View {
        val v = LayoutInflater.from(context).inflate(R.layout.near_item, this, true)
        root = v.findViewById(R.id.root)
        root.setOnClickListener {
            listener.startSearch()
            searchEngine.search(textView.text.toString(), SearchOptions(), this)
        }
        imageView = v.findViewById(R.id.icon)
        textView = v.findViewById(R.id.text)
        return v
    }

    fun setIcon(id: Int) {
        imageView.setImageResource(id)
    }

    fun setText(s: String) {
        textView.text = s
    }


    override fun onSuggestions(suggestions: List<SearchSuggestion>, responseInfo: ResponseInfo) {
        listener.endSearch()
        suggestions.firstOrNull()?.let {
            searchEngine.select(suggestions, this)
        } ?: kotlin.run {
            Toast.makeText(context, "no data", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResult(
        suggestions: List<SearchSuggestion>,
        results: List<SearchResult>,
        responseInfo: ResponseInfo
    ) {
        listener.endSearch()
        results.firstOrNull()?.coordinate?.let {
            search_lat = it.latitude()
            search_lng = it.longitude()
            context.startActivity(Intent(context, SearchResultActivity::class.java))
        } ?: kotlin.run {
            Toast.makeText(context, "no data", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onError(e: Exception) {
        listener.endSearch()
        Toast.makeText(context, "no data", Toast.LENGTH_SHORT).show()
    }
}