package com.const7.mvp.view.fragment

import android.content.res.Configuration
import android.graphics.Color.BLACK
import android.graphics.Color.WHITE
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import com.const7.mvp.R
import com.const7.mvp.view.table.TableInfoImp

class SelectedUser (private var name: Array<String>,
                    private var lat: Array<Double>,
                    private var lon: Array<Double>,
                    private var photoID: Array<Int>
) : Fragment() {

    private val k = TableInfoImp()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_child, container, false)

        val status : TextView = view.findViewById(R.id.status)
        val photoId : ImageView = view.findViewById(R.id.photo_id)
        val nameUser : TextView = view.findViewById(R.id.nameUser)
        val distance : TextView = view.findViewById(R.id.distance)
        val linLay : LinearLayout = view.findViewById(R.id.linLay)
        val linInfo : LinearLayout = view.findViewById(R.id.linInfo)
        val coordinates : TextView = view.findViewById(R.id.coordinates)

        when ((resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_NO -> {
                coordinates.setTextColor(BLACK)
                status.setTextColor(BLACK)
                linInfo.setBackgroundColor(WHITE)
            }
            Configuration.UI_MODE_NIGHT_YES -> {
                coordinates.setTextColor(WHITE)
                status.setTextColor(WHITE)
                linInfo.setBackgroundColor(BLACK)
            }
        }

        photoId.apply {
            setImageResource(k.photo[photoID[0]])
            layoutParams = LinearLayout.LayoutParams(100, 100, 1F)
        }
        nameUser.apply {
            text = name[0]
            textSize = 20F
            setTextColor(WHITE)
            typeface = Typeface.SANS_SERIF
        }
        coordinates.apply {
            gravity = Gravity.END
            text = ("${lat[0]}; ${lon[0]}")
        }
        distance.apply {
            textSize = 20F
            setTextColor(WHITE)
            gravity = Gravity.END
            text = ("0 m " + resources.getString(R.string.to_point))
        }
        linLay.apply {
            setPadding(40)
            setBackgroundResource(R.drawable.custom_fragment)
            layoutParams = LinearLayout.LayoutParams(-1, 200)
        }
        status.text = resources.getString(R.string.one)
        linInfo.setPadding(5)

        LinearLayout.LayoutParams(-2, -2, 1F).let {
            status.layoutParams = it
            distance.layoutParams = it
            nameUser.layoutParams = it
            coordinates.layoutParams = it
        }

        return view
    }
}