package dimstyl.pointsofinterest.ui.navigation

import kotlinx.serialization.Serializable

enum class NavRoute {

    PLACES,
    FAVOURITES;

    fun getRoute(): Any = when (this) {
        PLACES -> Places
        FAVOURITES -> Favourites
    }

    override fun toString(): String =
        when (this) {
            PLACES -> "Places"
            FAVOURITES -> "Favourites"
        }

}

@Serializable
object Favourites

@Serializable
object Places