# openTCS integration project example

This template allows to easily create a skeleton of a new openTCS integration project.
Which openTCS release it is based on is defined in [gradle/dependency-versions.gradle](gradle/dependency-versions.gradle).
(Usually, this should be the latest release.)

## Recommended usage

1. Run `gradlew cloneProject <-PintegrationName=SomeName> <-PclassPrefix=SomePrefix> <-PpackageName=com.example>`.
   The result will be a new integration project skeleton in the `build/` directory.
2. Copy the newly created project skeleton somewhere else to work on it.
   (The `build/` directory will be deleted whenever `gradlew clean` is run, which would also delete your project.)

## Licensing

### Code

All of this software project's source code, including scripts and configuration files, is distributed under the [MIT License](LICENSE.txt).

### Assets

Unless stated otherwise, all of this software project's documentation, resource bundles and media files are distributed under the [Creative Commons Attribution 4.0 International (CC BY 4.0)](LICENSE.assets.txt) license.
