package de.bund.bfr.rakip.editor

import java.awt.Color
import javax.swing.*
import javax.swing.table.DefaultTableCellRenderer
import javax.swing.table.DefaultTableModel

internal fun createLabel(text: String, tooltip: String): javax.swing.JLabel {
    val label = javax.swing.JLabel(text)
    label.toolTipText = tooltip

    return label
}

internal fun createAdvancedPanel(checkbox: javax.swing.JCheckBox): javax.swing.JPanel {
    val panel = javax.swing.JPanel()
    panel.background = java.awt.Color.lightGray
    panel.add(checkbox)

    return panel
}

/** Creates a JSpinner with 5 columns. */
internal fun createSpinner(spinnerModel: javax.swing.AbstractSpinnerModel): javax.swing.JSpinner {
    val spinner = javax.swing.JSpinner(spinnerModel)
    (spinner.editor as javax.swing.JSpinner.DefaultEditor).textField.columns = 5

    return spinner
}

/** Creates a SpinnerNumberModel for integers with no limits and initial value 0. */
internal fun createSpinnerIntegerModel() = javax.swing.SpinnerNumberModel(0, null, null, 1)

/** Creates a SpinnerNumberModel for real numbers with no limits and initial value 0.0. */
internal fun createSpinnerDoubleModel() = javax.swing.SpinnerNumberModel(0.0, null, null, .01)

/**
 * Creates a SpinnerNumberModel for percentages (doubles) and initial value 0.0.
 *
 * Has limits 0.0 and 1.0.
 * */
internal fun createSpinnerPercentageModel() = javax.swing.SpinnerNumberModel(0.0, 0.0, 1.0, .01)

public class NonEditableTableModel : javax.swing.table.DefaultTableModel(arrayOf(), arrayOf("header")) {
    override fun isCellEditable(row: Int, column: Int) = false
}

internal class HeadlessTable(model: de.bund.bfr.rakip.editor.NonEditableTableModel, val renderer: javax.swing.table.DefaultTableCellRenderer) : javax.swing.JTable(model) {

    init {
        tableHeader = null  // Hide header
        setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION)
    }

    override fun getCellRenderer(row: Int, column: Int) = renderer
}

internal class ButtonsPanel : javax.swing.JPanel() {

    val addButton = javax.swing.JButton("Add")
    val modifyButton = javax.swing.JButton("Modify")
    val removeButton = javax.swing.JButton("Remove")

    init {
        add(addButton)
        add(modifyButton)
        add(removeButton)
    }
}

/**
 * Shows Swing ok/cancel dialog.
 *
 * @return the selected option: JOptionPane.OK_OPTION or JOptionPane.CANCEL_OPTION
 */
internal fun showConfirmDialog(panel: javax.swing.JPanel, title: String): Int {
    return javax.swing.JOptionPane.showConfirmDialog(null, panel, title, javax.swing.JOptionPane.OK_OPTION, javax.swing.JOptionPane.PLAIN_MESSAGE)
}
