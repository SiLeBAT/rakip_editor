package de.bund.bfr.rakip.editor

import de.bund.bfr.knime.ui.AutoSuggestField
import de.bund.bfr.rakip.generic.DietaryAssessmentMethod


class  EditDietaryAssessmentMethodPanel(dietaryAssessmentMethod: DietaryAssessmentMethod? = null, isAdvanced: Boolean)
    : ValidatablePanel() {

    companion object {
        val dataCollectionTool = "Methodological tool to collect data"
        val dataCollectionToolTooltip = """
            |<html>
            |<p>food diaries, interview, 24-hour recall interview, food propensy
            |<p>questionnaire, portion size measurement aids, eating outside
            |<p>questionnaire
            """.trimMargin()

        val nonConsecutiveOneDays = "Number of non-conseccutive one-day"
        val nonConsecutiveOneDayTooltip = ""

        val dietarySoftwareTool = "Dietary software tool"
        val dietarySoftwareTooltip = ""
        val foodItemNumber = "Number of food items"
        val foodItemNumberTooltip = ""

        val recordType = "Type of records"
        val recordTypeTooltip = """
            |<html>
            |<p>consumption occasion, mean of consumption, quantified and described as
            |<p>eaten, recipes for self-made
            |</html>
            """.trimMargin()

        val foodDescription = "Food assayDescription"
        val foodDescriptionTooltip = "use foodex2 facets"
    }

    // fields. null if advanced
    val dataCollectionToolField = AutoSuggestField(10)
    val nonConsecutiveOneDayTextField = javax.swing.JTextField(30)
    val dietarySoftwareToolTextField = if (isAdvanced) javax.swing.JTextField(30) else null
    val foodItemNumberTextField = if (isAdvanced) javax.swing.JTextField(30) else null
    val recordTypeTextField = if (isAdvanced) javax.swing.JTextField(30) else null
    val foodDescriptorComboBox = if (isAdvanced) javax.swing.JComboBox<String>() else null

    init {

        // Populate interface with passed `dietaryAssessmentMethod`
        dietaryAssessmentMethod?.let {
            dataCollectionToolField.selectedItem = it.collectionTool
            nonConsecutiveOneDayTextField.text = it.numberOfNonConsecutiveOneDay.toString()
            dietarySoftwareToolTextField?.text = it.softwareTool
            foodItemNumberTextField?.text = it.numberOfFoodItems[0]
            recordTypeTextField?.text = it.recordTypes[0]
            foodDescriptorComboBox?.selectedItem = it.foodDescriptors[0]
        }

        initUI()
    }

    private fun initUI() {

        // Create labels
        val dataCollectionToolLabel = createLabel(text = dataCollectionTool, tooltip = dataCollectionToolTooltip)
        val nonConsecutiveOneDayLabel = createLabel(text = nonConsecutiveOneDays, tooltip = nonConsecutiveOneDayTooltip)
        val dietarySoftwareToolLabel = createLabel(text = dietarySoftwareTool, tooltip = dietarySoftwareTooltip)
        val foodItemNumberLabel = createLabel(text = foodItemNumber, tooltip = foodItemNumberTooltip)
        val recordTypeLabel = createLabel(text = recordType, tooltip = recordTypeTooltip)
        val foodDescriptionLabel = createLabel(text = foodDescription, tooltip = foodDescriptionTooltip)

        // init combo boxes
        dataCollectionToolField.setPossibleValues(vocabs["Method. tool to collect data"])
        foodDescriptorComboBox?.let { vocabs["Food descriptors"]?.forEach(it::addItem) }

        val pairList = mutableListOf<Pair<javax.swing.JLabel, javax.swing.JComponent>>()
        pairList.add(Pair(first = dataCollectionToolLabel, second = dataCollectionToolField))
        pairList.add(Pair(first = nonConsecutiveOneDayLabel, second = nonConsecutiveOneDayTextField))
        dietarySoftwareToolTextField?.let { pairList.add(Pair(first = dietarySoftwareToolLabel, second = it)) }
        foodItemNumberTextField?.let { pairList.add(Pair(first = foodItemNumberLabel, second = it)) }
        recordTypeTextField?.let { pairList.add(Pair(first = recordTypeLabel, second = it)) }
        foodDescriptorComboBox?.let { pairList.add(Pair(first = foodDescriptionLabel, second = it)) }

        addGridComponents(pairs = pairList)
    }

    fun toDietaryAssessmentMethod(): DietaryAssessmentMethod {

        // TODO: cast temporarily null values to empty string and 0 (SHOULD be validated)
        val dataCollectionTool = dataCollectionToolField.selectedItem as? String ?: ""
        val nonConsecutiveOneDays = nonConsecutiveOneDayTextField.text?.toIntOrNull() ?: 0

        val method = DietaryAssessmentMethod(collectionTool = dataCollectionTool, numberOfNonConsecutiveOneDay = nonConsecutiveOneDays)
        method.softwareTool = dietarySoftwareToolTextField?.text
        foodItemNumberTextField?.text?.let { method.numberOfFoodItems.add(it) }
        recordTypeTextField?.text?.let { method.recordTypes.add(it) }
        foodDescriptorComboBox?.selectedObjects?.filterNotNull()?.forEach { method.foodDescriptors.add(it as String) }

        return method
    }

    override fun validatePanel(): List<String> {
        val errors = mutableListOf<String>()
        if (!dataCollectionToolField.hasValidValue()) errors.add("Missing ${de.bund.bfr.rakip.editor.EditDietaryAssessmentMethodPanel.Companion.dataCollectionTool}")
        if (!nonConsecutiveOneDayTextField.hasValidValue()) errors.add("Missing ${de.bund.bfr.rakip.editor.EditDietaryAssessmentMethodPanel.Companion.nonConsecutiveOneDays}")

        return errors
    }
}