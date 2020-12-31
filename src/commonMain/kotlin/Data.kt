import kotlinx.serialization.Serializable

@Serializable
data class Data(val price: String) {
    val curPrice = price.toDouble()
}