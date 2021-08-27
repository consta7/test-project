package com.const7.mvp.view.table

import android.view.View

interface TableInfo {

    fun refreshTable()
    fun getData()
    fun onSuccessResult(result : Int, code : Int)
    fun onSuccessParse(result: Int, code: Int)
    fun onClickLayout(needLine: Int, child : Int)
    fun onClickFragment(view: View)
    fun onSetProgressBarVisible(vis : Int)

}