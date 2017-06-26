package de.bund.bfr.rakip.editor

import de.bund.bfr.knime.ui.AutoSuggestField
import de.bund.bfr.rakip.generic.Product

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
        if (!envNameField.hasValidValue()) errors.add("Missing ${de.bund.bfr.rakip.editor.EditProductPanel.Companion.envName}")
        if (!envUnitField.hasValidValue()) errors.add("Missing ${de.bund.bfr.rakip.editor.EditProductPanel.Companion.envUnit}")

        return errors
    }
}