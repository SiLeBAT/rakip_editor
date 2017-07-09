import com.gmail.gcolaianni5.jris.bean.Record
import de.bund.bfr.rakip.generic.*
import ezvcard.VCard
import java.awt.Dimension
import java.net.URI
import java.net.URL
import java.util.*
import javax.swing.JFrame
import javax.swing.JScrollPane
import javax.swing.JTree
import javax.swing.tree.DefaultMutableTreeNode

private var messages = ResourceBundle.getBundle("MessagesBundle", Locale.getDefault())!!

val REFERENCE_TYPE: String = messages.getString("GM.EditReferencePanel.typeLabel") 
val REFERENCE_DATE: String = messages.getString("GM.EditReferencePanel.dateLabel") 
val REFERENCE_DOI: String = messages.getString("GM.EditReferencePanel.doiLabel") 
val REFERENCE_AUTHORLIST: String = messages.getString("GM.EditReferencePanel.authorListLabel") 
val REFERENCE_TITLE: String = messages.getString("GM.EditReferencePanel.titleLabel") 
val REFERENCE_ABSTRACT: String = messages.getString("GM.EditReferencePanel.abstractLabel") 
val REFERENCE_JOURNAL: String = messages.getString("GM.EditReferencePanel.journalLabel") 
val REFERENCE_VOLUME: String = messages.getString("GM.EditReferencePanel.volumeLabel") 
val REFERENCE_ISSUE: String = messages.getString("GM.EditReferencePanel.issueLabel") 
val REFERENCE_WEBSITE: String = messages.getString("GM.EditReferencePanel.websiteLabel") 

val CREATOR_GIVENNAME: String = messages.getString("GM.EditCreatorPanel.givenNameLabel") 
val CREATOR_FAMILYNAME: String = messages.getString("GM.EditCreatorPanel.familyNameLabel") 
val CREATOR_CONTACT: String = messages.getString("GM.EditCreatorPanel.contactLabel") 

val PRODUCT_NAME: String = messages.getString("GM.EditProductPanel.envNameLabel") 
val PRODUCT_DESC: String = messages.getString("GM.EditProductPanel.envDescriptionLabel") 
val PRODUCT_UNIT: String = messages.getString("GM.EditProductPanel.envUnitLabel") 
val PRODUCT_PRODUCTIONMETHOD: String = messages.getString("GM.EditProductPanel.productionMethodLabel") 
val PRODUCT_PACKAGING: String = messages.getString("GM.EditProductPanel.packagingLabel") 
val PRODUCT_TREATMENT: String = messages.getString("GM.EditProductPanel.productTreatmentLabel") 
val PRODUCT_ORIGINCOUNTRY: String = messages.getString("GM.EditProductPanel.originCountryLabel") 
val PRODUCT_ORIGINAREA: String = messages.getString("GM.EditProductPanel.originAreaLabel") 
val PRODUCT_FISHERIES: String = messages.getString("GM.EditProductPanel.fisheriesAreaLabel") 
val PRODUCT_PRODUCTIONDATE: String = messages.getString("GM.EditProductPanel.productionDateLabel") 
val PRODUCT_EXPIRATIONDATE: String = messages.getString("GM.EditProductPanel.expirationDateLabel") 

val HAZARD_TYPE: String = messages.getString("GM.EditHazardPanel.hazardTypeLabel") 
val HAZARD_NAME: String = messages.getString("GM.EditHazardPanel.hazardNameLabel") 
val HAZARD_DESCRIPTION: String = messages.getString("GM.EditHazardPanel.hazardDescriptionLabel") 
val HAZARD_UNIT: String = messages.getString("GM.EditHazardPanel.hazardUnitLabel") 
val HAZARD_ADVERSE: String = messages.getString("GM.EditHazardPanel.adverseEffectLabel") 
val HAZARD_ORIGIN: String = messages.getString("GM.EditHazardPanel.originLabel") 
val HAZARD_BMD: String = messages.getString("GM.EditHazardPanel.bmdLabel") 
val HAZARD_RESIDUE: String = messages.getString("GM.EditHazardPanel.maxResidueLimitLabel") 
val HAZARD_NOADVERSE: String = messages.getString("GM.EditHazardPanel.noObservedAdverseLabel") 
val HAZARD_LOWESTADVERSE: String = messages.getString("GM.EditHazardPanel.lowestObserveLabel") 
val HAZARD_ACCEPTABLEOPERATOR: String = messages.getString("GM.EditHazardPanel.acceptableOperatorLabel") 
val HAZARD_ACUTEDOSE: String = messages.getString("GM.EditHazardPanel.acuteReferenceDoseLabel") 
val HAZARD_DAILYINTAKE: String = messages.getString("GM.EditHazardPanel.acceptableDailyIntakeLabel") 
val HAZARD_INDSUM: String = messages.getString("GM.EditHazardPanel.indSumLabel") 
val HAZARD_LABNAME: String = messages.getString("GM.EditHazardPanel.labNameLabel") 
val HAZARD_LABCOUNTRY: String = messages.getString("GM.EditHazardPanel.labCountryLabel") 
val HAZARD_DETECTIONLIM: String = messages.getString("GM.EditHazardPanel.detectionLimitLabel") 
val HAZARD_QUANTIFICATIONLIM: String = messages.getString("GM.EditHazardPanel.quantificationLimitLabel") 
val HAZARD_CENSOREDDATA: String = messages.getString("GM.EditHazardPanel.leftCensoredDataLabel") 
val HAZARD_CONTAMINATION: String = messages.getString("GM.EditHazardPanel.contaminationRangeLabel") 

val PG_NAME: String = messages.getString("GM.EditPopulationGroupPanel.populationNameLabel")
val PG_TARGET: String = messages.getString("GM.EditPopulationGroupPanel.targetPopulationLabel")
val PG_SPAN: String = messages.getString("GM.EditPopulationGroupPanel.populationSpanLabel")
val PG_DESC: String = messages.getString("GM.EditPopulationGroupPanel.populationDescriptionLabel")
val PG_AGE: String = messages.getString("GM.EditPopulationGroupPanel.populationAgeLabel")
val PG_GENDER: String = messages.getString("GM.EditPopulationGroupPanel.populationGenderLabel")
val PG_BMI: String = messages.getString("GM.EditPopulationGroupPanel.bmiLabel")
val PG_DIETGROUPS: String = messages.getString("GM.EditPopulationGroupPanel.specialDietGroupsLabel")
val PG_PATTERNCONSUMPTION: String = messages.getString(("GM.EditPopulationGroupPanel.patternConsumptionLabel"))
val PG_REGION: String = messages.getString("GM.EditPopulationGroupPanel.regionLabel")
val PG_COUNTRY: String = messages.getString("GM.EditPopulationGroupPanel.countryLabel")
val PG_RISK: String = messages.getString("GM.EditPopulationGroupPanel.riskAndPopulationLabel")
val PG_SEASON: String = messages.getString("GM.EditPopulationGroupPanel.seasonLabel")

val DB_STUDYSAMPLE: String = messages.getString("GM.DataBackgroundPanel.studySampleLabel")
val DB_DIETARYASSESSMENTMETHOD: String = messages.getString("GM.DataBackgroundPanel.dietaryAssessmentMethodLabel")
val DB_ACCREDITATION: String = messages.getString("GM.DataBackgroundPanel.laboratoryAccreditationLabel")
val DB_ASSAY: String = messages.getString("GM.DataBackgroundPanel.assayLabel")

val STUDY_TITLE: String = messages.getString("GM.StudyPanel.studyTitleLabel")
val STUDY_DESC: String = messages.getString("GM.StudyPanel.studyDescriptionLabel")
val STUDY_DESIGN: String = messages.getString("GM.StudyPanel.studyDesignTypeLabel")
val STUDY_MEASUREMENT: String = messages.getString("GM.StudyPanel.studyAssayMeasurementsTypeLabel")
val STUDY_TECH_TYPE: String = messages.getString("GM.StudyPanel.studyAssayTechnologyTypeLabel")
val STUDY_TECH_PLAT: String = messages.getString("GM.StudyPanel.studyAssayTechnologyPlatformLabel")
val STUDY_ACCREDITATION: String = messages.getString("GM.StudyPanel.accreditationProcedureLabel")
val STUDY_PROTOCOL_NAME: String = messages.getString("GM.StudyPanel.protocolNameLabel")
val STUDY_PROTOCOL_TYPE: String = messages.getString("GM.StudyPanel.protocolTypeLabel")
val STUDY_PROTOCOL_DESC: String = messages.getString("GM.StudyPanel.protocolDescriptionLabel")
val STUDY_PROTOCOL_URI: String = messages.getString("GM.StudyPanel.protocolURILabel")
val STUDY_PROTOCOL_VERSION: String = messages.getString("GM.StudyPanel.protocolVersionLabel")
val STUDY_PARAMETERS: String = messages.getString("GM.StudyPanel.parametersLabel")
val STUDY_COMPONENTS_TYPE: String = messages.getString("GM.StudyPanel.componentsTypeLabel")

val SS_SAMPLE: String = messages.getString("GM.EditStudySamplePanel.sampleNameLabel")
val SS_MOISTURE_PERC: String = messages.getString("GM.EditStudySamplePanel.moisturePercentageLabel")
val SS_FAT_PERC: String = messages.getString("GM.EditStudySamplePanel.fatPercentageLabel")
val SS_SAMPLE_PROTOCOL: String = messages.getString("GM.EditStudySamplePanel.sampleProtocolLabel")
val SS_SAMPLING_STRATEGY: String = messages.getString("GM.EditStudySamplePanel.samplingStrategyLabel")
val SS_SAMPLING_TYPE: String = messages.getString("GM.EditStudySamplePanel.samplingTypeLabel")
val SS_SAMPLING_METHOD: String = messages.getString("GM.EditStudySamplePanel.samplingMethodLabel")
val SS_SAMPLING_PLAN: String = messages.getString("GM.EditStudySamplePanel.samplingPlanLabel")
val SS_SAMPLING_WEIGHT: String = messages.getString("GM.EditStudySamplePanel.samplingWeightLabel")
val SS_SAMPLING_SIZE: String = messages.getString("GM.EditStudySamplePanel.samplingSizeLabel")
val SS_LOT_UNIT: String = messages.getString("GM.EditStudySamplePanel.lotSizeUnitLabel")
val SS_SAMPLING_POINT: String = messages.getString("GM.EditStudySamplePanel.samplingPointLabel")

val DAM_TOOL: String = messages.getString("GM.EditDietaryAssessmentMethodPanel.dataCollectionToolLabel")
val DAM_NUMBER_NON_CONSECUTIVE: String = messages.getString("GM.EditDietaryAssessmentMethodPanel.nonConsecutiveOneDaysLabel")
val DAM_SOFTWARE: String = messages.getString("GM.EditDietaryAssessmentMethodPanel.dietarySoftwareToolLabel")
val DAM_FOOD_ITEM: String = messages.getString("GM.EditDietaryAssessmentMethodPanel.foodItemNumberLabel")
val DAM_NON_CONSECUTIVE: String = messages.getString("GM.EditDietaryAssessmentMethodPanel.nonConsecutiveOneDaysLabel")
val DAM_RECORD_TYPE: String = messages.getString("GM.EditDietaryAssessmentMethodPanel.recordTypeLabel")

val ASSAY_NAME: String = messages.getString("GM.EditAssayPanel.nameLabel")
val ASSAY_DESC: String = messages.getString("GM.EditAssayPanel.descriptionLabel")

val GI_STUDY_NAME: String = messages.getString("GM.GeneralInformationPanel.studyNameLabel")
val GI_ID: String = messages.getString("GM.GeneralInformationPanel.identifierLabel")
val GI_CREATION_DATE: String = messages.getString("GM.GeneralInformationPanel.creationDateLabel")
val GI_RIGHTS: String = messages.getString("GM.GeneralInformationPanel.rightsLabel")
val GI_URL: String = messages.getString("GM.GeneralInformationPanel.urlLabel")
val GI_FORMAT: String = messages.getString("GM.GeneralInformationPanel.formatLabel")
val GI_LANGUAGE: String = messages.getString("GM.GeneralInformationPanel.languageLabel")
val GI_SOFTWARE: String = messages.getString("GM.GeneralInformationPanel.softwareLabel")
val GI_LANGUAGE_WRITTEN_IN: String = messages.getString("GM.GeneralInformationPanel.languageWrittenInLabel")
val GI_STATUS: String = messages.getString("GM.GeneralInformationPanel.statusLabel")
val GI_OBJECTIVE: String = messages.getString("GM.GeneralInformationPanel.objectiveLabel")
val GI_DESC: String = messages.getString("GM.GeneralInformationPanel.descriptionLabel")

val SCOPE_PRODUCT: String = messages.getString("GM.ScopePanel.productLabel")
val SCOPE_HAZARD: String = messages.getString("GM.ScopePanel.hazardLabel")
val SCOPE_COMMENT: String = messages.getString("GM.ScopePanel.commentLabel")
val SCOPE_TEMPORAL: String = messages.getString("GM.ScopePanel.temporalInformationLabel")
val SCOPE_REGION: String = messages.getString("GM.ScopePanel.regionLabel")
val SCOPE_COUNTRY: String = messages.getString("GM.ScopePanel.countryLabel")

val PARAMETER_ID: String = messages.getString("GM.EditParameterPanel.idLabel")
val PARAMETER_CLASIF: String = messages.getString("GM.EditParameterPanel.classificationLabel")
val PARAMETER_NAME: String = messages.getString("GM.EditParameterPanel.parameterNameLabel")
val PARAMETER_DESC: String = messages.getString("GM.EditParameterPanel.descriptionLabel")
val PARAMETER_TYPE: String = messages.getString("GM.EditParameterPanel.typeLabel")
val PARAMETER_UNIT: String = messages.getString("GM.EditParameterPanel.unitLabel")
val PARAMETER_UNIT_CATEGORY: String = messages.getString("GM.EditParameterPanel.unitCategoryLabel")
val PARAMETER_DATA_TYPE: String = messages.getString("GM.EditParameterPanel.dataTypeLabel")
val PARAMETER_SOURCE: String = messages.getString("GM.EditParameterPanel.sourceLabel")
val PARAMETER_SUBJECT: String = messages.getString("GM.EditParameterPanel.subjectLabel")
val PARAMETER_DIST: String = messages.getString("GM.EditParameterPanel.distributionLabel")
val PARAMETER_VALUE: String = messages.getString("GM.EditParameterPanel.valueLabel")
val PARAMETER_REFERENCE: String = messages.getString("GM.EditParameterPanel.referenceLabel")
val PARAMETER_VARIABILITY: String = messages.getString("GM.EditParameterPanel.variabilitySubjectLabel")
val PARAMETER_APPLICABILITY: String = messages.getString("GM.EditParameterPanel.applicabilityLabel")
val PARAMETER_ERROR: String = messages.getString("GM.EditParameterPanel.errorLabel")

val MODEL_EQUATION_NAME: String = messages.getString("GM.EditModelEquationPanel.nameLabel")
val MODEL_EQUATION_CLASS: String = messages.getString("GM.EditModelEquationPanel.classLabel")
val MODEL_EQUATION_SCRIPT: String = messages.getString("GM.EditModelEquationPanel.scriptLabel")

fun DefaultMutableTreeNode.add(label: String, value: String) {
    if (value.isNotBlank()) add(DefaultMutableTreeNode("$label: $value"))
}

fun DefaultMutableTreeNode.add(record: Record) {

    // isReferenceDescription is not supported

    record.type?.let { add(label = REFERENCE_TYPE, value = it.toString()) }
    record.date?.let {add(label = REFERENCE_DATE, value = it) }

    // PubMedId is not supported

    record.doi?.let {add(label = REFERENCE_DOI, value = it) }

    if (record.authors != null && record.authors.isNotEmpty()) {
        val listNode = DefaultMutableTreeNode(REFERENCE_AUTHORLIST)
        record.authors.forEach { listNode.add(label = "Author", value = it) }
    }

    record.title?.let { add(label = REFERENCE_TITLE, value = it) }
    record.abstr?.let { add(label = REFERENCE_ABSTRACT, value = it) }
    record.secondaryTitle?.let { add(label = REFERENCE_JOURNAL, value = it) }
    record.volumeNumber?.let { add(label = REFERENCE_VOLUME, value = it) }
    record.issueNumber?.let { add(label = REFERENCE_ISSUE, value = it.toString()) }

    // page not supported

    // status not supported

    record.websiteLink?.let { add(label = REFERENCE_WEBSITE, value = it) }

    // comment not supported
}

fun DefaultMutableTreeNode.add(vCard: VCard) {

    vCard.nickname?.values?.firstOrNull()?.let { add(label = CREATOR_GIVENNAME, value = it) }
    vCard.formattedName?.value?.let { add(label = CREATOR_FAMILYNAME, value = it) }
    vCard.emails?.firstOrNull()?.let { add(label = CREATOR_CONTACT, value = it.value) }
}

fun DefaultMutableTreeNode.add(product: Product) {

    add(label = PRODUCT_NAME, value = product.environmentName)
    product.environmentDescription?.let { add(label = PRODUCT_DESC, value = it) }
    add(label = PRODUCT_UNIT, value = product.environmentUnit)

    if (product.productionMethod.isNotEmpty()) {
        // Parent node that holds all the creators
        val parentNode = DefaultMutableTreeNode(PRODUCT_PRODUCTIONMETHOD)
        product.productionMethod.forEach { parentNode.add(DefaultMutableTreeNode(it)) }
    }

    if (product.packaging.isNotEmpty()) {
        // Parent node that holds all the packagings
        val parentNode = DefaultMutableTreeNode(PRODUCT_PACKAGING)
        product.packaging.forEach { parentNode.add(DefaultMutableTreeNode(it)) }
    }

    if (product.productTreatment.isNotEmpty()) {
        // Parent node that holds all the product treatments
        val parentNode = DefaultMutableTreeNode(PRODUCT_TREATMENT)
        product.productTreatment.forEach { parentNode.add(DefaultMutableTreeNode(it)) }
    }

    product.originCountry?.let { add(label = PRODUCT_ORIGINCOUNTRY, value = it) }
    product.areaOfOrigin?.let { add(label = PRODUCT_ORIGINAREA, value = it) }
    product.fisheriesArea?.let { add(label = PRODUCT_FISHERIES, value = it) }
    product.productionDate?.let { add(label = PRODUCT_PRODUCTIONDATE, value = it.toString()) }
    product.expirationDate?.let { add(label = PRODUCT_EXPIRATIONDATE, value = it.toString()) }
}

fun DefaultMutableTreeNode.add(hazard: Hazard) {
    
    add(label = HAZARD_TYPE, value = hazard.hazardType)
    add(label = HAZARD_NAME, value = hazard.hazardName)
    hazard.hazardDescription?.let { add(label = HAZARD_DESCRIPTION, value = it) }
    add(label = HAZARD_UNIT, value = hazard.hazardUnit)
    hazard.adverseEffect?.let { add(label = HAZARD_ADVERSE, value = it) }
    hazard.origin?.let { add(label = HAZARD_ORIGIN, value = it) }
    hazard.benchmarkDose?.let { add(label = HAZARD_BMD, value = it) }
    hazard.maximumResidueLimit?.let { add(label = HAZARD_RESIDUE, value = it) }
    hazard.noObservedAdverse?.let { add(label = HAZARD_NOADVERSE, value = it) }
    hazard.lowestObservedAdverse?.let { add(label = HAZARD_LOWESTADVERSE, value = it) }
    hazard.acceptableOperator?.let { add(label = HAZARD_ACCEPTABLEOPERATOR, value = it) }
    hazard.acuteReferenceDose?.let { add(label = HAZARD_ACUTEDOSE, value = it) }
    hazard.acceptableDailyIntake?.let { add(label = HAZARD_DAILYINTAKE, value = it) }
    hazard.hazardIndSum?.let { add(label = HAZARD_INDSUM, value = it) }
    hazard.laboratoryName?.let { add(label = HAZARD_LABNAME, value = it) }
    hazard.laboratoryCountry?.let { add(label = HAZARD_LABCOUNTRY, value = it) }
    hazard.detectionLimit?.let { add(label = HAZARD_DETECTIONLIM, value = it) }
    hazard.quantificationLimit?.let { add(label = HAZARD_QUANTIFICATIONLIM, value = it) }
    hazard.leftCensoredData?.let { add(label = HAZARD_CENSOREDDATA, value = it) }
    hazard.rangeOfContamination?.let { add(label = HAZARD_CONTAMINATION, value = it) }
}

fun DefaultMutableTreeNode.add(populationGroup: PopulationGroup) {

    add(label = PG_NAME, value = populationGroup.populationName)
    populationGroup.targetPopulation?.let { add(label = PG_TARGET, value = it) }

    if (populationGroup.populationSpan.isNotEmpty()) {
        val parentNode = DefaultMutableTreeNode(PG_SPAN)
        populationGroup.populationSpan.forEach { parentNode.add(DefaultMutableTreeNode(it)) }
        add(parentNode)
    }

    if (populationGroup.populationDescription.isNotEmpty()) {
        val parentNode = DefaultMutableTreeNode(PG_DESC)
        populationGroup.populationDescription.forEach { parentNode.add(DefaultMutableTreeNode(it)) }
        add(parentNode)
    }

    if (populationGroup.populationAge.isNotEmpty()) {
        val parentNode = DefaultMutableTreeNode(PG_AGE)
        populationGroup.populationAge.forEach { parentNode.add(DefaultMutableTreeNode(it)) }
        add(parentNode)
    }

    populationGroup.populationGender?.let { add(label = PG_GENDER, value = it) }

    if (populationGroup.bmi.isNotEmpty()) {
        val parentNode = DefaultMutableTreeNode(PG_BMI)
        populationGroup.bmi.forEach { parentNode.add(DefaultMutableTreeNode(it)) }
        add(parentNode)
    }

    if (populationGroup.specialDietGroups.isNotEmpty()) {
        val parentNode = DefaultMutableTreeNode(PG_DIETGROUPS)
        populationGroup.specialDietGroups.forEach { parentNode.add(DefaultMutableTreeNode(it)) }
        add(parentNode)
    }

    if (populationGroup.patternConsumption.isNotEmpty()) {
        val parentNode = DefaultMutableTreeNode(PG_PATTERNCONSUMPTION)
        populationGroup.patternConsumption.forEach { parentNode.add(DefaultMutableTreeNode(it)) }
        add(parentNode)
    }

    if (populationGroup.region.isNotEmpty()) {
        val parentNode = DefaultMutableTreeNode(PG_REGION)
        populationGroup.region.forEach { parentNode.add(DefaultMutableTreeNode(it)) }
        add(parentNode)
    }

    if (populationGroup.country.isNotEmpty()) {
        val parentNode = DefaultMutableTreeNode(PG_COUNTRY)
        populationGroup.country.forEach { parentNode.add(DefaultMutableTreeNode(it)) }
        add(parentNode)
    }

    if (populationGroup.populationRiskFactor.isNotEmpty()) {
        val parentNode = DefaultMutableTreeNode(PG_RISK)
        populationGroup.populationRiskFactor.forEach { parentNode.add(DefaultMutableTreeNode(it)) }
        add(parentNode)
    }

    if (populationGroup.season.isNotEmpty()) {
        val parentNode = DefaultMutableTreeNode(PG_SEASON)
        populationGroup.season.forEach { parentNode.add(DefaultMutableTreeNode(it)) }
    }
}

fun DefaultMutableTreeNode.add(generalInformation: GeneralInformation) {

    add(label = GI_STUDY_NAME, value = generalInformation.name)
    add(label = GI_ID, value = generalInformation.identifier)

    if (generalInformation.creators.isNotEmpty()) {
        // Parent node that holds all the creators
        val parentNode = DefaultMutableTreeNode("Creators")
        generalInformation.creators.forEach {
            val creatorNode = DefaultMutableTreeNode("Creator")
            creatorNode.add(it)
            parentNode.add(creatorNode)
        }
        add(parentNode)
    }

    add(label = GI_CREATION_DATE, value = generalInformation.creationDate.toString())

    generalInformation.modificationDate.let {
        // Parent node that holds all the modification dates
        val modificationDateNode = DefaultMutableTreeNode("Modification dates")
        it.forEach { modificationDateNode.add(label = "Modification date", value = it.toString()) }
        add(modificationDateNode)
    }

    add(label = GI_RIGHTS, value = generalInformation.rights)

    // TODO: isAvailable

    add(label = GI_URL, value = generalInformation.url.toString())
    generalInformation.format?.let { add(label = GI_FORMAT, value = it) }

    if (generalInformation.reference.isNotEmpty()) {
        // Parent node that holds all the reference nodes
        val parentNode = DefaultMutableTreeNode("References")
        generalInformation.reference.forEach {
            val referenceNode = DefaultMutableTreeNode("Reference")
            referenceNode.add(it)
            parentNode.add(referenceNode)
        }
        add(parentNode)
    }

    generalInformation.language?.let { add(label = GI_LANGUAGE, value = it) }
    generalInformation.software?.let { add(label = GI_SOFTWARE, value = it) }
    generalInformation.languageWrittenIn?.let { add(label = GI_LANGUAGE_WRITTEN_IN, value = it) }
    generalInformation.status?.let { add(label = GI_STATUS, value = it) }
    generalInformation.objective?.let { add(label = GI_OBJECTIVE, value = it) }
    generalInformation.description?.let { add(label = GI_DESC, value = it) }
}

fun DefaultMutableTreeNode.add(scope: Scope) {

    val productNode = DefaultMutableTreeNode(SCOPE_PRODUCT)
    scope.product?.let { productNode.add(product = it) }
    add(productNode)

    val hazardNode = DefaultMutableTreeNode(SCOPE_HAZARD)
    scope.hazard?.let { hazardNode.add(hazard = it) }
    add(hazardNode)

    val populationGroupNode = DefaultMutableTreeNode("Population group")
    scope.populationGroup?.let { populationGroupNode.add(it) }
    add(populationGroupNode)

    scope.generalComment?.let { add(label = SCOPE_COMMENT, value = it) }
    scope.temporalInformation?.let { add(label = SCOPE_TEMPORAL, value = it) }

    if (scope.region.isNotEmpty()) {
        val parentNode = DefaultMutableTreeNode(SCOPE_REGION)
        scope.region.forEach { parentNode.add(DefaultMutableTreeNode(it)) }
        add(parentNode)
    }

    if (scope.country.isNotEmpty()) {
        val parentNode = DefaultMutableTreeNode(SCOPE_COUNTRY)
        scope.country.forEach { parentNode.add(DefaultMutableTreeNode(it)) }
        add(parentNode)
    }
}

fun DefaultMutableTreeNode.add(dataBackground: DataBackground) {

    dataBackground.study?.let {
        val node = DefaultMutableTreeNode("Study")
        node.add(it)
        add(node)
    }

    dataBackground.studySample?.let {
        val node = DefaultMutableTreeNode(DB_STUDYSAMPLE)
        node.add(it)
        add(node)
    }

    dataBackground.dietaryAssessmentMethod?.let {
        val node = DefaultMutableTreeNode(DB_DIETARYASSESSMENTMETHOD)
        node.add(it)
        add(node)
    }

    if (dataBackground.laboratoryAccreditation.isNotEmpty()) {
        val parentNode = DefaultMutableTreeNode(DB_ACCREDITATION)
        dataBackground.laboratoryAccreditation.forEach { parentNode.add(DefaultMutableTreeNode(it)) }
        add(parentNode)
    }

    dataBackground.assay?.let {
        val node = DefaultMutableTreeNode(DB_ASSAY)
        node.add(it)
        add(node)
    }
}

fun DefaultMutableTreeNode.add(study: Study) {

    add(label = STUDY_TITLE, value = study.title)
    study.description?.let { add(label = STUDY_DESC, value = it) }
    study.designType?.let { add(label = STUDY_DESIGN, value = it) }
    study.measurementType?.let { add(label = STUDY_MEASUREMENT, value = it) }
    study.technologyType?.let { add(label = STUDY_TECH_TYPE, value = it) }
    study.technologyPlatform?.let { add(label = STUDY_TECH_PLAT, value = it) }
    study.accreditationProcedure?.let { add(label = STUDY_ACCREDITATION, value = it) }
    study.protocolName?.let { add(label = STUDY_PROTOCOL_NAME, value = it) }
    study.protocolType?.let { add(label = STUDY_PROTOCOL_TYPE, value = it) }
    study.protocolDescription?.let { add(label = STUDY_PROTOCOL_DESC, value = it) }
    study.protocolURI?.let { add(label = STUDY_PROTOCOL_URI, value = it.toString()) }
    study.protocolVersion?.let { add(label = STUDY_PROTOCOL_VERSION, value = it) }
    study.parametersName?.let { add(label = STUDY_PARAMETERS, value = it) }

    // TODO: componentsName

    study.componentsType?.let { add(label = STUDY_COMPONENTS_TYPE, value = it) }
}

fun DefaultMutableTreeNode.add(studySample: StudySample) {

    add(label = SS_SAMPLE, value = studySample.sample)
    studySample.moisturePercentage?.let { add(label = SS_MOISTURE_PERC, value = it.toString()) }
    studySample.fatPercentage?.let { add(label = SS_FAT_PERC, value = it.toString()) }
    add(label = SS_SAMPLE_PROTOCOL, value = studySample.collectionProtocol)
    studySample.samplingStrategy?.let { add(label = SS_SAMPLING_STRATEGY, value = it) }
    studySample.samplingProgramType?.let { add(label = SS_SAMPLING_TYPE, value = it) }
    studySample.samplingMethod?.let { add(label = SS_SAMPLING_METHOD, value = it) }
    add(label = SS_SAMPLING_PLAN, value = studySample.samplingWeight)
    add(label = SS_SAMPLING_WEIGHT, value = studySample.samplingWeight)
    add(label = SS_SAMPLING_SIZE, value = studySample.samplingSize)
    studySample.lotSizeUnit?.let { add(label = SS_LOT_UNIT, value = it) }
    studySample.samplingPoint?.let { add(label = SS_SAMPLING_POINT, value = it) }
}

fun DefaultMutableTreeNode.add(dietaryAssessmentMethod: DietaryAssessmentMethod) {

    add(label = DAM_TOOL, value = dietaryAssessmentMethod.collectionTool)
    add(label = DAM_NUMBER_NON_CONSECUTIVE, value = dietaryAssessmentMethod.numberOfNonConsecutiveOneDay.toString())
    dietaryAssessmentMethod.softwareTool?.let { add(label = DAM_SOFTWARE, value = it) }

    if (dietaryAssessmentMethod.numberOfFoodItems.isNotEmpty()) {
        val parentNode = DefaultMutableTreeNode(DAM_FOOD_ITEM)
        dietaryAssessmentMethod.numberOfFoodItems.forEach { parentNode.add(DefaultMutableTreeNode(it)) }
    }

    add(label = DAM_NUMBER_NON_CONSECUTIVE, value = dietaryAssessmentMethod.numberOfNonConsecutiveOneDay.toString())

    if (dietaryAssessmentMethod.recordTypes.isNotEmpty()) {
        val parentNode = DefaultMutableTreeNode(DAM_RECORD_TYPE)
        dietaryAssessmentMethod.recordTypes.forEach { parentNode.add(DefaultMutableTreeNode(it)) }
    }

    if (dietaryAssessmentMethod.foodDescriptors.isNotEmpty()) {
        val parentNode = DefaultMutableTreeNode(DAM_FOOD_ITEM)
        dietaryAssessmentMethod.foodDescriptors.forEach { parentNode.add(DefaultMutableTreeNode(it)) }
    }
}

fun DefaultMutableTreeNode.add(assay: Assay) {
    add(label = ASSAY_NAME, value = assay.name)
    assay.description?.let { add(label = ASSAY_DESC, value = it) }
}

fun DefaultMutableTreeNode.add(parameter: Parameter) {

    add(label = PARAMETER_ID, value = parameter.id)
    add(label = PARAMETER_CLASIF, value = parameter.classification.toString())
    add(label = PARAMETER_NAME, value = parameter.name)
    parameter.description?.let { add(label = PARAMETER_DESC, value = it) }
    add(label = PARAMETER_UNIT, value = parameter.unit)
    add(label = PARAMETER_UNIT_CATEGORY, value = parameter.unitCategory)
    add(label = PARAMETER_DATA_TYPE, value = parameter.dataType)
    parameter.source?.let { add(label = PARAMETER_SOURCE, value = it) }
    parameter.subject?.let { add(label = PARAMETER_SUBJECT, value = it) }
    parameter.distribution?.let { add(label = PARAMETER_DIST, value = it) }
    parameter.value?.let { add(label = PARAMETER_VALUE, value = it) }
    parameter.reference?.let { add(label = PARAMETER_REFERENCE, value = it) }
    parameter.variabilitySubject?.let { add(label = PARAMETER_VARIABILITY, value  = it) }

    if (parameter.modelApplicability.isNotEmpty()) {
        val parentNode = DefaultMutableTreeNode(PARAMETER_APPLICABILITY)
        parameter.modelApplicability.forEach { parentNode.add(DefaultMutableTreeNode(it)) }
        add(parentNode)
    }

    parameter.error?.let { add(label = PARAMETER_ERROR, value = it.toString()) }
}

fun DefaultMutableTreeNode.add(modelEquation: ModelEquation) {

    add(label = MODEL_EQUATION_NAME, value = modelEquation.equationName)
    modelEquation.equationClass?.let { add(label = MODEL_EQUATION_CLASS, value = it) }

    if (modelEquation.equationReference.isNotEmpty()) {
        val parentNode = DefaultMutableTreeNode("References")
        modelEquation.equationReference.forEach {
            val childNode = DefaultMutableTreeNode("Reference")
            childNode.add(it)
            parentNode.add(childNode)
        }
        add(parentNode)
    }

    add(label = MODEL_EQUATION_SCRIPT, value = modelEquation.equation)
}

fun DefaultMutableTreeNode.add(modelMath: ModelMath) {

    if (modelMath.parameter.isNotEmpty()) {
        val parentNode = DefaultMutableTreeNode("Parameters")
        modelMath.parameter.forEach {
            val childNode = DefaultMutableTreeNode("Parameter")
            childNode.add(parameter = it)
            parentNode.add(childNode)
        }
        add(parentNode)
    }

    modelMath.sse?.let { add(label = "SSE", value = it.toString()) }
    modelMath.mse?.let { add(label = "MSE", value = it.toString()) }
    modelMath.rmse?.let { add(label = "RMSE", value = it.toString()) }
    modelMath.rSquared?.let { add(label = "r-Squared", value = it.toString()) }
    modelMath.aic?.let { add(label = "AIC", value = it.toString()) }
    modelMath.bic?.let { add(label = "BIC", value = it.toString()) }

    modelMath.modelEquation?.let {
        val node = DefaultMutableTreeNode("Model equation")
        node.add(modelEquation = it)
        add(node)
    }

    modelMath.fittingProcedure?.let { add(label = "Fitting procedure", value = it) }

    // TODO: exposure

    if (modelMath.event.isNotEmpty()) {
        val parentNode = DefaultMutableTreeNode("Events")
        modelMath.event.forEach { parentNode.add(DefaultMutableTreeNode(it)) }
        add(parentNode)
    }
}

fun createTree(genericModel: GenericModel) : JTree {

    val generalInformationNode = DefaultMutableTreeNode("General information")
    generalInformationNode.add(genericModel.generalInformation)

    val scopeNode = DefaultMutableTreeNode("Scope")
    scopeNode.add(genericModel.scope)

    val dataBackgroundNode = DefaultMutableTreeNode("Data background")
    genericModel.dataBackground?.let { dataBackgroundNode.add(it) }

    val modelMathNode = DefaultMutableTreeNode("Model math")
    genericModel.modelMath?.let { modelMathNode.add(it) }

    val simulationNode = DefaultMutableTreeNode("Simulation")
    // TODO: simulation

    val rootNode = DefaultMutableTreeNode()

    rootNode.add(generalInformationNode)
    rootNode.add(scopeNode)
    rootNode.add(dataBackgroundNode)
    rootNode.add(modelMathNode)
    rootNode.add(simulationNode)

    return JTree(rootNode)
}

fun main(args: Array<String>) {

    fun createExampleGeneralInformation(): GeneralInformation {

        // Example data
        val gi = GeneralInformation(name = "name",
                identifier = "007",
                creationDate = Date(),
                modificationDate = mutableListOf(Date(), Date(), Date()),
                rights = "to remain silent",
                isAvailable = true,
                url = URL("https://google.de"),
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

    fun createExampleScope() : Scope {
        val product = Product(
                environmentName = "Cereals",
                environmentDescription = "bla bla bla",
                environmentUnit = "Bq/kg",
                productionMethod = mutableListOf("Traditional production"),
                packaging = mutableListOf("Wrapped"),
                productTreatment = mutableListOf("Processed"),
                originCountry = "Andorra",
                areaOfOrigin = "Wien",
                fisheriesArea = "Fao Sub Area 21.0",
                productionDate = Date(),
                expirationDate = Date())
        val hazard = Hazard(
                hazardType = "Biogenic amines",
                hazardName = "Chemical elements and derivatives",
                hazardDescription = "bla bla bla",
                hazardUnit = "Bq/kg",
                adverseEffect = "adverse effect",
                origin = "origin",
                benchmarkDose = "benchmark dose",
                maximumResidueLimit = "maximum residue limit",
                noObservedAdverse = "no observed adverse",
                lowestObservedAdverse = "lowest observe adverse",
                acceptableOperator = "acceptable operator",
                acuteReferenceDose = "acute reference dose",
                acceptableDailyIntake = "acceptable daily intake",
                hazardIndSum = "Individual",
                laboratoryName = "laboratoryName",
                laboratoryCountry = "Austria",
                detectionLimit = "detection limit",
                quantificationLimit = "quantification limit",
                leftCensoredData = "leftCensoredData",
                rangeOfContamination = "rangeOfContamination")
        val populationGroup = PopulationGroup(
                populationName = "populationName",
                targetPopulation = "targetPopulation",
                populationSpan = mutableListOf("populationSpan"),
                populationDescription = mutableListOf("populationDescription"),
                populationAge = mutableListOf("populationAge"),
                populationGender = "populationGender",
                bmi = mutableListOf("bmi"),
                specialDietGroups = mutableListOf("specialDietGroups"),
                patternConsumption = mutableListOf("patternConsumption"),
                region = mutableListOf("Österreich"),
                country = mutableListOf("Andorra"),
                populationRiskFactor = mutableListOf("populationRiskFactor"),
                season = mutableListOf("season"))

        val scope = Scope(
                product = product,
                hazard = hazard,
                populationGroup = populationGroup,
                generalComment = "general comment",
                temporalInformation = "temporal information",
                region = mutableListOf("region"),
                country = mutableListOf("country")
        )

        return scope
    }

    fun createExampleDataBackground() : DataBackground {

        val study = Study(
            title = "title",
                description = "description",
                designType = "design type",
                measurementType = "measurement type",
                technologyType = "technology type",
                accreditationProcedure = "accreditation procedure",
                protocolName = "protocol name",
                protocolType = "protocol type",
                protocolDescription = "protocol description",
                protocolURI = URI("http://google.es"),
                protocolVersion = "protocol version",
                parametersName = "parameters name",
                componentsName = "components name",
                componentsType = "components type"
        )

        val studySample = StudySample(
                sample = "sample",
                moisturePercentage = -1.0,
                fatPercentage = 9000.0,
                collectionProtocol = "collection protocol",
                samplingStrategy = "sampling strategy",
                samplingProgramType = "sampling program type",
                samplingMethod = "sampling method",
                samplingPlan = "sampling plan",
                samplingWeight = "sampling weight",
                samplingSize = "sampling size",
                lotSizeUnit = "lot size unit",
                samplingPoint = "sampling point"
        )

        val dietaryAssessmentMethod = DietaryAssessmentMethod(
            collectionTool = "collection tool",
                numberOfNonConsecutiveOneDay = 0,
                softwareTool = "Software tool",
                numberOfFoodItems = mutableListOf("1", "2", "3"),
                recordTypes = mutableListOf("1", "2", "3"),
                foodDescriptors = mutableListOf("1", "2", "3")
        )

        val laboratoryAccreditation = mutableListOf("accreditation1", "accreditation2", "accreditation-n")

        val assay = Assay(name = "name", description = "description")

        return DataBackground(study = study, studySample = studySample,
                dietaryAssessmentMethod = dietaryAssessmentMethod, laboratoryAccreditation = laboratoryAccreditation,
                assay = assay)
    }

    fun createExampleModelMath() : ModelMath {

        val param = Parameter(
                id = "id",
                classification = ParameterClassification.constant,
                name = "name",
                unit = "unit",
                unitCategory = "unit category",
                dataType = "data type",
                source = "source",
                subject = "subject",
                distribution = "distribution",
                value = "value",
                reference = "reference",
                variabilitySubject = "variability subject",
                modelApplicability = mutableListOf("1", "2", "3"),
                error = .0)

        val equation = ModelEquation(
                equationName = "equation name",
                equationClass = "equation class",
                equationReference = mutableListOf(Record(), Record()),
                equation = "equation")

        val exposure = Exposure(
                treatment = "treatment",
                contaminationLevel = "contamination level",
                exposureType = "exposure type",
                scenario = "scenario",
                uncertaintyEstimation = "uncertainty estimation"
        )

        return ModelMath(
                parameter = mutableListOf(param, param, param),
                sse = .0, mse = .0, rmse = .0, rSquared = .0, aic = .0, bic =.0,
                modelEquation = equation,
                fittingProcedure = "fitting procedure",
                exposure = exposure,
                event = mutableListOf("event1", "event2", "event-n")
        )
    }

    val tree = createTree(GenericModel(generalInformation = createExampleGeneralInformation(),
            scope = createExampleScope(), dataBackground = createExampleDataBackground(),
            modelMath = createExampleModelMath()))

    val frame = JFrame()
    frame.add(JScrollPane(tree))
    frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    frame.title = "main fun test"
    frame.size = Dimension(500, 300)
    frame.minimumSize = Dimension(800, 500)
    frame.isVisible = true
    frame.setLocationRelativeTo(null)  // center frame
}