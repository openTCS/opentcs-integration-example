package org.opentcs.scripts

import java.util.regex.Pattern
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.*
import java.io.FileFilter
import org.apache.commons.io.FileUtils
import groovy.io.FileType

// Path relative to the root project's build.gradle
String basePath = ''

String oldIntegrationName = 'Example'
String newIntegrationName = args[0]
String oldPackageName = "com.example"
String oldPackagePath = oldPackageName.replace(".", "/")
String newPackageName = args[1]
String newPackagePath = newPackageName.replace(".", "/")

// The path to the source project
Path sourcePath = new File(basePath).toPath().toAbsolutePath()

// Create a directory with the new project name
File destFile = new File('build/openTCS-Integration-' + newIntegrationName)

// The path to the (new) destination project
Path destPath = destFile.toPath().toAbsolutePath()

// Delete destination folder before cloning
FileUtils.deleteQuietly(destFile)

// Copy the project files but apply a file filter to exclude some files and directories
FileUtils.copyDirectory(sourcePath.toFile(), destPath.toFile(), new ExcludeFilter(sourcePath))

// Rename the copied sub-project directories to contain the new integration name and apply the new
// package name by moving the source files
destFile.eachFile { dir ->
  if (dir.isDirectory() && dir.name.contains('-' + oldIntegrationName + '-')) {
    renamePackages(dir.path + "/src/guiceConfig/java/", oldPackagePath, newPackagePath)
    renamePackages(dir.path + "/src/guiceConfig/resources/", oldPackagePath, newPackagePath)
    renamePackages(dir.path + "/src/main/java/", oldPackagePath, newPackagePath)
    renamePackages(dir.path + "/src/main/resources/", oldPackagePath, newPackagePath)
    renamePackages(dir.path + "/src/test/java/", oldPackagePath, newPackagePath)
    renamePackages(dir.path + "/src/test/resources/", oldPackagePath, newPackagePath)

    String newName = dir.getPath().replace('-' + oldIntegrationName + '-', '-' + newIntegrationName + '-')
    boolean result = dir.renameTo(newName)
    if (!result) {
      System.err.println("Could not rename directory " + dir.getPath() + " to " + newName)
      System.exit(1)
    }
  }
}

// Change the source files name and package information.
destFile.eachFileRecurse(FileType.FILES) { file ->
  // For all java files, change packages and paths to correspond to the new package name
  if (file.name.matches("(.+).java")) {
    file.text = file.text.replace(oldPackageName, newPackageName)
    file.text = file.text.replace(oldPackagePath, newPackagePath)
  }

  // For all form files, change paths to correspond to the new package name
  if (file.name.matches("(.+).form")) {
    file.text = file.text.replace(oldPackagePath, newPackagePath)
  }

  // For all guice modules, change packages to correspond to the new package name
  if (file.name.matches("(.+).KernelInjectionModule")
      || file.name.matches("(.+).ControlCenterInjectionModule")
      || file.name.matches("(.+).PlantOverviewInjectionModule")) {
    file.text = file.text.replace(oldPackageName, newPackageName)
  }

  // Adjust the build.gradle files for all sub-projects
  if (file.name.equals("build.gradle")) {
    file.text = file.text.replace(oldPackagePath, newPackagePath)
    file.text = file.text.replace(oldIntegrationName, newIntegrationName)
  }

  // For all asciidoctor files, change packages to correspond to the new package name
  if (file.name.matches("(.+).adoc")) {
    file.text = file.text.replace(oldPackagePath, newPackagePath)
  }
}

// Change file content for the root project's build.gradle
File buildGradle = new File(destPath.resolve('build.gradle').toString())
buildGradle.text = buildGradle.text.replace(oldIntegrationName, newIntegrationName)
    .replaceAll(Pattern.compile("\\Rrepositories\\s\\{([^}]|\\R)+\\}\\R"), '') // Remove the 'repositories' block. We don't need the groovy dependency.
    .replaceAll(Pattern.compile("\\Rdependencies\\s\\{([^}]|\\R)+\\}\\R"), '') // Remove the 'dependencies' block. We don't need the groovy dependency.
    .replace('apply plugin: \'groovy\'', '') // We also don't need the groovy plugin
    .replaceAll(Pattern.compile("\\R(\\/\\/ tag::cloneTask)((.|\\R)*)\\1\\R"), ''); // Remove the 'cloneProject' task

// Change file content for the root project's settings.gradle
File settingsGradle = new File(destPath.resolve('settings.gradle').toString())
settingsGradle.text = settingsGradle.text.replace(oldIntegrationName, newIntegrationName)

/**
 * Renames package directories in the {@code basePath} by moving files contained in the
 * {@code sourcePackagePath} to the {@code targetPackagePath} and deleting old package directories.
 *
 * @param basePath The base path containing the packages to rename.
 * @param sourcePackagePath The path relative to the {@code basePath} containing the files and
 * directories to move.
 * @param targetPackagePath The path relative to the {@code basePath} to move the files and
 * directories to.
 */
def renamePackages(String basePath, String sourcePackagePath, String targetPackagePath) {
  File sourceDir = new File(basePath + sourcePackagePath)
  if (!sourceDir.exists()) {
    // There is nothing to rename
    return;
  }

  // Move files and directories from the source package to a temporary directory (this is required
  // for cases where the target package and the default/source package share the same root package,
  // e.g. when they both start with "com")
  File tempDir = new File(basePath + "/tempDirectory")
  FileUtils.moveDirectory(sourceDir, tempDir)

  // Delete the old source package directories
  File baseDir = new File(basePath)
  while (sourceDir.path != baseDir.path) {
    FileUtils.deleteDirectory(sourceDir)
    sourceDir = sourceDir.getParentFile()
  }

  // Move files and directories form the temporary directory to the target package
  File targetDir = new File(basePath + targetPackagePath)
  FileUtils.moveDirectory(tempDir, targetDir)
}

class ExcludeFilter implements FileFilter {

  private Path excludeFrom;
  private List<String> toExclude = [".git", ".gitignore", ".gradle", ".nb-gradle", "build", "src", "README.md"];

  ExcludeFilter(Path excludeFrom) {
    this.excludeFrom = excludeFrom;
  }

  @Override
  public boolean accept(File file) {
    for (String exclude : toExclude) {
      Path excludePath = excludeFrom.resolve(exclude)
      if (Files.exists(excludePath) && Files.isSameFile(excludePath, file.toPath())) {
        return false;
      }
    }
    return true;
  }
}
