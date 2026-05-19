package com.ifpr.androidapptemplate.service

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast

class AppBlockAccessibilityService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val packageName = event?.packageName?.toString()

        Log.d("APPBLOCK", "App aberto: $packageName")
        Toast.makeText(this, packageName, Toast.LENGTH_SHORT).show()
    }

    override fun onInterrupt() {
    }
}