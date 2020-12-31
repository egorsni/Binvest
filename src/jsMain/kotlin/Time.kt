import kotlinx.browser.window
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.css.*
import react.*
import react.dom.*
import kotlinx.html.js.*
import kotlinx.html.InputType
import org.w3c.dom.events.Event
import org.w3c.dom.HTMLInputElement
import styled.css
import styled.styledDiv


private val scope = MainScope()

val TimeComponent = functionalComponent<InputProps> { props ->
    val (time, setTime) = useState("props.case")
    useEffect(dependencies = mutableListOf()) {
        GlobalScope.launch {
            while(true){
                setTime(getTime())
                delay(1000)
            }
        }
    }
//    h1{
//        key = case.price.toString()
//        +"${case.symbol} ${case.price}"
//    }
    div(classes = "Box"){
        +"$time"
    }
//    val (text, setText) = useState("")
//
//    val submitHandler: (Event) -> Unit = {
//        it.preventDefault()
//        setText("")
//        props.onSubmit(text)
//    }
//
//    val changeHandler: (Event) -> Unit = {
//        val value = (it.target as HTMLInputElement).value
//        setText(value)
//    }
//
//    form {
//        attrs.onSubmitFunction = submitHandler
//        input(InputType.text) {
//            attrs.onChangeFunction = changeHandler
//            attrs.value = text
//        }
//    }
}