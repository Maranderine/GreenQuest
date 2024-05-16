package de.hsb.greenquest

import android.app.Application
import de.hsb.greenquest.data.AppContainer
import de.hsb.greenquest.data.AppDataContainer

class GreenQuestApplication: Application() {

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}