@file:Suppress("unused")

package com.mairwunnx.projectessentials.core.impl.configurations

import com.mairwunnx.projectessentials.core.api.v1.configuration.Configuration
import com.mairwunnx.projectessentials.core.api.v1.configuration.IConfiguration
import com.mairwunnx.projectessentials.core.api.v1.helpers.projectConfigDirectory
import org.apache.logging.log4j.LogManager
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

@OptIn(ExperimentalUnsignedTypes::class)
@Configuration("general", 1u)
object GeneralConfiguration : IConfiguration<Properties> {
    private val logger = LogManager.getLogger()
    private var cachedData: Configuration? = null
    private val properties = Properties()

    override val configuration = take()
    override val path = projectConfigDirectory + File.separator + "general.properties"

    override fun load() = try {
        File(path).parentFile.mkdirs()
        File(path).createNewFile()
        FileInputStream(path).use { input ->
            properties.load(input)
        }
    } catch (ex: IOException) {
        logger.error("An error occurred while loading general configuration.", ex)
    }

    override fun save() = try {
        FileOutputStream(path).use { output ->
            // todo: add comment.
            properties.store(output, null)
        }
    } catch (ex: IOException) {
        logger.error("An error occurred while saving general configuration.", ex)
    }

    override fun take() = properties

    override fun data(): Configuration {
        if (cachedData == null) {
            cachedData = this.javaClass.getAnnotation(Configuration::class.java)
        }
        return cachedData!!
    }

    fun getDoubleOrDefault(key: String, value: Double): Double {
        val property = properties[key]
        if (property == null) {
            put(key, value.toString())
        }
        return property?.toString()?.toDouble() ?: value
    }

    fun getDouble(key: String) = properties[key].toString().toDouble()

    fun getFloatOrDefault(key: String, value: Float): Float {
        val property = properties[key]
        if (property == null) {
            put(key, value.toString())
        }
        return property?.toString()?.toFloat() ?: value
    }

    fun getFloatInt(key: String) = properties[key].toString().toFloat()

    fun getIntOrDefault(key: String, value: Int): Int {
        val property = properties[key]
        if (property == null) {
            put(key, value.toString())
        }
        return property?.toString()?.toInt() ?: value
    }

    fun getInt(key: String) = properties[key].toString().toInt()

    fun getBoolOrDefault(key: String, value: Boolean): Boolean {
        val property = properties[key]
        if (property == null) {
            put(key, value.toString())
        }
        return property?.toString()?.toBoolean() ?: value
    }

    fun getBool(key: String) = properties[key].toString().toBoolean()

    fun getStringOrDefault(key: String, value: String): String {
        val property = properties[key]
        if (property == null) {
            put(key, value)
        }
        return property?.toString() ?: value
    }

    fun getString(key: String) = properties[key].toString()

    fun getList(
        key: String,
        defaultValue: ArrayList<String> = arrayListOf()
    ): List<String> {
        val array = getStringOrDefault(key, "")
        if (array.isEmpty()) {
            putList(key, defaultValue)
            return defaultValue
        }
        return array
            .removeSurrounding("[", "]")
            .replace(" ", "")
            .split(",")
    }

    fun putList(key: String, value: List<String>) {
        properties[key] = value.toString()
    }

    fun put(key: String, value: String) {
        properties[key] = value
    }
}
