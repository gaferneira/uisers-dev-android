package co.tuister.uisers.utils.analytics

import android.app.Activity

interface Analytics {

    fun trackScreenView(activity: Activity, screenName: String, screenClassName: String)

    fun trackUserLogin()

    fun trackUserLogout()

    fun trackUserSignUp()

    fun trackForgotPassword()

    fun trackEvent(eventName: String)

    companion object {

        const val EVENT_LOGIN = "login"
        const val EVENT_CLICK_LOGIN = "login_click"
        const val EVENT_LOGOUT = "logout"
        const val EVENT_SIGN_UP = "sign_up"
        const val EVENT_FORGOT_PASSWORD = "forgot_password"

        const val EVENT_CLICK_HOME = "menu_home"
        const val EVENT_CLICK_MY_CAREER = "menu_my_career"
        const val EVENT_CLICK_TASKS = "menu_tasks"
        const val EVENT_CLICK_INSTITUTION = "menu_Institution"

        const val EVENT_CLICK_DRAWER_MENU = "drawer_menu"
        const val EVENT_CLICK_PROFILE = "option_profile"
        const val EVENT_CLICK_ABOUT = "option_about"
        const val EVENT_CLICK_FEEDBACK = "option_Feedback"
        const val EVENT_CLICK_THEME = "option_change_theme"

        const val EVENT_CLICK_MAP = "option_map"
        const val EVENT_CLICK_CALENDAR = "option_calendar"
        const val EVENT_CLICK_WHEELS = "option_Wheels"
        const val EVENT_CLICK_OPEN_WHEELS = "action_open_wheels"
    }
}
