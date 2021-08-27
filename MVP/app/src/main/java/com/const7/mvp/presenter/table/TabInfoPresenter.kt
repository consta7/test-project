package com.const7.mvp.presenter.table

import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import com.const7.mvp.model.data.DataBaseImp
import com.const7.mvp.view.fragment.SelectedUser
import com.const7.mvp.view.table.TableInfo
import kotlinx.coroutines.*
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.random.Random

class TabInfoPresenter : TabInfo {

    private var tabPresenter : TableInfo? = null
    private var data : DataBaseImp = DataBaseImp()
    private var handler : Handler = Handler(Looper.getMainLooper())
    private var mutableMap: MutableMap<String, Any> = HashMap()
    
    var name = emptyArray<String>()
    private var lat = emptyArray<Double>()  //долгота
    private var lon = emptyArray<Double>()  //широта
    private var photoID = emptyArray<Int>() //id для фотографий
    private var cordUsers  = emptyArray<String>()
    private var distanceUsers = emptyArray<Double>()

    fun tabPresenter(tabPresenter: TableInfo) {
        this.tabPresenter = tabPresenter
    }

    override fun localUser(name: String, lat : Double, lon : Double, photoId : Int) {
        data.localUser(name, lat, lon, photoId)
    }

    override fun successResult(code: Int) {
        data.userMap(mutableMap)
        Thread.sleep(5 * 1000)
        val result : Int = if (data.request() == 1) 1 else 0
        handler.postDelayed({ tabPresenter?.onSuccessResult(result, code) }, 1 * 1000)
    }

    override fun successParse(code: Int) {
        val result : Int = if (data.distanceCalculation()) 1 else 0
        name += data.setName()
        lat += data.setLat()
        lon += data.setLon()
        photoID += data.setPhoto()
        distanceUsers += data.setDistance()
        cordUsers += data.setCord()
        tabPresenter?.onSuccessParse(result, code)

        startRefresh()
    }

    private fun startRefresh() {
        GlobalScope.launch {
            do {
                for (i in 1..10) refreshDigits()
                delay(3 * 1000L)
            } while (true)
        }
    }

    override fun selectedChild(needLine : Int, selectedChild : Int, lastChild : Int) : Int {
        var selectLine = selectedChild
        if (needLine % 2 == 0 && needLine != 0) selectLine = needLine / 2

        val lastLine = selectLine
        if (needLine == 0) selectLine = lastLine

        //нашли нужного юзера, перетасуем данные
        name[0] = name[selectLine].also { name[selectLine] = name[0] }

        lat[0] = lat[selectLine].also { lat[selectLine] = lat[0] }

        lon[0] = lon[selectLine].also { lon[selectLine] = lon[0] }

        photoID[0] = photoID[selectLine].also { photoID[selectLine] = photoID[0] }

        cordUsers[0] = cordUsers[selectLine].also { cordUsers[selectLine] = cordUsers[0] }

        distanceCalculation()
        return lastLine
    }

    fun roundOffDecimal(number: Double, flag: Int): Double {
        val format = if (flag == 1) "##." else "##.######"
        val df = DecimalFormat(format)
        df.roundingMode = RoundingMode.FLOOR
        return df.format(number).replace(",", ".").toDouble()
    }

    override fun progressBarVisible(code: Int) {
        tabPresenter?.onSetProgressBarVisible(code)
    }

    fun openFragment(name: String, lat: Double, lon: Double, id: Int) : Fragment {
        return SelectedUser(this.name, this.lat, this.lon, this.photoID)
    }

    private fun distanceCalculation() {
        distanceUsers = emptyArray()
        for (i in 0..10) {
            distanceUsers += data.distance(lat[0], lon[0], lat[i], lon[i]).toDouble()
            cordUsers += ("${lat[i]}; ${lon[i]}")
        }
        tabPresenter?.getData()
    }

    private fun refreshDigits() {
        CoroutineScope(Dispatchers.IO).launch {
            Handler(Looper.getMainLooper()).post {
                val arithmeticChoose = Random.nextBoolean()
                val digit = Random.nextDouble(0.00001, 0.00005)
                when (arithmeticChoose) {
                    true -> {
                        for (i in 1..10) {
                            lat[i] = roundOffDecimal(lat[i] + digit, 2)
                            lon[i] = roundOffDecimal(lon[i] + digit, 2)
                            cordUsers[i] = (lat[i].toString() + "; " + lon[i].toString())
                        }
                        distanceCalculation()
                    }
                    false -> {
                        for (i in 1..10) {
                            lat[i] = roundOffDecimal(lat[i] - digit, 2)
                            lon[i] = roundOffDecimal(lon[i] - digit, 2)
                            cordUsers[i] = (lat[i].toString() + "; " + lon[i].toString())
                        }
                        distanceCalculation()
                    }
                }
            }
        }
    }

    fun setName() : Array<String> = name
    fun setCord() : Array<String> = cordUsers

    fun setLat() : Array<Double> = lat
    fun setLon() : Array<Double> = lon
    fun setDistance() : Array<Double> = distanceUsers

    fun setPhoto() : Array<Int> = photoID
}