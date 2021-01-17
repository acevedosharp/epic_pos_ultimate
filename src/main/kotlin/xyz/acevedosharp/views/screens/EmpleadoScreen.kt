package xyz.acevedosharp.views.screens

import xyz.acevedosharp.controllers.EmpleadoController
import xyz.acevedosharp.ui_models.Empleado
import xyz.acevedosharp.ui_models.EmpleadoModel
import xyz.acevedosharp.views.helpers.FormType
import xyz.acevedosharp.views.helpers.FormType.*
import xyz.acevedosharp.views.helpers.CurrentModule.*
import xyz.acevedosharp.views.MainStylesheet
import xyz.acevedosharp.views.shared_components.SideNavigation
import xyz.acevedosharp.views.UnknownErrorDialog
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import tornadofx.*
import xyz.acevedosharp.Joe
import xyz.acevedosharp.persistence.entities.EmpleadoDB

class EmpleadoView : View("Módulo de empleados") {

    private val empleadoController = find<EmpleadoController>()
    private val selectedId = SimpleIntegerProperty()
    private val existsSelection = SimpleBooleanProperty(false)
    private val searchByNombre = SimpleStringProperty("")
    private var table: TableView<EmpleadoDB> by singleAssign()
    private val view = this

    init {
        Joe.currentView = view

        searchByNombre.onChange {
            table.items = empleadoController.empleados.filter {
                it.nombre.toLowerCase().contains(searchByNombre.value.toLowerCase())
            }.asObservable()
        }

        empleadoController.empleados.onChange {
            table.items = empleadoController.empleados.filter {
                it.nombre.toLowerCase().contains(searchByNombre.value.toLowerCase())
            }.asObservable()
        }
    }

    override val root = hbox {
        setPrefSize(1920.0, 1080.0)
        add(SideNavigation(EMPLEADOS, view))
        borderpane {
            setPrefSize(1720.0, 1080.0)
            top {
                hbox {
                    addClass(MainStylesheet.topBar)
                    paddingBottom = 4
                    useMaxWidth = true
                    button("Nuevo Empleado") {
                        addClass(MainStylesheet.coolBaseButton, MainStylesheet.greenButton)
                        action {
                            openInternalWindow<NewEmpleadoFormView>(
                                closeButton = false,
                                modal = true,
                                params = mapOf("owner" to view)
                            )
                        }
                    }
                    button("Editar selección") {
                        enableWhen(existsSelection)
                        addClass(MainStylesheet.coolBaseButton, MainStylesheet.blueButton)
                        action {
                            openInternalWindow<EditEmpleadoFormView>(
                                closeButton = false,
                                modal = true,
                                params = mapOf(
                                    "id" to selectedId.value,
                                    "owner" to view
                                )
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
                            label("Buscar por nombre").apply { addClass(MainStylesheet.searchLabel) }
                            textfield(searchByNombre)

                            prefWidth = 250.0
                        }
                    }
                }
            }

            center {
                hbox {
                    table = tableview(empleadoController.empleados) {
                        column("Nombre", EmpleadoDB::nombre)
                        column("Teléfono", EmpleadoDB::telefono)

                        smartResize()

                        selectionModel.selectedItemProperty().onChange {
                            existsSelection.value = it != null
                            if (it != null) {
                                selectedId.set(it.empleadoId!!)
                            }
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

class BaseEmpleadoFormView(formType: FormType, id: Int?) : Fragment() {

    private val empleadoController = find<EmpleadoController>()
    private val model = if (formType == CREATE)
        EmpleadoModel()
    else
        EmpleadoModel().apply {
            val empleado = empleadoController.findById(id!!)!!.toModel()

            this.id.value = empleado.id
            this.nombre.value = empleado.nombre
            this.telefono.value = empleado.telefono
        }

    override val root = vbox(spacing = 0) {
        useMaxSize = true
        prefWidth = 800.0
        label(if (formType == CREATE) "Nuevo Empleado" else "Editar Empleado") {
            useMaxWidth = true
            addClass(MainStylesheet.titleLabel)
            addClass(if (formType == CREATE) MainStylesheet.greenLabel else MainStylesheet.blueLabel)
        }
        form {
            fieldset {
                field("Nombre") {
                    textfield(model.nombre).validator(trigger = ValidationTrigger.OnBlur) {
                        when {
                            if (formType == CREATE) empleadoController.isNombreAvailable(it.toString())
                            else empleadoController.existsOtherWithNombre(it.toString(), model.id.value)
                            -> error("Nombre no disponible")
                            it.isNullOrBlank() -> error("Nombre requerido")
                            it.length > 50 -> error("Máximo 50 caracteres (${it.length})")
                            else -> null
                        }
                    }
                }
                field("Teléfono") {
                    textfield(model.telefono).validator(trigger = ValidationTrigger.OnBlur) {
                        when {
                            if (formType == CREATE) empleadoController.isTelefonoAvailable(it.toString())
                            else empleadoController.existsOtherWithTelefono(it.toString(), model.id.value)
                            -> error("Teléfono no disponible")
                            it.isNullOrBlank() -> error("Teléfono requerido")
                            it.length > 20 -> error("Máximo 20 caracteres (${it.length})")
                            else -> null
                        }
                    }
                }
                hbox(spacing = 80, alignment = Pos.CENTER) {
                    button("Aceptar") {
                        addClass(MainStylesheet.coolBaseButton, MainStylesheet.greenButton, MainStylesheet.expandedButton)
                        action {
                            model.commit {
                                empleadoController.save(model.item)
                                close()
                            }
                        }
                    }
                    button("Cancelar") {
                        addClass(MainStylesheet.coolBaseButton, MainStylesheet.redButton, MainStylesheet.expandedButton)
                        action {
                            close()
                        }
                    }
                }
            }
        }
    }
}

class NewEmpleadoFormView : Fragment() {
    override val root = BaseEmpleadoFormView(CREATE, null).root

    override fun onDock() {
        Joe.currentView = this
        super.onDock()
    }

    override fun onUndock() {
        Joe.currentView = params["owner"] as UIComponent
        super.onUndock()
    }
}

class EditEmpleadoFormView : Fragment() {
    override val root = BaseEmpleadoFormView(EDIT, params["id"] as Int).root

    override fun onDock() {
        Joe.currentView = this
        super.onDock()
    }

    override fun onUndock() {
        Joe.currentView = params["owner"] as UIComponent
        super.onUndock()
    }
}
