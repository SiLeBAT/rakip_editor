package de.bund.bfr.rakip.editor

import de.bund.bfr.knime.ui.AutoSuggestField
import de.bund.bfr.rakip.generic.Hazard

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
        if (!hazardTypeField.hasValidValue()) errors.add("Missing ${de.bund.bfr.rakip.editor.EditHazardPanel.Companion.hazardType}")
        if (!hazardNameField.hasValidValue()) errors.add("Missing ${de.bund.bfr.rakip.editor.EditHazardPanel.Companion.hazardName}")
        if (!hazardUnitField.hasValidValue()) errors.add("Missing ${de.bund.bfr.rakip.editor.EditHazardPanel.Companion.hazardUnit}")

        return errors
    }
}