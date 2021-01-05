import io.ktor.http.*
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer

import kotlinx.browser.window

val endpoint = window.location.origin // only needed until https://github.com/ktorio/ktor/issues/1695 is resolved

val jsonClient = HttpClient {
    install(JsonFeature) { serializer = KotlinxSerializer() }
}

val twelveClient = HttpClient {
    install(JsonFeature) { serializer = KotlinxSerializer() }
    defaultRequest { // this: HttpRequestBuilder ->
        method = HttpMethod.Get
        headers.append("x-rapidapi-key", "8ae5233fd2msh2fa90d14987f249p17283djsnafba9eaec335")
        headers.append("x-rapidapi-host", "twelve-data1.p.rapidapi.com")
    }
}

suspend fun getTime(): String {
    return jsonClient.get("http://worldclockapi.com/api/json/est/now")
}

suspend fun getPrice(symbol: String) = twelveClient.get<Data>("https://twelve-data1.p.rapidapi.com/price?symbol=${symbol}&outputsize=30&format=json").price.toString()

suspend fun getUserBalance(username: String = "egorsni"): Double {
    return jsonClient.get("$endpoint/balance/${username}")
}

suspend fun setUserBalanceApi(username: String = "egorsni", newBalance: Double) {
    jsonClient.post<Unit>("$endpoint/balance/${username}") {
        contentType(ContentType.Application.Json)
        body = newBalance
    }
}

suspend fun getUserList(username: String = "egorsni"): MutableList<Case> {
    val list = jsonClient.get<MutableList<Case>>("$endpoint/list/${username}")
    return list
}

suspend fun addUserListCase(username: String = "egorsni", case: Case) {
    jsonClient.post<Unit>("$endpoint/case/${username}") {
        contentType(ContentType.Application.Json)
        body = case
    }
}

suspend fun setUserCase(username: String = "egorsni", case: Case) {
    jsonClient.post<Unit>("$endpoint/transaction/${username}") {
        contentType(ContentType.Application.Json)
        body = case
    }
}

suspend fun deleteUserCase(username: String = "egorsni", case: Case) {
    jsonClient.delete<Unit>("$endpoint/case/${username}") {
        contentType(ContentType.Application.Json)
        body = case
    }
}