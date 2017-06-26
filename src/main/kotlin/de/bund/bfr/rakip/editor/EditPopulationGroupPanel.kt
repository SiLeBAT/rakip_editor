package de.bund.bfr.rakip.editor

import de.bund.bfr.rakip.generic.PopulationGroup

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
        if (!populationNameTextField.hasValidValue()) errors.add("Missing ${de.bund.bfr.rakip.editor.EditPopulationGroupPanel.Companion.populationName}")
        return errors
    }
}