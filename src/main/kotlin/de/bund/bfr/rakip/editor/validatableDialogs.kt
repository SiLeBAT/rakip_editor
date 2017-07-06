package de.bund.bfr.rakip.editor

import com.gmail.gcolaianni5.jris.bean.Record
import com.gmail.gcolaianni5.jris.bean.Type
import de.bund.bfr.knime.ui.AutoSuggestField
import de.bund.bfr.rakip.generic.*
import java.awt.Frame
import java.awt.GridBagLayout
import javax.swing.*

/**
 * Validatable dialogs and panels.
 */
class ValidatableDialog(panel: ValidatablePanel, dialogTitle: String) : JDialog(null as Frame?, true) {

    private val optionPane = JOptionPane(JScrollPane(panel), JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_OPTION)

    init {

        title = dialogTitle

        // Handle window closing correctly
        defaultCloseOperation = WindowConstants.DISPOSE_ON_CLOSE

        optionPane.addPropertyChangeListener { e ->

            if (isVisible && e.source == optionPane &&
                    e.propertyName == JOptionPane.VALUE_PROPERTY &&
                    optionPane.value != JOptionPane.UNINITIALIZED_VALUE) {

                val value = optionPane.value as? Int

                if (value == JOptionPane.YES_OPTION) {
                    val errors = panel.validatePanel()
                    if (errors.isEmpty()) {
                        dispose()
                    } else {
                        val msg = errors.joinToString(separator = "\n")
                        JOptionPane.showMessageDialog(this, msg, "Missing fields",
                                JOptionPane.ERROR_MESSAGE)

                        // Reset the JOptionPane's value. If you don't this, the if the user presses
                        // the same button next time, no property change will be fired.
                        optionPane.value = JOptionPane.UNINITIALIZED_VALUE  // Reset value
                    }
                } else if (value == JOptionPane.NO_OPTION) {
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

abstract class ValidatablePanel : JPanel(GridBagLayout()) {

    abstract fun validatePanel(): List<String>
}

class EditAssayPanel(assay: Assay? = null, isAdvanced: Boolean) : ValidatablePanel() {

    val nameTextField = JTextField(30)
    val descriptionTextArea = if (isAdvanced) JTextArea(5, 30) else null

    init {

        // Populate UI with passed `assay`
        assay?.let {
            nameTextField.text = it.name
            descriptionTextArea?.text = it.description
        }

        initUI()
    }

    private fun initUI() {
        val nameLabel = createLabel(text = messages.getString("GM.EditAssayPanel.nameLabel") + " *",
                tooltip = messages.getString("GM.EditAssayPanel.nameTooltip"))
        val descriptionLabel = createLabel(text = messages.getString("GM.EditAssayPanel.descriptionLabel"),
                tooltip = messages.getString("GM.EditAssayPanel.descriptionTooltip"))

        val pairList = mutableListOf<Pair<JLabel, JComponent>>()
        pairList.add(Pair(first = nameLabel, second = nameTextField))
        descriptionTextArea?.let { pairList.add(Pair(first = descriptionLabel, second = it)) }

        addGridComponents(pairs = pairList)
    }

    fun toAssay() = Assay(name = nameTextField.text, description = descriptionTextArea?.text)

    override fun validatePanel(): List<String> {
        val errors = mutableListOf<String>()
        if (!nameTextField.hasValidValue()) errors.add("Missing ${messages.getString("GM.EditAssayPanel.nameLabel")}")
        return errors
    }
}

class EditDietaryAssessmentMethodPanel(dietaryAssessmentMethod: DietaryAssessmentMethod? = null, isAdvanced: Boolean)
    : ValidatablePanel() {

    // fields. null if advanced
    val dataCollectionToolField = AutoSuggestField(10)
    val nonConsecutiveOneDayTextField = JTextField(30)
    val dietarySoftwareToolTextField = if (isAdvanced) JTextField(30) else null
    val foodItemNumberTextField = if (isAdvanced) JTextField(30) else null
    val recordTypeTextField = if (isAdvanced) JTextField(30) else null
    val foodDescriptorComboBox = if (isAdvanced) JComboBox<String>() else null

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
        val dataCollectionToolLabel = createLabel(
                text = messages.getString("GM.EditDietaryAssessmentMethodPanel.dataCollectionToolLabel") + " *",
                tooltip = messages.getString("GM.EditDietaryAssessmentMethodPanel.dataCollectionToolTooltip"))
        val nonConsecutiveOneDayLabel = createLabel(
                text = messages.getString("GM.EditDietaryAssessmentMethodPanel.nonConsecutiveOneDaysLabel") + " *",
                tooltip = messages.getString("GM.EditDietaryAssessmentMethodPanel.nonConsecutiveOneDaysTooltip"))
        val dietarySoftwareToolLabel = createLabel(
                text = messages.getString("GM.EditDietaryAssessmentMethodPanel.dietarySoftwareToolLabel"),
                tooltip = messages.getString("GM.EditDietaryAssessmentMethodPanel.dietarySoftwareToolTooltip"))
        val foodItemNumberLabel = createLabel(
                text = messages.getString("GM.EditDietaryAssessmentMethodPanel.foodItemNumberLabel"),
                tooltip = messages.getString("GM.EditDietaryAssessmentMethodPanel.foodItemNumberTooltip"))
        val recordTypeLabel = createLabel(
                text = messages.getString("GM.EditDietaryAssessmentMethodPanel.recordTypeLabel"),
                tooltip = messages.getString("GM.EditDietaryAssessmentMethodPanel.recordTypeTooltip"))
        val foodDescriptionLabel = createLabel(
                text = messages.getString("GM.EditDietaryAssessmentMethodPanel.foodDescriptionLabel"),
                tooltip = messages.getString("GM.EditDietaryAssessmentMethodPanel.foodDescriptionTooltip"))

        // init combo boxes
        dataCollectionToolField.setPossibleValues(vocabs["Method. tool to collect data"])
        foodDescriptorComboBox?.let { vocabs["Food descriptors"]?.forEach(it::addItem) }

        val pairList = mutableListOf<Pair<JLabel, JComponent>>()
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
        if (!dataCollectionToolField.hasValidValue())
            errors.add("Missing ${messages.getString("GM.EditDietaryAssessmentMethodPanel.dataCollectionToolLabel")}")
        if (!nonConsecutiveOneDayTextField.hasValidValue())
            errors.add("Missing ${messages.getString("GM.EditDietaryAssessmentMethodPanel.nonConsecutiveOneDaysLabel")}")

        return errors
    }
}


class EditHazardPanel(hazard: Hazard? = null, isAdvanced: Boolean) : ValidatablePanel() {

    // Fields. Null if simple mode.
    private val hazardTypeField = AutoSuggestField(10)
    private val hazardNameField = AutoSuggestField(10)
    private val hazardDescriptionTextArea = if (isAdvanced) JTextArea(5, 30) else null
    private val hazardUnitField = AutoSuggestField(10)
    private val adverseEffectTextField = if (isAdvanced) JTextField(30) else null
    private val originTextField = if (isAdvanced) JTextField(30) else null
    private val bmdTextField = if (isAdvanced) JTextField(30) else null
    private val maxResidueLimitTextField = if (isAdvanced) JTextField(30) else null
    private val noObservedAdverseTextField = if (isAdvanced) JTextField(30) else null
    private val lowestObserveTextField = if (isAdvanced) JTextField(30) else null
    private val acceptableOperatorTextField = if (isAdvanced) JTextField(30) else null
    private val acuteReferenceDoseTextField = if (isAdvanced) JTextField(30) else null
    private val acceptableDailyIntakeTextField = if (isAdvanced) JTextField(30) else null
    private val indSumField = if (isAdvanced) AutoSuggestField(10) else null
    private val labNameTextField = if (isAdvanced) JTextField(30) else null
    private val labCountryField = if (isAdvanced) AutoSuggestField(10) else null
    private val detectionLimitTextField = if (isAdvanced) JTextField(30) else null
    private val quantificationLimitTextField = if (isAdvanced) JTextField(30) else null
    private val leftCensoredDataTextField = if (isAdvanced) JTextField(30) else null
    private val contaminationRangeTextField = if (isAdvanced) JTextField(30) else null

    init {
        // Populate interface if `hazard` is passed
        hazard?.let {
            hazardTypeField.selectedItem = it.hazardType
            hazardNameField.selectedItem = it.hazardName
            hazardDescriptionTextArea?.text = it.hazardDescription
            hazardUnitField.selectedItem = it.hazardUnit
            adverseEffectTextField?.text = it.adverseEffect
            originTextField?.text = it.origin
            bmdTextField?.text = it.benchmarkDose
            maxResidueLimitTextField?.text = it.maximumResidueLimit
            noObservedAdverseTextField?.text = it.noObservedAdverse
            acceptableOperatorTextField?.text = it.acceptableOperator
            acuteReferenceDoseTextField?.text = it.acuteReferenceDose
            indSumField?.selectedItem = it.hazardIndSum
            acceptableDailyIntakeTextField?.text = it.acceptableDailyIntake
            labNameTextField?.text = it.laboratoryName
            labCountryField?.selectedItem = it.laboratoryCountry
            detectionLimitTextField?.text = it.detectionLimit
            quantificationLimitTextField?.text = it.quantificationLimit
            leftCensoredDataTextField?.text = it.leftCensoredData
            contaminationRangeTextField?.text = it.rangeOfContamination
        }

        initUI()
    }

    private fun initUI() {

        // Create labels
        val hazardTypeLabel = createLabel(text = messages.getString("GM.EditHazardPanel.hazardTypeLabel"),
                tooltip = messages.getString("GM.EditHazardPanel.hazardTypeTooltip"))
        val hazardNameLabel = createLabel(text = messages.getString("GM.EditHazardPanel.hazardNameLabel"),
                tooltip = messages.getString("GM.EditHazardPanel.hazardNameTooltip"))
        val hazardDescriptionLabel = createLabel(
                text = messages.getString("GM.EditHazardPanel.hazardDescriptionLabel"),
                tooltip = messages.getString("GM.EditHazardPanel.hazardDescriptionTooltip"))
        val hazardUnitLabel = createLabel(text = messages.getString("GM.EditHazardPanel.hazardUnitLabel"),
                tooltip = messages.getString("GM.EditHazardPanel.hazardUnitTooltip"))
        val adverseEffectLabel = createLabel(text = messages.getString("GM.EditHazardPanel.adverseEffectLabel"),
                tooltip = messages.getString("GM.EditHazardPanel.adverseEffectTooltip"))
        val originLabel = createLabel(text = messages.getString("GM.EditHazardPanel.originLabel"),
                tooltip = messages.getString("GM.EditHazardPanel.originTooltip"))
        val bmdLabel = createLabel(text = messages.getString("GM.EditHazardPanel.bmdLabel"),
                tooltip = messages.getString("GM.EditHazardPanel.bmdTooltip"))
        val maxResidueLimitLabel = createLabel(text = messages.getString("GM.EditHazardPanel.maxResidueLimitLabel"),
                tooltip = messages.getString("GM.EditHazardPanel.maxResidueLimitTooltip"))
        val noObservedAdverseLabel = createLabel(
                text = messages.getString("GM.EditHazardPanel.noObservedAdverseLabel"),
                tooltip = messages.getString("GM.EditHazardPanel.noObservedAdverseTooltip"))
        val lowestObserveLabel = createLabel(text = messages.getString("GM.EditHazardPanel.lowestObserveLabel"),
                tooltip = messages.getString("GM.EditHazardPanel.lowestObserveTooltip"))
        val acceptableOperatorLabel = createLabel(text = messages.getString("GM.EditHazardPanel.acceptableOperatorLabel"),
                tooltip = messages.getString("GM.EditHazardPanel.acceptableOperatorTooltip"))
        val acuteReferenceDoseLabel = createLabel(text = messages.getString("GM.EditHazardPanel.acuteReferenceDoseLabel"),
                tooltip = messages.getString("GM.EditHazardPanel.acuteReferenceDoseTooltip"))
        val acceptableDailyIntakeLabel = createLabel(
                text = messages.getString("GM.EditHazardPanel.acceptableDailyIntakeLabel"),
                tooltip = messages.getString("GM.EditHazardPanel.acceptableDailyIntakeTooltip"))
        val indSumLabel = createLabel(text = messages.getString("GM.EditHazardPanel.indSumLabel"),
                tooltip = messages.getString("GM.EditHazardPanel.indSumTooltip"))
        val labNameLabel = createLabel(text = messages.getString("GM.EditHazardPanel.labNameLabel"),
                tooltip = messages.getString("GM.EditHazardPanel.labNameTooltip"))
        val labCountryLabel = createLabel(text = messages.getString("GM.EditHazardPanel.labCountryLabel"),
                tooltip = messages.getString("GM.EditHazardPanel.labCountryTooltip"))
        val detectionLimitLabel = createLabel(text = messages.getString("GM.EditHazardPanel.detectionLimitLabel"),
                tooltip = messages.getString("GM.EditHazardPanel.detectionLimitTooltip"))
        val quantificationLimitLabel = createLabel(text = messages.getString("GM.EditHazardPanel.quantificationLimitLabel"),
                tooltip = messages.getString("GM.EditHazardPanel.quantificationLimitTooltip"))
        val leftCensoredDataLabel = createLabel(text = messages.getString("GM.EditHazardPanel.leftCensoredDataLabel"),
                tooltip = messages.getString("GM.EditHazardPanel.leftCensoredDataTooltip"))
        val contaminationRangeLabel = createLabel(text = messages.getString("GM.EditHazardPanel.contaminationRangeLabel"),
                tooltip = messages.getString("GM.EditHazardPanel.contaminationRangeTooltip"))

        // Init combo boxes
        hazardTypeField.setPossibleValues(vocabs["Hazard type"])
        hazardNameField.setPossibleValues(vocabs["Hazard name"])
        hazardUnitField.setPossibleValues(vocabs["Hazard unit"])
        indSumField?.setPossibleValues(vocabs["Hazard ind sum"])
        labCountryField?.setPossibleValues(vocabs["Laboratory country"])

        val pairList = mutableListOf<Pair<JLabel, JComponent>>()
        pairList.add(Pair(first = hazardTypeLabel, second = hazardTypeField))
        pairList.add(Pair(first = hazardNameLabel, second = hazardNameField))
        hazardDescriptionTextArea?.let { pairList.add(Pair(first = hazardDescriptionLabel, second = it)) }
        pairList.add(Pair(first = hazardUnitLabel, second = hazardUnitField))
        adverseEffectTextField?.let { pairList.add(Pair(first = adverseEffectLabel, second = it)) }
        originTextField?.let { pairList.add(Pair(first = originLabel, second = it)) }
        bmdTextField?.let { pairList.add(Pair(first = bmdLabel, second = it)) }
        maxResidueLimitTextField?.let { pairList.add(Pair(first = maxResidueLimitLabel, second = it)) }
        noObservedAdverseTextField?.let { pairList.add(Pair(first = noObservedAdverseLabel, second = it)) }
        lowestObserveTextField?.let { pairList.add(Pair(first = lowestObserveLabel, second = it)) }
        acceptableOperatorTextField?.let { pairList.add(Pair(first = acceptableOperatorLabel, second = it)) }
        acuteReferenceDoseTextField?.let { pairList.add(Pair(first = acuteReferenceDoseLabel, second = it)) }
        acceptableDailyIntakeTextField?.let { pairList.add(Pair(first = acceptableDailyIntakeLabel, second = it)) }
        indSumField?.let { pairList.add(Pair(first = indSumLabel, second = it)) }
        labNameTextField?.let { pairList.add(Pair(first = labNameLabel, second = it)) }
        labCountryField?.let { pairList.add(Pair(first = labCountryLabel, second = it)) }
        detectionLimitTextField?.let { pairList.add(Pair(first = detectionLimitLabel, second = it)) }
        quantificationLimitTextField?.let { pairList.add(Pair(first = quantificationLimitLabel, second = it)) }
        leftCensoredDataTextField?.let { pairList.add(Pair(first = leftCensoredDataLabel, second = it)) }
        contaminationRangeTextField?.let { pairList.add(Pair(first = contaminationRangeLabel, second = it)) }

        addGridComponents(pairs = pairList)
    }

    fun toHazard(): Hazard {
        // Collect mandatory properties first.
        /*
        TODO: safe-cast comboboxes temporarily to empty strings.
        Should be validated so that one item is always selected
         */
        val type = hazardTypeField.selectedItem as? String ?: ""
        val hazardName = hazardNameField.selectedItem as? String ?: ""
        val hazardUnit = hazardUnitField.selectedItem as? String ?: ""

        val hazard = Hazard(hazardType = type, hazardName = hazardName, hazardUnit = hazardUnit)

        hazard.hazardDescription = hazardDescriptionTextArea?.text
        hazard.adverseEffect = adverseEffectTextField?.text
        hazard.origin = originTextField?.text
        hazard.benchmarkDose = bmdTextField?.text
        hazard.maximumResidueLimit = maxResidueLimitTextField?.text
        hazard.noObservedAdverse = noObservedAdverseTextField?.text
        hazard.acceptableOperator = acceptableOperatorTextField?.text
        hazard.acuteReferenceDose = acuteReferenceDoseTextField?.text
        hazard.acceptableDailyIntake = acceptableDailyIntakeTextField?.text
        hazard.hazardIndSum = indSumField?.selectedItem as? String ?: ""
        hazard.laboratoryName = labNameTextField?.text
        hazard.laboratoryCountry = labCountryField?.selectedItem as? String ?: ""
        hazard.detectionLimit = detectionLimitTextField?.text
        hazard.quantificationLimit = quantificationLimitTextField?.text
        hazard.leftCensoredData = leftCensoredDataTextField?.text
        hazard.rangeOfContamination = contaminationRangeTextField?.text

        return hazard
    }

    override fun validatePanel(): List<String> {
        val errors = mutableListOf<String>()
        if (!hazardNameField.hasValidValue())
            errors.add("Missing ${messages.getString("GM.EditHazardPanel.hazardNameLabel")}")
        if (!hazardTypeField.hasValidValue())
            errors.add("Missing ${messages.getString("GM.EditHazardPanel.hazardTypeLabel")}")
        if (!hazardUnitField.hasValidValue())
            errors.add("Missing ${messages.getString("GM.EditHazardPanel.hazardUnitLabel")}")

        return errors
    }
}


class EditModelEquationPanel(equation: ModelEquation? = null, isAdvanced: Boolean) : ValidatablePanel() {

    val equationNameLabel = createLabel(text = messages.getString("GM.EditModelEquationPanel.nameLabel"),
            tooltip = messages.getString("GM.EditModelEquationPanel.nameTooltip"))
    val equationNameTextField = JTextField(30)

    val equationClassLabel = createLabel(text = messages.getString("GM.EditModelEquationPanel.classLabel"),
            tooltip = messages.getString("GM.EditModelEquationPanel.classTooltip"))
    val equationClassTextField = if (isAdvanced) JTextField(30) else null

    val scriptLabel = createLabel(text = messages.getString("GM.EditModelEquationPanel.scriptLabel"),
            tooltip = messages.getString("GM.EditModelEquationPanel.scriptTooltip"))
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
        if (!equationNameTextField.hasValidValue())
            errors.add("Missing ${messages.getString("GM.EditModelEquationPanel.nameLabel")}")
        if (!scriptTextArea.hasValidValue())
            errors.add("Missing ${messages.getString("GM.EditModelEquationPanel.scriptLabel")}")

        return errors
    }
}

// TODO: idTextField <- Create UUID automatically
class EditParameterPanel(parameter: Parameter? = null, isAdvanced: Boolean) : ValidatablePanel() {

    val idLabel = createLabel(text = messages.getString("GM.EditParameterPanel.idLabel"),
            tooltip = messages.getString("GM.EditParameterPanel.idTooltip"))
    val idTextField = JTextField(30)

    // TODO: classificationComboBox is a ComboBox and in the GUI appears a Text entry instead
    val classificationLabel = createLabel(text = messages.getString("GM.EditParameterPanel.classificationLabel"),
            tooltip = messages.getString("GM.EditParameterPanel.classificationTooltip"))
    val classificationField = AutoSuggestField(10)

    val nameLabel = createLabel(text = messages.getString("GM.EditParameterPanel.parameterNameLabel"),
            tooltip = messages.getString("GM.EditParameterPanel.parameterNameTooltip"))
    val nameTextField = JTextField(30)

    val descriptionLabel = createLabel(text = messages.getString("GM.EditParameterPanel.descriptionLabel"),
            tooltip = messages.getString("GM.EditParameterPanel.descriptionTooltip"))
    val descriptionTextArea = if (isAdvanced) JTextArea(5, 30) else null

    val typeLabel = createLabel(text = messages.getString("GM.EditParameterPanel.typeLabel"),
            tooltip = messages.getString("GM.EditParameterPanel.typeTooltip"))
    val typeField = if (isAdvanced) AutoSuggestField(10) else null

    val unitLabel = createLabel(text = messages.getString("GM.EditParameterPanel.unitLabel"),
            tooltip = messages.getString("GM.EditParameterPanel.unitTooltip"))
    val unitField = AutoSuggestField(10)

    val unitCategoryLabel = createLabel(text = messages.getString("GM.EditParameterPanel.unitCategoryLabel"),
            tooltip = messages.getString("GM.EditParameterPanel.unitCategoryTooltip"))
    val unitCategoryField = AutoSuggestField(10)

    val dataTypeLabel = createLabel(text = messages.getString("GM.EditParameterPanel.dataTypeLabel"),
            tooltip = messages.getString("GM.EditParameterPanel.dataTypeTooltip"))
    val dataTypeField = AutoSuggestField(10)

    val sourceLabel = createLabel(text = messages.getString("GM.EditParameterPanel.sourceLabel"),
            tooltip = messages.getString("GM.EditParameterPanel.sourceTooltip"))
    val sourceField = if (isAdvanced) AutoSuggestField(10) else null

    val subjectLabel = createLabel(text = messages.getString("GM.EditParameterPanel.subjectLabel"),
            tooltip = messages.getString("GM.EditParameterPanel.subjectTooltip"))
    val subjectField = if (isAdvanced) AutoSuggestField(10) else null

    val distributionLabel = createLabel(text = messages.getString("GM.EditParameterPanel.distributionLabel"),
            tooltip = messages.getString("GM.EditParameterPanel.distributionTooltip"))
    val distributionField = if (isAdvanced) AutoSuggestField(10) else null

    val valueLabel = createLabel(text = messages.getString("GM.EditParameterPanel.valueLabel"),
            tooltip = messages.getString("GM.EditParameterPanel.valueTooltip"))
    val valueTextField = if (isAdvanced) JTextField(30) else null

    val referenceLabel = createLabel(text = messages.getString("GM.EditParameterPanel.referenceLabel"),
            tooltip = messages.getString("GM.EditParameterPanel.referenceTooltip"))
    val referenceTextField = if (isAdvanced) JTextField(30) else null

    val variabilitySubjectLabel = createLabel(text = messages.getString("GM.EditParameterPanel.variabilitySubjectLabel"),
            tooltip = messages.getString("GM.EditParameterPanel.variabilitySubjectTooltip"))
    val variabilitySubjectTextArea = if (isAdvanced) JTextArea(5, 30) else null

    val applicabilityLabel = createLabel(text = messages.getString("GM.EditParameterPanel.applicabilityLabel"),
            tooltip = messages.getString("GM.EditParameterPanel.applicabilityTooltip"))
    val applicabilityTextArea = if (isAdvanced) JTextArea(5, 30) else null

    val errorLabel = createLabel(text = messages.getString("GM.EditParameterPanel.errorLabel"),
            tooltip = messages.getString("GM.EditParameterPanel.errorTooltip"))
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

        val pairs = mutableListOf<Pair<JLabel, JComponent>>()
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
        if (!idTextField.hasValidValue())
            errors.add("Missing ${messages.getString("GM.EditParameterPanel.idLabel")}")
        if (!classificationField.hasValidValue())
            errors.add("Missing ${messages.getString("GM.EditParameterPanel.classificationLabel")}")
        if (!nameTextField.hasValidValue())
            errors.add("Missing ${messages.getString("GM.EditParameterPanel.parameterNameLabel")}")
        if (!unitField.hasValidValue())
            errors.add("Missing ${messages.getString("GM.EditParameterPanel.unitLabel")}")
        if (!unitCategoryField.hasValidValue())
            errors.add("Missing ${messages.getString("GM.EditParameterPanel.unitCategoryLabel")}")
        if (!dataTypeField.hasValidValue())
            errors.add("Missing ${messages.getString("GM.EditParameterPanel.dataTypeLabel")}")

        return errors
    }
}

class EditPopulationGroupPanel(populationGroup: PopulationGroup? = null, isAdvanced: Boolean)
    : ValidatablePanel() {

    private val populationNameTextField = JTextField(30)
    private val targetPopulationTextField = if (isAdvanced) JTextField(30) else null
    private val populationSpanTextField = if (isAdvanced) JTextField(30) else null
    private val populationDescriptionTextArea = if (isAdvanced) JTextField(30) else null
    private val populationAgeTextField = if (isAdvanced) JTextField(30) else null
    private val populationGenderTextField = if (isAdvanced) JTextField(30) else null
    private val bmiTextField = if (isAdvanced) JTextField(30) else null
    private val specialDietGroupTextField = if (isAdvanced) JTextField(30) else null
    private val patternConsumptionTextField = if (isAdvanced) JTextField(30) else null
    private val regionComboBox = if (isAdvanced) JComboBox<String>() else null
    private val countryComboBox = if (isAdvanced) JComboBox<String>() else null
    private val riskAndPopulationTextField = if (isAdvanced) JTextField(30) else null
    private val seasonTextField = if (isAdvanced) JTextField(30) else null

    init {
        // Populate interface if `populationGroup` is passed
        populationGroup?.let {
            populationNameTextField.text = it.populationName
            targetPopulationTextField?.text = it.targetPopulation
            populationSpanTextField?.text = it.populationSpan[0]
            populationDescriptionTextArea?.text = it.populationDescription[0]
            populationAgeTextField?.text = it.populationAge[0]
            populationGenderTextField?.text = it.populationAge[0]
            bmiTextField?.text = it.bmi[0]
            specialDietGroupTextField?.text = it.specialDietGroups[0]
            patternConsumptionTextField?.text = it.patternConsumption[0]
            regionComboBox?.selectedItem = it.region
            countryComboBox?.selectedItem = it.country
            riskAndPopulationTextField?.text = it.populationRiskFactor[0]
            seasonTextField?.text = it.season[0]
        }

        initUI()
    }

    private fun initUI() {

        // Create labels
        val populationNameLabel = createLabel(
                text = messages.getString("GM.EditPopulationGroupPanel.populationNameLabel"),
                tooltip = messages.getString("GM.EditPopulationGroupPanel.populationNameTooltip"))
        val targetPopulationLabel = createLabel(
                text = messages.getString("GM.EditPopulationGroupPanel.targetPopulationLabel"),
                tooltip = messages.getString("GM.EditPopulationGroupPanel.targetPopulationTooltip"))
        val populationSpanLabel = createLabel(
                text = messages.getString("GM.EditPopulationGroupPanel.populationSpanLabel"),
                tooltip = messages.getString("GM.EditPopulationGroupPanel.populationSpanTooltip"))
        val populationDescriptionLabel = createLabel(
                text = messages.getString("GM.EditPopulationGroupPanel.populationDescriptionLabel"),
                tooltip = messages.getString("GM.EditPopulationGroupPanel.populationDescriptionTooltip"))
        val populationAgeLabel = createLabel(
                text = messages.getString("GM.EditPopulationGroupPanel.populationAgeLabel"),
                tooltip = messages.getString("GM.EditPopulationGroupPanel.populationAgeTooltip"))
        val populationGenderLabel = createLabel(
                text = messages.getString("GM.EditPopulationGroupPanel.populationGenderLabel"),
                tooltip = messages.getString("GM.EditPopulationGroupPanel.populationGenderTooltip"))
        val bmiLabel = createLabel(
                text = messages.getString("GM.EditPopulationGroupPanel.bmiLabel"),
                tooltip = messages.getString("GM.EditPopulationGroupPanel.bmiTooltip"))
        val specialDietGroupLabel = createLabel(
                text = messages.getString("GM.EditPopulationGroupPanel.specialDietGroupsLabel"),
                tooltip = messages.getString("GM.EditPopulationGroupPanel.specialDietGroupsTooltip"))
        val patternConsumptionLabel = createLabel(
                text = messages.getString("GM.EditPopulationGroupPanel.patternConsumptionLabel"),
                tooltip = messages.getString("GM.EditPopulationGroupPanel.patternConsumptionTooltip"))
        val regionLabel = createLabel(
                text = messages.getString("GM.EditPopulationGroupPanel.regionLabel"),
                tooltip = messages.getString("GM.EditPopulationGroupPanel.regionTooltip"))
        val countryLabel = createLabel(
                text = messages.getString("GM.EditPopulationGroupPanel.countryLabel"),
                tooltip = messages.getString("GM.EditPopulationGroupPanel.countryTooltip"))
        val riskAndPopulationLabel = createLabel(
                text = messages.getString("GM.EditPopulationGroupPanel.riskAndPopulationLabel"),
                tooltip = messages.getString("GM.EditPopulationGroupPanel.riskAndPopulationTooltip"))
        val seasonLabel = createLabel(
                text = messages.getString("GM.EditPopulationGroupPanel.seasonLabel"),
                tooltip = messages.getString("GM.EditPopulationGroupPanel.seasonTooltip"))

        // init combo boxes
        regionComboBox?.let { vocabs["Region"]?.forEach(it::addItem) }
        countryComboBox?.let { vocabs["Country"]?.forEach(it::addItem) }

        val pairList = mutableListOf<Pair<JLabel, JComponent>>()
        pairList.add(Pair(first = populationNameLabel, second = populationNameTextField))
        targetPopulationTextField?.let { pairList.add(Pair(first = targetPopulationLabel, second = it)) }
        populationSpanTextField?.let { pairList.add(Pair(first = populationSpanLabel, second = it)) }
        populationDescriptionTextArea?.let { pairList.add(Pair(first = populationDescriptionLabel, second = it)) }
        populationAgeTextField?.let { pairList.add(Pair(first = populationAgeLabel, second = it)) }
        populationGenderTextField?.let { pairList.add(Pair(first = populationGenderLabel, second = it)) }
        bmiTextField?.let { pairList.add(Pair(first = bmiLabel, second = it)) }
        specialDietGroupTextField?.let { pairList.add(Pair(first = specialDietGroupLabel, second = it)) }
        patternConsumptionTextField?.let { pairList.add(Pair(first = patternConsumptionLabel, second = it)) }
        regionComboBox?.let { pairList.add(Pair(first = regionLabel, second = it)) }
        countryComboBox?.let { pairList.add(Pair(first = countryLabel, second = it)) }
        riskAndPopulationTextField?.let { pairList.add(Pair(first = riskAndPopulationLabel, second = it)) }
        seasonTextField?.let { pairList.add(Pair(first = seasonLabel, second = it)) }

        addGridComponents(pairs = pairList)
    }

    fun toPopulationGroup(): PopulationGroup {

        val populationGroup = PopulationGroup(populationName = populationNameTextField.text)
        populationGroup.targetPopulation = targetPopulationTextField?.text
        populationSpanTextField?.let { populationGroup.populationSpan.add(it.text) }
        populationDescriptionTextArea?.let { populationGroup.populationDescription.add(it.text) }
        populationAgeTextField?.let { populationGroup.populationAge.add(it.text) }
        populationGroup.populationGender = populationGenderTextField?.text
        bmiTextField?.let { populationGroup.bmi.add(it.text) }
        specialDietGroupTextField?.let { populationGroup.specialDietGroups.add(it.text) }
        patternConsumptionTextField?.let { populationGroup.patternConsumption.add(it.text) }
        regionComboBox?.selectedObjects?.forEach { populationGroup.region.add(it as String) }
        countryComboBox?.selectedObjects?.forEach { populationGroup.country.add(it as String) }
        riskAndPopulationTextField?.let { populationGroup.populationRiskFactor.add(it.text) }
        seasonTextField?.let { populationGroup.season.add(it.text) }

        return populationGroup
    }

    override fun validatePanel(): List<String> {
        val errors = mutableListOf<String>()
        if (!populationNameTextField.hasValidValue())
            errors.add("Missing ${messages.getString("GM.EditPopulationGroupPanel.populationNameLabel")}")
        return errors
    }
}

class EditProductPanel(product: Product? = null, isAdvanced: Boolean) : ValidatablePanel() {

    // Fields. null if simple mode
    private val envNameField = AutoSuggestField(10)
    private val envDescriptionTextArea = if (isAdvanced) JTextArea(5, 30) else null
    private val envUnitField = AutoSuggestField(10)
    private val productionMethodComboBox = if (isAdvanced) JComboBox<String>() else null
    private val packagingComboBox = if (isAdvanced) JComboBox<String>() else null
    private val productTreatmentComboBox = if (isAdvanced) JComboBox<String>() else null
    private val originCountryField = if (isAdvanced) AutoSuggestField(10) else null
    private val originAreaField = if (isAdvanced) AutoSuggestField(10) else null
    private val fisheriesAreaField = if (isAdvanced) AutoSuggestField(10) else null
    private val productionDateChooser = if (isAdvanced) FixedJDateChooser() else null
    private val expirationDateChooser = if (isAdvanced) FixedJDateChooser() else null

    init {
        product?.let {
            envNameField.selectedItem = it.environmentName
            envDescriptionTextArea?.text = it.environmentDescription
            envUnitField.selectedItem = it.environmentUnit

            /*
            TODO: init value of packagingComboBox
            TODO: init value of productTreatmentComboBox
            I ignore currently how to set a number of selected items in Swing ComboBox. KNIME include a list
            selection widget that supports this but lacks the autocompletion feature from the FCL widget.

            KNIME widget would be a temporary solution but ideally a new widget based on the KNIME widget and including
               the autocompletion feature should be developed.
            */
            originCountryField?.selectedItem = it.originCountry
            originAreaField?.selectedItem = it.areaOfOrigin
            fisheriesAreaField?.selectedItem = it.fisheriesArea
            productionDateChooser?.date = it.productionDate
            expirationDateChooser?.date = it.expirationDate
        }

        initUI()
    }

    private fun initUI() {

        // Create labels
        val envNameLabel = createLabel(text = messages.getString("GM.EditProductPanel.envNameLabel")
                , tooltip = messages.getString("GM.EditProductPanel.envNameTooltip"))
        val envDescriptionLabel = createLabel(text = messages.getString("GM.EditProductPanel.envDescriptionLabel"),
                tooltip = messages.getString("GM.EditProductPanel.envDescriptionTooltip"))
        val envUnitLabel = createLabel(text = messages.getString("GM.EditProductPanel.envUnitLabel"),
                tooltip = messages.getString("GM.EditProductPanel.envUnitTooltip"))
        val productionMethodLabel = createLabel(text = messages.getString("GM.EditProductPanel.productionMethodLabel"),
                tooltip = messages.getString("GM.EditProductPanel.productionMethodTooltip"))
        val packagingLabel = createLabel(text = messages.getString("GM.EditProductPanel.packagingLabel"),
                tooltip = messages.getString("GM.EditProductPanel.packagingTooltip"))
        val productTreatmentLabel = createLabel(text = messages.getString("GM.EditProductPanel.productTreatmentLabel"),
                tooltip = messages.getString("GM.EditProductPanel.productTreatmentTooltip"))
        val originCountryLabel = createLabel(text = messages.getString("GM.EditProductPanel.originCountryLabel"),
                tooltip = messages.getString("GM.EditProductPanel.originCountryTooltip"))
        val originAreaLabel = createLabel(text = messages.getString("GM.EditProductPanel.originAreaLabel"),
                tooltip = messages.getString("GM.EditProductPanel.originAreaTooltip"))
        val fisheriesAreaLabel = createLabel(text = messages.getString("GM.EditProductPanel.fisheriesAreaLabel"),
                tooltip = messages.getString("GM.EditProductPanel.fisheriesAreaTooltip"))
        val productionDateLabel = createLabel(text = messages.getString("GM.EditProductPanel.productionDateLabel"),
                tooltip = messages.getString("GM.EditProductPanel.productionDateTooltip"))
        val expirationDateLabel = createLabel(text = messages.getString("GM.EditProductPanel.expirationDateLabel"),
                tooltip = messages.getString("GM.EditProductPanel.expirationDateTooltip"))

        // Init combo boxes
        envNameField.setPossibleValues(vocabs.get("Product-matrix name"))
        envUnitField.setPossibleValues(vocabs.get("Product-matrix unit"))
        productionMethodComboBox?.let { vocabs["Method of production"]?.forEach(it::addItem) }
        packagingComboBox?.let { vocabs["Packaging"]?.forEach(it::addItem) }
        productTreatmentComboBox?.let { vocabs["Product treatment"]?.forEach(it::addItem) }
        originCountryField?.setPossibleValues(vocabs["Country of origin"])
        originAreaField?.setPossibleValues(vocabs["Area of origin"])
        fisheriesAreaField?.setPossibleValues(vocabs["Fisheries area"])

        val pairList = mutableListOf<Pair<JLabel, JComponent>>()
        pairList.add(Pair(first = envNameLabel, second = envNameField))
        envDescriptionTextArea?.let { pairList.add(Pair(first = envDescriptionLabel, second = it)) }
        pairList.add(Pair(first = envUnitLabel, second = envUnitField))
        productionMethodComboBox?.let { pairList.add(Pair(first = productionMethodLabel, second = it)) }
        packagingComboBox?.let { pairList.add(Pair(first = packagingLabel, second = it)) }
        productTreatmentComboBox?.let { pairList.add(Pair(first = productTreatmentLabel, second = it)) }
        originCountryField?.let { pairList.add(Pair(first = originCountryLabel, second = it)) }
        originAreaField?.let { pairList.add(Pair(first = originAreaLabel, second = it)) }
        fisheriesAreaField?.let { pairList.add(Pair(first = fisheriesAreaLabel, second = it)) }
        productionDateChooser?.let { pairList.add(Pair(first = productionDateLabel, second = it)) }
        expirationDateChooser?.let { pairList.add(Pair(first = expirationDateLabel, second = it)) }

        addGridComponents(pairs = pairList)
    }

    fun toProduct(): Product {

        val envName = envNameField.selectedItem as? String ?: ""
        val envUnit = envUnitField.selectedItem as? String ?: ""

        val product = Product(environmentName = envName, environmentUnit = envUnit)
        product.environmentDescription = envDescriptionTextArea?.text
        packagingComboBox?.selectedObjects?.forEach { product.packaging.add(it as String) }
        productionMethodComboBox?.selectedObjects?.forEach { product.productTreatment.add(it as String) }

        product.originCountry = originCountryField?.selectedItem as String?
        product.areaOfOrigin = originAreaField?.selectedItem as String?
        product.fisheriesArea = fisheriesAreaField?.selectedItem as String?
        product.productionDate = productionDateChooser?.date
        product.expirationDate = expirationDateChooser?.date

        return product
    }

    override fun validatePanel(): List<String> {
        val errors = mutableListOf<String>()
        if (!envNameField.hasValidValue())
            errors.add("Missing ${messages.getString("GM.EditProductPanel.envNameLabel")}")
        if (!envUnitField.hasValidValue())
            errors.add("Missing ${messages.getString("GM.EditProductPanel.envUnitLabel")}")

        return errors
    }
}


class EditReferencePanel(ref: Record? = null, isAdvanced: Boolean) : ValidatablePanel() {

    private val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd")

    // fields. null if advanced
    private val isReferenceDescriptionCheckBox = JCheckBox("Is reference description *")
    private val typeComboBox = if (isAdvanced) JComboBox<Type>() else null
    private val dateChooser = if (isAdvanced) FixedJDateChooser() else null
    private val pmidTextField = if (isAdvanced) JTextField(30) else null
    private val doiTextField = JTextField(30)
    private val authorListTextField = if (isAdvanced) JTextField(30) else null
    private val titleTextField = JTextField(30)
    private val abstractTextArea = if (isAdvanced) JTextArea(5, 30) else null
    private val journalTextField = if (isAdvanced) JTextField(30) else null
    private val volumeSpinnerModel = if (isAdvanced) createSpinnerIntegerModel() else null
    private val issueSpinnerModel = if (isAdvanced) createSpinnerIntegerModel() else null
    private val pageTextField = if (isAdvanced) JTextField(30) else null
    private val statusTextField = if (isAdvanced) JTextField(30) else null
    private val websiteTextField = if (isAdvanced) JTextField(30) else null
    private val commentTextField = if (isAdvanced) JTextArea(5, 30) else null


    init {
        // Add types to typeComboBox and set the selected type
        typeComboBox?.let {
            com.gmail.gcolaianni5.jris.bean.Type.values().forEach(it::addItem)
            it.selectedItem = ref?.type
        }

        initUI()

        // Populate interface if ref is provided
        ref?.let {
            it.date?.let { dateChooser?.date = dateFormat.parse(it) }
            doiTextField.text = it.doi
            authorListTextField?.text = it.authors?.joinToString(";")
            titleTextField.text = it.title
            abstractTextArea?.text = it.abstr
            journalTextField?.text = it.secondaryTitle
            if (it.volumeNumber != null) volumeSpinnerModel?.value = it.volumeNumber
            if (it.issueNumber != null) issueSpinnerModel?.value = it.issueNumber
            websiteTextField?.text = it.websiteLink
        }
    }

    private fun initUI() {

        val pairList = mutableListOf<Pair<JLabel, JComponent>>()
        typeComboBox?.let {
            val typeLabel = JLabel(messages.getString("GM.EditReferencePanel.typeLabel"))
            pairList.add(Pair(first = typeLabel, second = it))
        }
        dateChooser?.let {
            val dateLabel = JLabel(messages.getString("GM.EditReferencePanel.dateLabel"))
            pairList.add(Pair(first = dateLabel, second = it))
        }
        pmidTextField?.let {
            val pmidLabel = JLabel(messages.getString("GM.EditReferencePanel.pmidLabel"))
            pairList.add(Pair(first = pmidLabel, second = it))
        }

        pairList.add(Pair(first = JLabel(messages.getString("GM.EditReferencePanel.doiLabel")), second = doiTextField))

        authorListTextField?.let {
            val authorListLabel = JLabel(messages.getString("GM.EditReferencePanel.authorListLabel"))
            pairList.add(Pair(first = authorListLabel, second = it))
        }

        pairList.add(Pair(first = JLabel(messages.getString("GM.EditReferencePanel.titleLabel")), second = titleTextField))

        abstractTextArea?.let {
            val abstractLabel = JLabel(messages.getString("GM.EditReferencePanel.abstractLabel"))
            pairList.add(Pair(first = abstractLabel, second = it))
        }

        journalTextField?.let {
            val journalLabel = JLabel(messages.getString("GM.EditReferencePanel.journalLabel"))
            pairList.add(Pair(first = journalLabel, second = it))
        }

        volumeSpinnerModel?.let {
            val volumeLabel = JLabel(messages.getString("GM.EditReferencePanel.volumeLabel"))
            val spinner = createSpinner(it)
            pairList.add(Pair(first = volumeLabel, second = spinner))
        }

        issueSpinnerModel?.let {
            val issueLabel = JLabel(messages.getString("GM.EditReferencePanel.issueLabel"))
            val spinner = createSpinner(it)
            pairList.add(Pair(first = issueLabel, second = spinner))
        }

        pageTextField?.let {
            val pageLabel = JLabel(messages.getString("GM.EditReferencePanel.pageLabel"))
            pairList.add(Pair(first = pageLabel, second = it))
        }

        statusTextField?.let {
            val statusLabel = JLabel(messages.getString("GM.EditReferencePanel.statusLabel"))
            pairList.add(Pair(first = statusLabel, second = it))
        }

        websiteTextField?.let {
            val websiteLabel = JLabel(messages.getString("GM.EditReferencePanel.websiteLabel"))
            pairList.add(Pair(first = websiteLabel, second = it))
        }

        commentTextField?.let {
            val commentLabel = JLabel(messages.getString("GM.EditReferencePanel.commentLabel"))
            pairList.add(Pair(first = commentLabel, second = it))
        }

        add(comp = isReferenceDescriptionCheckBox, gridy = 0, gridx = 0)
        for ((index, pair) in pairList.withIndex()) {
            val label = pair.first
            val field = pair.second
            label.labelFor = field
            add(comp = label, gridy = index + 1, gridx = 0)
            add(comp = field, gridy = index + 1, gridx = 1)
        }
    }

    fun toRecord(): Record {
        val risRecord = Record()
        // TODO: can't do anything with ReferencePanel.isReferenceDescriptionCheckBox yet
        risRecord.type = typeComboBox?.selectedItem as? com.gmail.gcolaianni5.jris.bean.Type
        risRecord.date = dateChooser?.date?.toString()
        // TODO: can't do anything with PubMedId yet
        risRecord.doi = doiTextField.text
        if (!authorListTextField?.text.isNullOrEmpty()) {
            authorListTextField?.text?.split(";")?.forEach { risRecord.addAuthor(it) }
        }
        risRecord.title = titleTextField.text
        risRecord.abstr = abstractTextArea?.text
        risRecord.secondaryTitle = journalTextField?.text
        risRecord.volumeNumber = volumeSpinnerModel?.number?.toString()
        risRecord.issueNumber = issueSpinnerModel?.number?.toInt()
        // TODO: can't do anything with status yet
        risRecord.websiteLink = websiteTextField?.text
        // TODO: can't do anything with comment yet

        return risRecord
    }

    override fun validatePanel(): List<String> {
        val errorList = mutableListOf<String>()
        if (!doiTextField.hasValidValue())
            errorList.add("Missing ${messages.getString("GM.EditReferencePanel.doiLabel")}")
        if (!titleTextField.hasValidValue())
            errorList.add("Missing ${messages.getString("GM.EditReferencePanel.titleLabel")}")

        return errorList
    }
}

class EditStudySamplePanel(studySample: StudySample? = null, isAdvanced: Boolean) : ValidatablePanel() {

    // Fields. null if advanced mode
    val sampleNameTextField = JTextField(30)
    val moisturePercentageSpinnerModel = if (isAdvanced) createSpinnerPercentageModel() else null
    val fatPercentageSpinnerModel = if (isAdvanced) createSpinnerPercentageModel() else null
    val sampleProtocolTextField = JTextField(30)
    val samplingStrategyField = if (isAdvanced) AutoSuggestField(10) else null
    val samplingTypeField = if (isAdvanced) AutoSuggestField(10) else null
    val samplingMethodField = if (isAdvanced) AutoSuggestField(10) else null
    val samplingPlanTextField = JTextField(30)
    val samplingWeightTextField = JTextField(30)
    val samplingSizeTextField = JTextField(30)
    val lotSizeUnitField = if (isAdvanced) AutoSuggestField(10) else null
    val samplingPointField = if (isAdvanced) AutoSuggestField(10) else null

    init {

        // Populate UI with passed `studySample`
        studySample?.let {
            sampleNameTextField.text = it.sample
            if (it.moisturePercentage != null) moisturePercentageSpinnerModel?.value = it.moisturePercentage
            if (it.fatPercentage != null) fatPercentageSpinnerModel?.value = it.fatPercentage
            sampleProtocolTextField.text = it.collectionProtocol
            samplingStrategyField?.selectedItem = it.samplingStrategy
            samplingTypeField?.selectedItem = it.samplingProgramType
            samplingMethodField?.selectedItem = it.samplingMethod
            samplingPlanTextField.text = it.samplingPlan
            samplingWeightTextField.text = it.samplingWeight
            samplingSizeTextField.text = it.samplingSize
            lotSizeUnitField?.selectedItem = it.lotSizeUnit
            samplingPointField?.selectedItem = it.samplingPoint
        }

        initUI()
    }

    private fun initUI() {

        // Create labels
        val sampleNameLabel = createLabel(text = messages.getString("GM.EditStudySamplePanel.sampleNameLabel"),
                tooltip = messages.getString("GM.EditStudySamplePanel.sampleNameTooltip"))
        val moisturePercentageLabel = createLabel(
                text = messages.getString("GM.EditStudySamplePanel.moisturePercentageLabel"),
                tooltip = messages.getString("GM.EditStudySamplePanel.moisturePercentageTooltip"))
        val fatPercentageLabel = createLabel(
                text = messages.getString("GM.EditStudySamplePanel.fatPercentageLabel"),
                tooltip = messages.getString("GM.EditStudySamplePanel.fatPercentageTooltip"))
        val sampleProtocolLabel = createLabel(
                text = messages.getString("GM.EditStudySamplePanel.sampleProtocolLabel"),
                tooltip = messages.getString("GM.EditStudySamplePanel.sampleProtocolTooltip"))
        val samplingStrategyLabel = createLabel(
                text = messages.getString("GM.EditStudySamplePanel.samplingStrategyLabel"),
                tooltip = messages.getString("GM.EditStudySamplePanel.samplingStrategyTooltip"))
        val samplingTypeLabel = createLabel(
                text = messages.getString("GM.EditStudySamplePanel.samplingTypeLabel"),
                tooltip = messages.getString("GM.EditStudySamplePanel.samplingTypeTooltip"))
        val samplingMethodLabel = createLabel(
                text = messages.getString("GM.EditStudySamplePanel.samplingMethodLabel"),
                tooltip = messages.getString("GM.EditStudySamplePanel.samplingMethodTooltip"))
        val samplingPlanLabel = createLabel(
                text = messages.getString("GM.EditStudySamplePanel.samplingPlanLabel"),
                tooltip = messages.getString("GM.EditStudySamplePanel.samplingPlanTooltip"))
        val samplingWeightLabel = createLabel(
                text = messages.getString("GM.EditStudySamplePanel.samplingWeightLabel"),
                tooltip = messages.getString("GM.EditStudySamplePanel.samplingWeightTooltip"))
        val samplingSizeLabel = createLabel(
                text = messages.getString("GM.EditStudySamplePanel.samplingSizeLabel"),
                tooltip = messages.getString("GM.EditStudySamplePanel.samplingSizeTooltip"))
        val lotSizeUnitLabel = createLabel(
                text = messages.getString("GM.EditStudySamplePanel.lotSizeUnitLabel"),
                tooltip = messages.getString("GM.EditStudySamplePanel.lotSizeUnitTooltip"))
        val samplingPointLabel = createLabel(
                text = messages.getString("GM.EditStudySamplePanel.samplingPointLabel"),
                tooltip = messages.getString("GM.EditStudySamplePanel.samplingPointTooltip"))

        // init combo boxes
        samplingStrategyField?.setPossibleValues(vocabs["Sampling strategy"])
        samplingTypeField?.setPossibleValues(vocabs["Type of sampling program"])
        samplingMethodField?.setPossibleValues(vocabs["Sampling method"])
        lotSizeUnitField?.setPossibleValues(vocabs["Lot size unit"])
        samplingPointField?.setPossibleValues(vocabs["Sampling point"])

        val pairList = mutableListOf<Pair<JLabel, JComponent>>()
        pairList.add(Pair(first = sampleNameLabel, second = sampleNameTextField))
        moisturePercentageSpinnerModel?.let { pairList.add(Pair(first = moisturePercentageLabel, second = createSpinner(it))) }
        fatPercentageSpinnerModel?.let { pairList.add(Pair(first = fatPercentageLabel, second = createSpinner(it))) }
        pairList.add(Pair(first = sampleProtocolLabel, second = sampleProtocolTextField))
        samplingStrategyField?.let { pairList.add(Pair(first = samplingStrategyLabel, second = it)) }
        samplingTypeField?.let { pairList.add(Pair(first = samplingTypeLabel, second = it)) }
        samplingMethodField?.let { pairList.add(Pair(first = samplingMethodLabel, second = it)) }
        pairList.add(Pair(first = samplingPlanLabel, second = samplingPlanTextField))
        pairList.add(Pair(first = samplingWeightLabel, second = samplingWeightTextField))
        pairList.add(Pair(first = samplingSizeLabel, second = samplingSizeTextField))
        lotSizeUnitField?.let { pairList.add(Pair(first = lotSizeUnitLabel, second = it)) }
        samplingPointField?.let { pairList.add(Pair(first = samplingPointLabel, second = it)) }

        addGridComponents(pairs = pairList)
    }

    fun toStudySample(): StudySample {
        /*
         TODO: mandatory fields need to be validated
         Mandatory fields are casted to empty strings TEMPORARILY.
          */

        val sampleName = sampleNameTextField.text ?: ""
        val collectionProtocol = sampleProtocolTextField.text ?: ""
        val samplingPlan = samplingPlanTextField.text ?: ""
        val samplingWeight = samplingWeightTextField.text ?: ""
        val samplingSize = samplingSizeTextField.text ?: ""

        val studySample = StudySample(sample = sampleName, collectionProtocol = collectionProtocol,
                samplingPlan = samplingPlan, samplingWeight = samplingWeight, samplingSize = samplingSize)

        studySample.moisturePercentage = moisturePercentageSpinnerModel?.number?.toDouble()
        studySample.fatPercentage = fatPercentageSpinnerModel?.number?.toDouble()
        studySample.samplingStrategy = samplingStrategyField?.selectedItem as String?
        studySample.samplingProgramType = samplingTypeField?.selectedItem as String?
        studySample.samplingMethod = samplingMethodField?.selectedItem as String?
        studySample.lotSizeUnit = lotSizeUnitField?.selectedItem as String?
        studySample.samplingPoint = samplingPointField?.selectedItem as String?

        return studySample
    }

    override fun validatePanel(): List<String> {

        val errors = mutableListOf<String>()
        if (!sampleNameTextField.hasValidValue())
            errors.add("Missing ${messages.getString("GM.EditStudySamplePanel.sampleNameLabel")}")
        if (!sampleProtocolTextField.hasValidValue())
            errors.add("Missing ${messages.getString("GM.EditStudySamplePanel.sampleProtocolLabel")}")
        if (!samplingPlanTextField.hasValidValue())
            errors.add("Missing ${messages.getString("GM.EditStudySamplePanel.samplingPlanLabel")}")
        if (!samplingWeightTextField.hasValidValue())
            errors.add("Missing ${messages.getString("GM.EditStudySamplePanel.samplingWeightLabel")}")
        if (!samplingSizeTextField.hasValidValue())
            errors.add("Missing ${messages.getString("GM.EditStudySamplePanel.samplingSizeLabel")}")

        return errors
    }
}

// Validation methods
internal fun JTextField.hasValidValue() = text.isNotBlank()

internal fun JTextArea.hasValidValue() = text.isNotBlank()

internal fun AutoSuggestField.hasValidValue(): Boolean {
    val field = editor.editorComponent as JTextField
    return field.text.isNotBlank()
}