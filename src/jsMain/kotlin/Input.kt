import com.ccfraser.muirwik.components.mTextField
import kotlinx.css.*
import react.RProps
import react.functionalComponent
import react.*
import react.dom.*
import kotlinx.html.js.*
import kotlinx.html.InputType
import org.w3c.dom.events.Event
import org.w3c.dom.HTMLInputElement
import styled.css
import styled.styledDiv

external interface InputProps : RProps {
    var onSubmit: (String) -> Unit
}

val Input = functionalComponent<InputProps> { props ->
    val (text, setText) = useState("")

    val submitHandler: (Event) -> Unit = {
        it.preventDefault()
        setText("")
        props.onSubmit(text)
    }

    val changeHandler: (Event) -> Unit = {
        val value = (it.target as HTMLInputElement).value
        setText(value)
    }

        form (classes = "form"){
            attrs.onSubmitFunction = submitHandler
            mTextField(required = false, label ="Write company symbol", id = "standard-basic", fullWidth = true) {
                attrs.onChange = changeHandler
                attrs.value = text
            }
        }
}