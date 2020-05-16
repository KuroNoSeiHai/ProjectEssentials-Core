package com.mairwunnx.projectessentials.core.api.v1.configuration

/**
 * Configuration API, for interacting with configurations.
 * @since 2.0.0-SNAPSHOT.1.
 */
@Suppress("unused")
object ConfigurationAPI {
    /**
     * @return all installed and checked configurations.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun getAllConfigurations() = ConfigurationProcessor.getConfigurations()

    /**
     * @param name processor name.
     * @throws ConfigurationNotFoundException
     * @return configuration by name. If configuration with
     * name not exist then throws `ConfigurationNotFoundException`.
     * @since 2.0.0-SNAPSHOT.1.
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> getConfigurationByName(name: String): T where T : IConfiguration<*> =
        getAllConfigurations().find { it.name == name }?.let {
            return@let it as T
        } ?: throw ConfigurationNotFoundException(
            "Configuration with name $name not found."
        )

    /**
     * Reloads all initialized and processed configurations.
     * @param saveBeforeLoad if value is true then configuration
     * will be saved before loading. Default value is `true`.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun reloadAll(saveBeforeLoad: Boolean = true) {
        getAllConfigurations().forEach {
            if (saveBeforeLoad) it.save()
            it.load()
        }
    }

    /**
     * Reloads specified configuration.
     * @param configuration configuration for reloading.
     * @param saveBeforeLoad if value is true then configuration
     * will be saved before loading. Default value is `true`.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun reloadSpecified(
        configuration: IConfiguration<*>,
        saveBeforeLoad: Boolean = true
    ) {
        if (saveBeforeLoad) configuration.save()
        configuration.load()
    }

    /**
     * Saves all initialized and processed configurations.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun saveAll() = getAllConfigurations().forEach { it.save() }

    /**
     * Saves specified configuration.
     * @param configuration configuration for saving.
     * @since 2.0.0-SNAPSHOT.1.
     */
    fun saveSpecified(configuration: IConfiguration<*>) = configuration.save()
}
