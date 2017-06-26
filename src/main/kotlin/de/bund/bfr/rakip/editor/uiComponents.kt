package de.bund.bfr.rakip.editor

import de.bund.bfr.knime.ui.AutoSuggestField

/**
 * Created by miguelalba on 23.06.17.
 */

fun javax.swing.JTextField.hasValidValue() = text.isNotBlank()

fun javax.swing.JTextArea.hasValidValue() = text.isNotBlank()

fun AutoSuggestField.hasValidValue() : Boolean {
    val field = editor.editorComponent as javax.swing.JTextField
    return field.text.isNotBlank()
}