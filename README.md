# openTCS integration project example

This template allows to easily create a skeleton of a new openTCS integration project.
Which openTCS release it is based on is defined in [gradle/dependency-versions.gradle](gradle/dependency-versions.gradle).
(Usually, this should be the latest release.)

## Recommended usage

Run `gradlew clean cloneProject <-PintegrationName=somename> <-PpackageName=com.example>`.
The result will be a new integration project skeleton in the `build/` directory.
(Running the `clean` task before `cloneProject`, as shown here, is recommended.)

* `integrationName` is the name of the integration project.
  The name of the root project and subprojects for e.g. the kernel or the model editor application will contain this string.
  Values could be a name of a plant or company where the resulting distribution will be used, e.g. `robolab` or `yoyodyne`.
* `packageName` is the base package name for classes in the integration project.
  Should be in a namespace that you own / is unlikely to clash with someone else's code, e.g. `com.yourcompany`.

After cloning the project like this, move the newly created project skeleton somewhere else to work on it.
(The `build/` directory will be deleted whenever `gradlew clean` is run, which would also delete your project, so moving it somewhere else makes sense.)

## Licensing

### Code

All of this software project's source code, including scripts and configuration files, is distributed under the [MIT License](LICENSE.txt).

### Assets

Unless stated otherwise, all of this software project's documentation, resource bundles and media files are distributed under the [Creative Commons Attribution 4.0 International (CC BY 4.0)](LICENSE.assets.txt) license.
