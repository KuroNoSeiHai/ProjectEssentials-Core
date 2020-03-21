package com.mairwunnx.projectessentials.core.api.v1.configuration

/**
 * Configuration API, for interacting with configurations.
 * @since Mod: 1.14.4-2.0.0, API: 1.0.0
 */
@Suppress("unused")
object ConfigurationAPI {
    /**
     * @return all installed and checked configurations.
     * @since Mod: 1.14.4-2.0.0, API: 1.0.0
     */
    fun getAllConfigurations() = ConfigurationProcessor.getConfigurations()

    /**
     * @return allowed domains to indexing configuration classes.
     * @since Mod: 1.14.4-2.0.0, API: 1.0.0
     */
    fun getAllowedDomains() = ConfigurationProcessor.getAllowedDomains()

    /**
     * @param name processor name.
     * @throws ConfigurationNotFoundException
     * @return configuration by name. If configuration with
     * name not exist then throws `ConfigurationNotFoundException`.
     * @since Mod: 1.14.4-2.0.0, API: 1.0.0
     */
    fun getConfigurationByName(name: String): IConfiguration<*> {
        getAllConfigurations().forEach {
            if (it.data().name == name) {
                return it
            }
        }
        throw ConfigurationNotFoundException(
            "Configuration with name $name not found."
        )
    }

    /**
     * Reloads all initialized and processed configurations.
     * @param saveBeforeLoad if value is true then configuration
     * will be saved before loading. Default value is `true`.
     * @since Mod: 1.14.4-2.0.0, API: 1.0.0
     */
    fun reloadAll(saveBeforeLoad: Boolean = true) {
        getAllConfigurations().forEach {
            if (saveBeforeLoad) {
                it.save()
            }
            it.load()
        }
    }

    /**
     * Reloads specified configuration.
     * @param configuration configuration for reloading.
     * @param saveBeforeLoad if value is true then configuration
     * will be saved before loading. Default value is `true`.
     * @since Mod: 1.14.4-2.0.0, API: 1.0.0
     */
    fun reloadSpecified(
        configuration: IConfiguration<*>,
        saveBeforeLoad: Boolean = true
    ) {
        if (saveBeforeLoad) {
            configuration.save()
        }
        configuration.load()
    }

    /**
     * Saves all initialized and processed configurations.
     * @since Mod: 1.14.4-2.0.0, API: 1.0.0
     */
    fun saveAll() = getAllConfigurations().forEach {
        it.save()
    }

    /**
     * Saves specified configuration.
     * @param configuration configuration for saving.
     * @since Mod: 1.14.4-2.0.0, API: 1.0.0
     */
    fun saveSpecified(configuration: IConfiguration<*>) = configuration.save()
}
