package com.eden.orchid.api.resources.resourcesource

import com.eden.orchid.api.OrchidContext
import com.eden.orchid.api.resources.resource.OrchidResource
import com.eden.orchid.api.theme.AbstractTheme
import com.eden.orchid.utilities.OrchidUtils

class DefaultTemplateResourceSource
@JvmOverloads
constructor(
    private val theme: AbstractTheme,
    private val templateBaseDir: String = "templates",
    private val delegate: OrchidResourceSource
) : OrchidResourceSource by delegate, TemplateResourceSource {

    override fun getResourceEntry(context: OrchidContext, fileName: String): OrchidResource? {
        return locateTemplate(context, fileName)
    }

    override fun getResourceEntry(context: OrchidContext, templateSubdir: String, possibleFileNames: List<String>): OrchidResource? {
        return sequence {
            val themePreferredExtension = theme.preferredTemplateExtension
            val defaultExtension = context.defaultTemplateExtension

            for(template in possibleFileNames) {
                listOf(
                    "$templateBaseDir/$template",
                    "$templateBaseDir/$template.$themePreferredExtension",
                    "$templateBaseDir/$template.$defaultExtension",
                    "$templateBaseDir/$templateSubdir/$template",
                    "$templateBaseDir/$templateSubdir/$template.$themePreferredExtension",
                    "$templateBaseDir/$templateSubdir/$template.$defaultExtension"
                ).forEach { yield(it) }
            }
        }
            .filter { it.isNotBlank() }
            .distinct()
            .map { locateTemplate(context, it) }
            .filterNotNull()
            .firstOrNull()
    }

    override fun getResourceEntries(
        context: OrchidContext,
        dirName: String,
        fileExtensions: Array<String>?,
        recursive: Boolean
    ): List<OrchidResource> {
        throw NotImplementedError("TemplateResourceSource can only look up a single resource entry")
    }

// Implementation
//----------------------------------------------------------------------------------------------------------------------

    private fun locateTemplate(context: OrchidContext, fileNames: String): OrchidResource? {
        val possibleTemplates = if (fileNames.startsWith("?")) {
            fileNames.substring(1).split(",")
        } else {
            fileNames.split(",")
        }

        return possibleTemplates
            .asSequence()
            .map { locateSingleTemplate(context, it) }
            .filterNotNull()
            .firstOrNull()
    }

    private fun locateSingleTemplate(context: OrchidContext, templateName: String?): OrchidResource? {
        var fullFileName = OrchidUtils.normalizePath(OrchidUtils.normalizePath(templateName))
        if (!fullFileName.startsWith("$templateBaseDir/")) {
            fullFileName = "$templateBaseDir/$fullFileName"
        }
        if (!fullFileName.contains(".")) {
            fullFileName = fullFileName + "." + theme.preferredTemplateExtension
        }
        return delegate.getResourceEntry(context, fullFileName)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DefaultTemplateResourceSource

        if (theme != other.theme) return false
        if (templateBaseDir != other.templateBaseDir) return false
        if (delegate != other.delegate) return false

        return true
    }

    private val _hashcode by lazy {
        var result = theme.hashCode()
        result = 31 * result + templateBaseDir.hashCode()
        result = 31 * result + delegate.hashCode()
        result
    }
    override fun hashCode(): Int {
        return _hashcode
    }
}