package de.bund.bfr.rakip.editor

import com.gmail.gcolaianni5.jris.bean.Record
import com.gmail.gcolaianni5.jris.bean.Type
import de.bund.bfr.knime.ui.AutoSuggestField
import de.bund.bfr.rakip.*
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
        setLocationRelativeTo(null)  // center dialog
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
        val nameLabel = createLabel(text = ASSAY_NAME + " *", tooltip = ASSAY_NAME_TOOLTIP)
        val descriptionLabel = createLabel(text = ASSAY_DESC, tooltip = ASSAY_DESC_TOOLTIP)

        val pairList = mutableListOf<Pair<JLabel, JComponent>>()
        pairList.add(Pair(first = nameLabel, second = nameTextField))
        descriptionTextArea?.let { pairList.add(Pair(first = descriptionLabel, second = it)) }

        addGridComponents(pairs = pairList)
    }

    fun toAssay() = Assay(name = nameTextField.text, description = descriptionTextArea?.text)

    override fun validatePanel(): List<String> {
        val errors = mutableListOf<String>()
        if (!nameTextField.hasValidValue()) errors.add("Missing $ASSAY_NAME")
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
        val dataCollectionToolLabel = createLabel(text = DAM_TOOL + " *", tooltip = DAM_TOOL_TOOLTIP)
        val nonConsecutiveOneDayLabel = createLabel(text = DAM_NUMBER_NON_CONSECUTIVE + " *", tooltip = DAM_NUMBER_NON_CONSECUTIVE_TOOLTIP)
        val dietarySoftwareToolLabel = createLabel(text = DAM_SOFTWARE, tooltip = DAM_SOFTWARE_TOOLTIP)
        val foodItemNumberLabel = createLabel(text = DAM_FOOD_ITEM, tooltip = DAM_FOOD_ITEM_TOOLTIP)
        val recordTypeLabel = createLabel(text = DAM_RECORD_TYPE, tooltip = DAM_RECORD_TYPE_TOOLTIP)
        val foodDescriptionLabel = createLabel(text = DAM_FOOD_DESC, tooltip = DAM_FOOD_DESC_TOOLTIP)

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
        if (!dataCollectionToolField.hasValidValue()) errors.add("Missing $DAM_TOOL")
        if (!nonConsecutiveOneDayTextField.hasValidValue()) errors.add("Missing $DAM_NON_CONSECUTIVE")

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
        val hazardTypeLabel = createLabel(text = HAZARD_TYPE + " *", tooltip = HAZARD_TYPE_TOOLTIP)
        val hazardNameLabel = createLabel(HAZARD_NAME + " *", tooltip = HAZARD_NAME_TOOLTIP)
        val hazardDescriptionLabel = createLabel(text = HAZARD_DESCRIPTION, tooltip = HAZARD_DESCRIPTION_TOOLTIP)
        val hazardUnitLabel = createLabel(text = HAZARD_UNIT + " *", tooltip = HAZARD_UNIT_TOOLTIP)
        val adverseEffectLabel = createLabel(text = HAZARD_ADVERSE, tooltip = HAZARD_ADVERSE_TOOLTIP)
        val originLabel = createLabel(text = HAZARD_ORIGIN, tooltip = HAZARD_ORIGIN_TOOLTIP)
        val bmdLabel = createLabel(text = HAZARD_BMD, tooltip = HAZARD_BMD_TOOLTIP)
        val maxResidueLimitLabel = createLabel(text = HAZARD_RESIDUE, tooltip = HAZARD_RESIDUE_TOOLTIP)
        val noObservedAdverseLabel = createLabel(text = HAZARD_NOADVERSE, tooltip = HAZARD_NOADVERSE_TOOLTIP)
        val lowestObserveLabel = createLabel(text = HAZARD_LOWESTADVERSE, tooltip = HAZARD_LOWESTADVERSE_TOOLTIP)
        val acceptableOperatorLabel = createLabel(text = HAZARD_ACCEPTABLEOPERATOR, tooltip = HAZARD_ACCEPTABLEOPERATOR_TOOLTIP)
        val acuteReferenceDoseLabel = createLabel(text = HAZARD_ACUTEDOSE, tooltip = HAZARD_ACUTEDOSE_TOOLTIP)
        val acceptableDailyIntakeLabel = createLabel(text = HAZARD_DAILYINTAKE, tooltip = HAZARD_DAILYINTAKE_TOOLTIP)
        val indSumLabel = createLabel(text = HAZARD_INDSUM, tooltip = HAZARD_INDSUM_TOOLTIP)
        val labNameLabel = createLabel(text = HAZARD_LABNAME, tooltip = HAZARD_LABNAME_TOOLTIP)
        val labCountryLabel = createLabel(text = HAZARD_LABCOUNTRY, tooltip = HAZARD_LABCOUNTRY_TOOLTIP)
        val detectionLimitLabel = createLabel(text = HAZARD_DETECTIONLIM, tooltip = HAZARD_DETECTIONLIM_TOOLTIP)
        val quantificationLimitLabel = createLabel(text = HAZARD_QUANTIFICATIONLIM, tooltip = HAZARD_QUANTIFICATIONLIM_TOOLTIP)
        val leftCensoredDataLabel = createLabel(text = HAZARD_CENSOREDDATA, tooltip = HAZARD_CENSOREDDATA_TOOLTIP)
        val contaminationRangeLabel = createLabel(text = HAZARD_CONTAMINATION, tooltip = HAZARD_CONTAMINATION_TOOLTIP)

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
        if (!hazardNameField.hasValidValue()) errors.add("Missing $HAZARD_NAME")
        if (!hazardTypeField.hasValidValue()) errors.add("Missing $HAZARD_TYPE")
        if (!hazardUnitField.hasValidValue()) errors.add("Missing $HAZARD_UNIT")

        return errors
    }
}


class EditModelEquationPanel(equation: ModelEquation? = null, isAdvanced: Boolean) : ValidatablePanel() {

    val equationNameLabel = createLabel(text = MODEL_EQUATION_NAME + " *", tooltip = MODEL_EQUATION_NAME_TOOLTIP)
    val equationNameTextField = JTextField(30)

    val equationClassLabel = createLabel(text = MODEL_EQUATION_CLASS, tooltip = MODEL_EQUATION_CLASS_TOOLTIP)
    val equationClassTextField = if (isAdvanced) JTextField(30) else null

    val referencePanel = ReferencePanel(refs = equation?.equationReference ?: mutableListOf(), isAdvanced = isAdvanced)

    val scriptLabel = createLabel(text = MODEL_EQUATION_SCRIPT + " *", tooltip = MODEL_EQUATION_SCRIPT_TOOLTIP)
    val scriptTextArea = JTextArea(5, 30)

    init {
        add(comp = equationNameLabel, gridy = 0, gridx = 0)
        add(comp = equationNameTextField, gridy = 0, gridx = 1)

        equationClassTextField?.let {
            add(comp = equationClassLabel, gridy = 1, gridx = 0)
            add(comp = it, gridy = 1, gridx = 1)
        }

        add(comp = referencePanel, gridy = 2, gridx = 0, gridwidth = 2)

        add(comp = scriptLabel, gridy = 3, gridx = 0)
        add(comp = scriptTextArea, gridy = 3, gridx = 1, gridwidth = 2)

        // Initialize with passed modelEquation
        equation?.let {
            equationNameTextField.text = it.equationName
            equationClassTextField?.text = it.equationClass
            // referencePanel is already initialized on declaration
            scriptTextArea.text = it.equation
        }
    }

    override fun validatePanel(): List<String> {
        val errors = mutableListOf<String>()
        if (!equationNameTextField.hasValidValue()) errors.add("Missing $MODEL_EQUATION_NAME")
        if (!scriptTextArea.hasValidValue()) errors.add("Missing $MODEL_EQUATION_SCRIPT")

        return errors
    }

    fun toModelEquation() : ModelEquation {

        // Mandatory properties
        val equationName = equationNameTextField.text
        val equation = scriptTextArea.text
        val modelEquation = ModelEquation(equationName = equationName, equation = equation)

        // Optional properties
        modelEquation.equationClass = equationClassTextField?.text
        referencePanel.refs?.forEach { modelEquation.equationReference.add(element = it) }

        return modelEquation
    }
}

// TODO: idTextField <- Create UUID automatically
class EditParameterPanel(parameter: Parameter? = null, isAdvanced: Boolean) : ValidatablePanel() {

    val idLabel = createLabel(text = PARAMETER_ID + " *", tooltip = PARAMETER_ID_TOOLTIP)
    val idTextField = JTextField(30)

    val classificationLabel = createLabel(text = PARAMETER_CLASIF + " *", tooltip = PARAMETER_CLASIF_TOOLTIP)
    val classificationComboBox = JComboBox<ParameterClassification>(ParameterClassification.values())

    val nameLabel = createLabel(text = PARAMETER_NAME + " *", tooltip = PARAMETER_NAME_TOOLTIP)
    val nameTextField = JTextField(30)

    val descriptionLabel = createLabel(text = PARAMETER_DESC, tooltip = PARAMETER_DESC_TOOLTIP)
    val descriptionTextArea = if (isAdvanced) JTextArea(5, 30) else null

    val typeLabel = createLabel(text = PARAMETER_TYPE, tooltip = PARAMETER_TYPE_TOOLTIP)
    val typeField = if (isAdvanced) AutoSuggestField(10) else null

    val unitLabel = createLabel(text = PARAMETER_UNIT + " *", tooltip = PARAMETER_UNIT_TOOLTIP)
    val unitField = AutoSuggestField(10)

    val unitCategoryLabel = createLabel(text = PARAMETER_UNIT_CATEGORY + " *", tooltip = PARAMETER_UNIT_CATEGORY_TOOLTIP)
    val unitCategoryField = AutoSuggestField(10)

    val dataTypeLabel = createLabel(text = PARAMETER_DATA_TYPE + " *", tooltip = PARAMETER_DATA_TYPE_TOOLTIP)
    val dataTypeField = AutoSuggestField(10)

    val sourceLabel = createLabel(text = PARAMETER_SOURCE, tooltip = PARAMETER_SOURCE_TOOLTIP)
    val sourceField = if (isAdvanced) AutoSuggestField(10) else null

    val subjectLabel = createLabel(text = PARAMETER_SUBJECT, tooltip = PARAMETER_SUBJECT_TOOLTIP)
    val subjectField = if (isAdvanced) AutoSuggestField(10) else null

    val distributionLabel = createLabel(text = PARAMETER_DIST, tooltip = PARAMETER_DIST_TOOLTIP)
    val distributionField = if (isAdvanced) AutoSuggestField(10) else null

    val valueLabel = createLabel(text = PARAMETER_VALUE, tooltip = PARAMETER_VALUE_TOOLTIP)
    val valueTextField = if (isAdvanced) JTextField(30) else null

    val referenceLabel = createLabel(text = PARAMETER_REFERENCE, tooltip = PARAMETER_REFERENCE_TOOLTIP)
    val referenceTextField = if (isAdvanced) JTextField(30) else null

    val variabilitySubjectLabel = createLabel(text = PARAMETER_VARIABILITY, tooltip = PARAMETER_VARIABILITY_TOOLTIP)
    val variabilitySubjectTextArea = if (isAdvanced) JTextArea(5, 30) else null

    val applicabilityLabel = createLabel(text = PARAMETER_APPLICABILITY, tooltip = PARAMETER_APPLICABILITY_TOOLTIP)
    val applicabilityTextArea = if (isAdvanced) JTextArea(5, 30) else null

    val errorLabel = createLabel(text = PARAMETER_ERROR, tooltip = PARAMETER_ERROR_TOOLTIP)
    val errorSpinnerModel = if (isAdvanced) createSpinnerDoubleModel() else null

    init {

        // init combo boxes
        typeField?.setPossibleValues(vocabs["Parameter type"])
        unitField.setPossibleValues(vocabs["Parameter unit"])
        unitCategoryField.setPossibleValues(vocabs["Parameter unit category"])
        dataTypeField.setPossibleValues(vocabs["Parameter data type"])
        sourceField?.setPossibleValues(vocabs["Parameter source"])
        subjectField?.setPossibleValues(vocabs["Parameter subject"])
        distributionField?.setPossibleValues(vocabs["Parameter distribution"])

        val pairs = mutableListOf<Pair<JLabel, JComponent>>()
        pairs.add(Pair(first = idLabel, second = idTextField))
        pairs.add(Pair(first = classificationLabel, second = classificationComboBox))
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

        // Populate interface if ref is provided
        parameter?.let {
            idTextField.text = it.id
            classificationComboBox.selectedItem = it.classification
            nameTextField.text = it.name
            descriptionTextArea?.text = it.description
//            typeField?.selectedItem = it.  // TODO: typeField???
            unitField.selectedItem = it.unit
            unitCategoryField.selectedItem = it.unitCategory
            dataTypeField.selectedItem = it.dataType
            sourceField?.selectedItem = it.source
            subjectField?.selectedItem = it.subject
            distributionField?.selectedItem = it.distribution
            valueTextField?.text = it.value
            referenceTextField?.text = it.reference
            variabilitySubjectTextArea?.text = it.variabilitySubject
            // applicabilityTextArea?.text = it.modelApplicability  // TODO: fix model applicability
            errorSpinnerModel?.value = it.error
        }
    }

    fun toParameter() : Parameter {

        val id = idTextField.text
        val classification = classificationComboBox.selectedItem as ParameterClassification
        val name = nameTextField.text
        val unit = unitField.selectedItem as String
        val unitCategory = unitCategoryField.selectedItem as String
        val dataType = dataTypeField.selectedItem as String
        // TODO get modelApplicability from GUI
        val modelApplicability = mutableListOf<String>()

        val parameter = Parameter(id = id, classification = classification,
                name = name, unit = unit, unitCategory = unitCategory,
                dataType = dataType, modelApplicability = modelApplicability)

        // Optional properties
        parameter.description = descriptionTextArea?.text
        parameter.source = sourceField?.selectedItem as? String
        parameter.subject = subjectField?.selectedItem as? String
        parameter.distribution = distributionField?.selectedItem as? String
        parameter.value = valueTextField?.text
        parameter.reference = referenceTextField?.text
        parameter.variabilitySubject = variabilitySubjectTextArea?.text
        parameter.error = errorSpinnerModel?.number?.toDouble()

        return parameter
    }


    override fun validatePanel(): List<String> {
        val errors = mutableListOf<String>()
        if (!idTextField.hasValidValue()) errors.add("Missing $PARAMETER_ID")
        if (classificationComboBox.selectedItem == -1) errors.add("Missing $PARAMETER_CLASIF")
        if (!nameTextField.hasValidValue()) errors.add("Missing $PARAMETER_NAME")
        if (!unitField.hasValidValue()) errors.add("Missing $PARAMETER_UNIT")
        if (!unitCategoryField.hasValidValue()) errors.add("Missing $PARAMETER_UNIT_CATEGORY")
        if (!dataTypeField.hasValidValue()) errors.add("Missing $PARAMETER_DATA_TYPE")

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
        val populationNameLabel = createLabel(text = PG_NAME + " *", tooltip = PG_NAME_TOOLTIP)
        val targetPopulationLabel = createLabel(text = PG_TARGET, tooltip = PG_TARGET_TOOLTIP)
        val populationSpanLabel = createLabel(text = PG_SPAN, tooltip = PG_SPAN_TOOLTIP)
        val populationDescriptionLabel = createLabel(text = PG_DESC, tooltip = PG_DESC_TOOLTIP)
        val populationAgeLabel = createLabel(text = PG_AGE, tooltip = PG_AGE_TOOLTIP)
        val populationGenderLabel = createLabel(text = PG_GENDER, tooltip = PG_GENDER_TOOLTIP)
        val bmiLabel = createLabel(text = PG_BMI, tooltip = PG_BMI_TOOLTIP)
        val specialDietGroupLabel = createLabel(text = PG_DIETGROUPS, tooltip = PG_DIETGROUPS_TOOLTIP)
        val patternConsumptionLabel = createLabel(text = PG_PATTERNCONSUMPTION, tooltip = PG_PATTERNCONSUMPTION_TOOLTIP)
        val regionLabel = createLabel(text = PG_REGION, tooltip = PG_REGION_TOOLTIP)
        val countryLabel = createLabel(text = PG_COUNTRY, tooltip = PG_COUNTRY_TOOLTIP)
        val riskAndPopulationLabel = createLabel(text = PG_RISK, tooltip = PG_RISK_TOOLTIP)
        val seasonLabel = createLabel(text = PG_SEASON, tooltip = PG_SEASON_TOOLTIP)

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
        if (!populationNameTextField.hasValidValue()) errors.add("Missing $PG_NAME")
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
        val envNameLabel = createLabel(text = PRODUCT_NAME + " *", tooltip = PRODUCT_NAME_TOOLTIP)
        val envDescriptionLabel = createLabel(text = PRODUCT_DESC, tooltip = PRODUCT_DESC_TOOLTIP)
        val envUnitLabel = createLabel(text = PRODUCT_UNIT + " *", tooltip = PRODUCT_UNIT_TOOLTIP)
        val productionMethodLabel = createLabel(text = PRODUCT_PRODUCTIONMETHOD, tooltip = PRODUCT_PRODUCTIONMETHOD_TOOLTIP)
        val packagingLabel = createLabel(text = PRODUCT_PACKAGING, tooltip = PRODUCT_PACKAGING_TOOLTIP)
        val productTreatmentLabel = createLabel(text = PRODUCT_TREATMENT, tooltip = PRODUCT_TREATMENT_TOOLTIP)
        val originCountryLabel = createLabel(text = PRODUCT_ORIGINCOUNTRY, tooltip = PRODUCT_ORIGINCOUNTRY_TOOLTIP)
        val originAreaLabel = createLabel(text = PRODUCT_ORIGINAREA, tooltip = PRODUCT_ORIGINAREA_TOOLTIP)
        val fisheriesAreaLabel = createLabel(text = PRODUCT_FISHERIES, tooltip = PRODUCT_FISHERIES_TOOLTIP)
        val productionDateLabel = createLabel(text = PRODUCT_PRODUCTIONDATE, tooltip = PRODUCT_PRODUCTIONDATE_TOOLTIP)
        val expirationDateLabel = createLabel(text = PRODUCT_EXPIRATIONDATE, tooltip = PRODUCT_EXPIRATIONDATE_TOOLTIP)

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
        if (!envNameField.hasValidValue()) errors.add("Missing $PRODUCT_NAME")
        if (!envUnitField.hasValidValue()) errors.add("Missing $PRODUCT_UNIT")

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
        typeComboBox?.let { pairList.add(Pair(first = JLabel(REFERENCE_TYPE), second = it)) }
        dateChooser?.let { pairList.add(Pair(first = JLabel(REFERENCE_DATE), second = it)) }
        pmidTextField?.let { pairList.add(Pair(first = JLabel(REFERENCE_PMID), second = it)) }
        pairList.add(Pair(first = JLabel(REFERENCE_DOI + " *"), second = doiTextField))
        authorListTextField?.let { pairList.add(Pair(first = JLabel(REFERENCE_AUTHORLIST), second = it)) }
        pairList.add(Pair(first = JLabel(REFERENCE_TITLE + " *"), second = titleTextField))
        abstractTextArea?.let { pairList.add(Pair(first = JLabel(REFERENCE_ABSTRACT), second = it)) }
        journalTextField?.let { pairList.add(Pair(first = JLabel(REFERENCE_JOURNAL), second = it)) }

        volumeSpinnerModel?.let {
            val spinner = createSpinner(spinnerModel = it)
            pairList.add(Pair(first = JLabel(REFERENCE_VOLUME), second = spinner))
        }

        issueSpinnerModel?.let {
            val spinner = createSpinner(spinnerModel = it)
            pairList.add(Pair(first = JLabel(REFERENCE_ISSUE), second = spinner))
        }

        pageTextField?.let { pairList.add(Pair(first = JLabel(REFERENCE_PAGE), second = it)) }
        statusTextField?.let { pairList.add(Pair(first = JLabel(REFERENCE_STATUS), second = it)) }
        websiteTextField?.let { pairList.add(Pair(first = JLabel(REFERENCE_WEBSITE), second = it)) }
        commentTextField?.let { pairList.add(Pair(first = JLabel(REFERENCE_COMMENT), second = it)) }

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
        val errors = mutableListOf<String>()
        if (!doiTextField.hasValidValue()) errors.add("Missing $REFERENCE_DOI")
        if (!titleTextField.hasValidValue()) errors.add("Missing $REFERENCE_TITLE")

        return errors
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
        val sampleNameLabel = createLabel(text = SS_SAMPLE + " *", tooltip = SS_SAMPLE_TOOLTIP)
        val moisturePercentageLabel = createLabel(text = SS_MOISTURE_PERC, tooltip = SS_MOISTURE_PERC_TOOLTIP)
        val fatPercentageLabel = createLabel(text = SS_FAT_PERC, tooltip = SS_FAT_PERC_TOOLTIP)
        val sampleProtocolLabel = createLabel(text = SS_SAMPLE_PROTOCOL + " *", tooltip = SS_SAMPLE_PROTOCOL_TOOLTIP)
        val samplingStrategyLabel = createLabel(text = SS_SAMPLING_STRATEGY, tooltip = SS_SAMPLING_STRATEGY_TOOLTIP)
        val samplingTypeLabel = createLabel(text = SS_SAMPLING_TYPE, tooltip = SS_SAMPLING_TYPE_TOOLTIP)
        val samplingMethodLabel = createLabel(text = SS_SAMPLING_METHOD, tooltip = SS_SAMPLING_METHOD_TOOLTIP)
        val samplingPlanLabel = createLabel(text = SS_SAMPLING_PLAN + " *", tooltip = SS_SAMPLING_PLAN_TOOLTIP)
        val samplingWeightLabel = createLabel(text = SS_SAMPLING_WEIGHT + " *", tooltip = SS_SAMPLING_WEIGHT_TOOLTIP)
        val samplingSizeLabel = createLabel(text = SS_SAMPLING_SIZE + " *", tooltip = SS_SAMPLING_SIZE_TOOLTIP)
        val lotSizeUnitLabel = createLabel(text = SS_LOT_UNIT, tooltip = SS_LOT_UNIT_TOOLTIP)
        val samplingPointLabel = createLabel(text = SS_SAMPLING_POINT, tooltip = SS_SAMPLING_POINT_TOOLTIP)

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
        if (!sampleNameTextField.hasValidValue()) errors.add("Missing $SS_SAMPLE")
        if (!sampleProtocolTextField.hasValidValue()) errors.add("Missing $SS_SAMPLE_PROTOCOL")
        if (!samplingPlanTextField.hasValidValue()) errors.add("Missing $SS_SAMPLING_PLAN")
        if (!samplingWeightTextField.hasValidValue()) errors.add("Missing $SS_SAMPLING_WEIGHT")
        if (!samplingSizeTextField.hasValidValue()) errors.add("Missing $SS_SAMPLING_SIZE")

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