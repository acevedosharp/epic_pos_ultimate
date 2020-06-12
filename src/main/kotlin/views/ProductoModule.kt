package views

import controllers.ProductoController
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import javafx.scene.text.TextAlignment
import misc.FormType
import misc.FormType.*
import models.CurrentModule.*
import models.Producto
import models.ProductoModel
import styles.MainStylesheet
import tornadofx.*

class ProductosView : View("Módulo de productos") {

    private val productoController = find<ProductoController>()
    private val model: ProductoModel by inject()
    private val existsSelection = SimpleBooleanProperty(false)
    private val searchByCodigo = SimpleStringProperty()
    private val searchByDescripcion = SimpleStringProperty()
    private var table: TableView<Producto> by singleAssign()
    private val view = this

    init {
        searchByCodigo.onChange {
            searchByDescripcion.value = ""
            table.items = FXCollections.observableArrayList(productoController.productos.filter { it.codigo.toLowerCase().contains(searchByCodigo.value.toLowerCase()) })
        }
        searchByDescripcion.onChange {
            searchByCodigo.value = ""
            table.items = FXCollections.observableArrayList(productoController.productos.filter {
                it.descLarga.toLowerCase().contains(searchByDescripcion.value.toLowerCase()) || it.descCorta.toLowerCase().contains(searchByDescripcion.value.toLowerCase())
            })
        }
    }

    override val root = hbox {
        setPrefSize(1920.0, 1080.0)
        add(SideNavigation(PRODUCTOS, view))
        borderpane {
            setPrefSize(1720.0, 1080.0)
            top {
                hbox {
                    addClass(MainStylesheet.topBar)
                    paddingBottom = 4
                    useMaxWidth = true
                    button("Nuevo Producto") {
                        addClass(MainStylesheet.coolBaseButton)
                        addClass(MainStylesheet.greenButton)
                        action {
                            openInternalWindow<NewProductoFormView>(closeButton = false, modal = true)
                        }
                    }
                    button("Editar selección") {
                        enableWhen(existsSelection)
                        addClass(MainStylesheet.coolBaseButton)
                        addClass(MainStylesheet.blueButton)
                        action {
                            openInternalWindow<EditProductoFormView>(closeButton = false, modal = true)
                        }
                    }
                    button("Eliminar selección") {
                        enableWhen(existsSelection)
                        addClass(MainStylesheet.coolBaseButton)
                        addClass(MainStylesheet.redButton)
                        action {
                            openInternalWindow<ConfirmDeleteProductoDialog>(
                                closeButton = false,
                                modal = true,
                                params = mapOf("selected" to table.selectedItem)
                            )
                        }
                    }
                    rectangle(width = 10, height = 0)
                    line(0, 0, 0, 35).style {
                        stroke = c(255, 255, 255, 0.25)
                    }
                    rectangle(width = 10, height = 0)
                    hbox(spacing = 10, alignment = Pos.CENTER) {
                        vbox {
                            label("Buscar por código").apply { addClass(MainStylesheet.searchLabel) }
                            textfield(searchByCodigo)

                            prefWidth = 250.0
                        }
                        vbox {
                            label("Buscar por descripción").apply { addClass(MainStylesheet.searchLabel) }
                            textfield(searchByDescripcion)

                            prefWidth = 250.0
                        }
                    }
                }

            }

            center {
                hbox {
                    table = tableview(productoController.productos) {
                        column("Código", Producto::codigoProperty)
                        column("Desc. Larga", Producto::descLargaProperty).remainingWidth()
                        column("Desc. Corta", Producto::descCortaProperty).pctWidth(20)
                        column("Precio Venta", Producto::precioVentaProperty)
                        column("Existencias", Producto::existenciasProperty)
                        column("Ver pedidos", Producto::verPedidosButton).style {
                            alignment = Pos.CENTER
                            textAlignment = TextAlignment.CENTER
                            tileAlignment = Pos.CENTER
                        }

                        smartResize()

                        bindSelected(model)
                        selectionModel.selectedItemProperty().onChange {
                            existsSelection.value = it != null
                        }

                        hgrow = Priority.ALWAYS
                    }
                    paddingAll = 6
                    style {
                        backgroundColor += Color.WHITE
                    }
                }
            }

            style {
                backgroundColor += Color.WHITE
            }
        }
    }
}

class BaseProductoFormField(formType: FormType): Fragment() {

    private val model = if (formType == CREATE) ProductoModel() else find(ProductoModel::class)

    override val root = vbox(spacing = 0) {
        useMaxSize = true
        label(if (formType == CREATE) "Nuevo producto" else "Editar producto") {
            useMaxWidth = true
            addClass(MainStylesheet.titleLabel)
            addClass(if (formType == CREATE) MainStylesheet.greenLabel else MainStylesheet.blueLabel)
        }
        form {
            fieldset {
                field("Código") {
                    textfield(model.codigo).validator {
                        when {
                            it.isNullOrBlank() -> error("Código requerido")
                            it.length > 10 -> error("Máximo 10 caracteres (${it.length})")
                            else -> null
                        }
                    }
                }
                field("Descripción larga") {
                    textfield(model.descLarga).validator {
                        when {
                            it.isNullOrBlank() -> error("Descripción larga requerida")
                            it.length > 50 -> error("Máximo 50 caracteres (${it.length})")
                            else -> null
                        }
                    }
                }
                field("Descripción corta") {
                    textfield(model.descCorta).validator {
                        when {
                            it.isNullOrBlank() -> error("Descripción corta requerida")
                            it.length > 25 -> error("Máximo 25 caracteres (${it.length})")
                            else -> null
                        }
                    }
                }
                field("Precio de venta") {
                    if (formType == CREATE) model.precioVenta.value = 50
                    spinner(property = model.precioVenta, initialValue = 50, min = 50, max = Int.MAX_VALUE, amountToStepBy = 500, editable = true)
                }
                field("Existencias") {
                    if (formType == CREATE) model.existencias.value = 0
                    spinner(property = model.existencias, initialValue = 0, min = 0, max = Int.MAX_VALUE, amountToStepBy = 1, editable = true)
                }
                hbox(spacing = 80, alignment = Pos.CENTER) {
                    button("Aceptar") {
                        addClass(MainStylesheet.coolBaseButton)
                        addClass(MainStylesheet.greenButton)
                        addClass(MainStylesheet.expandedButton)
                        action {
                            if (formType == CREATE) {
                                model.commit {
                                    find<ProductoController>().productos.add(
                                        Producto(
                                            model.codigo.value,
                                            model.descLarga.value,
                                            model.descCorta.value,
                                            model.precioVenta.value.toInt(),
                                            model.existencias.value.toInt()
                                        )
                                    )
                                    close()
                                }
                            } else {
                                model.commit()
                                close()
                            }
                        }
                    }
                    button("Cancelar") {
                        addClass(MainStylesheet.coolBaseButton)
                        addClass(MainStylesheet.redButton)
                        addClass(MainStylesheet.expandedButton)
                        action {
                            if (formType == CREATE) {
                                close()
                            } else {
                                model.rollback()
                                close()
                            }
                        }
                    }
                }
            }
        }
    }
}

// 1. These views need to be accesible from anywhere so that they can be used in other modules for convenience.
class NewProductoFormView : Fragment() {
    override val root = BaseProductoFormField(CREATE).root
}

class EditProductoFormView : View() {
    override val root = BaseProductoFormField(EDIT).root
}

class ConfirmDeleteProductoDialog : View() {
    private val productoController: ProductoController by inject()

    override val root = vbox(spacing = 0) {
        useMaxSize = true
        label("¿Está seguro de eliminar la selección?") {
            useMaxWidth = true
            addClass(MainStylesheet.titleLabel)
            addClass(MainStylesheet.redLabel)
        }
        label("Esta acción no se puede deshacer. ¿Confirmar?").style {
            padding = box(vertical = 30.px, horizontal = 5.px)
        }
        hbox(spacing = 80, alignment = Pos.CENTER) {
            button("Sí") {
                addClass(MainStylesheet.coolBaseButton)
                addClass(MainStylesheet.greenButton)
                addClass(MainStylesheet.expandedButton)
                action {
                    productoController.productos.remove(params["selected"])
                    close()
                }
            }
            button("No") {
                addClass(MainStylesheet.coolBaseButton)
                addClass(MainStylesheet.redButton)
                addClass(MainStylesheet.expandedButton)
                action { close() }
            }
        }
    }
}