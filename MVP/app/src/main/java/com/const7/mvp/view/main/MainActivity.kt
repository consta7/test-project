package com.const7.mvp.view.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.const7.mvp.R
import com.const7.mvp.presenter.login.LoginPresenter
import com.const7.mvp.view.table.TableInfoImp
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), LogResInf {

    private var mLoginPresenter : LoginPresenter = LoginPresenter()
    private var locationManager: LocationManager? = null
    private var latGeo: Double = 0.0
    private var lonGeo: Double = 0.0
    private val tag = "const7_Project"
    private var permLocation: Int = 0

    private val locationListener: LocationListener = object : LocationListener {

        override fun onLocationChanged(location: Location) = showLocation(location)

        override fun onProviderDisabled(provider: String) = onProviderEnabled(provider)

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

        override fun onProviderEnabled(provider: String) {
            if (ActivityCompat.checkSelfPermission(this@MainActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager
                    .PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this@MainActivity,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager
                    .PERMISSION_GRANTED) return

            showLocation(locationManager?.getLastKnownLocation(provider))
        }
    }

    private fun showLocation(location: Location?) {
        if (location == null) return
        if (location.provider == LocationManager.GPS_PROVIDER ||
            location.provider == LocationManager.NETWORK_PROVIDER) {
            latGeo = location.latitude
            lonGeo = location.longitude
            locationManager?.removeUpdates(locationListener)
            Log.w(tag, "$latGeo; $lonGeo")
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            permLocation -> {
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // разрешение было предоставлено
                } else ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), permLocation)
                return
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            window.navigationBarColor = getColor(R.color.qq1)
            home.setBackgroundColor(getColor(R.color.aa2))
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == -1) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 0
            )
        }
        initFun()
        home.setOnClickListener {
            validResult()
            val btnStart = Intent(this, TableInfoImp::class.java)
            btnStart.putExtra("name", userName.text.toString())
            btnStart.putExtra("lat", latGeo)
            btnStart.putExtra("lon", lonGeo)
            startActivity(btnStart)
            finish()
        }
    }

    private fun validResult() {
        mLoginPresenter.progressBarVisible(View.VISIBLE)
        val username = userName.text.toString()
        mLoginPresenter.successLogin(username)
    }

    private fun initFun() {
        mLoginPresenter.loginPresenter(this)
        mLoginPresenter.progressBarVisible(View.INVISIBLE)
    }

    override fun onValidResult(result: Boolean, code: Int) {
        mLoginPresenter.progressBarVisible(View.INVISIBLE)
        val username = userName
        if (!result) {
            username.requestFocus()
            when (code) {
                1 -> username.error = getString(R.string.error)
                2 -> username.error = getString(R.string.error1)
                3 -> username.error = getString(R.string.error2)
            }
        }
    }

    override fun onSetProgressBarVisible(vis: Int) {
        progress_login.visibility = vis

    }

    override fun onResume() {
        super.onResume()
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        if (latGeo != 0.0) locationManager?.removeUpdates(locationListener)
        locationManager?.requestLocationUpdates(
            LocationManager.GPS_PROVIDER, (1000 * 3).toLong(), 0f,
            locationListener
        )
        locationManager?.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER, (1000 * 3).toLong(), 0f,
            locationListener
        )
    }

    override fun onPause() {
        super.onPause()
        locationManager?.removeUpdates(locationListener)
    }
}