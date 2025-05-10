package dimstyl.pointsofinterest.ui.navigation

import kotlinx.serialization.Serializable

enum class NavRoute {

    DISCOVERIES,
    FAVORITES;

    fun getRoute(): Any = when (this) {
        DISCOVERIES -> Discoveries
        FAVORITES -> Favorites
    }

    override fun toString(): String =
        when (this) {
            DISCOVERIES -> "Your Discoveries"
            FAVORITES -> "Favorites"
        }

}

@Serializable
object Favorites

@Serializable
object Discoveries