package de.bund.bfr.rakip.editor

import com.gmail.gcolaianni5.jris.bean.Record
import com.toedter.calendar.JDateChooser
import de.bund.bfr.knime.ui.AutoSuggestField
import de.bund.bfr.rakip.generic.*
import ezvcard.VCard
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook

val logger: java.util.logging.Logger = java.util.logging.Logger.getAnonymousLogger()

fun loadVocabularies(): Map<String, Set<String>> {

    val inputStream = "".javaClass.getResourceAsStream("/FSKLab_Config_Controlled Vocabularies.xlsx")

    val workbook = XSSFWorkbook(inputStream)
    val vocabs = listOf(

            // GeneralInformation controlled vocabularies
            "Rights", "Format", "Software", "Language written in", "Status",

            // Product controlled vocabularies
            "Product-matrix name", "Product-matrix unit", "Method of production", "Packaging", "Product treatment",
            "Country of origin", "Area of origin", "Fisheries area",

            // Hazard controlled vocabularies
            "Hazard type", "Hazard name", "Hazard unit", "Hazard ind sum", "Laboratory country",

            // PopulationGroup controlled vocabularies
            "Region", "Country",

            // DataBackground controlled vocabularies
            "Laboratory accreditation",

            // Study controlled vocabularies
            "Study Design Type", "Study Assay Measurement Type", "Study Assay Technology Type",
            "Accreditation procedure Ass.Tec", "Study Protocol Type", "Study Protocol Parameters Name",
            "Study Protocol Components Type",

            // StudySample controlled vocabularies
            "Sampling strategy", "Type of sampling program", "Sampling method", "Lot size unit", "Sampling point",

            // DietaryAssessmentMethod controlled vocabularies
            "Method. tool to collect data", "Food descriptors",

            // Parameter controlled vocabularies
            "Parameter classification", "Parameter unit", "Parameter type", "Parameter unit category",
            "Parameter data type", "Parameter source", "Parameter subject", "Parameter distribution"
    ).associateBy({ it }, { de.bund.bfr.rakip.editor.readVocabFromSheet(workbook = workbook, sheetname = it) })

    workbook.close()

    return vocabs
}

val vocabs = de.bund.bfr.rakip.editor.loadVocabularies()

/**
 * Read controlled vocabulary from spreadsheet.
 *
 * @return Set with controlled vocabulary. If the sheet is not found returns empty set.
 */
fun readVocabFromSheet(workbook: Workbook, sheetname: String): Set<String> {

    val sheet = workbook.getSheet(sheetname)
    if (sheet == null) {
        de.bund.bfr.rakip.editor.logger.warning("Spreadsheet not found: $sheetname")
        return emptySet<String>()
    }

    val vocab = sheet
            .filter { it.rowNum != 0 } // Skip header
            .mapNotNull { it.getCell(0) }
            // Replace faulty cells with "" that are later discarded
            .map {
                try {
                    it.stringCellValue
                } catch (e: Exception) {
                    de.bund.bfr.rakip.editor.logger.warning("Controlled vocabulary ${sheet.sheetName}: wrong value $it")
                    ""
                }
            }
            .filter { it.isNotBlank() }  // Skip empty cells
            .toSet()

    return vocab
}

fun main(args: Array<String>) {

    fun createExampleGeneralInformation(): GeneralInformation {

        // Example data
        val gi = GeneralInformation(name = "name",
                identifier = "007",
                creationDate = java.util.Date(),
                rights = "to remain silent",
                isAvailable = true,
                url = java.net.URL("https://google.de"),
                format = "fskx",
                language = "spanish",
                software = "KNIME",
                languageWrittenIn = "Matlab",
                status = "super curated",
                objective = "world domination",
                description = "by taking over M&Ms"
        )

        // example references
        Record().let {
            it.addAuthor("Florent Baty")
            it.pubblicationYear = "2012"
            it.title = "Package `nlstools`"
            gi.reference.add(it)
        }

        Record().let {
            it.addAuthor("Jagannath")
            it.pubblicationYear = "2005"
            it.title = "Comparison of the thermal inactivation"
            gi.reference.add(it)
        }

        // Add example creator
        VCard().let {
            it.setNickname("Gump")
            it.setFormattedName("Forrest Gump")
            it.addEmail("forrestgump@example.com")
            gi.creators.add(it)
        }

        return gi
    }

    var gi = createExampleGeneralInformation()

    val frame = javax.swing.JFrame()
    val generalInformationPanel = de.bund.bfr.rakip.editor.GeneralInformationPanel(gi)
    generalInformationPanel.studyNameTextField.text = gi.name
    generalInformationPanel.identifierTextField.text = gi.identifier
    generalInformationPanel.creationDateChooser.date = gi.creationDate
    generalInformationPanel.rightsField.setPossibleValues(de.bund.bfr.rakip.editor.vocabs["Rights"])
    generalInformationPanel.rightsField.selectedItem = gi.rights
    generalInformationPanel.availabilityCheckBox.isSelected = gi.isAvailable
    generalInformationPanel.urlTextField.text = gi.url.toString()
    generalInformationPanel.formatField.setPossibleValues(de.bund.bfr.rakip.editor.vocabs["Format"])
    generalInformationPanel.formatField.selectedItem = gi.format
    generalInformationPanel.languageTextField.text = gi.language
    generalInformationPanel.softwareField.setPossibleValues(de.bund.bfr.rakip.editor.vocabs["Software"])
    generalInformationPanel.softwareField.selectedItem = gi.software
    generalInformationPanel.languageWrittenInField.setPossibleValues(de.bund.bfr.rakip.editor.vocabs["Language writte in"])
    generalInformationPanel.languageWrittenInField.selectedItem = gi.languageWrittenIn
    generalInformationPanel.statusField.setPossibleValues(de.bund.bfr.rakip.editor.vocabs["Status"])
    generalInformationPanel.statusField.selectedItem = gi.status
    generalInformationPanel.objectiveTextField.text = gi.objective
    generalInformationPanel.descriptionTextField.text = gi.description

    val scopePanel = de.bund.bfr.rakip.editor.ScopePanel(Scope())
    val dataBackgroundPanel = de.bund.bfr.rakip.editor.DataBackgroundPanel()
    val modelMathPanel = de.bund.bfr.rakip.editor.ModelMathPanel()

    // Tabbed pane
    val tabbedPane = javax.swing.JTabbedPane()
    tabbedPane.addTab("General information", javax.swing.JScrollPane(generalInformationPanel))
    tabbedPane.addTab("Scope", javax.swing.JScrollPane(scopePanel))
    tabbedPane.addTab("Data background", javax.swing.JScrollPane(dataBackgroundPanel))
    tabbedPane.addTab("Model math", javax.swing.JScrollPane(modelMathPanel))

    frame.add(tabbedPane)
    frame.defaultCloseOperation = javax.swing.JFrame.EXIT_ON_CLOSE
    frame.title = "JTree Example"
    frame.setSize(500, 300)
    frame.minimumSize = java.awt.Dimension(800, 500)
    frame.isVisible = true
    frame.addWindowListener(object : java.awt.event.WindowAdapter() {
        // Save changes on close
        override fun windowClosing(windowEvent: java.awt.event.WindowEvent?) {
            gi = generalInformationPanel.toGeneralInformation()
            System.exit(0)
        }
    })
}

fun javax.swing.JPanel.add(comp: javax.swing.JComponent, gridx: Int, gridy: Int, gridwidth: Int = 1, gridheight: Int = 1): Unit {
    val constraints = java.awt.GridBagConstraints()
    constraints.gridx = gridx
    constraints.gridy = gridy
    constraints.gridwidth = gridwidth
    constraints.gridheight = gridheight
    constraints.ipadx = 10
    constraints.ipady = 10
    constraints.anchor = java.awt.GridBagConstraints.LINE_START

    add(comp, constraints)
}

fun javax.swing.JPanel.addGridComponents(pairs: List<Pair<javax.swing.JLabel, javax.swing.JComponent>>) {

    val labelConstraints = java.awt.GridBagConstraints()
    labelConstraints.gridx = 0
    labelConstraints.ipadx = 10
    labelConstraints.ipady = 10
    labelConstraints.anchor = java.awt.GridBagConstraints.LINE_START

    val fieldConstraints = java.awt.GridBagConstraints()
    fieldConstraints.gridx = 1
    fieldConstraints.ipadx = 10
    fieldConstraints.ipady = 10
    fieldConstraints.anchor = java.awt.GridBagConstraints.LINE_START

    for ((index, entry) in pairs.withIndex()) {
        val label = entry.first
        val field = entry.second
        label.labelFor = field

        labelConstraints.gridy = index
        add(label, labelConstraints)

        fieldConstraints.gridy = index
        add(field, fieldConstraints)
    }
}

class GeneralInformationPanel(generalInformation: GeneralInformation) : javax.swing.Box(javax.swing.BoxLayout.PAGE_AXIS) {

    companion object {
        val studyName = "Study name"
        val studyNameTooltip = "Name given to the model or data"

        val identifier = "Identifier"
        val identifierTooltip = "Unambiguous ID given to the model or data"

        val creationDate = "Creation date"
        val creationDateTooltip = "Model creation date"

        val rights = "Rights"
        val rightsTooltip = "Rights held in over the resource"

        val availability = "Is available"
        val availabilityTooltip = "Availability of data or model"

        val url = "URL"
        val urlTooltip = "Web address referencing the resource location"

        val format = "Format"
        val formatTooltip = "Form of data (file extension)"

        val language = "Language"
        val languageTooltip = "Language used to write the model"

        val software = "Software"
        val softwareTooltip = "Program in which the model has been implemented"

        val languageWrittenIn = "Language written in"
        val languageWrittenInTooltip = "Language used to write the model"

        val status = "Status"
        val statusTooltip = "The curation status of the model"

        val objective = "Objective"
        val objectiveTooltip = "Objective of model or data"

        val description = "Description"
        val descriptionTooltip = "General assayDescription of the study, data or model"
    }

    val advancedCheckBox = javax.swing.JCheckBox("Advanced")

    val studyNameTextField = javax.swing.JTextField(30)
    val identifierTextField = javax.swing.JTextField(30)
    val creatorPanel = de.bund.bfr.rakip.editor.CreatorPanel(generalInformation.creators)
    val creationDateChooser = de.bund.bfr.rakip.editor.FixedJDateChooser()
    val rightsField = AutoSuggestField(10)
    val availabilityCheckBox = javax.swing.JCheckBox()
    val urlTextField = javax.swing.JTextField(30)
    val formatField = AutoSuggestField(10)
    val referencePanel = de.bund.bfr.rakip.editor.ReferencePanel(refs = generalInformation.reference, isAdvanced = advancedCheckBox.isSelected)
    val languageTextField = javax.swing.JTextField(30)
    val softwareField = AutoSuggestField(10)
    val languageWrittenInField = AutoSuggestField(10)
    val statusField = AutoSuggestField(10)
    val objectiveTextField = javax.swing.JTextField(30)
    val descriptionTextField = javax.swing.JTextField(30)

    init {

        // init combo boxes
        rightsField.setPossibleValues(de.bund.bfr.rakip.editor.vocabs["Rigthts"])
        formatField.setPossibleValues(de.bund.bfr.rakip.editor.vocabs["Format"])
        softwareField.setPossibleValues(de.bund.bfr.rakip.editor.vocabs["Software"])
        languageWrittenInField.setPossibleValues(de.bund.bfr.rakip.editor.vocabs["Language written in"])
        statusField.setPossibleValues(de.bund.bfr.rakip.editor.vocabs["Status"])

        // initialize interface with `generalInformation`
        studyNameTextField.text = generalInformation.name
        identifierTextField.text = generalInformation.identifier
        creationDateChooser.date = generalInformation.creationDate
        rightsField.selectedItem = generalInformation.rights
        availabilityCheckBox.isSelected = generalInformation.isAvailable
        urlTextField.text = generalInformation.url.toString()
        formatField.selectedItem = generalInformation.format
        languageTextField.text = generalInformation.language
        softwareField.selectedItem = generalInformation.software
        languageWrittenInField.selectedItem = generalInformation.languageWrittenIn
        statusField.selectedItem = generalInformation.status
        objectiveTextField.text = generalInformation.objective
        descriptionTextField.text = generalInformation.description

        initUI()
    }

    private fun initUI() {

        val studyNameLabel = createLabel(text = studyName, tooltip = studyNameTooltip)
        val identifierLabel = createLabel(text = identifier, tooltip = identifierTooltip)
        val creationDateLabel = createLabel(text = creationDate, tooltip = creationDateTooltip)
        val rightsLabel = createLabel(text = rights, tooltip = rightsTooltip)
        val urlLabel = createLabel(text = url, tooltip = urlTooltip)
        val formatLabel = createLabel(text = format, tooltip = formatTooltip)
        val languageLabel = createLabel(text = language, tooltip = languageTooltip)
        val softwareLabel = createLabel(text = software, tooltip = softwareTooltip)
        val languageWrittenInLabel = createLabel(text = languageWrittenIn, tooltip = languageWrittenInTooltip)
        val statusLabel = createLabel(text = status, tooltip = statusTooltip)
        val objectiveLabel = createLabel(text = objective, tooltip = objectiveTooltip)
        val descriptionLabel = createLabel(text = description, tooltip = descriptionTooltip)

        // hide initially advanced comps
        val advancedComps = listOf<javax.swing.JComponent>(
                urlLabel, urlTextField,
                formatLabel, formatField,
                languageLabel, languageTextField,
                softwareLabel, softwareField,
                languageWrittenInLabel, languageWrittenInField,
                statusLabel, statusField,
                objectiveLabel, objectiveTextField,
                descriptionLabel, descriptionTextField)
        advancedComps.forEach { it.isVisible = false }

        val propertiesPanel = javax.swing.JPanel(java.awt.GridBagLayout())

        propertiesPanel.add(comp = studyNameLabel, gridy = 1, gridx = 0)
        propertiesPanel.add(comp = studyNameTextField, gridy = 1, gridx = 1, gridwidth = 2)

        propertiesPanel.add(comp = identifierLabel, gridy = 2, gridx = 0)
        propertiesPanel.add(comp = identifierTextField, gridy = 2, gridx = 1, gridwidth = 2)

        propertiesPanel.add(comp = creatorPanel, gridy = 3, gridx = 0, gridwidth = 3)

        propertiesPanel.add(comp = creationDateLabel, gridy = 4, gridx = 0)
        propertiesPanel.add(comp = creationDateChooser, gridy = 4, gridx = 1)

        propertiesPanel.add(comp = rightsLabel, gridy = 5, gridx = 0)
        propertiesPanel.add(comp = rightsField, gridy = 5, gridx = 1, gridwidth = 2)

        availabilityCheckBox.text = de.bund.bfr.rakip.editor.GeneralInformationPanel.Companion.availability
        availabilityCheckBox.toolTipText = de.bund.bfr.rakip.editor.GeneralInformationPanel.Companion.availabilityTooltip
        propertiesPanel.add(comp = availabilityCheckBox, gridy = 6, gridx = 0)

        propertiesPanel.add(comp = urlLabel, gridy = 7, gridx = 0)
        propertiesPanel.add(comp = urlTextField, gridy = 7, gridx = 1, gridwidth = 2)

        propertiesPanel.add(comp = formatLabel, gridy = 8, gridx = 0)
        propertiesPanel.add(comp = formatField, gridy = 8, gridx = 1, gridwidth = 2)

        propertiesPanel.add(comp = referencePanel, gridy = 9, gridx = 0, gridwidth = 3)

        propertiesPanel.add(comp = languageLabel, gridy = 10, gridx = 0)
        propertiesPanel.add(comp = languageTextField, gridy = 10, gridx = 1, gridwidth = 2)

        propertiesPanel.add(comp = softwareLabel, gridy = 11, gridx = 0)
        propertiesPanel.add(comp = softwareField, gridy = 11, gridx = 1)

        propertiesPanel.add(comp = languageWrittenInLabel, gridy = 12, gridx = 0)
        propertiesPanel.add(comp = languageWrittenInField, gridy = 12, gridx = 1)

        propertiesPanel.add(comp = statusLabel, gridy = 13, gridx = 0)
        propertiesPanel.add(comp = statusField, gridy = 13, gridx = 1, gridwidth = 2)

        propertiesPanel.add(comp = objectiveLabel, gridy = 14, gridx = 0)
        propertiesPanel.add(comp = objectiveTextField, gridy = 14, gridx = 1, gridwidth = 2)

        propertiesPanel.add(comp = descriptionLabel, gridy = 15, gridx = 0)
        propertiesPanel.add(comp = descriptionTextField, gridy = 15, gridx = 1, gridwidth = 2)

        advancedCheckBox.addItemListener { _ ->
            val showAdvanced = advancedCheckBox.isSelected
            advancedComps.forEach { it.isVisible = showAdvanced }
            referencePanel.isAdvanced = showAdvanced
        }

        add(createAdvancedPanel(checkbox = advancedCheckBox))
        add(javax.swing.Box.createGlue())
        add(propertiesPanel)
    }

    fun toGeneralInformation(): GeneralInformation {

        // Get mandatory fields first  // TODO: cast them temporarily to empty strings (SHOULD BE VALIDATED)
        val studyName = studyNameTextField.text ?: ""
        val identifier = identifierTextField.text ?: ""
        val creationDateChooser = creationDateChooser.date ?: java.util.Date()
        val rights = rightsField.selectedItem as? String ?: ""
        val isAvailable = availabilityCheckBox.isSelected
        val url = java.net.URL(urlTextField.text ?: "")

        val gi = GeneralInformation(name = studyName, identifier = identifier, creationDate = creationDateChooser,
                rights = rights, isAvailable = isAvailable, url = url)

        gi.creators.addAll(elements = creatorPanel.creators)
        gi.format = formatField.selectedItem as? String ?: ""
        gi.reference.addAll(referencePanel.refs)
        gi.language = languageTextField.text
        gi.software = softwareField.selectedItem as? String ?: ""
        gi.languageWrittenIn = languageWrittenInField.selectedItem as? String ?: ""
        gi.status = statusField.selectedItem as? String ?: ""
        gi.objective = objectiveTextField.text
        gi.description = descriptionTextField.text

        return gi
    }
}

class ReferencePanel(val refs: MutableList<Record>, var isAdvanced: Boolean) : javax.swing.JPanel(java.awt.BorderLayout()) {

    init {
        border = javax.swing.BorderFactory.createTitledBorder("References")

        val dtm = NonEditableTableModel()
        refs.forEach { dtm.addRow(arrayOf(it)) }

        val renderer = object : javax.swing.table.DefaultTableCellRenderer() {
            override fun setValue(value: Any?) {
                if (value == null) text = ""
                else {
                    val record = value as Record

                    val firstAuthor = record.authors?.get(0) ?: ""
                    val publicationYear = record.pubblicationYear.orEmpty()
                    val title = record.title.orEmpty()
                    text = "${firstAuthor}_${publicationYear}_$title"
                }
            }
        }
        val myTable = HeadlessTable(model = dtm, renderer = renderer)

        // buttons
        val buttonsPanel = ButtonsPanel()
        buttonsPanel.addButton.addActionListener { _ ->
            val editPanel = EditReferencePanel(isAdvanced = isAdvanced)

            val dlg = ValidatableDialog(panel = editPanel, dialogTitle = "Create reference")
            if (dlg.getValue() == javax.swing.JOptionPane.OK_OPTION) {
                dtm.addRow(arrayOf(editPanel.toRecord()))
            }
        }

        buttonsPanel.modifyButton.addActionListener { _ ->
            val rowToEdit = myTable.selectedRow
            if (rowToEdit != -1) {
                val ref = dtm.getValueAt(rowToEdit, 0) as Record

                val editPanel = EditReferencePanel(ref, isAdvanced = isAdvanced)

                val dlg = ValidatableDialog(panel = editPanel, dialogTitle = "Modify reference")
                if (dlg.getValue() == javax.swing.JOptionPane.OK_OPTION) {
                    dtm.setValueAt(editPanel.toRecord(), rowToEdit, 0)
                }
            }
        }

        buttonsPanel.removeButton.addActionListener { _ ->
            val rowToDelete = myTable.selectedRow
            if (rowToDelete != -1) dtm.removeRow(rowToDelete)
        }

        add(myTable, java.awt.BorderLayout.NORTH)
        add(buttonsPanel, java.awt.BorderLayout.SOUTH)
    }
}

/** Fixes JDateChooser and disables the text field */
class FixedJDateChooser : JDateChooser() {

    init {
        // Fixes bug AP-5865
        popup.isFocusable = false

        // Text field is disabled so that the dates are only chooseable through the calendar widget. Then there is no
        // need to validate the dates
        dateEditor.setEnabled(true)
    }
}

class CreatorPanel(val creators: MutableList<VCard>) : javax.swing.JPanel(java.awt.BorderLayout()) {

    init {
        border = javax.swing.BorderFactory.createTitledBorder("Creators")

        val dtm = NonEditableTableModel()
        creators.forEach { dtm.addRow(arrayOf(it)) }

        val renderer = object : javax.swing.table.DefaultTableCellRenderer() {
            override fun setValue(value: Any?) {
                value?.let {
                    val creator = value as VCard

                    val givenName = creator.nickname?.values?.get(0)
                    val familyName = creator.formattedName.value
                    val contact = creator.emails?.get(0)?.value

                    text = "${givenName}_${familyName}_$contact"
                }
            }
        }
        val myTable = HeadlessTable(model = dtm, renderer = renderer)

        // buttons
        val buttonsPanel = ButtonsPanel()
        buttonsPanel.addButton.addActionListener { _ ->
            val editPanel = de.bund.bfr.rakip.editor.EditCreatorPanel()
            val result = showConfirmDialog(panel = editPanel, title = "Create creator")
            if (result == javax.swing.JOptionPane.OK_OPTION) {
                dtm.addRow(arrayOf(editPanel.toVCard()))
            }
        }

        buttonsPanel.modifyButton.addActionListener { _ ->
            val rowToEdit = myTable.selectedRow
            if (rowToEdit != -1) {
                val creator = dtm.getValueAt(rowToEdit, 0) as VCard

                val editPanel = de.bund.bfr.rakip.editor.EditCreatorPanel(creator)
                val result = showConfirmDialog(panel = editPanel, title = "Modify creator")
                if (result == javax.swing.JOptionPane.OK_OPTION) {
                    dtm.setValueAt(editPanel.toVCard(), rowToEdit, 0)
                }
            }
        }

        buttonsPanel.removeButton.addActionListener { _ ->
            val rowToDelete = myTable.selectedRow
            if (rowToDelete != -1) dtm.removeRow(rowToDelete)
        }

        add(myTable, java.awt.BorderLayout.NORTH)
        add(buttonsPanel, java.awt.BorderLayout.SOUTH)
    }
}

class EditCreatorPanel(creator: VCard? = null) : javax.swing.JPanel(java.awt.GridBagLayout()) {

    private val givenNameField = javax.swing.JTextField(30)
    private val familyNameField = javax.swing.JTextField(30)
    private val contactField = javax.swing.JTextField(30)

    companion object {
        val givenName = "Given name"
        val familyName = "Family name"
        val contact = "Contact"
    }

    init {
        initUI()

        // Populate interface if `creator` is provided
        creator?.let {
            givenNameField.text = it.nickname?.values?.firstOrNull()
            familyNameField.text = it.formattedName?.value
            contactField.text = it.emails?.first()?.value
        }
    }

    private fun initUI() {
        val pairList = listOf<Pair<javax.swing.JLabel, javax.swing.JComponent>>(
                Pair(first = javax.swing.JLabel(givenName), second = givenNameField),
                Pair(first = javax.swing.JLabel(familyName), second = familyNameField),
                Pair(first = javax.swing.JLabel(contact), second = contactField)
        )

        addGridComponents(pairs = pairList)
    }

    fun toVCard(): VCard {
        val vCard = VCard()
        givenNameField.text?.let { vCard.setNickname(it) }
        familyNameField.text?.let { vCard.setFormattedName(it) }
        contactField.text?.let { vCard.addEmail(it) }

        return vCard
    }
}

class ScopePanel(val scope: Scope) : javax.swing.Box(javax.swing.BoxLayout.PAGE_AXIS) {

    val productButton = javax.swing.JButton()
    val hazardButton = javax.swing.JButton()
    val populationButton = javax.swing.JButton()
    val commentField = javax.swing.JTextArea(5, 30)
    val dateChooser = de.bund.bfr.rakip.editor.FixedJDateChooser()
    val regionField = AutoSuggestField(10)
    val countryField = AutoSuggestField(10)

    val advancedCheckBox = javax.swing.JCheckBox("Advanced")

    companion object {
        val product = "Product"

        val hazard = "Hazard"

        val populationGroup = "Population group"

        val comment = "General comment"
        val commentTooltip = "General comments on the scope"

        val temporalInformation = "Temporal information"
        val temporalInformationTooltip = "Temporal information on which the model or data applies"

        val region = "Region"
        val regionTooltip = "Spatial information (area) on which the model or data applies"

        val country = "Country"
        val countryTooltip = "Country on which the model or data applies"
    }

    init {

        // init combo boxes
        regionField.setPossibleValues(de.bund.bfr.rakip.editor.vocabs["Region"])
        countryField.setPossibleValues(de.bund.bfr.rakip.editor.vocabs["Country"])

        regionField.selectedItem = scope.region.firstOrNull()
        countryField.selectedItem = scope.country.firstOrNull()

        initUI()
    }

    private fun initUI() {
        val propertiesPanel = javax.swing.JPanel(java.awt.GridBagLayout())

        productButton.toolTipText = "Click me to add a product"
        productButton.addActionListener { _ ->
            val editPanel = EditProductPanel(product = scope.product,
                    isAdvanced = advancedCheckBox.isSelected)
            val dlg = ValidatableDialog(panel = editPanel, dialogTitle = "Create a product")

            if (dlg.getValue() == javax.swing.JOptionPane.OK_OPTION) {
                val product = editPanel.toProduct()
                productButton.text = "${product.environmentName} [${product.environmentUnit}]"
                scope.product = product
            }
        }

        hazardButton.toolTipText = "Click me to add a hazard"
        hazardButton.addActionListener { _ ->
            val editPanel = EditHazardPanel(hazard = scope.hazard,
                    isAdvanced = advancedCheckBox.isSelected)
            val dlg = ValidatableDialog(panel = editPanel, dialogTitle = "Create a hazard")

            if (dlg.getValue() == javax.swing.JOptionPane.OK_OPTION) {
                val hazard = editPanel.toHazard()
                hazardButton.text = "${hazard.hazardName} [${hazard.hazardUnit}]"
                scope.hazard = hazard
            }
        }

        populationButton.toolTipText = "Click me to add a Population group"
        populationButton.addActionListener { _ ->
            val editPanel = EditPopulationGroupPanel(populationGroup = scope.populationGroup, isAdvanced = advancedCheckBox.isSelected)

            val dlg = ValidatableDialog(panel = editPanel, dialogTitle = "Create a Population Group")

            if (dlg.getValue() == javax.swing.JOptionPane.OK_OPTION) {
                val populationGroup = editPanel.toPopulationGroup()
                populationButton.text = populationGroup.populationName
                scope.populationGroup = populationGroup
            }
        }

        val pairList = listOf<Pair<javax.swing.JLabel, javax.swing.JComponent>>(
                Pair(first = javax.swing.JLabel(product), second = productButton),
                Pair(first = javax.swing.JLabel(hazard), second = hazardButton),
                Pair(first = javax.swing.JLabel(populationGroup), second = populationButton),
                Pair(first = createLabel(text = comment, tooltip = commentTooltip), second = commentField),
                Pair(first = createLabel(text = temporalInformation, tooltip = temporalInformationTooltip), second = dateChooser),
                Pair(first = createLabel(text = region, tooltip = regionTooltip), second = regionField),
                Pair(first = createLabel(text = country, tooltip = countryTooltip), second = countryField)
        )
        propertiesPanel.addGridComponents(pairs = pairList)

        // advancedCheckBox
        advancedCheckBox.addItemListener { _ -> println("dummy listener") }  // TODO: not implemented yet

        add(createAdvancedPanel(checkbox = advancedCheckBox))
        add(javax.swing.Box.createGlue())
        add(propertiesPanel)
    }
}

class DataBackgroundPanel(var dataBackground: DataBackground? = null) : javax.swing.Box(javax.swing.BoxLayout.PAGE_AXIS) {

    companion object {
        val studySample = "Study sample"
        val dietaryAssessmentMethod = "Dietary assessment method"
        val laboratoryAccreditation = "Laboratory accreditation"
        val assay = "Assay"
    }

    val advancedCheckBox = javax.swing.JCheckBox("Advanced")

    val laboratoryAccreditationField = AutoSuggestField(10)

    init {
        initUI()
    }

    private fun initUI() {

        val studyPanel = de.bund.bfr.rakip.editor.StudyPanel()
        studyPanel.border = javax.swing.BorderFactory.createTitledBorder("Study")

        val studySampleButton = javax.swing.JButton()
        studySampleButton.toolTipText = "Click me to add Study Sample"
        studySampleButton.addActionListener { _ ->
            val editPanel = EditStudySamplePanel(studySample = dataBackground?.studySample, isAdvanced = advancedCheckBox.isSelected)

            val dlg = ValidatableDialog(panel = editPanel, dialogTitle = "Create Study sample")

            if (dlg.getValue() == javax.swing.JOptionPane.OK_OPTION) {
                val studySample = editPanel.toStudySample()

                if (dataBackground == null) dataBackground = DataBackground()
                dataBackground?.studySample = studySample
            }
        }

        val dietaryAssessmentMethodButton = javax.swing.JButton()
        dietaryAssessmentMethodButton.toolTipText = "Click me to add Dietary assessment method"
        dietaryAssessmentMethodButton.addActionListener { _ ->
            val editPanel = EditDietaryAssessmentMethodPanel(
                    dietaryAssessmentMethod = dataBackground?.dietaryAssessmentMethod, isAdvanced = advancedCheckBox.isSelected)

            val dlg = ValidatableDialog(panel = editPanel, dialogTitle = "Create dietary assessment method")
            if (dlg.getValue() == javax.swing.JOptionPane.OK_OPTION) {
                val dietaryAssessmentMethod = editPanel.toDietaryAssessmentMethod()

                if (dataBackground == null) dataBackground = DataBackground(dietaryAssessmentMethod = dietaryAssessmentMethod)
                else dataBackground?.dietaryAssessmentMethod = dietaryAssessmentMethod
            }
        }

        laboratoryAccreditationField.setPossibleValues(de.bund.bfr.rakip.editor.vocabs["Laboratory accreditation"])

        val assayButton = javax.swing.JButton()
        assayButton.toolTipText = "Click me to add Assay"
        assayButton.addActionListener { _ ->
            val editPanel = EditAssayPanel(assay = dataBackground?.assay, isAdvanced = advancedCheckBox.isSelected)

            val dlg = ValidatableDialog(panel = editPanel, dialogTitle = "Create assay")
            if (dlg.getValue() == javax.swing.JOptionPane.OK_OPTION) {
                val assay = editPanel.toAssay()

                if (dataBackground == null) dataBackground = DataBackground(assay = assay)
                else dataBackground?.assay = assay
            }
        }

        val propertiesPanel = javax.swing.JPanel(java.awt.GridBagLayout())

        propertiesPanel.add(comp = studyPanel, gridy = 0, gridx = 0, gridwidth = 3)
        propertiesPanel.add(comp = javax.swing.JLabel(studySample), gridy = 1, gridx = 0)
        propertiesPanel.add(comp = studySampleButton, gridy = 1, gridx = 1)

        propertiesPanel.add(comp = javax.swing.JLabel(dietaryAssessmentMethod), gridy = 2, gridx = 0)
        propertiesPanel.add(comp = dietaryAssessmentMethodButton, gridy = 2, gridx = 1)

        propertiesPanel.add(comp = javax.swing.JLabel(laboratoryAccreditation), gridy = 3, gridx = 0)
        propertiesPanel.add(comp = laboratoryAccreditationField, gridy = 3, gridx = 1)

        propertiesPanel.add(comp = javax.swing.JLabel(assay), gridy = 4, gridx = 0)
        propertiesPanel.add(comp = assayButton, gridy = 4, gridx = 1)

        // `Advanced` checkbox
        advancedCheckBox.addItemListener { _ ->
            studyPanel.advancedComps.forEach { it.isVisible = advancedCheckBox.isSelected }
            println("dummy listener")  // TODO: implement listener
        }

        add(createAdvancedPanel(checkbox = advancedCheckBox))
        add(javax.swing.Box.createGlue())
        add(propertiesPanel)
    }
}

class StudyPanel(study: Study? = null) : javax.swing.JPanel(java.awt.GridBagLayout()) {

    companion object {
        val studyIdentifier = "Study identifier"
        val studyIdentifierTooltip = "A user defined identifier for the study"

        val studyTitle = "Study title"
        val studyTitleTooltip = "A title for the Study."

        val studyDescription = "Study description"
        val studyDescriptionTooltip = "A brief assayDescription of the study aims."

        val studyDesignType = "Study design type"
        val studyDesignTypeTooltip = "The type of study design being employed"

        val studyAssayMeasurementsType = "Study assay measurements Type"
        val studyAssayMeasurementsTypeTooltip = "The measurement being observed in this assay"

        val studyAssayTechnologyType = "Study Assay Technology Type"
        val studyAssayTechnologyTypeTooltip = "The technology being employed to observe this measurement"

        val studyAssayTechnologyPlatform = "Study Assay Technology Platform"
        val studyAssayTechnologyPlatformTooltip = "The technology platform used"

        val accreditationProcedure = "<html><p>Accreditation procedure for<p>the assay technology</html>"
        val accreditationProcedureTooltip = "The type of study design being employed"

        val studyProtocolName = "Study protocol name"
        val studyProtocolNameTooltip = "The name of the protocol, e.g.Extraction Protocol"

        val studyProtocolType = "Study protocol type"
        val studyProtocolTypeTooltip = """
            |<html>
            |<p>The type of the protocol, preferably coming from an Ontology, e.g.
            |<p>Extraction Protocol
            |</html>
            """.trimMargin()

        val studyProtocolDescription = "Study protocol"
        val studyProtocolDescriptionTooltip = "A description of the Protocol."

        val studyProtocolURI = "Study protocol URI"
        val studyProtocolURITooltip = "A URI to link out to a publication, web page, etc. describing the protocol."

        val studyProtocolVersion = "Study protocol version"
        val studyProtocolVersionTooltip = "The version of the protocol used, where applicable."

        val studyProtocolParameters = "Study protocol parameters name"
        val studyProtocolParametersTooltip = "The parameters used when executing this protocol."

        val studyProtocolComponentsType = "Study protocol components"
        val studyProtocolComponentsTypeTooltip = "The components used when carrying out this protocol."
    }

    val studyIdentifierLabel = createLabel(text = studyIdentifier, tooltip = studyIdentifierTooltip)
    val studyIdentifierTextField = javax.swing.JTextField(30)

    val studyTitleLabel = createLabel(text = studyTitle, tooltip = studyTitleTooltip)
    val studyTitleTextField = javax.swing.JTextField(30)

    val studyDescriptionLabel = createLabel(text = studyDescription, tooltip = studyDescriptionTooltip)
    val studyDescriptionTextArea = javax.swing.JTextArea(5, 30)

    val studyDesignTypeLabel = createLabel(text = studyDesignType, tooltip = studyDesignTypeTooltip)
    val studyDesignTypeField = AutoSuggestField(10)

    val studyAssayMeasurementsTypeLabel = createLabel(text = studyAssayMeasurementsType, tooltip = studyAssayMeasurementsTypeTooltip)
    val studyAssayMeasurementsTypeField = AutoSuggestField(10)

    val studyAssayTechnologyTypeLabel = createLabel(text = studyAssayTechnologyType, tooltip = studyAssayTechnologyTypeTooltip)
    val studyAssayTechnologyTypeField = AutoSuggestField(10)

    val studyAssayTechnologyPlatformLabel = createLabel(text = studyAssayTechnologyPlatform, tooltip = studyAssayTechnologyPlatformTooltip)
    val studyAssayTechnologyPlatformTextField = javax.swing.JTextField(30)

    val accreditationProcedureLabel = createLabel(text = accreditationProcedure, tooltip = accreditationProcedureTooltip)
    val accreditationProcedureField = AutoSuggestField(10)

    val studyProtocolNameLabel = createLabel(text = studyProtocolName, tooltip = studyProtocolNameTooltip)
    val studyProtocolNameTextField = javax.swing.JTextField(30)

    val studyProtocolTypeLabel = createLabel(text = studyProtocolType, tooltip = studyProtocolTypeTooltip)
    val studyProtocolTypeField = AutoSuggestField(10)

    val studyProtocolDescriptionLabel = createLabel(text = studyProtocolDescription, tooltip = studyProtocolDescriptionTooltip)
    val studyProtocolDescriptionTextField = javax.swing.JTextField(30)

    val studyProtocolURILabel = createLabel(text = studyProtocolURI, tooltip = studyProtocolURITooltip)
    val studyProtocolURITextField = javax.swing.JTextField(30)

    val studyProtocolVersionLabel = createLabel(text = studyProtocolVersion, tooltip = studyProtocolVersionTooltip)
    val studyProtocolVersionTextField = javax.swing.JTextField(30)

    val studyProtocolParametersLabel = createLabel(text = studyProtocolParameters, tooltip = studyProtocolParametersTooltip)
    val studyProtocolParametersField = AutoSuggestField(10)

    val studyProtocolComponentsTypeLabel = createLabel(text = studyProtocolComponentsType, tooltip = studyProtocolComponentsTypeTooltip)
    val studyProtocolComponentsTypeField = AutoSuggestField(10)

    val advancedComps = listOf<javax.swing.JComponent>(
            studyDescriptionLabel, studyDescriptionTextArea,
            studyDesignTypeLabel, studyDesignTypeField,
            studyAssayMeasurementsTypeLabel, studyAssayMeasurementsTypeField,
            studyAssayTechnologyTypeLabel, studyAssayTechnologyTypeField,
            studyAssayTechnologyPlatformLabel, studyAssayTechnologyPlatformTextField,
            accreditationProcedureLabel, accreditationProcedureField,
            studyProtocolNameLabel, studyProtocolNameTextField,
            studyProtocolTypeLabel, studyProtocolTypeField,
            studyProtocolDescriptionLabel, studyProtocolDescriptionTextField,
            studyProtocolURILabel, studyProtocolURITextField,
            studyProtocolVersionLabel, studyProtocolVersionTextField,
            studyProtocolParametersLabel, studyProtocolParametersField,
            studyProtocolComponentsTypeLabel, studyProtocolComponentsTypeField
    )

    init {

        advancedComps.forEach { it.isVisible = false }

        // init combo boxes
        studyDesignTypeField.setPossibleValues(de.bund.bfr.rakip.editor.vocabs["Study Design Type"])
        studyAssayMeasurementsTypeField.setPossibleValues(de.bund.bfr.rakip.editor.vocabs["Study Assay Measurement Type"])
        studyAssayTechnologyTypeField.setPossibleValues(de.bund.bfr.rakip.editor.vocabs["Study Assay Technology Type"])
        accreditationProcedureField.setPossibleValues(de.bund.bfr.rakip.editor.vocabs["Accreditation procedure Ass.Tec"])
        studyProtocolTypeField.setPossibleValues(de.bund.bfr.rakip.editor.vocabs["Study Protocol Type"])
        studyProtocolParametersField.setPossibleValues(de.bund.bfr.rakip.editor.vocabs["Study Protocol Parameters Name"])
        studyProtocolComponentsTypeField.setPossibleValues(de.bund.bfr.rakip.editor.vocabs["Study Protocol Components Type"])

        val pairList = listOf<Pair<javax.swing.JLabel, javax.swing.JComponent>>(
                Pair(first = studyIdentifierLabel, second = studyIdentifierTextField),
                Pair(first = studyTitleLabel, second = studyTitleTextField),
                Pair(first = studyDescriptionLabel, second = studyDescriptionTextArea),
                Pair(first = studyDesignTypeLabel, second = studyDesignTypeField),
                Pair(first = studyAssayMeasurementsTypeLabel, second = studyAssayMeasurementsTypeField),
                Pair(first = studyAssayTechnologyTypeLabel, second = studyAssayTechnologyTypeField),
                Pair(first = studyAssayTechnologyPlatformLabel, second = studyAssayTechnologyPlatformTextField),
                Pair(first = accreditationProcedureLabel, second = accreditationProcedureField),
                Pair(first = studyProtocolNameLabel, second = studyProtocolNameTextField),
                Pair(first = studyProtocolTypeLabel, second = studyProtocolTypeField),
                Pair(first = studyProtocolDescriptionLabel, second = studyProtocolDescriptionTextField),
                Pair(first = studyProtocolURILabel, second = studyProtocolURITextField),
                Pair(first = studyProtocolVersionLabel, second = studyProtocolVersionTextField),
                Pair(first = studyProtocolParametersLabel, second = studyProtocolParametersField),
                Pair(first = studyProtocolComponentsTypeLabel, second = studyProtocolComponentsTypeField)
        )

        addGridComponents(pairs = pairList)
    }
}

class ModelMathPanel(modelMath: ModelMath? = null) : javax.swing.Box(javax.swing.BoxLayout.PAGE_AXIS) {

    companion object {

        val parameters = "Parameters"
        val qualityMeasures = "Quality measures"
        val modelEquation = "Model equation"
        val fittingProcedure = "Fitting procedure"
    }

    val advancedCheckBox = javax.swing.JCheckBox("Advanced")

    init {

        val parametersPanel = de.bund.bfr.rakip.editor.ParameterPanel(isAdvanced = advancedCheckBox.isSelected)

        val qualityMeasuresPanel = de.bund.bfr.rakip.editor.QualityMeasuresPanel()

        val modelEquationPanel = de.bund.bfr.rakip.editor.ModelEquationsPanel(isAdvanced = advancedCheckBox.isSelected)

//        val fittingProcedure = JPanel()
//        fittingProcedure.border = BorderFactory.createTitledBorder(fittingProcedure)

        val propertiesPanel = javax.swing.JPanel(java.awt.GridBagLayout())
        propertiesPanel.add(comp = parametersPanel, gridy = 0, gridx = 0)
        propertiesPanel.add(comp = qualityMeasuresPanel, gridy = 1, gridx = 0)
        propertiesPanel.add(comp = modelEquationPanel, gridy = 2, gridx = 0)

        add(createAdvancedPanel(checkbox = advancedCheckBox))
        add(javax.swing.Box.createGlue())
        add(propertiesPanel)
    }
}

class ParameterPanel(val parameters: MutableList<Parameter> = mutableListOf(), isAdvanced: Boolean) : javax.swing.JPanel(java.awt.BorderLayout()) {

    init {
        border = javax.swing.BorderFactory.createTitledBorder(de.bund.bfr.rakip.editor.ModelMathPanel.Companion.parameters)

        val dtm = NonEditableTableModel()
        parameters.forEach { dtm.addRow(arrayOf(it)) }

        val renderer = object : javax.swing.table.DefaultTableCellRenderer() {
            override fun setValue(value: Any?) {
                text = (value as Parameter?)?.id
            }
        }
        val myTable = HeadlessTable(model = dtm, renderer = renderer)

        // buttons
        val buttonsPanel = ButtonsPanel()
        buttonsPanel.addButton.addActionListener { _ ->
            val editPanel = EditParameterPanel(isAdvanced = isAdvanced)

            val dlg = ValidatableDialog(panel = editPanel, dialogTitle = "Create parameter")
            if (dlg.getValue() == javax.swing.JOptionPane.OK_OPTION) {
                // FIXME: Uncomment once EditParameterPanel.toParameter is implemented
//                dtm.addRow(arrayOf(editParameterPanel.toParameter()))
            }
        }

        buttonsPanel.modifyButton.addActionListener { _ ->
            println("dummy listener")
        }

        buttonsPanel.removeButton.addActionListener { _ ->
            println("dummy listener")
        }

        add(myTable, java.awt.BorderLayout.NORTH)
        add(buttonsPanel, java.awt.BorderLayout.SOUTH)
    }
}

class QualityMeasuresPanel(sse: Double? = null, mse: Double? = null, rmse: Double? = null,
                           r2: Double? = null, aic: Double? = null, bic: Double? = null) : javax.swing.JPanel(java.awt.GridBagLayout()) {

    val sseSpinnerModel = createSpinnerDoubleModel()
    val mseSpinnerModel = createSpinnerDoubleModel()
    val rmseSpinnerModel = createSpinnerDoubleModel()
    val r2SpinnerModel = createSpinnerDoubleModel()
    val aicSpinnerModel = createSpinnerDoubleModel()
    val bicSpinnerModel = createSpinnerDoubleModel()

    init {
        val pairList = listOf<Pair<javax.swing.JLabel, javax.swing.JComponent>>(
                Pair(first = javax.swing.JLabel("SSE"), second = createSpinner(sseSpinnerModel)),
                Pair(first = javax.swing.JLabel("MSE"), second = createSpinner(mseSpinnerModel)),
                Pair(first = javax.swing.JLabel("RMSE"), second = createSpinner(rmseSpinnerModel)),
                Pair(first = javax.swing.JLabel("r-Squared"), second = createSpinner(r2SpinnerModel)),
                Pair(first = javax.swing.JLabel("AIC"), second = createSpinner(aicSpinnerModel)),
                Pair(first = javax.swing.JLabel("BIC"), second = createSpinner(bicSpinnerModel))
        )

        addGridComponents(pairs = pairList)

        border = javax.swing.BorderFactory.createTitledBorder(de.bund.bfr.rakip.editor.ModelMathPanel.Companion.qualityMeasures)
    }

    // TODO: toQualityMeasures
}

class ModelEquationsPanel(
        val equations: MutableList<ModelEquation> = mutableListOf(),
        isAdvanced: Boolean
) : javax.swing.JPanel(java.awt.BorderLayout()) {

    init {
        border = javax.swing.BorderFactory.createTitledBorder(de.bund.bfr.rakip.editor.ModelMathPanel.Companion.modelEquation)

        val dtm = NonEditableTableModel()
        equations.forEach { dtm.addRow(arrayOf(it)) }

        val renderer = object : javax.swing.table.DefaultTableCellRenderer() {
            override fun setValue(value: Any?) {
                text = (value as ModelEquation?)?.equationName
            }
        }
        val myTable = HeadlessTable(model = dtm, renderer = renderer)

        val buttonsPanel = ButtonsPanel()
        buttonsPanel.addButton.addActionListener { _ ->
            val editPanel = EditModelEquationPanel(isAdvanced = isAdvanced)

            val dlg = ValidatableDialog(panel = editPanel, dialogTitle = "Create equation")
            if (dlg.getValue() == javax.swing.JOptionPane.OK_OPTION) {
                // TODO: process result
            }
        }

        buttonsPanel.modifyButton.addActionListener { _ ->
            val rowToEdit = myTable.selectedRow
            if (rowToEdit != -1) {
                val equation = dtm.getValueAt(rowToEdit, 0) as ModelEquation

                val editPanel = EditModelEquationPanel(equation = equation, isAdvanced = isAdvanced)

                val dlg  = ValidatableDialog(panel = editPanel, dialogTitle = "Modify equation")

                if (dlg.getValue() == javax.swing.JOptionPane.OK_OPTION) {
                    // TODO: process result
                }
            }
        }

        buttonsPanel.removeButton.addActionListener { _ ->
            val rowToDelete = myTable.selectedRow
            if (rowToDelete != -1) dtm.removeRow(rowToDelete)
        }

        add(myTable, java.awt.BorderLayout.NORTH)
        add(buttonsPanel, java.awt.BorderLayout.SOUTH)
    }
}