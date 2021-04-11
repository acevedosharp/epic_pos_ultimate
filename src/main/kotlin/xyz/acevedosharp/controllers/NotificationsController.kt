@file:Suppress("ConvertTwoComparisonsToRangeCheck")

package xyz.acevedosharp.controllers

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import tornadofx.*
import xyz.acevedosharp.CustomApplicationContextWrapper
import xyz.acevedosharp.GlobalSettings
import xyz.acevedosharp.persistence.repositories.ClienteRepo
import xyz.acevedosharp.persistence.repositories.ProductoRepo
import xyz.acevedosharp.ui_models.Notification
import xyz.acevedosharp.ui_models.NotificationType
import java.util.*

class NotificationsController : Controller() {
    private val applicationContext = find<CustomApplicationContextWrapper>().context

    // for notifications on startup
    private val clienteRepo = applicationContext.getBean(ClienteRepo::class.java)
    private val productoRepo = applicationContext.getBean(ProductoRepo::class.java)

    private val notifications = FXCollections.observableArrayList<Notification>()

    private val millisDayOffset = 86_400_000L * GlobalSettings.daysForBirthdayCheck

    init {
        doBirthDayCheck()
        doInventoryCheck()
    }

    fun pushNotifications(notification: List<Notification>) = notifications.addAll(notification)
    fun getNotifications(): ObservableList<Notification> = notifications
    fun clearNotification(uuid: UUID) = notifications.removeIf { it.uuid == uuid }
    fun clearAllNotifications() = notifications.clear()

    fun doBirthDayCheck() {
        if (GlobalSettings.isBirthdayCheckEnabled) {
            val allClientes = clienteRepo.findAll()
            val clientesWithBirthDay = allClientes.mapNotNull {
                if (it.birthdayDay == null || it.birthdayMonth == null) {
                    return@mapNotNull null
                } else {
                    val calendar = Calendar.getInstance()
                    calendar.set(Calendar.DAY_OF_MONTH, it.birthdayDay!!)
                    calendar.set(Calendar.MONTH, it.birthdayMonth!!)

                    val res = calendar.timeInMillis - System.currentTimeMillis()

                    if (res >= 0 && res <= millisDayOffset) {
                        return@mapNotNull Notification(
                            uuid = UUID.randomUUID(),
                            type = NotificationType.CUSTOMER_BIRTHDAY,
                            message = "Cumpleaños de ${it.nombre} el ${it.birthdayDay}/${it.birthdayMonth}."
                        )
                    } else return@mapNotNull null
                }
            }
            pushNotifications(clientesWithBirthDay)
        }
    }

    fun doInventoryCheck() {
        if (GlobalSettings.isProductInventoryCheckEnabled) {
            val allProducts = productoRepo.findAll()
            val productsWithLowInventory = allProducts.mapNotNull {
                if (it.codigo != "bolsa" && it.existencias <= it.alertaExistencias) {
                    return@mapNotNull Notification(
                        uuid = UUID.randomUUID(),
                        type = NotificationType.INVENTORY_ALERT,
                        message = "Inventario de ${it.descripcionCorta}: ${it.existencias}. Alerta en: ${it.alertaExistencias}."
                    )
                } else return@mapNotNull null
            }
            pushNotifications(productsWithLowInventory)
        }
    }
}