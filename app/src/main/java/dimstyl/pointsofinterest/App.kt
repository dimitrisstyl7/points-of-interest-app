package dimstyl.pointsofinterest

import android.app.Application
import dimstyl.pointsofinterest.di.AppModule

class App : Application() {

    companion object {
        lateinit var appModule: AppModule
    }

    override fun onCreate() {
        super.onCreate()
        appModule = AppModule(this)
    }

}