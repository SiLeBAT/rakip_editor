package de.bund.bfr.rakip.editor

import de.bund.bfr.rakip.generic.ModelEquation
import javax.swing.JTextArea
import javax.swing.JTextField

class EditModelEquationPanel(equation: ModelEquation? = null, isAdvanced: Boolean) : ValidatablePanel() {

    companion object {

        val equationName = "Model equation name *"
        val equationNameTooltip = "A name given to the model equation"

        val equationClass = "Model equation class"
        val equationClassTooltip = "Information on that helps to categorize model equations"

        val script = "Equation *"
        val scriptToolTip = "The pointer to the file that holds the software code (e.g. R-script)"
    }

    val equationNameLabel = createLabel(text = equationName, tooltip = equationNameTooltip)
    val equationNameTextField = JTextField(30)

    val equationClassLabel = createLabel(text = equationClass, tooltip = equationClassTooltip)
    val equationClassTextField = if (isAdvanced) JTextField(30) else null

    val scriptLabel = createLabel(text = script, tooltip = scriptToolTip)
    val scriptTextArea = JTextArea(5, 30)

    init {

        val referencePanel = ReferencePanel(refs = equation?.equationReference ?: mutableListOf(), isAdvanced = isAdvanced)

        add(comp = equationNameLabel, gridy = 0, gridx = 0)
        add(comp = equationNameTextField, gridy = 0, gridx = 1)

        equationClassTextField?.let {
            add(comp = equationClassLabel, gridy = 1, gridx = 0)
            add(comp = it, gridy = 1, gridx = 1)
        }

        add(comp = referencePanel, gridy = 2, gridx = 0, gridwidth = 2)

        add(comp = scriptLabel, gridy = 3, gridx = 0)
        add(comp = scriptTextArea, gridy = 3, gridx = 1, gridwidth = 2)
    }

    override fun validatePanel(): List<String> {
        val errors = mutableListOf<String>()
        if (!equationNameTextField.hasValidValue()) errors.add("Missing ${equationName}")
        if (!scriptTextArea.hasValidValue()) errors.add("Missing " + script)

        return errors
    }
}
