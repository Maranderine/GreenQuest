package de.hsb.greenquest

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.Executors


@HiltAndroidApp
class MyApp: Application(){
    var executorService = Executors.newFixedThreadPool(4)
}