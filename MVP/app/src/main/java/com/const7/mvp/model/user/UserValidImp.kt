package com.const7.mvp.model.user

class UserValidImp : UserValid {

    private var userName = ""

    fun userValid(userName: String) {
        this.userName = userName
    }

    override fun getUserName(): String = userName

    override fun checkValidity(userName: String) : Int {
        return when {
            userName.isEmpty() -> 1
            userName.length < 3 -> 2
            userName.length > 15 -> 3
            else -> 0
        }
    }
}