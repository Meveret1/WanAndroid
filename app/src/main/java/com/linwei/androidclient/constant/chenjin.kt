package com.linwei.androidclient.constant

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.annotation.RequiresApi
import java.lang.reflect.Field
import java.lang.reflect.Method

object chenjin {

    @RequiresApi(Build.VERSION_CODES.M)
    fun setStatusBarTranslucent(activity: Activity?, isLightStatusBar: Boolean) {
        if (activity == null) return
        val window = activity.window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
        }
        if (isXiaomi) {
            setXiaomiStatusBar(window, isLightStatusBar)
        } else if (isMeizu) {
            setMeizuStatusBar(window, isLightStatusBar)
        }
    }

    // 是否是小米手机
    val isXiaomi: Boolean = "Xiaomi" == Build.MANUFACTURER

    // 设置小米状态栏
    @SuppressLint("PrivateApi")
    fun setXiaomiStatusBar(window: Window, isLightStatusBar: Boolean) {
        val clazz: Class<out Window> = window.javaClass
        try {
            val layoutParams = Class.forName("android.view.MiuiWindowManager\$LayoutParams")
            val field: Field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE")
            val darkModeFlag: Int = field.getInt(layoutParams)
            val extraFlagField: Method = clazz.getMethod(
                "setExtraFlags",
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType
            )
            extraFlagField.invoke(
                window,
                if (isLightStatusBar) darkModeFlag else 0,
                darkModeFlag
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // 是否是魅族手机
    val isMeizu: Boolean
        get() {
            try {
                val method: Method = Build::class.java.getMethod("hasSmartBar")
                return method != null
            } catch (e: NoSuchMethodException) {
            }
            return false
        }

    // 设置魅族状态栏
    fun setMeizuStatusBar(window: Window, isLightStatusBar: Boolean) {
        val params = window.attributes
        try {
            val darkFlag: Field =
                WindowManager.LayoutParams::class.java.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON")
            val meizuFlags: Field =
                WindowManager.LayoutParams::class.java.getDeclaredField("meizuFlags")
            darkFlag.setAccessible(true)
            meizuFlags.setAccessible(true)
            val bit: Int = darkFlag.getInt(null)
            var value: Int = meizuFlags.getInt(params)
            value = if (isLightStatusBar) {
                value or bit
            } else {
                value and bit.inv()
            }
            meizuFlags.setInt(params, value)
            window.attributes = params
            darkFlag.setAccessible(false)
            meizuFlags.setAccessible(false)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}