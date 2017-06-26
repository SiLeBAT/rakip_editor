package de.bund.bfr.rakip.editor

import javax.swing.JOptionPane


class ValidatableDialog(panel: ValidatablePanel, dialogTitle: String) : javax.swing.JDialog(null as java.awt.Frame?, true) {

    private val optionPane = javax.swing.JOptionPane(panel, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_OPTION)

    init {

        title = dialogTitle

        // Handle window closing correctly
        defaultCloseOperation = javax.swing.WindowConstants.DISPOSE_ON_CLOSE

        optionPane.addPropertyChangeListener { e ->

            if (isVisible && e.source == optionPane &&
                    e.propertyName == javax.swing.JOptionPane.VALUE_PROPERTY &&
                    optionPane.value != javax.swing.JOptionPane.UNINITIALIZED_VALUE) {

                val value = optionPane.value as? Int

                if (value == javax.swing.JOptionPane.YES_OPTION) {
                    val errors = panel.validatePanel()
                    if (errors.isEmpty()) {
                        dispose()
                    } else {
                        val msg = errors.joinToString(separator = "\n")
                        javax.swing.JOptionPane.showMessageDialog(this, msg, "Missing fields",
                                javax.swing.JOptionPane.ERROR_MESSAGE)

                        // Reset the JOptionPane's value. If you don't this, the if the user presses
                        // the same button next time, no property change will be fired.
                        optionPane.value = javax.swing.JOptionPane.UNINITIALIZED_VALUE  // Reset value
                    }
                } else if (value == javax.swing.JOptionPane.NO_OPTION) {
                    dispose()
                }
            }
        }

        contentPane = optionPane
        pack()
        isVisible = true
    }

    fun getValue(): Any = optionPane.value
}