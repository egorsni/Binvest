import kotlinx.css.*
import react.*
import react.dom.*
import kotlinx.html.js.*
import styled.css
import styled.styledDiv
import com.ccfraser.muirwik.components.*
import com.ccfraser.muirwik.components.button.*
import com.ccfraser.muirwik.components.card.*
import com.ccfraser.muirwik.components.styles.ThemeOptions
import com.ccfraser.muirwik.components.transitions.mCollapse
import kotlinx.coroutines.*
import kotlinx.css.properties.Transform
import kotlinx.css.properties.Transforms
import styled.StyleSheet

external interface BoxProps : RProps {
    var onBuyCase: (Case) -> Unit
    var onDelete: (Case) -> Unit
    var onSellCase: (Case) -> Unit
    var case: Case
}

private val scope = MainScope()

val Box = functionalComponent<BoxProps> { props ->
    val (case, setCase) = useState(Case(props.case.symbol, 0.0, props.case.count))
    useEffect(dependencies = mutableListOf()) {
        scope.launch() {
            while (true) {
                setCase(getCase(props.case))
                console.log("2")
                delay(10000)
            }
        }
    }

    mCard {
        css {
            marginBottom = 20.px
            width = 40.pct
            height = 200.px
            position = Position.relative
//            border: 2px solid #D4D4D4;
//            border-radius: 30px;
//            box-shadow: 0 0 15px #A9A9A9;
//            padding:20px 40px;
            marginLeft = LinearDimension("auto")
            marginRight = LinearDimension("auto")
        }

        mCardContent {
            mTypography(
                variant = MTypographyVariant.h4,
                className = "classes.title",
                color = MTypographyColor.textSecondary
            ) {
                +case.symbol
            }
            mTypography(variant = MTypographyVariant.h3, component = "h2") {
                key = case.price.toString()
                if (case.price != 0.0) {
                    +"${case.price}$"
                }
            }
            styledDiv {
                css {
                    position = Position.absolute
                    bottom = 20.px
                    left = 20.px
                    display = Display.flex
                }
                styledDiv {
                    css {
                        paddingRight = 20.px
                    }
                    mButton(caption = "BUY") {
                        attrs {
                            variant = MButtonVariant.contained
                            color = MColor.primary
                            onClick = {
                                if (case.price != 0.0) {
                                    GlobalScope.launch {
                                        if (getUserBalance() >= case.price) {
                                            case.count += 1
                                        }
                                        props.onBuyCase(case)
                                    }
                                }
                            }
                        }
                    }
                }
                mButton(caption = "SELL", color = MColor.primary) {
                    attrs {
                        variant = MButtonVariant.contained
                        onClick = {
                            if (case.price != 0.0) {
                                GlobalScope.launch {
                                    if (getUserBalance() >= case.price) {
                                        case.count -= 1
                                    }
                                    props.onSellCase(case)
                                }
                            }
                        }
                    }
                }
            }

            styledDiv {
                css {
                    position = Position.absolute
                    top = 10.px
                    right = 10.px
                }
                mIconButton(size = MIconButtonSize.small) {
                    attrs {
                        img {
                            attrs {
                                width = "30px"
                                height = "30px"
                                src =
                                    "data:image/png;base64,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"
                            }
                            onClick = {
                                props.onDelete(case)
                            }
                        }
                    }
                }
            }
            styledDiv {
                css {
                    position = Position.absolute
                    right = 10.px
                    bottom = 20.px
                    width = 36.px
                    textAlign = TextAlign.center
                }
                mTypography(
                    variant = MTypographyVariant.h6,
                    className = "classes.title",
                    color = MTypographyColor.textSecondary
                ) {
                    +"${case.count}"
                    attrs.align = MTypographyAlign.center
                }
            }
        }
    }
}