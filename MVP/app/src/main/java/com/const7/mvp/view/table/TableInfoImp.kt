package com.const7.mvp.view.table

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setPadding
import com.const7.mvp.R
import com.const7.mvp.presenter.table.TabInfoPresenter
import com.const7.mvp.view.main.MainActivity
import kotlinx.android.synthetic.main.activity_table_info.*

class TableInfoImp : AppCompatActivity(), TableInfo {

    private val tag = "const7_Project"
    private var tablePresenter : TabInfoPresenter = TabInfoPresenter()
    private val valueUsers : MutableMap<String, TextView> = HashMap()

    private var name : Array<String> = emptyArray()
    private var lat : Array<Double> = emptyArray()
    private var lon : Array<Double> = emptyArray()
    private var photoID : Array<Int> = emptyArray()
    private var cordUsers : Array<String> = emptyArray()
    private var distanceUsers : Array<Double> = emptyArray()

    private var selectedChild = 0
    private var lastChild = 0
    private var indexUser = 0
    var photo : Array<Int> = arrayOf(
        R.drawable.user_photo0, R.drawable.user_photo1, R.drawable.user_photo2,
        R.drawable.user_photo3, R.drawable.user_photo4, R.drawable.user_photo5,
        R.drawable.user_photo6, R.drawable.user_photo7, R.drawable.user_photo8,
        R.drawable.user_photo9, R.drawable.user_photo10
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_table_info)

        val latGeo = intent.extras?.getDouble("lat") ?: 0.0
        val lonGeo = intent.extras?.getDouble("lon") ?: 0.0
        val userName = intent.extras?.getString("name").toString()

        localUser(userName, latGeo, lonGeo)
        successResult()
    }

    private fun localUser(name: String, lat : Double, lon : Double) {
        tablePresenter.localUser(name, lat, lon, 0)
    }

    private fun successResult() {
        tablePresenter.tabPresenter(this)
        tablePresenter.progressBarVisible(View.VISIBLE)
        tablePresenter.successResult(code = 1)
    }

    override fun onClickLayout(needLine: Int, child: Int) {
        lastChild = tablePresenter.selectedChild(needLine, child, lastChild)
        supportFragmentManager.beginTransaction()
            .add(R.id.frame_lay, tablePresenter.openFragment(
                name[needLine], lat[needLine], lon[needLine], photoID[needLine])
            ).commit()
    }

    override fun onClickFragment(view: View) {
        frame_lay.removeAllViewsInLayout()
        tablePresenter.selectedChild(0, lastChild, lastChild)
    }

    override fun onSetProgressBarVisible(vis : Int) {
        progressBar.visibility = vis
    }

    override fun onSuccessResult(result : Int, code : Int) {
        if (result == 1) tablePresenter.successParse(1)
        else tablePresenter.successResult(1)
    }

    override fun onSuccessParse(result: Int, code: Int) {
        if (result == 1) getData()
        else tablePresenter.successParse(1)
    }

    override fun getData() {
        //clear all
        clearData()
        //get new info
        name += tablePresenter.setName()
        lat += tablePresenter.setLat()
        lon += tablePresenter.setLon()
        photoID += tablePresenter.setPhoto()
        distanceUsers += tablePresenter.setDistance()
        cordUsers += tablePresenter.setCord()

        refreshTable()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val back = Intent(this, MainActivity::class.java)
        startActivity(back)
        finish()
    }

    override fun refreshTable() {
        for (i in 0..10) valueUsers["name$i"] = TextView(this)

        tableUsers.removeAllViewsInLayout()

        for ((i, _) in valueUsers) {

            val number = TextView(this)
            val cordUser = TextView(this)
            val statusBar = TextView(this)
            val photoUser = ImageView(this)
            val linearLay = LinearLayout(this)
            val linearInf = LinearLayout(this)

            linearLay.isClickable = true
            linearLay.setOnClickListener {
                var needLine = 0
                while (needLine < tableUsers.childCount) {
                    if (linearLay == tableUsers.getChildAt(needLine)) {
                        tableUsers.removeAllViewsInLayout()
                        lastChild = selectedChild
                        onClickLayout(needLine, selectedChild)
                        break
                    }
                    needLine++
                }
            }
            photoUser.apply {
                setImageResource(photo[photoID[indexUser]])
                layoutParams = LinearLayout.LayoutParams(100, 100, 1F)
            }
            valueUsers[i]?.apply {
                textSize = 20F
                setTextColor(Color.WHITE)
                typeface = Typeface.SANS_SERIF
                layoutParams = LinearLayout.LayoutParams(-2, -2, 1F)
                text = name[indexUser]
            }
            cordUser.apply {
                text = cordUsers[indexUser]
                layoutParams = LinearLayout.LayoutParams(-2, -2, 1F)
                gravity = Gravity.END
            }
            statusBar.apply {
                when {
                    distanceUsers[indexUser] in 0.0..7.0 ->
                        text = resources.getString(R.string.one)
                    distanceUsers[indexUser] in 7.0..50.0 ->
                        text = resources.getString(R.string.two)
                    distanceUsers[indexUser] in 50.0..250.0 ->
                        text = resources.getString(R.string.three)
                    distanceUsers[indexUser] > 250.0 ->
                        text = resources.getString(R.string.four)
                }
                layoutParams = LinearLayout.LayoutParams(-2, -2, 1F)
            }
            number.apply {
                text = when (distanceUsers[indexUser]) {
                    0.0 -> distanceUsers[indexUser]
                        .toInt().toString() + " m " + resources.getString(R.string.to_point)
                    else -> tablePresenter.roundOffDecimal(distanceUsers[indexUser], 1)
                        .toInt().toString() + " m " + resources.getString(R.string.to_point)
                }
                layoutParams = LinearLayout.LayoutParams(-2, -2, 1F)
                gravity = Gravity.END
                textSize = 20F
                setTextColor(Color.WHITE)
            }
            linearLay.apply {
                layoutParams = LinearLayout.LayoutParams(-1, 200)
                setPadding(40)
                setBackgroundResource(R.drawable.custom_design)
                addView(photoUser)
                addView(valueUsers[i])
                addView(number)
            }
            linearInf.apply {
                addView(statusBar)
                addView(cordUser)
                setPadding(5)
            }
            tableUsers.addView(linearLay)
            tableUsers.addView(linearInf)

            indexUser++
        }
        indexUser = 0
        tablePresenter.progressBarVisible(View.INVISIBLE)
        scrollView2.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        Log.d(tag, "onResume")

    }

    override fun onPause() {
        super.onPause()
        Log.d(tag, "onPause")
    }

    private fun clearData() {
        name = emptyArray()
        lat = emptyArray()
        lon = emptyArray()
        photoID = emptyArray()
        distanceUsers = emptyArray()
        cordUsers = emptyArray()
    }
}