package lv.mikeliskaneps.encyclopediaofcountries.navigation

sealed class NavigationEvent {
    data class Navigate(val destination: Destination) : NavigationEvent()
}