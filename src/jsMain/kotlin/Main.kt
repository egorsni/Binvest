import react.child
import react.dom.render
import kotlinx.browser.document
import kotlinx.css.paddingTop
import kotlinx.css.px
import styled.*

fun main() {
    render(document.getElementById("root")) {
        child(App)
    }
}