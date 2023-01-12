package com.example.myapplication

import android.app.Application
import com.lokalise.sdk.Lokalise

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Lokalise.init(
            this,
            "965ad69726043171e32d8bd4ca7e2474a71e5c33",
            "585664596234604f7151d3.63186061"
        )
        Lokalise.isPreRelease = true
        Lokalise.updateTranslations()
    }
}