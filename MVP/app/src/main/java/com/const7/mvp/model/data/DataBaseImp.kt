package com.const7.mvp.model.data

import android.location.Location
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import java.math.RoundingMode
import java.text.DecimalFormat

class DataBaseImp : DataBase {

    val tag = "const7_Project"
    var code = 0
    private var mutableMap : MutableMap<String, Any> = HashMap()

    var name : Array<String> = emptyArray()
    private var lat : Array<Double> = emptyArray()  //долгота
    private var lon : Array<Double> = emptyArray()  //широта
    private var photoID : Array<Int> = emptyArray() //id для фотографий
    var cordUsers  = emptyArray<String>()
    var distanceUsers = emptyArray<Double>()

    fun userMap(mutableMap: MutableMap<String, Any>) {
        this.mutableMap = mutableMap
    }

    override fun getMutableMap() : MutableMap<String, Any> = mutableMap

    override fun localUser(name: String, lat : Double, lon : Double, photoId : Int) {
        this.name += name
        this.lat += roundOffDecimal(lat, 2)
        this.lon += roundOffDecimal(lon, 2)
        this.photoID += photoId
    }

    override fun request() : Int {
        val dataBase = Firebase.database
        val refUsers = dataBase.getReference("/user")
        Log.i(tag, "ref ------>>>>> $refUsers")
        refUsers.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                mutableMap = dataSnapshot.getValue<MutableMap<String, Any>>()!!
                code = 1
                parseData()
                Log.i(tag, "Данные получены")
            }
            override fun onCancelled(error: DatabaseError) {
                Log.w(tag, "Failed to read value.", error.toException())
            }
        })
        return code
    }

    override fun subData(type: String, index : Int) : String {
        val fstDel = ","
        val sndDel = "}"
        return when (type) {
            "nameIn" -> mutableMap["${index}_key"].toString()
                .substringAfter("name=").substringBefore(fstDel)

            "lonIn" -> mutableMap["${index}_key"].toString()
                .substringAfter("lon=").substringBefore(fstDel)

            "latIn" -> {
                if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.M)
                    mutableMap["${index}_key"].toString()
                        .substringAfter("lat=").substringBefore(fstDel)
                else mutableMap["${index}_key"].toString()
                    .substringAfter("lat=").substringBefore(sndDel)
            }
            "photoIn" -> {
                if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.M)
                    mutableMap["${index}_key"].toString()
                        .substringAfter("photo_id=").substringBefore(sndDel)
                else mutableMap["${index}_key"].toString()
                    .substringAfter("photo_id=").substringBefore(fstDel)
            }
            else -> "Error parsing data!"
        }
    }

    override fun parseData() {
        for (i in 1..10) {
            name += subData("nameIn", i - 1)
            lon += subData("lonIn", i - 1).toDouble()
            lat += subData("latIn", i - 1).toDouble()
            photoID += subData("photoIn", i - 1).toInt()
        }
        distanceCalculation()
    }

    fun distanceCalculation(): Boolean {
        for (i in 0..10) {
            distanceUsers += distance(lat[0], lon[0], lat[i], lon[i]).toDouble()
            cordUsers += ("${lat[i]}; ${lon[i]}")
        }
        return true
    }

    fun distance(lat0: Double, lon0: Double, lat1: Double, lon1: Double): Float {
        val distance = FloatArray(3)
        Location.distanceBetween(lat0, lon0, lat1, lon1, distance)
        return distance[0]
    }

    private fun roundOffDecimal(number: Double, flag: Int): Double {
        val format = if (flag == 1) "##." else "##.######"
        val df = DecimalFormat(format)
        df.roundingMode = RoundingMode.FLOOR
        return df.format(number).replace(",", ".").toDouble()
    }

    fun setName() : Array<String> = name
    fun setLat() : Array<Double> = lat
    fun setLon() : Array<Double> = lon
    fun setPhoto() : Array<Int> = photoID
    fun setCord() : Array<String> = cordUsers
    fun setDistance() : Array<Double> = distanceUsers
}