package xyz.acevedosharp

import javafx.application.Application
import javafx.application.Platform
import org.springframework.boot.autoconfigure.SpringBootApplication
import java.io.IOException

import java.net.MalformedURLException
import java.net.URL
import java.net.URLConnection
import kotlinx.coroutines.*
import tornadofx.*
import xyz.acevedosharp.views.NoInternetConnectionErrorDialog


@SpringBootApplication
class LocalSpringBootApplication

fun main() {
    Application.launch(ClientApplication::class.java)
}

object InternetConnection {
    fun isAvailable(): Boolean {
        return try {
            val url = URL("http://www.google.com")
            val conn: URLConnection = url.openConnection()
            conn.connect()
            conn.getInputStream().close()
            true
        } catch (e: MalformedURLException) {
            throw RuntimeException(e)
        } catch (e: IOException) {
            println("IOException while testing for internet connection.")
            false
        }
    }
}

object Joe {
    var currentView: UIComponent? = null
}