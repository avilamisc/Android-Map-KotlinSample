package com.github.devjn.kotlinmap

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.github.devjn.kotlinmap.Common.Companion.LOCATION_PERMISSION_REQUEST_CODE
import com.github.devjn.kotlinmap.Common.Companion.STORAGE_PERMISSION_REQUEST_CODE
import com.github.devjn.kotlinmap.utils.PermissionUtils

class SplashActivity : AppCompatActivity() {
    val TAG = SplashActivity::class.java.kotlin.simpleName

    var locationGrated = false
    var storageGranted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        if (checkPermissions()) {
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish()
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
    }

    private fun checkPermissions(): Boolean {
        if (!PermissionUtils.isLocationGranted) {
            PermissionUtils.requestPermission(this, PERMISSIONS_REQUEST_CODE,
                    Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_FINE_LOCATION)
//            !PermissionUtils.isStorageGranted ||
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE -> if (grantResults.isNotEmpty()) {
                if (PermissionUtils.isPermissionGranted(permissions, grantResults, Manifest.permission.ACCESS_FINE_LOCATION))
                    locationGrated = true
                if (PermissionUtils.isPermissionGranted(permissions, grantResults, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                    storageGranted = true
                else PermissionUtils.requestPermission(this, STORAGE_PERMISSION_REQUEST_CODE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)

                if (!locationGrated) {
                    PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                            Manifest.permission.ACCESS_FINE_LOCATION, true)
                }
            } else {
                // Display the missing permission error dialog when the fragments resume.
                Log.w(TAG, "Permissions are not granted: " + permissions)
                PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                        Manifest.permission.ACCESS_FINE_LOCATION, true)
            }
            STORAGE_PERMISSION_REQUEST_CODE -> if (PermissionUtils.isPermissionGranted(permissions, grantResults, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                storageGranted = true
            LOCATION_PERMISSION_REQUEST_CODE -> if (PermissionUtils.isPermissionGranted(permissions, grantResults, Manifest.permission.ACCESS_FINE_LOCATION))
                locationGrated = true

        }
        if(locationGrated) {
            Log.w(TAG, "Location permission is granted")
            val intent = Intent(this@SplashActivity, MainActivity::class.java);
            startActivity(intent)
            finish()
        }
    }

    companion object {
        private val PERMISSIONS_REQUEST_CODE = 100
    }

}
