# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).

## [Unreleased]
- Properties strings for *Process Model*
- Properties strings for *Predictive Model*
- Properties strings for *Other Empirical Model*
- Bugfix with `GeneralInformationPanel` initialized twice
- Language field in `GeneralInformationPanel` is now a `AutoSuggestField`

## [0.0.5a1]
### Changed
- Bugfix within fun main in editorUI.kt. Panels from GeneralInformationPanel were given again controlled vocabularies.

## [0.0.4a1]
### Changed
- NonEditableTableModel is made public
- Table model, `dtm`, for `CreatorPanel` and `ReferencePanel` is a property of the class

## [0.0.3a1]
### Changed
- Local jars with maven repositories

## [0.0.2a1]
### Removed
- Unused `loadVocabularies` in `editorUI.kt`.

## [0.0.1a1]
### Changed
- Fix loading Excel spreadsheet with controlled vocabularies.
- Argument `generalInformation` in `GeneralInformationPanel` is optional.
- Argument `creators` in `CreatorPanel` is optional.
- Argument `scope` in `ScopePanel` is optional.

## [0.0.0a1] - 2017-06-27
### Changed
- Rename properties from the *Generic model* with a prefix `GM.` Other RAKIP models will have similar prefixes.

## [0.0.3a0] - 2017-06-27
### Changed
- Fix layout in `ValidatableDialog`. Temporal solution before using FCL UI utils.
- Fix property in MessagesBundle `EditHazardPanel.contaminationRangeTooltip`

## [0.0.2a0]
### Changed
- Fixed advanced mode for `EditAssayPanel`
- Fixed advanced mode for `EditParameterPanel`
- Fixed advanced mode for `EditModelEquationPanel`

## [0.0.1a0]
### Added
- Properties file with strings for JComponents `MessageBundle.properties`

### Removed
- Companion objects from customized JComponents with strings

## [0.0.0a0] - 2017-06-26
### Added
- Changelog
- `de.bund.bfr.rakip.editor.validatableDialogs.kt`

### Changed
- Moved `ValidatableDialog` and `ValidatablePanel`(s) to a single file validatableDialogs.kt
- Moved validation methods from `uiComponents.kt` to `validatableDialogs.kt`

### Removed
- `de.bund.bfr.rakip.editor.ValidatableDialog`
- `de.bund.bfr.rakip.editor.editorUI.ValidatablePanel`
- `de.bund.bfr.rakip.editor.EditAssayPanel`
- `de.bund.bfr.rakip.editor.EditDietaryAssessmentMethodPanel`
- `de.bund.bfr.rakip.editor.EditHazardPanel`
- `de.bund.bfr.rakip.editor.EditModelEquationPanel`
- `de.bund.bfr.rakip.editor.EditParameterPanel`
- `de.bund.bfr.rakip.editor.EditPopulationGroupPanel`
- `de.bund.bfr.rakip.editor.EditProductPanel`
- `de.bund.bfr.rakip.editor.EditReferencePanel`
- `de.bund.bfr.rakip.editor.EditStudySamplePanel`
- `de.bund.bfr.rakip.editor.uiComponents.kt`