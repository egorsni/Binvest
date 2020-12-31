import com.ccfraser.muirwik.components.MTypographyColor
import com.ccfraser.muirwik.components.MTypographyVariant
import com.ccfraser.muirwik.components.mTypography
import react.*
import react.dom.*
import kotlinext.js.*
import kotlinx.browser.window
import kotlinx.coroutines.*
import kotlinx.css.*
import styled.css
import styled.styledDiv
import kotlin.math.min
import kotlin.math.round


private val scope = MainScope()

external interface AppState : RState {
    var userList: MutableList<Case>
    var balance: Double
}

val App = functionalComponent<RProps> { _ ->
    val (userList, setUserList) = useState(mutableListOf<Case>())
    val (userBalance, setUserBalance) = useState(0.0)
    val (time, setTime) = useState("")
    useEffect(dependencies = listOf()) {
        GlobalScope.launch {
            setUserBalance(getUserBalance())
            setUserList(getUserList())
        }
    }
    styledDiv(){
        css{
            display = Display.flex
        }
        mTypography(variant = MTypographyVariant.h2, className = "classes.title") {
            +"Your balance: ${userBalance.round(2)}$"
            css{
                paddingBottom = 20.px
                width = 650.px
            }
        }
        styledDiv{
            css{
                position = Position.absolute
                top = 10.px
                right = 10.px
            }
            div(classes = "input"){
                child(
                    Input,
                    props = jsObject {
                        onSubmit = { input ->
                            GlobalScope.launch {
                                val newCase = getCase(Case(symbol = input, count = 0))
                                setUserList((userList + newCase).toMutableList())
                                addUserListCase(case = newCase)
                            }
                        }
                    }
                )
            }
        }
    }
    div{
        key = userList.toString()
        for (i in 0 until (userList.size + 1)/2){
            var k = i*2
            styledDiv {
                css{
                    display = Display.flex
                }
                for (y in 0..min(1,userList.size - 1 - k)){
                    child(
                        Box,
                        props = jsObject{
                            case = userList[k + y]
                            onBuyCase = { case ->
                                if (userBalance - case.price >= 0) {
                                    setUserBalance(userBalance - case.price)
                                    GlobalScope.launch {
                                        setUserBalanceApi(newBalance = userBalance - case.price)
                                        setUserCase(case = case)
                                    }
                                }
                                else{
                                    window.alert("No money for buying")
                                }
                            }
                            onDelete = { case ->
                                if (case.count == 0){
                                    setUserList((userList - case).toMutableList())
                                    GlobalScope.launch {
                                        deleteUserCase(case = case)
                                    }
                                } else {
                                    window.alert("Sell all before deleting")
                                }
                            }
                            onSellCase = { case ->
                                if (case.count >= 0) {
                                    setUserBalance(userBalance + case.price)
                                    GlobalScope.launch {
                                        setUserCase(case = case)
                                        setUserBalanceApi(newBalance = userBalance + case.price)
//                                        setUserListApi(userList)
                                    }
                                }
                                else{
                                    window.alert("Nothing to sell")
                                }
                            }
                        }
                    )
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