# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Beginner
* IDE and level of expertise: Beginner

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Java coding standard

All Java code in this project must follow the project-local `$seedu-java-coding-standard` skill. Invoke the skill when creating, modifying, or reviewing Java code, and correct applicable standard violations before handing off a code change.

## Console UI testing

After every code update, review `test/ui-test-plan.md` and update it when the change adds, removes, or changes user-visible console behavior. Then invoke the project-local `$test-ui` skill to run the UI test plan and report its console test session. Do this before handing off the code update; if the plan has no complete test cases, report that limitation clearly.

## JUnit testing

Maintain JUnit tests for approximately the top 50% highest-value methods, prioritizing complex, core, and critical business logic over trivial accessors. After every code change, review and update the JUnit tests as needed to keep the test suite compliant with this coverage target.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
