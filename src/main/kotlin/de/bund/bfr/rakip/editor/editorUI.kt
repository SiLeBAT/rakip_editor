package de.bund.bfr.rakip.editor

import com.gmail.gcolaianni5.jris.bean.Record
import com.toedter.calendar.JDateChooser
import de.bund.bfr.knime.ui.AutoSuggestField
import de.bund.bfr.rakip.generic.*
import ezvcard.VCard
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.util.*
import javax.swing.*
import javax.swing.table.DefaultTableCellRenderer

val logger: java.util.logging.Logger = java.util.logging.Logger.getAnonymousLogger()
val messages = ResourceBundle.getBundle("MessagesBundle", Locale.getDefault())!!

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

    val frame = JFrame()
    val generalInformationPanel = de.bund.bfr.rakip.editor.GeneralInformationPanel(gi)
    generalInformationPanel.studyNameTextField.text = gi.name
    generalInformationPanel.identifierTextField.text = gi.identifier
    generalInformationPanel.creationDateChooser.date = gi.creationDate
    generalInformationPanel.rightsField.setPossibleValues(vocabs["Rights"])
    generalInformationPanel.rightsField.selectedItem = gi.rights
    generalInformationPanel.availabilityCheckBox.isSelected = gi.isAvailable
    generalInformationPanel.urlTextField.text = gi.url.toString()
    generalInformationPanel.formatField.setPossibleValues(vocabs["Format"])
    generalInformationPanel.formatField.selectedItem = gi.format
    generalInformationPanel.languageTextField.text = gi.language
    generalInformationPanel.softwareField.setPossibleValues(vocabs["Software"])
    generalInformationPanel.softwareField.selectedItem = gi.software
    generalInformationPanel.languageWrittenInField.setPossibleValues(vocabs["Language writte in"])
    generalInformationPanel.languageWrittenInField.selectedItem = gi.languageWrittenIn
    generalInformationPanel.statusField.setPossibleValues(vocabs["Status"])
    generalInformationPanel.statusField.selectedItem = gi.status
    generalInformationPanel.objectiveTextField.text = gi.objective
    generalInformationPanel.descriptionTextField.text = gi.description

    val scopePanel = de.bund.bfr.rakip.editor.ScopePanel(Scope())
    val dataBackgroundPanel = de.bund.bfr.rakip.editor.DataBackgroundPanel()
    val modelMathPanel = de.bund.bfr.rakip.editor.ModelMathPanel()

    // Tabbed pane
    val tabbedPane = JTabbedPane()
    tabbedPane.addTab("General information", JScrollPane(generalInformationPanel))
    tabbedPane.addTab("Scope", JScrollPane(scopePanel))
    tabbedPane.addTab("Data background", JScrollPane(dataBackgroundPanel))
    tabbedPane.addTab("Model math", JScrollPane(modelMathPanel))

    frame.add(tabbedPane)
    frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    frame.title = "JTree Example"
    frame.setSize(500, 300)
    frame.minimumSize = Dimension(800, 500)
    frame.isVisible = true
    frame.addWindowListener(object : WindowAdapter() {
        // Save changes on close
        override fun windowClosing(windowEvent: WindowEvent?) {
            gi = generalInformationPanel.toGeneralInformation()
            System.exit(0)
        }
    })
}

fun JPanel.add(comp: JComponent, gridx: Int, gridy: Int, gridwidth: Int = 1, gridheight: Int = 1): Unit {
    val constraints = GridBagConstraints()
    constraints.gridx = gridx
    constraints.gridy = gridy
    constraints.gridwidth = gridwidth
    constraints.gridheight = gridheight
    constraints.ipadx = 10
    constraints.ipady = 10
    constraints.anchor = GridBagConstraints.LINE_START

    add(comp, constraints)
}

fun JPanel.addGridComponents(pairs: List<Pair<JLabel, JComponent>>) {

    val labelConstraints = GridBagConstraints()
    labelConstraints.gridx = 0
    labelConstraints.ipadx = 10
    labelConstraints.ipady = 10
    labelConstraints.anchor = GridBagConstraints.LINE_START

    val fieldConstraints = GridBagConstraints()
    fieldConstraints.gridx = 1
    fieldConstraints.ipadx = 10
    fieldConstraints.ipady = 10
    fieldConstraints.anchor = GridBagConstraints.LINE_START

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

class GeneralInformationPanel(generalInformation: GeneralInformation) : Box(BoxLayout.PAGE_AXIS) {

    val advancedCheckBox = JCheckBox("Advanced")

    val studyNameTextField = JTextField(30)
    val identifierTextField = JTextField(30)
    val creatorPanel = de.bund.bfr.rakip.editor.CreatorPanel(generalInformation.creators)
    val creationDateChooser = de.bund.bfr.rakip.editor.FixedJDateChooser()
    val rightsField = AutoSuggestField(10)
    val availabilityCheckBox = JCheckBox()
    val urlTextField = JTextField(30)
    val formatField = AutoSuggestField(10)
    val referencePanel = de.bund.bfr.rakip.editor.ReferencePanel(refs = generalInformation.reference, isAdvanced = advancedCheckBox.isSelected)
    val languageTextField = JTextField(30)
    val softwareField = AutoSuggestField(10)
    val languageWrittenInField = AutoSuggestField(10)
    val statusField = AutoSuggestField(10)
    val objectiveTextField = JTextField(30)
    val descriptionTextField = JTextField(30)

    init {

        // init combo boxes
        rightsField.setPossibleValues(vocabs["Rigthts"])
        formatField.setPossibleValues(vocabs["Format"])
        softwareField.setPossibleValues(vocabs["Software"])
        languageWrittenInField.setPossibleValues(vocabs["Language written in"])
        statusField.setPossibleValues(vocabs["Status"])

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

        val studyNameLabel = createLabel(text = messages.getString("GeneralInformationPanel.studyNameLabel"),
                tooltip = messages.getString("GeneralInformationPanel.studyNameTooltip"))
        val identifierLabel = createLabel(text = messages.getString("GeneralInformationPanel.identifierLabel"),
                tooltip = messages.getString("GeneralInformationPanel.identifierTooltip"))
        val creationDateLabel = createLabel(text = messages.getString("GeneralInformationPanel.creationDateLabel"),
                tooltip = messages.getString("GeneralInformationPanel.creationDateTooltip"))
        val rightsLabel = createLabel(text = messages.getString("GeneralInformationPanel.rightsLabel"),
                tooltip = messages.getString("GeneralInformationPanel.rightsTooltip"))
        val urlLabel = createLabel(text = messages.getString("GeneralInformationPanel.urlLabel"),
                tooltip = messages.getString("GeneralInformationPanel.urlTooltip"))
        val formatLabel = createLabel(text = messages.getString("GeneralInformationPanel.formatLabel"),
                tooltip = messages.getString("GeneralInformationPanel.formatTooltip"))
        val languageLabel = createLabel(text = messages.getString("GeneralInformationPanel.languageLabel"),
                tooltip = messages.getString("GeneralInformationPanel.languageTooltip"))
        val softwareLabel = createLabel(text = messages.getString("GeneralInformationPanel.softwareLabel"),
                tooltip = messages.getString("GeneralInformationPanel.softwareTooltip"))
        val languageWrittenInLabel = createLabel(
                text = messages.getString("GeneralInformationPanel.languageWrittenInLabel"),
                tooltip = messages.getString("GeneralInformationPanel.languageWrittenInTooltip"))
        val statusLabel = createLabel(text = messages.getString("GeneralInformationPanel.statusLabel"),
                tooltip = messages.getString("GeneralInformationPanel.statusTooltip"))
        val objectiveLabel = createLabel(text = messages.getString("GeneralInformationPanel.objectiveLabel"),
                tooltip = messages.getString("GeneralInformationPanel.objectiveTooltip"))
        val descriptionLabel = createLabel(text = messages.getString("GeneralInformationPanel.descriptionLabel"),
                tooltip = messages.getString("GeneralInformationPanel.descriptionTooltip"))

        // hide initially advanced comps
        val advancedComps = listOf<JComponent>(
                urlLabel, urlTextField,
                formatLabel, formatField,
                languageLabel, languageTextField,
                softwareLabel, softwareField,
                languageWrittenInLabel, languageWrittenInField,
                statusLabel, statusField,
                objectiveLabel, objectiveTextField,
                descriptionLabel, descriptionTextField)
        advancedComps.forEach { it.isVisible = false }

        val propertiesPanel = JPanel(GridBagLayout())

        propertiesPanel.add(comp = studyNameLabel, gridy = 1, gridx = 0)
        propertiesPanel.add(comp = studyNameTextField, gridy = 1, gridx = 1, gridwidth = 2)

        propertiesPanel.add(comp = identifierLabel, gridy = 2, gridx = 0)
        propertiesPanel.add(comp = identifierTextField, gridy = 2, gridx = 1, gridwidth = 2)

        propertiesPanel.add(comp = creatorPanel, gridy = 3, gridx = 0, gridwidth = 3)

        propertiesPanel.add(comp = creationDateLabel, gridy = 4, gridx = 0)
        propertiesPanel.add(comp = creationDateChooser, gridy = 4, gridx = 1)

        propertiesPanel.add(comp = rightsLabel, gridy = 5, gridx = 0)
        propertiesPanel.add(comp = rightsField, gridy = 5, gridx = 1, gridwidth = 2)

        availabilityCheckBox.text = messages.getString("GeneralInformationPanel.availabilityLabel")
        availabilityCheckBox.toolTipText = messages.getString("GeneralInformationPanel.availabilityTooltip")
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
        add(Box.createGlue())
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

class ReferencePanel(val refs: MutableList<Record>, var isAdvanced: Boolean) : JPanel(BorderLayout()) {

    init {
        border = BorderFactory.createTitledBorder("References")

        val dtm = NonEditableTableModel()
        refs.forEach { dtm.addRow(arrayOf(it)) }

        val renderer = object : DefaultTableCellRenderer() {
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
            if (dlg.getValue() == JOptionPane.OK_OPTION) {
                dtm.addRow(arrayOf(editPanel.toRecord()))
            }
        }

        buttonsPanel.modifyButton.addActionListener { _ ->
            val rowToEdit = myTable.selectedRow
            if (rowToEdit != -1) {
                val ref = dtm.getValueAt(rowToEdit, 0) as Record

                val editPanel = EditReferencePanel(ref, isAdvanced = isAdvanced)

                val dlg = ValidatableDialog(panel = editPanel, dialogTitle = "Modify reference")
                if (dlg.getValue() == JOptionPane.OK_OPTION) {
                    dtm.setValueAt(editPanel.toRecord(), rowToEdit, 0)
                }
            }
        }

        buttonsPanel.removeButton.addActionListener { _ ->
            val rowToDelete = myTable.selectedRow
            if (rowToDelete != -1) dtm.removeRow(rowToDelete)
        }

        add(myTable, BorderLayout.NORTH)
        add(buttonsPanel, BorderLayout.SOUTH)
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

class CreatorPanel(val creators: MutableList<VCard>) : JPanel(BorderLayout()) {

    init {
        border = BorderFactory.createTitledBorder("Creators")

        val dtm = NonEditableTableModel()
        creators.forEach { dtm.addRow(arrayOf(it)) }

        val renderer = object : DefaultTableCellRenderer() {
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
            if (result == JOptionPane.OK_OPTION) {
                dtm.addRow(arrayOf(editPanel.toVCard()))
            }
        }

        buttonsPanel.modifyButton.addActionListener { _ ->
            val rowToEdit = myTable.selectedRow
            if (rowToEdit != -1) {
                val creator = dtm.getValueAt(rowToEdit, 0) as VCard

                val editPanel = de.bund.bfr.rakip.editor.EditCreatorPanel(creator)
                val result = showConfirmDialog(panel = editPanel, title = "Modify creator")
                if (result == JOptionPane.OK_OPTION) {
                    dtm.setValueAt(editPanel.toVCard(), rowToEdit, 0)
                }
            }
        }

        buttonsPanel.removeButton.addActionListener { _ ->
            val rowToDelete = myTable.selectedRow
            if (rowToDelete != -1) dtm.removeRow(rowToDelete)
        }

        add(myTable, BorderLayout.NORTH)
        add(buttonsPanel, BorderLayout.SOUTH)
    }
}

class EditCreatorPanel(creator: VCard? = null) : JPanel(GridBagLayout()) {

    private val givenNameField = JTextField(30)
    private val familyNameField = JTextField(30)
    private val contactField = JTextField(30)

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
        val pairList = listOf<Pair<JLabel, JComponent>>(
                Pair(first = JLabel(messages.getString("EditCreatorPanel.givenNameLabel")), second = givenNameField),
                Pair(first = JLabel(messages.getString("EditCreatorPanel.familyNameLabel")), second = familyNameField),
                Pair(first = JLabel(messages.getString("EditCreatorPanel.contactLabel")), second = contactField)
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

class ScopePanel(val scope: Scope) : Box(BoxLayout.PAGE_AXIS) {

    val productButton = JButton()
    val hazardButton = JButton()
    val populationButton = JButton()
    val commentField = JTextArea(5, 30)
    val dateChooser = de.bund.bfr.rakip.editor.FixedJDateChooser()
    val regionField = AutoSuggestField(10)
    val countryField = AutoSuggestField(10)

    val advancedCheckBox = JCheckBox("Advanced")

    init {

        // init combo boxes
        regionField.setPossibleValues(vocabs["Region"])
        countryField.setPossibleValues(vocabs["Country"])

        regionField.selectedItem = scope.region.firstOrNull()
        countryField.selectedItem = scope.country.firstOrNull()

        initUI()
    }

    private fun initUI() {
        val propertiesPanel = JPanel(GridBagLayout())

        productButton.toolTipText = "Click me to add a product"
        productButton.addActionListener { _ ->
            val editPanel = EditProductPanel(product = scope.product,
                    isAdvanced = advancedCheckBox.isSelected)
            val dlg = ValidatableDialog(panel = editPanel, dialogTitle = "Create a product")

            if (dlg.getValue() == JOptionPane.OK_OPTION) {
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

            if (dlg.getValue() == JOptionPane.OK_OPTION) {
                val hazard = editPanel.toHazard()
                hazardButton.text = "${hazard.hazardName} [${hazard.hazardUnit}]"
                scope.hazard = hazard
            }
        }

        populationButton.toolTipText = "Click me to add a Population group"
        populationButton.addActionListener { _ ->
            val editPanel = EditPopulationGroupPanel(populationGroup = scope.populationGroup, isAdvanced = advancedCheckBox.isSelected)

            val dlg = ValidatableDialog(panel = editPanel, dialogTitle = "Create a Population Group")

            if (dlg.getValue() == JOptionPane.OK_OPTION) {
                val populationGroup = editPanel.toPopulationGroup()
                populationButton.text = populationGroup.populationName
                scope.populationGroup = populationGroup
            }
        }

        val productLabel = JLabel(messages.getString("ScopePanel.productLabel"))
        val hazardLabel = JLabel(messages.getString("ScopePanel.hazardLabel"))
        val populationLabel = JLabel(messages.getString("ScopePanel.populationGroupLabel"))
        val commentLabel = createLabel(text = messages.getString("ScopePanel.commentLabel"),
                tooltip = messages.getString("ScopePanel.commentTooltip"))
        val temporalInformationLabel = createLabel(text = messages.getString("ScopePanel.temporalInformationLabel"),
                tooltip = messages.getString("ScopePanel.temporalInformationTooltip"))
        val regionLabel = createLabel(text = messages.getString("ScopePanel.regionLabel"),
                tooltip = messages.getString("ScopePanel.regionTooltip"))
        val countryLabel = createLabel(text = messages.getString("ScopePanel.countryLabel"),
                tooltip = messages.getString("ScopePanel.countryTooltip"))

        val pairList = listOf<Pair<JLabel, JComponent>>(
                Pair(first = productLabel, second = productButton),
                Pair(first = hazardLabel, second = hazardButton),
                Pair(first = populationLabel, second = populationButton),
                Pair(first = commentLabel, second = commentField),
                Pair(first = temporalInformationLabel, second = dateChooser),
                Pair(first = temporalInformationLabel, second = dateChooser),
                Pair(first = regionLabel, second = regionField),
                Pair(first = countryLabel, second = countryField)
        )
        propertiesPanel.addGridComponents(pairs = pairList)

        // advancedCheckBox
        advancedCheckBox.addItemListener { _ -> println("dummy listener") }  // TODO: not implemented yet

        add(createAdvancedPanel(checkbox = advancedCheckBox))
        add(Box.createGlue())
        add(propertiesPanel)
    }
}

class DataBackgroundPanel(var dataBackground: DataBackground? = null) : Box(BoxLayout.PAGE_AXIS) {

    val advancedCheckBox = JCheckBox("Advanced")

    val laboratoryAccreditationField = AutoSuggestField(10)

    init {
        initUI()
    }

    private fun initUI() {

        val studyPanel = de.bund.bfr.rakip.editor.StudyPanel()
        studyPanel.border = BorderFactory.createTitledBorder("Study")

        val studySampleButton = JButton()
        studySampleButton.toolTipText = "Click me to add Study Sample"
        studySampleButton.addActionListener { _ ->
            val editPanel = EditStudySamplePanel(studySample = dataBackground?.studySample, isAdvanced = advancedCheckBox.isSelected)

            val dlg = ValidatableDialog(panel = editPanel, dialogTitle = "Create Study sample")

            if (dlg.getValue() == JOptionPane.OK_OPTION) {
                val studySample = editPanel.toStudySample()

                if (dataBackground == null) dataBackground = DataBackground()
                dataBackground?.studySample = studySample
            }
        }

        val dietaryAssessmentMethodButton = JButton()
        dietaryAssessmentMethodButton.toolTipText = "Click me to add Dietary assessment method"
        dietaryAssessmentMethodButton.addActionListener { _ ->
            val editPanel = EditDietaryAssessmentMethodPanel(
                    dietaryAssessmentMethod = dataBackground?.dietaryAssessmentMethod, isAdvanced = advancedCheckBox.isSelected)

            val dlg = ValidatableDialog(panel = editPanel, dialogTitle = "Create dietary assessment method")
            if (dlg.getValue() == JOptionPane.OK_OPTION) {
                val dietaryAssessmentMethod = editPanel.toDietaryAssessmentMethod()

                if (dataBackground == null) dataBackground = DataBackground(dietaryAssessmentMethod = dietaryAssessmentMethod)
                else dataBackground?.dietaryAssessmentMethod = dietaryAssessmentMethod
            }
        }

        laboratoryAccreditationField.setPossibleValues(vocabs["Laboratory accreditation"])

        val assayButton = JButton()
        assayButton.toolTipText = "Click me to add Assay"
        assayButton.addActionListener { _ ->
            val editPanel = EditAssayPanel(assay = dataBackground?.assay, isAdvanced = advancedCheckBox.isSelected)

            val dlg = ValidatableDialog(panel = editPanel, dialogTitle = "Create assay")
            if (dlg.getValue() == JOptionPane.OK_OPTION) {
                val assay = editPanel.toAssay()

                if (dataBackground == null) dataBackground = DataBackground(assay = assay)
                else dataBackground?.assay = assay
            }
        }

        val studySampleLabel = JLabel(messages.getString("DataBackgroundPanel.studySampleLabel"))
        val dietaryAssessmentMethodLabel = JLabel(messages.getString("DataBackgroundPanel.dietaryAssessmentMethodLabel"))
        val laboratoryAccreditationLabel = JLabel(messages.getString("DataBackgroundPanel.laboratoryAccreditationLabel"))
        val assayLabel = JLabel(messages.getString("DataBackgroundPanel.assayLabel"))

        val propertiesPanel = JPanel(GridBagLayout())

        propertiesPanel.add(comp = studyPanel, gridy = 0, gridx = 0, gridwidth = 3)
        propertiesPanel.add(comp = studySampleLabel, gridy = 1, gridx = 0)
        propertiesPanel.add(comp = studySampleButton, gridy = 1, gridx = 1)

        propertiesPanel.add(comp = dietaryAssessmentMethodLabel, gridy = 2, gridx = 0)
        propertiesPanel.add(comp = dietaryAssessmentMethodButton, gridy = 2, gridx = 1)

        propertiesPanel.add(comp = laboratoryAccreditationLabel, gridy = 3, gridx = 0)
        propertiesPanel.add(comp = laboratoryAccreditationField, gridy = 3, gridx = 1)

        propertiesPanel.add(comp = assayLabel, gridy = 4, gridx = 0)
        propertiesPanel.add(comp = assayButton, gridy = 4, gridx = 1)

        // `Advanced` checkbox
        advancedCheckBox.addItemListener { _ ->
            studyPanel.advancedComps.forEach { it.isVisible = advancedCheckBox.isSelected }
            println("dummy listener")  // TODO: implement listener
        }

        add(createAdvancedPanel(checkbox = advancedCheckBox))
        add(Box.createGlue())
        add(propertiesPanel)
    }
}

class StudyPanel(study: Study? = null) : JPanel(GridBagLayout()) {

    val studyIdentifierLabel = createLabel(text = messages.getString("StudyPanel.studyIdentifierLabel"),
            tooltip = messages.getString("StudyPanel.studyIdentifierTooltip"))
    val studyIdentifierTextField = JTextField(30)

    val studyTitleLabel = createLabel(text = messages.getString("StudyPanel.studyTitleLabel"),
            tooltip = messages.getString("StudyPanel.studyTitleTooltip"))
    val studyTitleTextField = JTextField(30)

    val studyDescriptionLabel = createLabel(text = messages.getString("StudyPanel.studyDescriptionLabel"),
            tooltip = messages.getString("StudyPanel.studyDescriptionTooltip"))
    val studyDescriptionTextArea = JTextArea(5, 30)

    val studyDesignTypeLabel = createLabel(text = messages.getString("StudyPanel.studyDesignTypeLabel"),
            tooltip = messages.getString("StudyPanel.studyDesignTypeTooltip"))
    val studyDesignTypeField = AutoSuggestField(10)

    val studyAssayMeasurementsTypeLabel = createLabel(text = messages.getString("StudyPanel.studyAssayMeasurementsTypeLabel"),
            tooltip = messages.getString("StudyPanel.studyAssayMeasurementsTypeTooltip"))
    val studyAssayMeasurementsTypeField = AutoSuggestField(10)

    val studyAssayTechnologyTypeLabel = createLabel(text = messages.getString("StudyPanel.studyAssayTechnologyTypeLabel"),
            tooltip = messages.getString("StudyPanel.studyAssayTechnologyTypeTooltip"))
    val studyAssayTechnologyTypeField = AutoSuggestField(10)

    val studyAssayTechnologyPlatformLabel = createLabel(text = messages.getString("StudyPanel.studyAssayTechnologyPlatformLabel"),
            tooltip = messages.getString("StudyPanel.studyAssayTechnologyPlatformTooltip"))
    val studyAssayTechnologyPlatformTextField = JTextField(30)

    val accreditationProcedureLabel = createLabel(text = messages.getString("StudyPanel.accreditationProcedureLabel"),
            tooltip = messages.getString("StudyPanel.accreditationProcedureTooltip"))
    val accreditationProcedureField = AutoSuggestField(10)

    val studyProtocolNameLabel = createLabel(text = messages.getString("StudyPanel.protocolNameLabel"),
            tooltip = messages.getString("StudyPanel.protocolNameTooltip"))
    val studyProtocolNameTextField = JTextField(30)

    val studyProtocolTypeLabel = createLabel(text = messages.getString("StudyPanel.protocolTypeLabel"),
            tooltip = messages.getString("StudyPanel.protocolTypeTooltip"))
    val studyProtocolTypeField = AutoSuggestField(10)

    val studyProtocolDescriptionLabel = createLabel(text = messages.getString("StudyPanel.protocolDescriptionLabel"),
            tooltip = messages.getString("StudyPanel.protocolDescriptionTooltip"))
    val studyProtocolDescriptionTextField = JTextField(30)

    val studyProtocolURILabel = createLabel(text = messages.getString("StudyPanel.protocolURILabel"),
            tooltip = messages.getString("StudyPanel.protocolURITooltip"))
    val studyProtocolURITextField = JTextField(30)

    val studyProtocolVersionLabel = createLabel(text = messages.getString("StudyPanel.protocolVersionLabel"),
            tooltip = messages.getString("StudyPanel.protocolVersionTooltip"))
    val studyProtocolVersionTextField = JTextField(30)

    val studyProtocolParametersLabel = createLabel(text = messages.getString("StudyPanel.parametersLabel"),
            tooltip = messages.getString("StudyPanel.parametersTooltip"))
    val studyProtocolParametersField = AutoSuggestField(10)

    val studyProtocolComponentsTypeLabel = createLabel(text = messages.getString("StudyPanel.componentsTypeLabel"),
            tooltip = messages.getString("StudyPanel.componentsTypeTooltip"))
    val studyProtocolComponentsTypeField = AutoSuggestField(10)

    val advancedComps = listOf<JComponent>(
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
        studyDesignTypeField.setPossibleValues(vocabs["Study Design Type"])
        studyAssayMeasurementsTypeField.setPossibleValues(vocabs["Study Assay Measurement Type"])
        studyAssayTechnologyTypeField.setPossibleValues(vocabs["Study Assay Technology Type"])
        accreditationProcedureField.setPossibleValues(vocabs["Accreditation procedure Ass.Tec"])
        studyProtocolTypeField.setPossibleValues(vocabs["Study Protocol Type"])
        studyProtocolParametersField.setPossibleValues(vocabs["Study Protocol Parameters Name"])
        studyProtocolComponentsTypeField.setPossibleValues(vocabs["Study Protocol Components Type"])

        val pairList = listOf<Pair<JLabel, JComponent>>(
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

class ModelMathPanel(modelMath: ModelMath? = null) : Box(BoxLayout.PAGE_AXIS) {

    companion object {

        val parameters = "Parameters"
        val qualityMeasures = "Quality measures"
        val modelEquation = "Model equation"
        val fittingProcedure = "Fitting procedure"
    }

    val advancedCheckBox = JCheckBox("Advanced")

    init {

        val parametersPanel = de.bund.bfr.rakip.editor.ParameterPanel(isAdvanced = advancedCheckBox.isSelected)

        val qualityMeasuresPanel = de.bund.bfr.rakip.editor.QualityMeasuresPanel()

        val modelEquationPanel = de.bund.bfr.rakip.editor.ModelEquationsPanel(isAdvanced = advancedCheckBox.isSelected)

//        val fittingProcedure = JPanel()
//        fittingProcedure.border = BorderFactory.createTitledBorder(fittingProcedure)

        val propertiesPanel = JPanel(GridBagLayout())
        propertiesPanel.add(comp = parametersPanel, gridy = 0, gridx = 0)
        propertiesPanel.add(comp = qualityMeasuresPanel, gridy = 1, gridx = 0)
        propertiesPanel.add(comp = modelEquationPanel, gridy = 2, gridx = 0)

        add(createAdvancedPanel(checkbox = advancedCheckBox))
        add(Box.createGlue())
        add(propertiesPanel)

        advancedCheckBox.addItemListener {
            parametersPanel.isAdvanced = advancedCheckBox.isSelected
            modelEquationPanel.isAdvanced = advancedCheckBox.isSelected
        }
    }
}

class ParameterPanel(val parameters: MutableList<Parameter> = mutableListOf(), var isAdvanced: Boolean)
    : JPanel(BorderLayout()) {

    init {
        border = BorderFactory.createTitledBorder(de.bund.bfr.rakip.editor.ModelMathPanel.Companion.parameters)

        val dtm = NonEditableTableModel()
        parameters.forEach { dtm.addRow(arrayOf(it)) }

        val renderer = object : DefaultTableCellRenderer() {
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
            if (dlg.getValue() == JOptionPane.OK_OPTION) {
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

        add(myTable, BorderLayout.NORTH)
        add(buttonsPanel, BorderLayout.SOUTH)
    }
}

class QualityMeasuresPanel(sse: Double? = null, mse: Double? = null, rmse: Double? = null,
                           r2: Double? = null, aic: Double? = null, bic: Double? = null) : JPanel(GridBagLayout()) {

    val sseSpinnerModel = createSpinnerDoubleModel()
    val mseSpinnerModel = createSpinnerDoubleModel()
    val rmseSpinnerModel = createSpinnerDoubleModel()
    val r2SpinnerModel = createSpinnerDoubleModel()
    val aicSpinnerModel = createSpinnerDoubleModel()
    val bicSpinnerModel = createSpinnerDoubleModel()

    init {
        val pairList = listOf<Pair<JLabel, JComponent>>(
                Pair(first = JLabel("SSE"), second = createSpinner(sseSpinnerModel)),
                Pair(first = JLabel("MSE"), second = createSpinner(mseSpinnerModel)),
                Pair(first = JLabel("RMSE"), second = createSpinner(rmseSpinnerModel)),
                Pair(first = JLabel("r-Squared"), second = createSpinner(r2SpinnerModel)),
                Pair(first = JLabel("AIC"), second = createSpinner(aicSpinnerModel)),
                Pair(first = JLabel("BIC"), second = createSpinner(bicSpinnerModel))
        )

        addGridComponents(pairs = pairList)

        border = BorderFactory.createTitledBorder(de.bund.bfr.rakip.editor.ModelMathPanel.Companion.qualityMeasures)
    }

    // TODO: toQualityMeasures
}

class ModelEquationsPanel(
        val equations: MutableList<ModelEquation> = mutableListOf(),
        var isAdvanced: Boolean
) : JPanel(BorderLayout()) {

    init {
        border = BorderFactory.createTitledBorder(de.bund.bfr.rakip.editor.ModelMathPanel.Companion.modelEquation)

        val dtm = NonEditableTableModel()
        equations.forEach { dtm.addRow(arrayOf(it)) }

        val renderer = object : DefaultTableCellRenderer() {
            override fun setValue(value: Any?) {
                text = (value as ModelEquation?)?.equationName
            }
        }
        val myTable = HeadlessTable(model = dtm, renderer = renderer)

        val buttonsPanel = ButtonsPanel()
        buttonsPanel.addButton.addActionListener { _ ->
            val editPanel = EditModelEquationPanel(isAdvanced = isAdvanced)

            val dlg = ValidatableDialog(panel = editPanel, dialogTitle = "Create equation")
            if (dlg.getValue() == JOptionPane.OK_OPTION) {
                // TODO: process result
            }
        }

        buttonsPanel.modifyButton.addActionListener { _ ->
            val rowToEdit = myTable.selectedRow
            if (rowToEdit != -1) {
                val equation = dtm.getValueAt(rowToEdit, 0) as ModelEquation

                val editPanel = EditModelEquationPanel(equation = equation, isAdvanced = isAdvanced)

                val dlg = ValidatableDialog(panel = editPanel, dialogTitle = "Modify equation")

                if (dlg.getValue() == JOptionPane.OK_OPTION) {
                    // TODO: process result
                }
            }
        }

        buttonsPanel.removeButton.addActionListener { _ ->
            val rowToDelete = myTable.selectedRow
            if (rowToDelete != -1) dtm.removeRow(rowToDelete)
        }

        add(myTable, BorderLayout.NORTH)
        add(buttonsPanel, BorderLayout.SOUTH)
    }
}