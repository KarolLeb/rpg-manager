package com.rpgmanager.backend.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(
    packages = "com.rpgmanager",
    importOptions = {ImportOption.DoNotIncludeTests.class})
public class HexagonalArchitectureTest {

  @ArchTest
  public static final ArchRule hexagonal_architecture_should_be_respected =
      layeredArchitecture()
          .consideringOnlyDependenciesInAnyPackage(
              "com.rpgmanager.backend.character..", "com.rpgmanager.backend.campaign..")
          .layer("Domain")
          .definedBy("com.rpgmanager.backend..domain..")
          .layer("Application")
          .definedBy("com.rpgmanager.backend..application..")
          .layer("Infrastructure")
          .definedBy("com.rpgmanager.backend..infrastructure..")
          .whereLayer("Domain")
          .mayOnlyBeAccessedByLayers("Application", "Infrastructure")
          .whereLayer("Application")
          .mayOnlyBeAccessedByLayers("Infrastructure")
          .whereLayer("Infrastructure")
          .mayNotBeAccessedByAnyLayer();

  @ArchTest
  public static final ArchRule domain_should_be_completely_isolated =
      classes()
          .that()
          .resideInAPackage("..domain..")
          .should()
          .onlyDependOnClassesThat()
          .resideInAnyPackage(
              "com.rpgmanager.backend..domain..", "java..", "lombok..", "org.slf4j..");

  @ArchTest
  public static final ArchRule application_should_not_depend_on_infrastructure =
      classes()
          .that()
          .resideInAPackage("..application..")
          .should()
          .onlyDependOnClassesThat()
          .resideInAnyPackage(
              "..application..",
              "..domain..",
              "java..",
              "lombok..",
              "org.slf4j..",
              "org.springframework..",
              "org.mapstruct..",
              "com.rpgmanager.backend..application..",
              "com.rpgmanager.backend..domain..",
              "com.rpgmanager.backend.activitylog..",
              "com.rpgmanager.common.security..");
}
