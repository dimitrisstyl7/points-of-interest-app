package dimstyl.pointsofinterest.ui.navigation

import kotlinx.serialization.Serializable

enum class NavRoute {

    PLACES,
    FAVORITES;

    fun getRoute(): Any = when (this) {
        PLACES -> Places
        FAVORITES -> Favorites
    }

    override fun toString(): String =
        when (this) {
            PLACES -> "Your Places"
            FAVORITES -> "Favorites"
        }

}

@Serializable
object Favorites

@Serializable
object Places