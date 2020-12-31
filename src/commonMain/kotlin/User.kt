import kotlinx.serialization.Serializable

@Serializable
data class User(val name: String, var balance: Double, var cases: MutableList<Case> = mutableListOf())