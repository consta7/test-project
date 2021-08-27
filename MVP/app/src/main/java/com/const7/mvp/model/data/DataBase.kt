package com.const7.mvp.model.data

interface DataBase {

    fun request() : Int
    fun getMutableMap() : MutableMap<String, Any>
    fun subData(type: String, index : Int) : String
    fun parseData()
    fun localUser(name: String, lat : Double, lon : Double, photoId : Int)

}