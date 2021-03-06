package xyz.acevedosharp.views.helpers

import org.springframework.stereotype.Service
import xyz.acevedosharp.persistence.entities.ItemVentaDB
import xyz.acevedosharp.persistence.entities.VentaDB
import java.text.SimpleDateFormat
import javax.print.*
import javax.print.attribute.HashPrintRequestAttributeSet
import javax.print.attribute.PrintRequestAttributeSet
import kotlin.math.max

@Service
class RecipePrintingService {

    fun printRecipe(venta: VentaDB, impName: String) {
        fun formatItem(item: ItemVentaDB): String {
            val res = StringBuilder()

            res.append(item.producto.descripcionCorta)
            res.append(" ".repeat(max(26 - item.producto.descripcionCorta.length, 0)))
            val s1 = "$${item.producto.precioVenta}"
            res.append(s1)
            res.append(" ".repeat(max(7 - s1.length, 0)))
            val s2 = "x${item.cantidad}"
            res.append(s2)
            res.append(" ".repeat(max(6 - s2.length, 0)))
            res.append("= $${item.producto.precioVenta * item.cantidad}")

            return res.toString()
        }

        val lowerPadding = "\n\n\n\n\n\n\n"

        val sb = StringBuilder()

        sb.append("*==============================================*\n")
        sb.append("||          Autoservicio Mercamás             ||\n")
        sb.append("||    Tel: 6000607 - Dir: Calle 35 #34-168    ||\n")
        sb.append("*==============================================*\n")
        //sb.append("Atendido por: ${venta.empleado.nombre}\n"          )
        //sb.append("Cliente: ${venta.cliente.nombre} \n"               )
        //sb.append("------------------------------------------------\n")
        venta.items.forEach {
            sb.append(formatItem(it))
            sb.append("\n")
        }
        sb.append("------------------------------------------------\n")
        val pago = "Pago: \$${venta.pagoRecibido}"
        sb.append(pago)
        sb.append(" ".repeat(max(34 - pago.length, 0)))
        sb.append("Total: \$${venta.precioTotal}\n")
        sb.append("Cambio: $${venta.pagoRecibido - venta.precioTotal}\n")
        sb.append("Gracias por su compra el ${SimpleDateFormat("dd/MM/yy HH:mm:ss").format(venta.fechaHora)}.")
        sb.append(lowerPadding)

        printString(impName, sb.toString())
        printBytes(impName, byteArrayOf(0x1d, 'V'.toByte(), 1))
    }

    fun getPrinters(): List<String> {
        val flavor: DocFlavor = DocFlavor.BYTE_ARRAY.AUTOSENSE
        val printRequestAttributes: PrintRequestAttributeSet = HashPrintRequestAttributeSet()

        val printServices: Array<PrintService> = PrintServiceLookup.lookupPrintServices(flavor, printRequestAttributes)

        return printServices.map { it.name }.filter { it !in listOf("Microsoft XPS Document Writer", "Microsoft Print to PDF", "Fax") }
    }

    private fun printString(printerName: String, text: String) {

        // find the printService of name printerName
        val flavor: DocFlavor = DocFlavor.BYTE_ARRAY.AUTOSENSE
        val pras: PrintRequestAttributeSet = HashPrintRequestAttributeSet()
        val printService = PrintServiceLookup.lookupPrintServices(
            flavor, pras
        )
        val service = findPrintService(printerName, printService)
        val job = service!!.createPrintJob()
        try {
            // use a charset that can use many characters
            val bytes: ByteArray = text.toByteArray(charset("CP437"))
            val doc: Doc = SimpleDoc(bytes, flavor, null)
            job.print(doc, null)
        } catch (e: Exception) {
            e.printStackTrace()
            TODO("show printing exception dialog.")
        }
    }

    private fun printBytes(printerName: String, bytes: ByteArray?) {
        val flavor: DocFlavor = DocFlavor.BYTE_ARRAY.AUTOSENSE
        val pras: PrintRequestAttributeSet = HashPrintRequestAttributeSet()
        val printService = PrintServiceLookup.lookupPrintServices(
            flavor, pras
        )
        val service = findPrintService(printerName, printService)
        val job = service!!.createPrintJob()
        try {
            val doc: Doc = SimpleDoc(bytes, flavor, null)
            job.print(doc, null)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun findPrintService(printerName: String, services: Array<PrintService>): PrintService? {
        for (service in services) {
            if (service.name.equals(printerName, ignoreCase = true)) {
                return service
            }
        }
        return null
    }
}
