package xyz.acevedosharp.views.shared_components

import xyz.acevedosharp.views.MainStylesheet
import javafx.geometry.Pos
import xyz.acevedosharp.views.helpers.CurrentModule
import xyz.acevedosharp.views.helpers.CurrentModule.*
import xyz.acevedosharp.views.screens.*
import tornadofx.*

class SideNavigation(currentModule: CurrentModule, root: View): Fragment() {
    override val root = vbox(alignment = Pos.TOP_CENTER) {
        rectangle(width = 0, height = 40)
        imageview("images/epic_logo.png") {
            fitWidth = 150.0
            fitHeight = 150.0
        }
        rectangle(width = 0, height = 25)
        line(startX = 0, endX = 185).style {
            stroke = c(255, 255, 255, 0.35)
        }
        button("PdV") {
            val tag = PUNTO_DE_VENTA
            addClass(MainStylesheet.navigationButton, if (tag == currentModule) MainStylesheet.selectedButton else MainStylesheet.unselectedButton)
            graphic = imageview("images/store_logo_icon.png") {
                fitWidth = 50.0
                fitHeight = 50.0
            }
            action {
                root.replaceWith(PuntoDeVentaView())
            }
        }
        line(startX = 0, endX = 175).style {
            stroke = c(255, 255, 255, 0.25)
        }
        button("Productos") {
            tag = PRODUCTOS
            addClass(MainStylesheet.navigationButton, if (tag == currentModule) MainStylesheet.selectedButton else MainStylesheet.unselectedButton)
            graphic = imageview("images/products.png") {
                fitWidth = 50.0
                fitHeight = 50.0
            }
            action {
                root.replaceWith(ProductoView())
            }
        }
        button("Pedidos") {
            tag = PEDIDOS
            addClass(MainStylesheet.navigationButton, if (tag == currentModule) MainStylesheet.selectedButton else MainStylesheet.unselectedButton)
            graphic = imageview("images/products.png") {
                fitWidth = 50.0
                fitHeight = 50.0
            }
            action {
                root.replaceWith(PedidoView())
            }
        }
        line(startX = 0, endX = 175).style {
            stroke = c(255, 255, 255, 0.25)
        }
        button("Familias") {
            tag = FAMILIAS
            addClass(MainStylesheet.navigationButton, if (tag == currentModule) MainStylesheet.selectedButton else MainStylesheet.unselectedButton)
            graphic = imageview("images/products.png") {
                fitWidth = 50.0
                fitHeight = 50.0
            }
            action {
                root.replaceWith(FamiliaView())
            }
        }
        button("Proveedores") {
            tag = PROVEEDORES
            addClass(MainStylesheet.navigationButton, if (tag == currentModule) MainStylesheet.selectedButton else MainStylesheet.unselectedButton)
            graphic = imageview("images/products.png") {
                fitWidth = 50.0
                fitHeight = 50.0
            }
            action {
                root.replaceWith(ProveedorView())
            }
        }
        button("Empleados") {
            tag = EMPLEADOS
            addClass(MainStylesheet.navigationButton, if (tag == currentModule) MainStylesheet.selectedButton else MainStylesheet.unselectedButton)
            graphic = imageview("images/products.png") {
                fitWidth = 50.0
                fitHeight = 50.0
            }
            action {
                root.replaceWith(EmpleadoView())
            }
        }
        button("Clientes") {
            tag = CLIENTES
            addClass(MainStylesheet.navigationButton, if (tag == currentModule) MainStylesheet.selectedButton else MainStylesheet.unselectedButton)
            graphic = imageview("images/products.png") {
                fitWidth = 50.0
                fitHeight = 50.0
            }
            action {
                root.replaceWith(ClienteView())
            }
        }

        style {
            backgroundColor += c(21, 55, 83)
        }
    }
}