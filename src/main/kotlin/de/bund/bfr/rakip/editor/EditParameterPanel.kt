package de.bund.bfr.rakip.editor

import de.bund.bfr.knime.ui.AutoSuggestField
import de.bund.bfr.rakip.generic.Parameter

import de.bund.bfr.rakip.editor.vocabs

// TODO: idTextField <- Create UUID automatically
class EditParameterPanel(parameter: Parameter? = null, isAdvanced: Boolean) : ValidatablePanel() {

    companion object {

        val id = "Parameter ID *"
        val idTooltip = "An unambiguous and sequential ID given to the parameter"

        val classification = "Parameter classification *"
        val classificationTooltip = "General classification of the parameter (e.g. Input, Constant, Output...)"

        val parameterName = "Parameter name *"
        val parameterNameTooltip = "A name given to the parameter"

        val description = "Parameter classification"
        val descriptionTooltip = "General description of the parameter"

        val type = "Parameter type"
        val typeTooltip = "The type of the parameter"

        val unit = "Parameter unit *"
        val unitTooltip = "Unit of the parameter"

        val unitCategory = "Parameter unit category *"
        val unitCategoryTooltip = "General classification of the parameter unit"

        val dataType = "Parameter data type *"
        val dataTypeTooltip = """
            |<html>
            |<p>Information on the data format of the parameter, e.g. if it is a
            |<p>categorical variable, int, double, array of size x,y,z
            |</html>
            """.trimMargin()

        val source = "Parameter source"
        val sourceTooltip = "Information on the type of knowledge used to define the parameter value"

        val subject = "Parameter subject"
        val subjectTooltip = """
            |<html>
            |<p>Scope of the parameter, e.g. if it refers to an animal, a batch of
            |<p>animals, a batch of products, a carcass, a carcass skin etc
            |</html>
            """.trimMargin()

        val distribution = "Parameter distribution"
        val distributionTooltip = """
            |<html>
            |<p>Information on the expected distribution of parameter values in of
            |<p>uncertainty and variability - if available. SUGGESTION: Information on
            |<p>the distribution describing the parameter (e.g variability, uncertainty,
            |<p>point estimate...)
            |</html>
            """.trimMargin()

        val value = "Parameter value"
        val valueTooltip = "Numerical value of the parameter"

        val reference = "Parameter reference"
        val referenceTooltip = """
            |<html>
            |<p>Information on the source, where the value of the parameter has been
            |<p>extracted from - if available. The format should use that used in other
            |<p>"Reference" metadata"
            |</html>
            """.trimMargin()

        val variabilitySubject = """
            |<html>
            |<p>Parameter variability
            |<p>subject
            |</html>
            """.trimMargin()
        val variabilitySubjectTooltip = """
            |<html>
            |<p>Information "per what" the variability is described. It can be
            |<p>variability between broiler in a flock,  variability between all meat
            |<p>packages sold in Denmark, variability between days, etc.
            |</html>
            """.trimMargin()

        val applicability = """
            |<html>
            |<p>Range of applicability
            |<p>of the model
            |</html>
            """.trimMargin()

        val applicabilityTooltip = """
            |<html>
            |<p>Numerical values of the maximum and minimum limits of the parameter that
            |<p>determine the range of applicability for which the model applies
            |</html>
            """.trimMargin()

        val error = "Parameter error"
        val errorTooltip = "Error of the parameter value"
    }

    val idLabel = createLabel(text = id, tooltip = idTooltip)
    val idTextField = javax.swing.JTextField(30)

    // TODO: classificationComboBox is a ComboBox and in the GUI appears a Text entry instead
    val classificationLabel = createLabel(text = classification, tooltip = classificationTooltip)
    val classificationField = AutoSuggestField(10)

    val nameLabel = createLabel(text = parameterName, tooltip = parameterNameTooltip)
    val nameTextField = javax.swing.JTextField(30)

    val descriptionLabel = createLabel(text = description, tooltip = descriptionTooltip)
    val descriptionTextArea = if (isAdvanced) javax.swing.JTextArea(5, 30) else null

    val typeLabel = createLabel(text = type, tooltip = typeTooltip)
    val typeField = if (isAdvanced) AutoSuggestField(10) else null

    val unitLabel = createLabel(text = unit, tooltip = unitTooltip)
    val unitField = AutoSuggestField(10)

    val unitCategoryLabel = createLabel(text = unitCategory, tooltip = unitCategoryTooltip)
    val unitCategoryField = AutoSuggestField(10)

    val dataTypeLabel = createLabel(text = dataType, tooltip = dataTypeTooltip)
    val dataTypeField = AutoSuggestField(10)

    val sourceLabel = createLabel(text = source, tooltip = sourceTooltip)
    val sourceField = if (isAdvanced) AutoSuggestField(10) else null

    val subjectLabel = createLabel(text = subject, tooltip = subjectTooltip)
    val subjectField = if (isAdvanced) AutoSuggestField(10) else null

    val distributionLabel = createLabel(text = distribution, tooltip = distributionTooltip)
    val distributionField = if (isAdvanced) AutoSuggestField(10) else null

    val valueLabel = createLabel(text = value, tooltip = valueTooltip)
    val valueTextField = if (isAdvanced) javax.swing.JTextField(30) else null

    val referenceLabel = createLabel(text = reference, tooltip = referenceTooltip)
    val referenceTextField = if (isAdvanced) javax.swing.JTextField(30) else null

    val variabilitySubjectLabel = createLabel(text = variabilitySubject, tooltip = variabilitySubjectTooltip)
    val variabilitySubjectTextArea = if (isAdvanced) javax.swing.JTextArea(5, 30) else null

    val applicabilityLabel = createLabel(text = applicability, tooltip = applicabilityTooltip)
    val applicabilityTextArea = if (isAdvanced) javax.swing.JTextArea(5, 30) else null

    val errorLabel = createLabel(text = error, tooltip = errorTooltip)
    val errorSpinnerModel = if (isAdvanced) createSpinnerDoubleModel() else null

    init {

        // init combo boxes
        classificationField.setPossibleValues(vocabs["Parameter classification"])
        typeField?.setPossibleValues(vocabs["Parameter type"])
        unitField.setPossibleValues(vocabs["Parameter unit"])
        unitCategoryField.setPossibleValues(vocabs["Parameter unit category"])
        dataTypeField.setPossibleValues(vocabs["Parameter data type"])
        sourceField?.setPossibleValues(vocabs["Parameter source"])
        subjectField?.setPossibleValues(vocabs["Parameter subject"])
        distributionField?.setPossibleValues(vocabs["Parameter distribution"])

        val pairs = mutableListOf<Pair<javax.swing.JLabel, javax.swing.JComponent>>()
        pairs.add(Pair(first = idLabel, second = idTextField))
        pairs.add(Pair(first = classificationLabel, second = classificationField))
        pairs.add(Pair(first = nameLabel, second = nameTextField))
        descriptionTextArea?.let { pairs.add(Pair(first = descriptionLabel, second = it)) }
        typeField?.let { pairs.add(Pair(first = typeLabel, second = it)) }
        pairs.add(Pair(first = unitLabel, second = unitField))
        pairs.add(Pair(first = unitCategoryLabel, second = unitCategoryField))
        pairs.add(Pair(first = dataTypeLabel, second = dataTypeField))
        sourceField?.let { pairs.add(Pair(first = sourceLabel, second = it)) }
        subjectField?.let { pairs.add(Pair(first = subjectLabel, second = it)) }
        distributionField?.let { pairs.add(Pair(first = distributionLabel, second = it)) }
        valueTextField?.let { pairs.add(Pair(first = valueLabel, second = it)) }
        referenceTextField?.let { pairs.add(Pair(first = referenceLabel, second = it)) }
        variabilitySubjectTextArea?.let { pairs.add(Pair(first = variabilitySubjectLabel, second = it)) }
        applicabilityTextArea?.let { pairs.add(Pair(first = applicabilityLabel, second = it)) }
        errorSpinnerModel?.let { pairs.add(Pair(first = errorLabel, second = createSpinner(spinnerModel = it))) }

        addGridComponents(pairs = pairs)
    }

    // TODO: toParameter

    override fun validatePanel(): List<String> {
        val errors = mutableListOf<String>()
        if (!idTextField.hasValidValue()) errors.add("Missing ${de.bund.bfr.rakip.editor.EditParameterPanel.Companion.id}")
        if (!classificationField.hasValidValue()) errors.add("Missing ${de.bund.bfr.rakip.editor.EditParameterPanel.Companion.classification}")
        if (!nameTextField.hasValidValue()) errors.add("Missing $name")
        if (!unitField.hasValidValue()) errors.add("Missing ${de.bund.bfr.rakip.editor.EditParameterPanel.Companion.unit}")
        if (!unitCategoryField.hasValidValue()) errors.add("Missing ${de.bund.bfr.rakip.editor.EditParameterPanel.Companion.unitCategory}")
        if (!dataTypeField.hasValidValue()) errors.add("Missing ${de.bund.bfr.rakip.editor.EditParameterPanel.Companion.dataType}")

        return errors
    }
}