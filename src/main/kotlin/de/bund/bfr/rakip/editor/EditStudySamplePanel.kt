package de.bund.bfr.rakip.editor

import de.bund.bfr.knime.ui.AutoSuggestField
import de.bund.bfr.rakip.generic.StudySample

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
        if (!sampleNameTextField.hasValidValue()) errors.add("Missing ${de.bund.bfr.rakip.editor.EditStudySamplePanel.Companion.sampleName}")
        if (!sampleProtocolTextField.hasValidValue()) errors.add("Missing ${de.bund.bfr.rakip.editor.EditStudySamplePanel.Companion.sampleProtocol}")
        if (!samplingPlanTextField.hasValidValue()) errors.add("Missing ${de.bund.bfr.rakip.editor.EditStudySamplePanel.Companion.samplingPlan}")
        if (!samplingWeightTextField.hasValidValue()) errors.add("Missing ${de.bund.bfr.rakip.editor.EditStudySamplePanel.Companion.samplingWeight}")
        if (!samplingSizeTextField.hasValidValue()) errors.add("Missing ${de.bund.bfr.rakip.editor.EditStudySamplePanel.Companion.samplingSize}")

        return errors
    }
}