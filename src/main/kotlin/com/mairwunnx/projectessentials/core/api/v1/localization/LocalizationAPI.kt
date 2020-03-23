@file:Suppress("unused")

package com.mairwunnx.projectessentials.core.api.v1.localization

import com.mairwunnx.projectessentials.core.extensions.empty
import com.mairwunnx.projectessentials.core.localization.fallbackLanguage

/**
 * Localization API class, for interacting with
 * localization.
 * @since Mod: 1.14.4-2.0.0, API: 1.0.0
 */
object LocalizationAPI {
    /**
     * Applying localization, without processing. Thread safe.
     * @param localization localization data class instance.
     * @since Mod: 1.14.4-2.0.0, API: 1.0.0
     */
    fun apply(localization: Localization) {
        synchronized(this) {
            LocalizationProcessor.localizations.add(localization)
        }
    }

    /**
     * Removing localization from localizations. (For
     * applying changes you need remove all localization data
     * and re-process localization processor).
     * @param localization localization data class with specified
     * name and sources.
     * @return true if any elements were removed.
     * @since Mod: 1.14.4-2.0.0, API: 1.0.0
     */
    fun remove(localization: Localization): Boolean =
        LocalizationProcessor.localizations.removeIf {
            it.sourceName == localization.sourceName &&
                    it.sources == localization.sources
        }

    /**
     * Removing all localizations.
     * @since Mod: 1.14.4-2.0.0, API: 1.0.0
     */
    fun removeAll() = LocalizationProcessor.localizations.clear()

    /**
     * @return fall back localizations language.
     * @since Mod: 1.14.4-2.0.0, API: 1.0.0
     */
    fun getFallBackLanguage() = LocalizationProcessor.fallbackLanguage

    /**
     * Install new fall back localizations language.
     * @param language language string in format `xx_xx`.
     * @throws IllegalLanguageCodeException when language
     * code is illegal.
     * @since Mod: 1.14.4-2.0.0, API: 1.0.0
     */
    fun setFallBackLanguage(language: String) {
        if (language.matches(Regex("^[a-z]{2}_[a-z]{2}$"))) {
            LocalizationProcessor.fallbackLanguage = language
        } else {
            throw IllegalLanguageCodeException(
                "Language code format $language incorrect and unsupported."
            )
        }
    }

    /**
     * @param name target localization name.
     * @return Localization data by specified name.
     * @throws LocalizationNotFoundException when localization
     * not found.
     * @since Mod: 1.14.4-2.0.0, API: 1.0.0
     */
    fun getByName(name: String) =
        LocalizationProcessor.localizations.find {
            it.sourceName == name
        } ?: throw LocalizationNotFoundException(
            "Localization with name $name not found."
        )

    /**
     * @return all localizations as mutable list.
     * @since Mod: 1.14.4-2.0.0, API: 1.0.0
     */
    fun getAll() = LocalizationProcessor.localizations

    /**
     * @param targetLanguage target language, in format `xx_xx`.
     * @param l10nString minecraft localization string.
     * @param args some arguments for string if provided.
     * @param argumentChar argument char for processing arguments.
     * @return localized string, if localization string
     * not found, then return empty string.
     * @since Mod: 1.14.4-2.0.0, API: 1.0.0
     */
    fun getLocalizedString(
        targetLanguage: String,
        l10nString: String,
        vararg args: String,
        argumentChar: Char = 's'
    ): String {
        val arg = "%$argumentChar"

        var msg = String.empty
        val messagesList = LocalizationProcessor.localizationsData[
                targetLanguage.toLowerCase()
        ] ?: LocalizationProcessor.localizationsData[fallbackLanguage]

        messagesList?.forEach {
            it[l10nString]?.let { message ->
                msg = message
            }
        }

        val argumentCount = msg.filter { it == '%' }.count()
        for (i in 0 until argumentCount) {
            msg = msg.replaceFirst(arg, args[i])
        }
        return msg
    }
}
