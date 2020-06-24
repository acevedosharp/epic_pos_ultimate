package com.acevedosharp.ui_models

import javafx.beans.property.SimpleObjectProperty
import tornadofx.*
import java.time.LocalDateTime

class Pedido(var id: Int?, fechaHora: LocalDateTime, proveedor: Proveedor, empleado: Empleado) {
    val fechaHoraProperty = SimpleObjectProperty<LocalDateTime>(this, "fechaHora", fechaHora)
    var fechaHora by fechaHoraProperty

    val proveedorProperty = SimpleObjectProperty<Proveedor>(this, "proveedor", proveedor)
    var proveedor by proveedorProperty

    val empleadoProperty = SimpleObjectProperty<Empleado>(this, "empleado", empleado)
    var empleado by empleadoProperty
}

class PedidoModel: ItemViewModel<Pedido>() {
    val id =        bind(Pedido::id)
    val fechaHora = bind(Pedido::fechaHoraProperty)
    val proveedor = bind(Pedido::proveedorProperty)
    val empleado =  bind(Pedido::empleadoProperty)
}