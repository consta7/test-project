package com.const7.mvp.presenter.login

import android.os.Handler
import android.os.Looper
import com.const7.mvp.view.main.LogResInf
import com.const7.mvp.model.user.UserValidImp
import com.const7.mvp.presenter.login.LoginInf

class LoginPresenter : LoginInf {

    private var logResI : LogResInf? = null
    private var user : UserValidImp = UserValidImp()
    private var handler : Handler = Handler(Looper.getMainLooper())

    fun loginPresenter(logRes: LogResInf) {
        logResI = logRes
    }

    override fun successLogin(name: String) {
        user.userValid(name)
        var isLoginSuccess = true
        val code: Int = user.checkValidity(name)
        if (code != 0) isLoginSuccess = false
        val result = isLoginSuccess
        handler.postDelayed({ logResI?.onValidResult(result, code) }, 1 * 1000)
    }

    override fun progressBarVisible(vis: Int) {
        logResI?.onSetProgressBarVisible(vis)
    }
}