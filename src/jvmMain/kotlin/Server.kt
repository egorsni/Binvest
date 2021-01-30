import com.mongodb.ConnectionString
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.sessions.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.litote.kmongo.MongoOperator
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.eq
import org.litote.kmongo.reactivestreams.KMongo
import org.litote.kmongo.setValue
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.*
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.sessions.*
import javax.security.sasl.AuthenticationException
import kotlin.collections.set


val client = KMongo.createClient().coroutine
val database = client.getDatabase("shoppingList")
val collection = database.getCollection<User>()
fun main() {
    embeddedServer(Netty, 9090) {
        install(ContentNegotiation) {
            json()
        }
        install(CORS) {
            method(HttpMethod.Get)
            method(HttpMethod.Post)
            method(HttpMethod.Delete)
            anyHost()
        }
        install(Compression) {
            gzip()
        }
        install(Sessions) { //Enable Sessions
            cookie<UserIdPrincipal>( //Use UserIdPrincipal as user data principal container, and identifying it via cookies
                "auth", // Just String constant with value: const val AUTH_COOKIE = "auth"
                storage = SessionStorageMemory() // We use default in-memory storage, but you can write your own realization and store sessions whereever you wish
            ) {
                cookie.path = "/" // We cookies should work
                cookie.extensions["SameSite"] = "lax"
            }
        }

        routing {
            get("/") {
                call.respondText(
                    this::class.java.classLoader.getResource("index.html")!!.readText(),
                    ContentType.Text.Html
                )
            }
            static("/") {
                resources("")
            }

            get("/id_{name}") {
                call.respondText(
                    this::class.java.classLoader.getResource("index.html")!!.readText(),
                    ContentType.Text.Html
                )
            }
            static("/id_{name}") {
                resources("")
            }

            get("/shop") {
                call.respondText(
                    this::class.java.classLoader.getResource("index.html")!!.readText(),
                    ContentType.Text.Html
                )
            }
            static("/shop") {
                resources("")
            }

            route("/case/{username}") {
                post {
                    val username = call.parameters["username"]?.toString() ?: error("Invalid delete request")
                    val user = collection.findOne(User::name eq username)!!
                    user.cases.add(call.receive<Case>())
                    collection.updateOne(User::name eq username, user)
                    call.respond(HttpStatusCode.OK)
                }
                delete() {
                    val username = call.parameters["username"]?.toString() ?: error("Invalid delete request")
                    val user = collection.findOne(User::name eq username)!!
                    val symb = call.receive<Case>().symbol
                    user.cases.removeIf { it.symbol == symb }
                    collection.updateOne(User::name eq username, user)
                    call.respond(HttpStatusCode.OK)
                }
            }
            route("/list/{username}") {
                get {
                    val username = call.parameters["username"]?.toString() ?: error("Invalid delete request")
                    val list = collection.findOne(User::name eq username)!!.cases
                    call.respond(list)
                }
            }
            route("/transaction") {
                post("/{username}") {
                    val username = call.parameters["username"]?.toString() ?: error("Invalid delete request")
                    val newCase = call.receive<Case>()
                    val list = collection.findOne(User::name eq username)!!.cases
                    for (case in list) {
                        if (case.symbol == newCase.symbol) {
                            case.count = newCase.count
                        }
                    }
                    collection.updateOne(
                        User::name eq username,
                        setValue(User::cases, list)
                    )
                    call.respond(HttpStatusCode.OK)
                }
            }
            route("/balance/{username}") {
                get {
                    val username = call.parameters["username"]?.toString() ?: error("Invalid delete request")
                    call.respond(collection.findOne(User::name eq username)!!.balance)
                }
                post {
                    val username = call.parameters["username"]?.toString() ?: error("Invalid delete request")
                    val user = collection.findOne(User::name eq username)!!
                    user.balance = call.receive<Double>()
                    collection.updateOne(User::name eq username, user)
                    call.respond(HttpStatusCode.OK)
                }
            }
            route("/user") {
                post {
                    collection.insertOne(call.receive<User>())
                    call.respond(HttpStatusCode.OK)
                }
                delete{
                    val username = call.receive<User>().name
                    collection.deleteOne(User::name eq username)
                    call.respond(HttpStatusCode.OK)
                }
            }
            route("/list") {
                get {
                    call.respond(collection.find().toList())
                }
            }
            route("/check"){
                post{
                    val user = call.receive<User>()
                    val myuser = collection.findOne(User::name eq user.name) ?: error("Invalid delete request")
                    if(myuser.password == user.password) {
                        call.respond(true)
                    } else{
                        call.respond(false)
                    }
                }
            }
        }
    }.start(wait = true)
}