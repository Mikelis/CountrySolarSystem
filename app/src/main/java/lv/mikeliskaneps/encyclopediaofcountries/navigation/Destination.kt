package lv.mikeliskaneps.encyclopediaofcountries.navigation


interface Destination {
    val route: String
}


object Home : Destination {
    override val route = "Home"
}

object Details : Destination {
    override val route = "Details"
}





