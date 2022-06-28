package liu.pei.qie

import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import com.github.iielse.switchbutton.SwitchView
import com.tencent.mmkv.MMKV
import org.greenrobot.eventbus.EventBus

class SettingsFragment : BaseFragment(R.layout.fragment_settings) {
    private lateinit var style1: SwitchView
    private lateinit var style2: SwitchView
    private lateinit var style3: SwitchView
    private lateinit var style4: SwitchView
    override fun xInit() {
        style1 = layoutView.findViewById(R.id.style1)
        style2 = layoutView.findViewById(R.id.style2)
        style3 = layoutView.findViewById(R.id.style3)
        style4 = layoutView.findViewById(R.id.style4)
        when (MMKV.defaultMMKV().decodeInt("mapStyle", 0)) {
            0 -> {
                style1.toggleSwitch(true)
            }
            1 -> {
                style2.toggleSwitch(true)
            }
            2 -> {
                style3.toggleSwitch(true)
            }
            3 -> {
                style4.toggleSwitch(true)
            }
        }
        style1.setOnStateChangedListener(object : SwitchView.OnStateChangedListener {
            override fun toggleToOn(view: SwitchView?) {
                view?.toggleSwitch(true)
                style2.toggleSwitch(false)
                style3.toggleSwitch(false)
                style4.toggleSwitch(false)
                MMKV.defaultMMKV().encode("mapStyle", 0)
                EventBus.getDefault().post(Event("styleChange"))
            }

            override fun toggleToOff(view: SwitchView?) {
            }
        })
        style2.setOnStateChangedListener(object : SwitchView.OnStateChangedListener {
            override fun toggleToOn(view: SwitchView?) {
                view?.toggleSwitch(true)
                style1.toggleSwitch(false)
                style3.toggleSwitch(false)
                style4.toggleSwitch(false)
                MMKV.defaultMMKV().encode("mapStyle", 1)
                EventBus.getDefault().post(Event("styleChange"))
            }

            override fun toggleToOff(view: SwitchView?) {
            }

        })
        style3.setOnStateChangedListener(object : SwitchView.OnStateChangedListener {
            override fun toggleToOn(view: SwitchView?) {
                view?.toggleSwitch(true)
                style2.toggleSwitch(false)
                style1.toggleSwitch(false)
                style4.toggleSwitch(false)
                MMKV.defaultMMKV().encode("mapStyle", 2)
                EventBus.getDefault().post(Event("styleChange"))
            }

            override fun toggleToOff(view: SwitchView?) {
            }

        })
        style4.setOnStateChangedListener(object : SwitchView.OnStateChangedListener {
            override fun toggleToOn(view: SwitchView?) {
                view?.toggleSwitch(true)
                style2.toggleSwitch(false)
                style3.toggleSwitch(false)
                style1.toggleSwitch(false)
                MMKV.defaultMMKV().encode("mapStyle", 3)
                EventBus.getDefault().post(Event("styleChange"))
            }

            override fun toggleToOff(view: SwitchView?) {
            }

        })
        layoutView.findViewById<RelativeLayout>(R.id.share).apply {
            setOnClickListener {
                (this@SettingsFragment.requireActivity() as AppCompatActivity).showShareDialog()
            }
        }
        layoutView.findViewById<RelativeLayout>(R.id.rate).apply {
            setOnClickListener {
                (this@SettingsFragment.requireActivity() as AppCompatActivity).showInputDialog()
            }
        }
    }

}