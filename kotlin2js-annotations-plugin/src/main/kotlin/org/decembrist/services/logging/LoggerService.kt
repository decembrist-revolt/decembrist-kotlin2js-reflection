package org.decembrist.services.logging

object LoggerService: Logger {

    var logger: Logger? = null

    override fun debug(message: String){
        checkLogger().debug(message)
    }

    override fun warn(message: String) {
        checkLogger().warn(message)
    }

    private fun checkLogger(): Logger {
        return if (logger == null) throw RuntimeException("Logger not found") else logger!!
    }

}