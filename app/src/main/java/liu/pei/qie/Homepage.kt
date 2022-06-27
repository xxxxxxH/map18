package liu.pei.qie

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import liu.pei.qiezidezhenming.utils.LiuPeiQie


class Homepage :AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LiuPeiQie.liuPeiQie(this)
    }
}