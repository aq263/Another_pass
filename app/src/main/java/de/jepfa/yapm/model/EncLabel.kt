package de.jepfa.yapm.model

import java.util.*

data class EncLabel(var id: Int?,
                    var name: Encrypted,
                    var description: Encrypted,
                    var color: Int?) {

    constructor(id: Int?, nameBase64: String, descriptionBase64: String, color: Int?) :
            this(id, Encrypted.fromBase64String(nameBase64), Encrypted.fromBase64String(descriptionBase64), color)

    fun isPersistent(): Boolean {
        return id != null
    }

    companion object {
        const val EXTRA_LABEL_NAME = "de.jepfa.yapm.ui.label.name"
        const val EXTRA_LABEL_DESC = "de.jepfa.yapm.ui.label.description"
        const val EXTRA_LABEL_COLOR = "de.jepfa.yapm.ui.label.color"

        const val ATTRIB_NAME = "name"
        const val ATTRIB_DESC = "description"
        const val ATTRIB_COLOR = "color"

    }
}