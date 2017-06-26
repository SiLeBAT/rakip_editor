package de.bund.bfr.rakip.editor

import com.gmail.gcolaianni5.jris.bean.Record
import com.gmail.gcolaianni5.jris.bean.Type
import de.bund.bfr.knime.ui.AutoSuggestField
import de.bund.bfr.rakip.generic.*
import javax.swing.JOptionPane
import javax.swing.JTextArea
import javax.swing.JTextField

/**
 * Validatable dialogs and panels.
 */
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

abstract class ValidatablePanel : javax.swing.JPanel(java.awt.GridBagLayout()) {

    abstract fun validatePanel(): List<String>
}

class EditAssayPanel(assay: Assay? = null, isAdvanced: Boolean) : ValidatablePanel() {

    companion object {
        val assayName = "Name *"
        val assayNameTooltip = "A name given to the assay"

        val assayDescription = "Description"
        val assayDescriptionTooltip = "General description of the assay. Corresponds to the Protocol REF in ISA"
    }

    val nameTextField = javax.swing.JTextField(30)
    val descriptionTextArea = if (isAdvanced) javax.swing.JTextArea(5, 30) else null

    init {

        // Populate UI with passed `assay`
        assay?.let {
            nameTextField.text = it.name
            descriptionTextArea?.text = it.description
        }

        initUI()
    }

    private fun initUI() {
        val nameLabel = createLabel(text = assayName, tooltip = assayNameTooltip)
        val descriptionLabel = createLabel(text = assayDescription, tooltip = assayDescriptionTooltip)

        val pairList = mutableListOf<Pair<javax.swing.JLabel, javax.swing.JComponent>>()
        pairList.add(Pair(first = nameLabel, second = nameTextField))
        descriptionTextArea?.let { Pair(first = descriptionLabel, second = it) }

        addGridComponents(pairs = pairList)
    }

    fun toAssay() = Assay(name = nameTextField.text, description = descriptionTextArea?.text)

    override fun validatePanel(): List<String> {
        val errors = mutableListOf<String>()
        if (!nameTextField.hasValidValue()) errors.add("Missing $assayName")
        return errors
    }
}

class EditDietaryAssessmentMethodPanel(dietaryAssessmentMethod: DietaryAssessmentMethod? = null, isAdvanced: Boolean)
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
        if (!dataCollectionToolField.hasValidValue()) errors.add("Missing $dataCollectionTool")
        if (!nonConsecutiveOneDayTextField.hasValidValue()) errors.add("Missing $nonConsecutiveOneDays")

        return errors
    }
}


class EditHazardPanel(hazard: Hazard? = null, isAdvanced: Boolean) : ValidatablePanel() {

    companion object {
        val hazardType = "Hazard type *"
        val hazardTypeTooltip = "General classification of the hazard"

        val hazardName = "Hazard name *"
        val hazardNameTooltip = "Name of the hazard for which the model or data applies"

        val hazardDescription = "Hazard description"
        val hazardDescriptionTooltip = "Description of te hazard for which the model or data applies"

        val hazardUnit = "Hazard unit *"
        val hazardUnitTooltip = "Unit of the hazard for which the model or data applies"

        val adverseEffect = "Adverse effect"
        val adverseEffectTooltip = "morbity, mortality, effect"

        val origin = "Origin"
        val originTooltip = "source of contamination, source"

        val bmd = "Benchmark dose, BMD"
        val bmdTooltip = "A dose or concentration that produces a predetermined change in response rate of an adverse effect"

        val maxResidueLimit = "Maximumm Residue Limit"
        val maxResidueLimitTooltip = "International regulations and permissible maximum residue levels in food and drinking water"

        val noObservedAdverse = "No observed adverse"
        val noObservedAdverseTooltip = "Level of exposure of an organism, found by experiment or observation"

        val lowestObserve = "Lowest observe"
        val lowestObserveTooltip = """
            |<html>
            |<p>Lowest concentration or amount of a substance found by experiment or observation
            |<p>that causes an adverse alteration of morphology, function, capacity, growth,
            |<p>development, or lifespan of a target organism distinguished from normal organisms
            |<p>of the same species under defined conditions of exposure.
            |</html>
            """.trimMargin()

        val acceptableOperator = "Acceptable operator"
        val acceptableOperatorTooltip = """
            |<html>
            |<p>Maximum amount of active substance to which the operator may be exposed without
            |<p>any adverse health effects. The AOEL is expressed as milligrams of the chemical
            |<p>per kilogram body weight of the operator.
            |</html>
            """.trimMargin()

        val acuteReferenceDose = "Acute reference dose"
        val acuteReferenceDoseTooltip = """
            |<html>
            |<p>An estimate (with uncertainty spanning perhaps an order of magnitude) of daily
            |<p>oral exposure for an acute duration (24 hours or less) to the human population
            |<p>including sensitive subgroups) that is likely to be without an appreciate risk
            |<p>of deleterious effects during a lifetime.
            |</html>
            """.trimMargin()

        val acceptableDailyIntake = "Acceptable daily intake"
        val acceptableDailyIntakeTooltip = """
            |<html>
            |<p>measure of amount of a specific substance in food or in drinking water that can
            |<p>be ingested (orally) on a daily basis over a lifetime without an appreciable
            |<p>health risk.
            |</html>
            """.trimMargin()

        val indSum = "Hazard ind/sum"
        val indSumTooltip = """
            |<html>
            |<p>Define if the parameter reported is an individual residue/analyte, a summed
            |<p>residue definition or part of a sum a summed residue definition.
            |</html>
            """.trimMargin()

        val labName = "Laboratory name"
        val labNameTooltip = """
            |<html>
            |<p>Laboratory code (National laboratory code if available) or Laboratory name
            |</html>
            """.trimMargin()

        val labCountry = "Laboratory country"
        val labCountryTooltip = "Country where the laboratory is placed. (ISO 3166-1-alpha-2)."

        val detectionLimit = "Limit of detection"
        val detectionLimitTooltip = """
            |<html>
            |<p>Limit of detection reported in the unit specified by the variable “Hazard unit”.
            |</html>
            """.trimMargin()

        val quantificationLimit = "Limit of quantification"
        val quantificationLimitTootlip = """
            |<html>
            |<p>Limit of quantification reported in the unit specified by the variable “Hazard
            |<p>unit”
            |</html>
            """.trimMargin()

        val leftCensoredData = "Left-censored data"
        val leftCensoredDataTooltip = "percentage of measures equal to LOQ and/or LOD"

        val contaminationRange = "Range of contamination"
        val contaminationRangeTooltip = """
            |<html>
            |<p>Range of result of the analytical measure reported in the unit specified by the
            |<p>variable “Hazard unit”
            |</html>
            """.trimMargin()
    }

    // Fields. Null if simple mode.
    private val hazardTypeField = AutoSuggestField(10)
    private val hazardNameField = AutoSuggestField(10)
    private val hazardDescriptionTextArea = if (isAdvanced) javax.swing.JTextArea(5, 30) else null
    private val hazardUnitField = AutoSuggestField(10)
    private val adverseEffectTextField = if (isAdvanced) javax.swing.JTextField(30) else null
    private val originTextField = if (isAdvanced) javax.swing.JTextField(30) else null
    private val bmdTextField = if (isAdvanced) javax.swing.JTextField(30) else null
    private val maxResidueLimitTextField = if (isAdvanced) javax.swing.JTextField(30) else null
    private val noObservedAdverseTextField = if (isAdvanced) javax.swing.JTextField(30) else null
    private val lowestObserveTextField = if (isAdvanced) javax.swing.JTextField(30) else null
    private val acceptableOperatorTextField = if (isAdvanced) javax.swing.JTextField(30) else null
    private val acuteReferenceDoseTextField = if (isAdvanced) javax.swing.JTextField(30) else null
    private val acceptableDailyIntakeTextField = if (isAdvanced) javax.swing.JTextField(30) else null
    private val indSumField = if (isAdvanced) AutoSuggestField(10) else null
    private val labNameTextField = if (isAdvanced) javax.swing.JTextField(30) else null
    private val labCountryField = if (isAdvanced) AutoSuggestField(10) else null
    private val detectionLimitTextField = if (isAdvanced) javax.swing.JTextField(30) else null
    private val quantificationLimitTextField = if (isAdvanced) javax.swing.JTextField(30) else null
    private val leftCensoredDataTextField = if (isAdvanced) javax.swing.JTextField(30) else null
    private val contaminationRangeTextField = if (isAdvanced) javax.swing.JTextField(30) else null

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
        val hazardTypeLabel = createLabel(text = hazardType, tooltip = hazardTypeTooltip)
        val hazardNameLabel = createLabel(text = hazardName, tooltip = hazardNameTooltip)
        val hazardDescriptionLabel = createLabel(text = hazardDescription, tooltip = hazardDescriptionTooltip)
        val hazardUnitLabel = createLabel(text = hazardUnit, tooltip = hazardUnitTooltip)
        val adverseEffectLabel = createLabel(text = adverseEffect, tooltip = adverseEffectTooltip)
        val originLabel = createLabel(text = origin, tooltip = originTooltip)
        val bmdLabel = createLabel(text = bmd, tooltip = bmdTooltip)
        val maxResidueLimitLabel = createLabel(text = maxResidueLimit, tooltip = maxResidueLimitTooltip)
        val noObservedAdverseLabel = createLabel(text = noObservedAdverse, tooltip = noObservedAdverseTooltip)
        val lowestObserveLabel = createLabel(text = lowestObserve, tooltip = lowestObserveTooltip)
        val acceptableOperatorLabel = createLabel(text = acceptableOperator, tooltip = acceptableOperatorTooltip)
        val acuteReferenceDoseLabel = createLabel(text = acuteReferenceDose, tooltip = acuteReferenceDoseTooltip)
        val acceptableDailyIntakeLabel = createLabel(text = acceptableDailyIntake, tooltip = acceptableDailyIntakeTooltip)
        val indSumLabel = createLabel(text = indSum, tooltip = indSumTooltip)
        val labNameLabel = createLabel(text = labName, tooltip = labNameTooltip)
        val labCountryLabel = createLabel(text = labCountry, tooltip = labCountryTooltip)
        val detectionLimitLabel = createLabel(text = detectionLimit, tooltip = detectionLimitTooltip)
        val quantificationLimitLabel = createLabel(text = quantificationLimit, tooltip = quantificationLimitTootlip)
        val leftCensoredDataLabel = createLabel(text = leftCensoredData, tooltip = leftCensoredDataTooltip)
        val contaminationRangeLabel = createLabel(text = contaminationRange, tooltip = contaminationRangeTooltip)

        // Init combo boxes
        hazardTypeField.setPossibleValues(vocabs["Hazard type"])
        hazardNameField.setPossibleValues(vocabs["Hazard name"])
        hazardUnitField.setPossibleValues(vocabs["Hazard unit"])
        indSumField?.setPossibleValues(vocabs["Hazard ind sum"])
        labCountryField?.setPossibleValues(vocabs["Laboratory country"])

        val pairList = mutableListOf<Pair<javax.swing.JLabel, javax.swing.JComponent>>()
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
        if (!hazardTypeField.hasValidValue()) errors.add("Missing $hazardType")
        if (!hazardNameField.hasValidValue()) errors.add("Missing $hazardName")
        if (!hazardUnitField.hasValidValue()) errors.add("Missing $hazardUnit")

        return errors
    }
}


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
        if (!equationNameTextField.hasValidValue()) errors.add("Missing $equationName")
        if (!scriptTextArea.hasValidValue()) errors.add("Missing $script")

        return errors
    }
}

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
        if (!idTextField.hasValidValue()) errors.add("Missing $id")
        if (!classificationField.hasValidValue()) errors.add("Missing $classification")
        if (!nameTextField.hasValidValue()) errors.add("Missing $name")
        if (!unitField.hasValidValue()) errors.add("MIssing $unit")
        if (!unitCategoryField.hasValidValue()) errors.add("Missing $unitCategory")
        if (!unitCategoryField.hasValidValue()) errors.add("Missing $dataType")

        return errors
    }
}

class EditPopulationGroupPanel(populationGroup: PopulationGroup? = null, isAdvanced: Boolean)
    : ValidatablePanel() {

    companion object {
        val populationName = "Population name *"
        val populationNameTooltip = "Name of the population for which the model or data applies"

        val targetPopulation = "Target population"
        val targetPopulationTooltip = """
            |<html>
            |<p>population of individual that we are interested in describing and making
            |<p>statistical inferences about
            |</html>
            """.trimMargin()

        val populationSpan = "Population span"
        val populationSpanTooltip = """
            |<html>
            |<p>Temporal information on the exposure pattern of the population to the
            |<p>hazard SUGGESTION: Temporal information on the exposure of the
            |<p>population to the hazard OR Temporal information on the exposure period
            |<p>of the population to the hazard.
            |</html>
            """.trimMargin()

        val populationDescription = "Population assayDescription"
        val populationDescriptionTooltip = """
            |<html>
            |<p>Description of the population for which the model applies (demographic
            |<p>and socio-economic characteristics for example). Background information
            |<p>that are needed in the data analysis phase: size of household, education
            |<p>level, employment status, professional category, ethnicity, etc.
            |</html>
            """.trimMargin()

        val populationAge = "Population age"
        val populationAgeTooltip = "describe the range of age or group of age"

        val populationGender = "Population gender"
        val populationGenderTooltip = "describe the percentage of gender"

        val bmi = "BMI"
        val bmiTooltip = "describe the range of BMI or class of BMI or BMI mean"

        val specialDietGroups = "Special diet groups"
        val specialDietGroupsTooltip = """
            |<html>
            |<p>sub-population with special diets (vegetarians, diabetics, group
            |<p>following special ethnic diets)
            |</html>
            """

        val patternConsumption = "Pattern consumption"
        val patternConsumptionTooltip = """
            |<html>
            |<p>describe the consumption of different food items: frequency, portion
            |<p>size
            |</html>
            """.trimMargin()

        val region = "Region"
        val regionTooltip = "Spatial information (area) on which the model or data applies"

        val country = "Country"
        val countryTooltip = "Country on which the model or data applies"

        val riskAndPopulation = "Risk and population"
        val riskAndPopulationTooltip = """
            |<html>
            |<p>population risk factor that may influence the outcomes of the
            |<p>study, confounder should be included
            |</html>
            """.trimMargin()

        val season = "Season"
        val seasonTooltip = "distribution of surveyed people according to the season (influence consumption pattern)"
    }

    private val populationNameTextField = javax.swing.JTextField(30)
    private val targetPopulationTextField = if (isAdvanced) javax.swing.JTextField(30) else null
    private val populationSpanTextField = if (isAdvanced) javax.swing.JTextField(30) else null
    private val populationDescriptionTextArea = if (isAdvanced) javax.swing.JTextField(30) else null
    private val populationAgeTextField = if (isAdvanced) javax.swing.JTextField(30) else null
    private val populationGenderTextField = if (isAdvanced) javax.swing.JTextField(30) else null
    private val bmiTextField = if (isAdvanced) javax.swing.JTextField(30) else null
    private val specialDietGroupTextField = if (isAdvanced) javax.swing.JTextField(30) else null
    private val patternConsumptionTextField = if (isAdvanced) javax.swing.JTextField(30) else null
    private val regionComboBox = if (isAdvanced) javax.swing.JComboBox<String>() else null
    private val countryComboBox = if (isAdvanced) javax.swing.JComboBox<String>() else null
    private val riskAndPopulationTextField = if (isAdvanced) javax.swing.JTextField(30) else null
    private val seasonTextField = if (isAdvanced) javax.swing.JTextField(30) else null

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
        val populationNameLabel = createLabel(text = populationName, tooltip = populationNameTooltip)
        val targetPopulationLabel = createLabel(text = targetPopulation, tooltip = targetPopulationTooltip)
        val populationSpanLabel = createLabel(text = populationSpan, tooltip = populationSpanTooltip)
        val populationDescriptionLabel = createLabel(text = populationDescription, tooltip = populationDescriptionTooltip)
        val populationAgeLabel = createLabel(text = populationAge, tooltip = populationAgeTooltip)
        val populationGenderLabel = createLabel(text = populationGender, tooltip = populationGenderTooltip)
        val bmiLabel = createLabel(text = bmi, tooltip = bmiTooltip)
        val specialDietGroupLabel = createLabel(text = specialDietGroups, tooltip = specialDietGroupsTooltip)
        val patternConsumptionLabel = createLabel(text = patternConsumption, tooltip = patternConsumptionTooltip)
        val regionLabel = createLabel(text = region, tooltip = regionTooltip)
        val countryLabel = createLabel(text = country, tooltip = countryTooltip)
        val riskAndPopulationLabel = createLabel(text = riskAndPopulation, tooltip = riskAndPopulationTooltip)
        val seasonLabel = createLabel(text = season, tooltip = seasonTooltip)

        // init combo boxes
        regionComboBox?.let { vocabs["Region"]?.forEach(it::addItem) }
        countryComboBox?.let { vocabs["Country"]?.forEach(it::addItem) }

        val pairList = mutableListOf<Pair<javax.swing.JLabel, javax.swing.JComponent>>()
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
        if (!populationNameTextField.hasValidValue()) errors.add("Missing $populationName")
        return errors
    }
}

class EditProductPanel(product: Product? = null, isAdvanced: Boolean) : ValidatablePanel() {

    companion object {
        val envName = "Environment name *"
        val envNameTooltip = """
            |<html>
            |<p>The environment (animal, food product, matrix, etc.) for which the model
            |<p>or data applies
            |</html>
            """.trimMargin()

        val envDescription = "Environment description"
        val envDescriptionTooltip = """
            |<html>
            |<p>Description of the environment (animal, food product, matrix, etc.) for
            |<p>which the model or data applies
            |</html>
            """.trimMargin()

        val productionMethod = "Method of production"
        val productionMethodTooltip = "Type of production for the product/ matrix"

        val envUnit = "Environment unit *"
        val envUnitTooltip = "Units of the environment for which the model or data applies"

        val packaging = "Packaging"
        val packagingTooltip = """
            |<html>
            |<p>Describe container or wrapper that holds the product/matrix. Common type
            |<p>of packaging: paper or plastic bags, boxes, tinplate or aluminium cans,
            |<p>plastic trays, plastic bottles, glass bottles or jars.
            |</html>
            """.trimMargin()

        val productTreatment = "Product treatment"
        val productTreatmentTooltip = """
            |<html>
            |<p>Used to characterise a product/matrix based on the treatment or
            |<p>processes applied to the product or any indexed ingredient.
            |</html>
            """.trimMargin()

        val originCountry = "Country of origin"
        val originCountryTooltip = "Country of origin of the food/product (ISO 3166 1-alpha-2 country code)"

        val originArea = "Area of origin"
        val originAreaTooltip = """
            |<html>
            |<p>Area of origin of the food/product (Nomenclature of territorial units
            |<p>for statistics – NUTS – coding system valid only for EEA and Switzerland).
            |</html>
            """.trimMargin()

        val fisheriesArea = "Fisheries area"
        val fisheriesAreaTooltip = """
            |<html>
            |<p>Fisheries or aquaculture area specifying the origin of the
            |<p>sample (FAO Fisheries areas).
            |</html>
            """.trimMargin()

        val productionDate = "Production date"
        val productionDateTooltip = "date of production of food/product"

        val expirationDate = "Expiration date"
        val expirationDateTooltip = "date of expiry of food/product"
    }

    // Fields. null if simple mode
    private val envNameField = AutoSuggestField(10)
    private val envDescriptionTextArea = if (isAdvanced) javax.swing.JTextArea(5, 30) else null
    private val envUnitField = AutoSuggestField(10)
    private val productionMethodComboBox = if (isAdvanced) javax.swing.JComboBox<String>() else null
    private val packagingComboBox = if (isAdvanced) javax.swing.JComboBox<String>() else null
    private val productTreatmentComboBox = if (isAdvanced) javax.swing.JComboBox<String>() else null
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
        val envNameLabel = createLabel(text = envName, tooltip = envNameTooltip)
        val envDescriptionLabel = createLabel(text = envDescription, tooltip = envDescriptionTooltip)
        val envUnitLabel = createLabel(text = envUnit, tooltip = envUnitTooltip)
        val productionMethodLabel = createLabel(text = productionMethod, tooltip = productionMethodTooltip)
        val packagingLabel = createLabel(text = packaging, tooltip = packagingTooltip)
        val productTreatmentLabel = createLabel(text = productTreatment, tooltip = productTreatmentTooltip)
        val originCountryLabel = createLabel(text = originCountry, tooltip = originCountryTooltip)
        val originAreaLabel = createLabel(text = originArea, tooltip = originAreaTooltip)
        val fisheriesAreaLabel = createLabel(text = fisheriesArea, tooltip = fisheriesAreaTooltip)
        val productionDateLabel = createLabel(text = productionDate, tooltip = productionDateTooltip)
        val expirationDateLabel = createLabel(text = expirationDate, tooltip = expirationDateTooltip)

        // Init combo boxes
        envNameField.setPossibleValues(vocabs.get("Product-matrix name"))
        envUnitField.setPossibleValues(vocabs.get("Product-matrix unit"))
        productionMethodComboBox?.let { vocabs["Method of production"]?.forEach(it::addItem) }
        packagingComboBox?.let { vocabs["Packaging"]?.forEach(it::addItem) }
        productTreatmentComboBox?.let { vocabs["Product treatment"]?.forEach(it::addItem) }
        originCountryField?.setPossibleValues(vocabs["Country of origin"])
        originAreaField?.setPossibleValues(vocabs["Area of origin"])
        fisheriesAreaField?.setPossibleValues(vocabs["Fisheries area"])

        val pairList = mutableListOf<Pair<javax.swing.JLabel, javax.swing.JComponent>>()
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
        if (!envNameField.hasValidValue()) errors.add("Missing $envName")
        if (!envUnitField.hasValidValue()) errors.add("Missing $envUnit")

        return errors
    }
}


class EditReferencePanel(ref: Record? = null, isAdvanced: Boolean) : ValidatablePanel() {

    private val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd")

    // fields. null if advanced
    private val isReferenceDescriptionCheckBox = javax.swing.JCheckBox("Is reference description *")
    private val typeComboBox = if (isAdvanced) javax.swing.JComboBox<Type>() else null
    private val dateChooser = if (isAdvanced) FixedJDateChooser() else null
    private val pmidTextField = if (isAdvanced) javax.swing.JTextField(30) else null
    private val doiTextField = javax.swing.JTextField(30)
    private val authorListTextField = if (isAdvanced) javax.swing.JTextField(30) else null
    private val titleTextField = javax.swing.JTextField(30)
    private val abstractTextArea = if (isAdvanced) javax.swing.JTextArea(5, 30) else null
    private val journalTextField = if (isAdvanced) javax.swing.JTextField(30) else null
    private val volumeSpinnerModel = if (isAdvanced) createSpinnerIntegerModel() else null
    private val issueSpinnerModel = if (isAdvanced) createSpinnerIntegerModel() else null
    private val pageTextField = if (isAdvanced) javax.swing.JTextField(30) else null
    private val statusTextField = if (isAdvanced) javax.swing.JTextField(30) else null
    private val websiteTextField = if (isAdvanced) javax.swing.JTextField(30) else null
    private val commentTextField = if (isAdvanced) javax.swing.JTextArea(5, 30) else null

    companion object {
        val publicationType = "Publication type"
        val publicationDate = "Publication date"
        val pubMedId = "PubMed ID"
        val publicationDoi = "Publication DOI *"
        val publicationAuthorList = "Publication author list"
        val publicationTitle = "Publication title *"
        val publicationAbstract = "Publication abstract"
        val publicationJournal = "Publication journal"
        val publicationVolume = "Publication volume"
        val publicationIssue = "Publication issue"
        val publicationPage = "Publication page"
        val publicationStatus = "Publication status"
        val publicationWebsite = "Publication website"
        val comment = "Comment"
    }

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

        val pairList = mutableListOf<Pair<javax.swing.JLabel, javax.swing.JComponent>>()
        typeComboBox?.let { pairList.add(Pair(first = javax.swing.JLabel(publicationType), second = it)) }
        dateChooser?.let { pairList.add(Pair(first = javax.swing.JLabel(publicationDate), second = it)) }
        pmidTextField?.let { pairList.add(Pair(first = javax.swing.JLabel(pubMedId), second = it)) }
        pairList.add(Pair(first = javax.swing.JLabel(publicationDoi), second = doiTextField))
        authorListTextField?.let { pairList.add(Pair(first = javax.swing.JLabel(publicationAuthorList), second = it)) }
        pairList.add(Pair(first = javax.swing.JLabel(publicationTitle), second = titleTextField))
        abstractTextArea?.let { pairList.add(Pair(first = javax.swing.JLabel(publicationAbstract), second = it)) }
        journalTextField?.let { pairList.add(Pair(first = javax.swing.JLabel(publicationJournal), second = it)) }
        volumeSpinnerModel?.let {
            val spinner = createSpinner(it)
            pairList.add(Pair(first = javax.swing.JLabel(publicationVolume), second = spinner))
        }
        issueSpinnerModel?.let {
            val spinner = createSpinner(it)
            pairList.add(Pair(first = javax.swing.JLabel(publicationIssue), second = spinner))
        }
        pageTextField?.let { pairList.add(Pair(first = javax.swing.JLabel(publicationPage), second = it)) }
        statusTextField?.let { pairList.add(Pair(first = javax.swing.JLabel(publicationStatus), second = it)) }
        websiteTextField?.let { pairList.add(Pair(first = javax.swing.JLabel(publicationWebsite), second = it)) }
        commentTextField?.let { pairList.add(Pair(first = javax.swing.JLabel(comment), second = it)) }

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
        if (!doiTextField.hasValidValue()) errorList.add("Missing $publicationDoi")
        if (!titleTextField.hasValidValue()) errorList.add("Missing $publicationTitle")

        return errorList
    }
}

class EditStudySamplePanel(studySample: StudySample? = null, isAdvanced: Boolean) : ValidatablePanel() {

    companion object {
        val sampleName = "Sample name (ID) *"
        val sampleNameTooltip = "An unambiguous ID given to the samples used in the assay"

        val moisturePercentage = "Moisture percentage"
        val moisturePercentageTooltip = "Percentage of moisture in the original sample"

        val fatPercentage = "Fat percentage"
        val fatPercentageTooltip = "Percentage of fat in the original sample"

        val sampleProtocol = "Protocol of sample *"
        val sampleProtocolTooltip = """
            |<html>
            |<p>Additional protocol for sample and sample collection. Corresponds to the
            |<p>Protocol REF in ISA
            |</html>
            """.trimMargin()

        val samplingStrategy = "Sampling strategy"
        val samplingStrategyTooltip = """
            |<html>
            |<p>Sampling strategy (ref. EUROSTAT - Typology of sampling strategy,
            |<p>version of July 2009)
            |</html>
            """.trimMargin()

        val samplingType = "Type of sampling"
        val samplingTypeTooltip = """
            |<html>
            |<p>Indicate the type programme for which the samples have been collected"
            |</html>
            """.trimMargin()

        val samplingMethod = "Sampling method"
        val samplingMethodTooltip = "Sampling method used to take the sample"

        val samplingPlan = "Sampling plan *"
        val samplingPlanTooltip = """
            |<html>
            |<p>assayDescription of data collection technique: stratified or complex sampling
            |<p>(several stages)
            |</html>
            """.trimMargin()

        val samplingWeight = "Sampling weight *"
        val samplingWeightTooltip = """
            |<html>
            |<p>assayDescription of the method employed to compute sampling weight
            |<p>(nonresponse-adjusted weight)
            |</html>
            """.trimMargin()

        val samplingSize = "Sampling size *"
        val samplingSizeTooltip = """
            |<html>
            |<p>number of units, full participants, partial participants, eligibles, not
            |<p>eligible, unresolved (eligibility status not resolved)…
            |</html>
            """.trimMargin()

        val lotSizeUnit = "Lot size unit"
        val lotSizeUnitTooltip = "Unit in which the lot size is expressed."

        val samplingPoint = "Sampling point"
        val samplingPointTooltip = """
            |<html>
            |<p>Point in the food chain where the sample was taken.
            |<p>(Doc. ESTAT/F5/ES/155 "Data dictionary of activities of the
            |<p>establishments").
            |</html>
            """.trimMargin()
    }

    // Fields. null if advanced mode
    val sampleNameTextField = javax.swing.JTextField(30)
    val moisturePercentageSpinnerModel = if (isAdvanced) createSpinnerPercentageModel() else null
    val fatPercentageSpinnerModel = if (isAdvanced) createSpinnerPercentageModel() else null
    val sampleProtocolTextField = javax.swing.JTextField(30)
    val samplingStrategyField = if (isAdvanced) AutoSuggestField(10) else null
    val samplingTypeField = if (isAdvanced) AutoSuggestField(10) else null
    val samplingMethodField = if (isAdvanced) AutoSuggestField(10) else null
    val samplingPlanTextField = javax.swing.JTextField(30)
    val samplingWeightTextField = javax.swing.JTextField(30)
    val samplingSizeTextField = javax.swing.JTextField(30)
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
        val sampleNameLabel = createLabel(text = sampleName, tooltip = sampleNameTooltip)
        val moisturePercentageLabel = createLabel(text = moisturePercentage, tooltip = moisturePercentageTooltip)
        val fatPercentageLabel = createLabel(text = fatPercentage, tooltip = fatPercentageTooltip)
        val sampleProtocolLabel = createLabel(text = sampleProtocol, tooltip = sampleProtocolTooltip)
        val samplingStrategyLabel = createLabel(text = samplingStrategy, tooltip = samplingStrategyTooltip)
        val samplingTypeLabel = createLabel(text = samplingType, tooltip = samplingStrategyTooltip)
        val samplingMethodLabel = createLabel(text = samplingMethod, tooltip = samplingMethodTooltip)
        val samplingPlanLabel = createLabel(text = samplingPlan, tooltip = samplingPlanTooltip)
        val samplingWeightLabel = createLabel(text = samplingWeight, tooltip = samplingWeightTooltip)
        val samplingSizeLabel = createLabel(text = samplingSize, tooltip = samplingSizeTooltip)
        val lotSizeUnitLabel = createLabel(text = lotSizeUnit, tooltip = lotSizeUnitTooltip)
        val samplingPointLabel = createLabel(text = samplingPoint, tooltip = samplingPointTooltip)

        // init combo boxes
        samplingStrategyField?.setPossibleValues(vocabs["Sampling strategy"])
        samplingTypeField?.setPossibleValues(vocabs["Type of sampling program"])
        samplingMethodField?.setPossibleValues(vocabs["Sampling method"])
        lotSizeUnitField?.setPossibleValues(vocabs["Lot size unit"])
        samplingPointField?.setPossibleValues(vocabs["Sampling point"])

        val pairList = mutableListOf<Pair<javax.swing.JLabel, javax.swing.JComponent>>()
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
        if (!sampleNameTextField.hasValidValue()) errors.add("Missing $sampleName")
        if (!sampleProtocolTextField.hasValidValue()) errors.add("Missing $sampleProtocol")
        if (!samplingPlanTextField.hasValidValue()) errors.add("Missing $samplingPlan")
        if (!samplingWeightTextField.hasValidValue()) errors.add("Missing $samplingWeight")
        if (!samplingSizeTextField.hasValidValue()) errors.add("Missing $samplingSize")

        return errors
    }
}

// Validation methods
internal fun javax.swing.JTextField.hasValidValue() = text.isNotBlank()

internal fun javax.swing.JTextArea.hasValidValue() = text.isNotBlank()

internal fun AutoSuggestField.hasValidValue() : Boolean {
    val field = editor.editorComponent as javax.swing.JTextField
    return field.text.isNotBlank()
}