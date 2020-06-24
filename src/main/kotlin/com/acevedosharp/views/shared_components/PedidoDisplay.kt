package com.acevedosharp.views.shared_components

import com.acevedosharp.entities.PedidoDB
import com.acevedosharp.ui_models.Pedido
import com.acevedosharp.views.modules.NewPedidoFormView
import com.acevedosharp.views.modules.PedidoSummaryView
import com.acevedosharp.views.modules.PedidoView
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.paint.Color
import javafx.scene.paint.LinearGradient
import javafx.scene.paint.Stop
import javafx.scene.text.FontWeight
import javafx.scene.text.TextAlignment
import org.springframework.data.repository.findByIdOrNull
import tornadofx.*
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*

class PedidoDisplay(val pedido: Pedido, val view: View) : Fragment() {
    override val root = vbox {
        prefWidth = 300.0
        prefHeight = 300.0
        stackpane {
            rectangle(width = 300, height = 200) {
                fill = LinearGradient(
                    1.0,
                    0.0,
                    1.0,
                    1.0,
                    true,
                    null,
                    listOf(
                        Stop(0.0000, c(169, 193, 215)),
                        Stop(0.0075, c(169, 193, 215)),
                        Stop(1.0000, c(36, 116, 191))
                    )
                )
            }
            text(pedido.proveedor.nombre) {
                wrappingWidth = 280.0
                alignment = Pos.CENTER
                textAlignment = TextAlignment.CENTER
                style {
                    fontSize = 28.px
                    fontWeight = FontWeight.BOLD
                }
            }
        }
        hbox {
            prefWidth = 300.0
            prefHeight = 100.0
            style {
                backgroundColor += c(31, 81, 128)
            }

            stackpane {
                rectangle(width = 150, height = 100) {
                    fill = c(31, 81, 128)
                }
                text(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").format(pedido.fechaHora)) {
                    wrappingWidth = 140.0
                    alignment = Pos.CENTER
                    textAlignment = TextAlignment.CENTER
                    fill = Color.WHITE
                    style { fontSize = 22.px }
                }
            }
            button("Ver más") {
                setPrefSize(150.0, 100.0)
                setMinSize(150.0, 100.0)
                style {
                    borderRadius += box(0.px)
                    backgroundRadius += box(0.px)
                    backgroundColor += c(255, 255, 255, 0.5)
                    fontSize = 24.px
                }
                action {
                    openInternalWindow<PedidoSummaryView>(
                        closeButton = false,
                        modal = true,
                        owner = view.root,
                        params = mapOf("pedido" to (view as PedidoView).pedidoService.repo.findByIdOrNull(pedido.id)!!))
                }
            }
        }
    }
}