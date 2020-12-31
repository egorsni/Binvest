import kotlinx.serialization.Serializable

@Serializable
data class Case(val symbol: String, var price: Double = 0.0, var count: Int = 0)