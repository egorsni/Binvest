import com.ccfraser.muirwik.components.MTypographyVariant
import com.ccfraser.muirwik.components.mTypography
import react.*
import react.dom.*
import kotlinext.js.*
import kotlinx.coroutines.*
import kotlinx.css.*
import kotlinx.css.properties.Transforms
import kotlinx.html.InputType
import styled.css
import styled.styledDiv
import kotlin.math.min
import kotlin.math.round

external interface UserPanelProps : RProps {
    var username: String
}

val UserPanel = functionalComponent<UserPanelProps> { props ->
    val (userList, setUserList) = useState(mutableListOf<Case>())
    val (userBalance, setUserBalance) = useState(0.0)
    val (time, setTime) = useState("")
    useEffect(dependencies = listOf()) {
        GlobalScope.launch {
            setUserBalance(getUserBalance(props.username))
            setUserList(getUserList(props.username))
            console.log("0")
        }
    }
    styledDiv {
        styledDiv() {
            css {
                display = Display.flex
                marginBottom = 20.px
            }
            mTypography(variant = MTypographyVariant.h2, className = "classes.title") {
                +"Your balance: ${userBalance.round(2)}$"
            }
            styledDiv{
                css{
                    position = Position.absolute
                    right = 20.px
                }
                styledDiv{
                    css{
                        position = Position.relative
                        bottom = 5.px
                    }
                    child(
                        Input,
                        props = jsObject {
                            onSubmit = { input ->
                                GlobalScope.launch {
                                    val newCase = Case(symbol = input, price = getPrice(input).toDouble(), count = 0)
                                    setUserList((userList + newCase).toMutableList())
                                    addUserListCase(case = newCase, username = props.username)
                                }
                            }
                        }
                    )
                }
            }
        }
        div {
            key = userList.size.toString()
            for (i in 0 until (userList.size + 1) / 2) {
                var k = i * 2
                styledDiv {
                    css {
                        display = Display.flex
                    }
                    for (y in 0..min(1, userList.size - 1 - k)) {
                        child(
                            Box,
                            props = jsObject {
                                username = props.username
                                case = userList[k + y]
                                onBuyCase = { case ->
                                    userList.find { it.symbol == case.symbol }!!.count += 1
                                    setUserBalance(userBalance - case.price)
                                    GlobalScope.launch {
                                        setUserBalanceApi(newBalance = userBalance - case.price, username = props.username)
                                        setUserCase(case = case, username = props.username)
                                    }
                                }
                                onDelete = { case ->
                                    val deletecase = userList.find { it.symbol == case.symbol }!!
                                    setUserList((userList - deletecase).toMutableList())
                                    GlobalScope.launch {
                                        deleteUserCase(case = case, username = props.username)
                                    }
                                }
                                onSellCase = { case ->
                                    userList.find { it.symbol == case.symbol }!!.count -= 1
                                    setUserBalance(userBalance + case.price)
                                    GlobalScope.launch {
                                        setUserCase(case = case, username = props.username)
                                        setUserBalanceApi(newBalance = userBalance + case.price, username = props.username)
//                                        setUserListApi(userList)
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

fun Double.round(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return round(this * multiplier) / multiplier
}