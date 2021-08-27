package com.const7.mvp.presenter.table

interface TabInfo {

    fun successResult(code: Int)
    fun successParse(code : Int)
    fun selectedChild(needLine : Int, selectedChild : Int, lastChild : Int) : Int
    fun localUser(name: String, lat : Double, lon : Double, photoId : Int)
    fun progressBarVisible(code : Int)

}