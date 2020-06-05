package co.tuister.uisers.modules.institutional

import co.tuister.uisers.R
import co.tuister.uisers.common.BaseViewModel
import co.tuister.uisers.utils.Result

class InstitutionalViewModel() : BaseViewModel() {

    fun initialize() {
        val menus = listOf(
            InstitutionalMenu(
                R.string.title_fragment_map,
                R.drawable.institutional_menu_map,
                R.color.grey_600
            ),
            InstitutionalMenu(
                R.string.title_fragment_calendar,
                R.drawable.institutional_menu_calendar,
                R.color.green_600
            ),
            InstitutionalMenu(
                R.string.title_fragment_publish,
                R.drawable.icon_add,
                R.color.green_600
            ),
            InstitutionalMenu(
                R.string.title_fragment_wheels,
                R.drawable.institutional_menu_wheels,
                R.color.blue_600
            )
        )
        setState(InstitutionalState.LoadItems(Result.Success(menus)))
    }
}
