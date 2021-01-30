import io.ktor.http.*
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer

import kotlinx.browser.window

val endpoint = window.location.origin // only needed until https://github.com/ktorio/ktor/issues/1695 is resolved

val jsonClient = HttpClient {
    install(JsonFeature) {
        serializer = KotlinxSerializer()
        accept(ContentType.Application.Json)
        accept(ContentType("application", "json-rpc"))// I'd extract it to a top-level property
    }
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
    return jsonClient.get<Time>("https://api.ipgeolocation.io/timezone?apiKey=a847d016cb034e5b83716c1b4c2c3b5b&tz=America/Los_Angeles").date_time_unix.toString()
}

suspend fun getList(): RestaurantsList {
    val list : RestaurantsList = jsonClient.get<RestaurantsList>{url("https://raw.githubusercontent.com/woltapp/summer2021-internship/main/restaurants.json")}
    return list
}

suspend fun getPrice(symbol: String) = twelveClient.get<Data>("https://twelve-data1.p.rapidapi.com/price?symbol=${symbol}&outputsize=30&format=json").price.toString()

suspend fun getUserBalance(username: String): Double {
    return jsonClient.get("$endpoint/balance/${username}")
}

suspend fun setUserBalanceApi(username: String, newBalance: Double) {
    jsonClient.post<Unit>("$endpoint/balance/${username}") {
        contentType(ContentType.Application.Json)
        body = newBalance
    }
}

suspend fun isPasswordCorrect(username: String, password: String): Boolean {
    val u = User(name = username, password = password, balance = 10000.0)
    return jsonClient.post("$endpoint/check") {
        contentType(ContentType.Application.Json)
        body = u
    }
}

suspend fun getUserList(username: String): MutableList<Case> {
    val list = jsonClient.get<MutableList<Case>>("$endpoint/list/${username}")
    return list
}

suspend fun addUserListCase(username: String, case: Case) {
    jsonClient.post<Unit>("$endpoint/case/${username}") {
        contentType(ContentType.Application.Json)
        body = case
    }
}

suspend fun setUserCase(username: String, case: Case) {
    jsonClient.post<Unit>("$endpoint/transaction/${username}") {
        contentType(ContentType.Application.Json)
        body = case
    }
}

suspend fun addUser(user: User) {
    jsonClient.post<Unit>("$endpoint/user") {
        contentType(ContentType.Application.Json)
        body = user
    }
}

suspend fun deleteUserCase(username: String , case: Case) {
    jsonClient.delete<Unit>("$endpoint/case/${username}") {
        contentType(ContentType.Application.Json)
        body = case
    }
}