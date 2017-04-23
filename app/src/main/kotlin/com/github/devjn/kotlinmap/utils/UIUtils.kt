package com.github.devjn.kotlinmap.utils

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v4.widget.NestedScrollView
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ScrollView
import android.widget.TextView
import com.github.devjn.kotlinmap.BuildConfig
import com.github.devjn.kotlinmap.Common
import com.github.devjn.kotlinmap.utils.LogUtils.LOGE
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


/**
 * An assortment of UI helpers.
 */
object UIUtils {
    private val TAG = UIUtils.javaClass.kotlin.simpleName

    private val typefaceCache = Hashtable<String, Typeface>()
    var isRTL = false
    var density = 1f

    val writeFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
    val readFormat: DateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm:ss", Locale.getDefault())

    private val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    init {
        density = Common.applicationContext.resources.displayMetrics.density
        writeFormat.timeZone = TimeZone.getTimeZone("UTC")
        readFormat.timeZone = TimeZone.getDefault()
        //checkDisplaySize();
    }

    /**
     * Factor applied to session color to derive the background color on panels and when
     * a session photo could not be downloaded (or while it is being downloaded)
     */
    val SESSION_BG_COLOR_SCALE_FACTOR = 0.65f
    val SESSION_PHOTO_SCRIM_ALPHA = 0.75f

    val TARGET_FORM_FACTOR_HANDSET = "handset"
    val TARGET_FORM_FACTOR_TABLET = "tablet"


    val ANIMATION_FADE_IN_TIME = 250
    val TRACK_ICONS_TAG = "tracks"


    private val BRIGHTNESS_THRESHOLD = 130

    private val df = arrayOf(DateFormat.getDateInstance(), DateFormat.getTimeInstance())

    var displaySize = Point()

    /**
     * Calculate whether a color is light or dark, based on a commonly known
     * brightness formula.

     * @see {@literal http://en.wikipedia.org/wiki/HSV_color_space%23Lightness}
     */
    fun isColorDark(color: Int): Boolean {
        return (30 * Color.red(color) +
                59 * Color.green(color) +
                11 * Color.blue(color)) / 100 <= BRIGHTNESS_THRESHOLD
    }

    fun isTablet(context: Context): Boolean {
        return context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE
    }

    private val sAppLoadTime = System.currentTimeMillis()

    fun getCurrentTime(context: Context): Long {
        if (BuildConfig.DEBUG) {
            return context.getSharedPreferences("mock_data", Context.MODE_PRIVATE)
                    .getLong("mock_current_time", System.currentTimeMillis()) + System.currentTimeMillis() - sAppLoadTime
            //            return ParserUtils.parseTime("2012-06-27T09:44:45.000-07:00")
            //                    + System.currentTimeMillis() - sAppLoadTime;
        } else {
            return System.currentTimeMillis()
        }
    }

    //TimeZone.getTimeZone("UTC")
    fun getTime(datetime: String?): Long {
        if (datetime == null) return 0
        try {
            val date = writeFormat.parse(datetime)
            return date.time
        } catch (e: ParseException) {
            return 0
        }

    }

    fun convertDbTimeToLocal(datetime: String): String? {
        try {
            val date = writeFormat.parse(datetime)
            return readFormat.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return null
    }

    fun getDbTimeToLocal(datetime: String): Long {
        try {
            var date = writeFormat.parse(datetime)
            date = readFormat.parse(readFormat.format(date))
            return date.time
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return 0
    }

    val now: String
        get() {
            val date = Calendar.getInstance().time
            return writeFormat.format(date)
        }

    val localNow: String
        get() {
            val date = Calendar.getInstance(TimeZone.getDefault()).time
            return writeFormat.format(date)
        }

    val currentTime: Long
        get() = Date().time


    fun getViewInset(view: View?): Int {
        if (view == null || Build.VERSION.SDK_INT < 21) {
            return 0
        }
        try {
            val mAttachInfoField = View::class.java.getDeclaredField("mAttachInfo")
            mAttachInfoField.isAccessible = true
            val mAttachInfo = mAttachInfoField.get(view)
            if (mAttachInfo != null) {
                val mStableInsetsField = mAttachInfo.javaClass.getDeclaredField("mStableInsets")
                mStableInsetsField.isAccessible = true
                val insets = mStableInsetsField.get(mAttachInfo) as Rect
                return insets.bottom
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return 0
    }

    val currentActionBarHeight: Int
        get() {
            if (isTablet(Common.applicationContext)) {
                Log.d("abc", 64.toString())
                return dp(64f)
            } else if (Common.applicationContext.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                Log.d("abc", 48.toString())
                return dp(48f)
            } else {
                Log.d("abc", 56.toString())
                return dp(56f)
            }
        }

    val statusBarHeight: Int
        get() {
            var result = 0
            val resourceId = Common.applicationContext.resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                result = Common.applicationContext.resources.getDimensionPixelSize(resourceId)
            }
            return result
        }


    private val RES_IDS_ACTION_BAR_SIZE = intArrayOf(android.R.attr.actionBarSize)

    /** Calculates the Action Bar height in pixels.  */
    fun calculateActionBarSize(context: Context?): Int {
        if (context == null) {
            return 0
        }

        val curTheme = context.theme ?: return 0

        val att = curTheme.obtainStyledAttributes(RES_IDS_ACTION_BAR_SIZE) ?: return 0

        val size = att.getDimension(0, 0f)
        att.recycle()
        return size.toInt()
    }

    fun setColorAlpha(color: Int, alpha: Float): Int {
        val alpha_int = Math.min(Math.max((alpha * 255.0f).toInt(), 0), 255)
        return Color.argb(alpha_int, Color.red(color), Color.green(color), Color.blue(color))
    }

    fun scaleColor(color: Int, factor: Float, scaleAlpha: Boolean): Int {
        return Color.argb(if (scaleAlpha) Math.round(Color.alpha(color) * factor) else Color.alpha(color),
                Math.round(Color.red(color) * factor), Math.round(Color.green(color) * factor),
                Math.round(Color.blue(color) * factor))
    }

    fun scaleSessionColorToDefaultBG(color: Int): Int {
        return scaleColor(color, SESSION_BG_COLOR_SCALE_FACTOR, false)
    }

    fun hasActionBar(activity: Activity): Boolean {
        return activity.actionBar != null
    }

    fun setStartPadding(view: View, padding: Int) {
        if (isRtl) {
            view.setPadding(view.paddingLeft, view.paddingTop, padding, view.paddingBottom)
        } else {
            view.setPadding(padding, view.paddingTop, view.paddingRight, view.paddingBottom)
        }
    }

    fun getTypeface(assetPath: String): Typeface? {
        synchronized(typefaceCache) {
            if (!typefaceCache.containsKey(assetPath)) {
                try {
                    val t = Typeface.createFromAsset(Common.applicationContext.assets, assetPath)
                    typefaceCache.put(assetPath, t)
                } catch (e: Exception) {
                    Log.e("Typefaces", "Could not get typeface '" + assetPath + "' because " + e.message)
                    return null
                }

            }
            return typefaceCache[assetPath]
        }
    }

    val isRtl: Boolean
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
        get() {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                return false
            } else {
                return Common.applicationContext.resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL
            }
        }

    @SuppressLint("NewApi")
    fun setAccessibilityIgnore(view: View) {
        view.isClickable = false
        view.isFocusable = false
        view.contentDescription = ""
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
        }
    }


    fun getProgress(value: Int, min: Int, max: Int): Float {
        if (min == max) {
            throw IllegalArgumentException("Max ($max) cannot equal min ($min)")
        }

        return (value - min) / (max - min).toFloat()
    }

    fun convertDpToPixel(context: Context?, dp: Float): Int {
        if (context == null) {
            return 0
        }
        val metrics = context.resources.displayMetrics
        val px = dp * (metrics.densityDpi / 160f)
        return px.toInt()
    }

    fun dp(value: Float): Int {
        return Math.ceil((density * value).toDouble()).toInt()
    }

    fun dpf2(value: Float): Float {
        if (value == 0f) {
            return 0f
        }
        return density * value
    }

    fun getScreenWidth(context: Context): Int {
        val metrics = context.resources.displayMetrics
        return metrics.widthPixels
    }

    val screenWidth: Int
        get() {
            val metrics = Common.applicationContext.resources.displayMetrics
            return metrics.widthPixels
        }

    fun getScreenHeight(context: Context): Int {
        val metrics = context.resources.displayMetrics
        return metrics.heightPixels
    }

    val screenHeight: Int
        get() {
            val metrics = Common.applicationContext.resources.displayMetrics
            return metrics.heightPixels
        }

    fun showKeyboard(view: View?) {
        if (view == null) {
            return
        }
        val inputManager = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)

        (view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).showSoftInput(view, 0)
    }

    fun isKeyboardShowed(view: View?): Boolean {
        if (view == null) {
            return false
        }
        val inputManager = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        return inputManager.isActive(view)
    }

    fun hideKeyboard(view: View?) {
        if (view == null) {
            return
        }
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (!imm.isActive) {
            return
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun clearCursorDrawable(editText: EditText?) {
        if (editText == null || Build.VERSION.SDK_INT < 12) {
            return
        }
        try {
            val mCursorDrawableRes = TextView::class.java.getDeclaredField("mCursorDrawableRes")
            mCursorDrawableRes.isAccessible = true
            mCursorDrawableRes.setInt(editText, 0)
        } catch (e: Exception) {
            Log.wtf("lifet", e)
        }

    }

    /**
     * Helper method to focus view on the center of the screen which is located inside of scrollview
     * @param scroll [ScrollView]
     * *
     * @param view a view to focus on
     */
    fun focusOnView(scroll: ScrollView?, view: View?) {
        if (scroll == null || view == null) {
            LOGE("memStor", "focus view is null")
            return
        }
        Handler().post {
            val vTop = view.top
            val vBottom = view.bottom
            val sHeight = scroll.height
            scroll.smoothScrollTo(0, (vTop + vBottom - sHeight) / 2)
        }
    }

    /**
     * Helper method to focus view on the center of the screen which is located inside of scrollview
     * @param scroll [ScrollView]
     * *
     * @param view a view to focus on
     */
    fun focusOnNestedView(scroll: NestedScrollView?, view: View?) {
        if (scroll == null || view == null) {
            LOGE("memStor", "focus view is null")
            return
        }
        Handler().post {
            val vTop = view.top
            val vBottom = view.bottom
            val sHeight = scroll.height
            scroll.smoothScrollTo(0, (vTop + vBottom - sHeight) / 2)
        }
    }

    @JvmOverloads fun runOnUiThread(runnable: Runnable, delay: Long = 0) {
        if (delay == 0L) {
            Common.applicationHandler.post(runnable)
        } else {
            Common.applicationHandler.postDelayed(runnable, delay)
        }
    }

    @JvmOverloads fun getTintDrawableResource(context: Context, resId: Int, tintColor: Int = Color.WHITE): Drawable {
        var drawable = ContextCompat.getDrawable(context, resId)
        drawable = DrawableCompat.wrap(drawable)
        DrawableCompat.setTint(drawable, tintColor)
        return drawable
    }

    fun getTintDrawable(drawable: Drawable): Drawable {
        return getTintedDrawable(drawable, Color.WHITE)
    }

    fun getTintedDrawable(drawable: Drawable, color: Int): Drawable {
        val tintedDrawable = DrawableCompat.wrap(drawable)
        DrawableCompat.setTint(tintedDrawable, color)
        return tintedDrawable
    }

     fun getBitmap(drawable: Drawable): Bitmap? {
        try {
            val bitmap: Bitmap
            bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)

            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            return bitmap
        } catch (e: OutOfMemoryError) {
            // Handle the error
            return null
        }

    }

    fun setBitmapColorFilter(fabDrawable: Drawable, filterColor: Int): Bitmap {

        val mBitmap = (fabDrawable as BitmapDrawable).bitmap.copy(Bitmap.Config.ARGB_8888, true)

        val pnt = Paint()
        val myCanvas = Canvas(mBitmap)

        val myColor = mBitmap.getPixel(0, 0)

        // Set the colour to replace.
        val filter = LightingColorFilter(myColor, filterColor)
        pnt.colorFilter = filter

        // Draw onto new bitmap. result Bitmap is newBit
        myCanvas.drawBitmap(mBitmap, 0f, 0f, pnt)
        return mBitmap
    }

    /**
     * Consider using [as it will return correct theme][.getBackgroundColor]
     * @return color value based on [android.R.attr.colorBackground] of base theme
     */
    val backgroundColor: Int
        get() {
            val theme = Common.applicationContext.theme
            val typedValue = TypedValue()
            theme.resolveAttribute(android.R.attr.colorBackground, typedValue, true)
            return typedValue.data
        }

    /**
     * Consider passing activities context to get correct current theme
     * @param context
     * *
     * @return color value based on [android.R.attr.colorBackground] of current theme
     */
    fun getBackgroundColor(context: Context): Int {
        val theme = context.theme
        val typedValue = TypedValue()
        theme.resolveAttribute(android.R.attr.colorBackground, typedValue, true)
        return typedValue.data
    }

    /**
     * Rrturns view background color if avaiable, Transparent one otherwise

     * @param view
     * *
     * @return color int representation
     */
    fun getBackgroundColor(view: View): Int {
        var color = Color.TRANSPARENT
        val background = view.background
        if (background is ColorDrawable)
            color = background.color
        return color
    }

    fun decodeSampledBitmapFromFile(file: String, reqWidth: Int, reqHeight: Int): Bitmap {
        // First decode with inJustDecodeBounds=true to check dimensions
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(file, options)
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeFile(file, options)
    }

    fun calculateImageHeight(path: String, itemWidth: Int): Int {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(path, options)
        var height = options.outHeight
        val width = options.outWidth
        val PHOTO_ASPECT_RATIO = height.toFloat() / width
        Log.d("sss", "PHOTO_ASPECT_RATIO= $PHOTO_ASPECT_RATIO height= $height width= $width")
        height = (itemWidth * PHOTO_ASPECT_RATIO).toInt()
        return height
    }

    /**

     * @param path
     * *
     * @param result int[2] array, first is height, second is width
     */
    fun getBitmapSize(path: String, result: IntArray) {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(path, options)
        result[0] = options.outHeight
        result[1] = options.outWidth
    }

    //Given the bitmap size and View size calculate a subsampling size (powers of 2)
    fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        var inSampleSize = 1    //Default subsampling size
        // See if image raw height and width is bigger than that of required view
        if (options.outHeight > reqHeight || options.outWidth > reqWidth) {
            //bigger
            val halfHeight = options.outHeight / 2
            val halfWidth = options.outWidth / 2
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize > reqHeight && halfWidth / inSampleSize > reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

}