package liu.pei.qie

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment

abstract class BaseFragment(layoutId:Int):Fragment(layoutId) {

    protected lateinit var layoutView:View

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        layoutView = view
        xInit()
    }

    abstract fun xInit()
}