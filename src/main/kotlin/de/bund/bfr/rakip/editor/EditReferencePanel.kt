package de.bund.bfr.rakip.editor

import com.gmail.gcolaianni5.jris.bean.Record
import com.gmail.gcolaianni5.jris.bean.Type


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
        if (!doiTextField.hasValidValue()) errorList.add("Missing DOI")
        if (!titleTextField.hasValidValue()) errorList.add("Missing title")

        return errorList
    }
}