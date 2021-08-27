package com.const7.mvp.model.user

interface UserValid {

    fun getUserName(): String
    fun checkValidity(userName: String): Int

}