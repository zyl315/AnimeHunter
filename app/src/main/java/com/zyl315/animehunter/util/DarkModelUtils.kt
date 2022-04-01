import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate


object DarkModeUtils {
    private const val KEY_CURRENT_MODEL = "night_mode_state_sp"
    private fun getNightModel(context: Context): Int {
        val sp: SharedPreferences =
            context.getSharedPreferences(KEY_CURRENT_MODEL, Context.MODE_PRIVATE)
        return sp.getInt(KEY_CURRENT_MODEL, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }

    private fun setNightModel(context: Context, nightMode: Int) {
        val sp: SharedPreferences =
            context.getSharedPreferences(KEY_CURRENT_MODEL, Context.MODE_PRIVATE)
        sp.edit().putInt(KEY_CURRENT_MODEL, nightMode).apply()
    }

    /**
     * ths method should be called in Application onCreate method
     *
     * @param application application
     */
    fun init(application: Application) {
        val nightMode = getNightModel(application)
        AppCompatDelegate.setDefaultNightMode(nightMode)
    }

    /**
     * 应用夜间模式
     */
    fun applyNightMode(context: Context) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        setNightModel(context, AppCompatDelegate.MODE_NIGHT_YES)
    }

    /**
     * 应用日间模式
     */
    fun applyDayMode(context: Context) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setNightModel(context, AppCompatDelegate.MODE_NIGHT_NO)
    }

    /**
     * 跟随系统主题时需要动态切换
     */
    fun applySystemMode(context: Context) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        setNightModel(context, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }

    /**
     * 判断App当前是否处于暗黑模式状态
     *
     * @param context 上下文
     * @return 返回
     */
    fun isDarkMode(context: Context): Boolean {
        val nightMode = getNightModel(context)
        return if (nightMode == AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM) {
            val applicationUiMode: Int = context.resources.configuration.uiMode
            val systemMode = applicationUiMode and Configuration.UI_MODE_NIGHT_MASK
            systemMode == Configuration.UI_MODE_NIGHT_YES
        } else {
            nightMode == AppCompatDelegate.MODE_NIGHT_YES
        }
    }
}