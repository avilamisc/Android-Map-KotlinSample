package com.devjn.kotlinmap

import android.app.AlertDialog
import android.app.Application
import android.content.*
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Handler
import android.preference.PreferenceManager
import android.widget.Toast
import rx.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*

class Common : Application() {

    override fun onCreate() {
        super.onCreate()
        Foreground.init(this)
        Companion.applicationContext = getApplicationContext()
        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        applicationHandler = Handler(applicationContext!!.mainLooper)
    }

    companion object {

        private val TAG = "Common Global Application"
        @Volatile lateinit var applicationContext: Context
        @Volatile lateinit var applicationHandler: Handler

        private lateinit var prefs: SharedPreferences

        private val ACC_NUMBER = "ACCOUNT_NUMBER"
        private val PROF_NUMBER = "PROFILE_NUMBER"


        private val AB_COLOR = "AB_COLOR"
        private val FAB_COLOR = "FAB_COLOR"
        private val BAR_COLOR = "BAR_COLOR"
        private val THEME_IMAGE = "THEME_IMAGE"
        val PROFILE_IMAGE_PATH = "Profile_Image"
        val NEAR_VERSION = "NEAR_VERSION"

        private val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        private val now = Calendar.getInstance(TimeZone.getTimeZone("UTC")).time

        //--------- Colors

        fun setPaletteTheme(abColor: Int, fabColor: Int, imageId: Int) {
            prefs.edit().putInt(AB_COLOR, abColor)
                    .putInt(FAB_COLOR, fabColor)
                    .putInt(THEME_IMAGE, imageId).apply()
        }

        val abColor: Int
            get() = prefs.getInt(AB_COLOR, Color.parseColor("#207117"))

        val fabColor: Int
            get() = prefs.getInt(FAB_COLOR, Color.parseColor("#2baf2b"))


        var nearVersion: Int
            get() = prefs.getInt(NEAR_VERSION, -1)
            set(version) {
                Schedulers.io().createWorker().schedule { prefs.edit().putInt(NEAR_VERSION, version).apply() }
            }

        var profileImageDirectory: String
            get() = prefs.getString("PROFILE_DIRECTORY", null)
            set(directory) {
                prefs.edit().putString("PROFILE_DIRECTORY", directory).commit()
            }

        val newTestContactId: Int
            get() = prefs.getInt("testid", 10000) + 1

        fun updateTestId() {
            prefs.edit().putInt("testid", newTestContactId + 1).apply()
        }

        val isNightModeEnabled: Boolean
            get() = prefs.getBoolean("night_mode", false)


        //-----------------------------------------------------------------

        //Function to display simple Alert Dialog
        fun showAlertDialog(context: Context, title: String, message: String, status: Boolean?) {
            val alertDialog = AlertDialog.Builder(context).create()
            // Set Dialog Title
            alertDialog.setTitle(title)
            // Set Dialog Message
            alertDialog.setMessage(message)
            if (status != null)
                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK") { dialog, which -> } // Set OK Button
            // Show Alert Message
            alertDialog.show()
        }

        // Checking for all possible internet providers
        val isConnectingToInternet: Boolean
            get() {

                val connectivity = applicationContext.getSystemService(
                        Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                if (connectivity != null) {
                    val info = connectivity.allNetworkInfo
                    if (info != null)
                        for (i in info.indices)
                            if (info[i].state == NetworkInfo.State.CONNECTED) {
                                return true
                            }

                }
                return false
            }


        fun needShowWarn(context: Context, title: String?, text: String?,
                         listener: DialogInterface.OnClickListener) {
            var title = title
            if (text == null) {
                return
            }
            val builder = AlertDialog.Builder(context)
            if (title == null) title = context.getString(R.string.app_name)
            builder.setTitle(title)
            builder.setMessage(text)
            builder.setPositiveButton("OK", listener)//LocaleController.getString("OK", R.string.OK), null);
            builder.setNeutralButton(R.string.cancel, null)

            builder.create().show()
            //showDialog(builder.create());
        }

        fun makeDirectCall(ctx: Context, num: String?) {
            if (num != null) {
                val callIntent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + num.trim { it <= ' ' }))
                try {
                    ctx.startActivity(callIntent)
                } catch (ex: ActivityNotFoundException) {
                    // TODO: handle exception
                    Toast.makeText(ctx, "Sorry, it's impossible now", Toast.LENGTH_SHORT).show()
                }

            }
        }
    }

}