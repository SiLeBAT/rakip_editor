# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).

## [Unreleased]

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