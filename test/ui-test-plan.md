# Console UI Test Plan

Record all console user-interface test cases here. Run them with the project-local `$test-ui` skill.

## Test environment

- Run commands from the repository root.
- Use Java 25 for Java build and run commands.
- Compare complete standard output. Line endings and one final trailing newline are normalized; all other whitespace is significant.

## Test cases

Add cases in the following format. The command must include any required input piping or redirection.

## Test case: `<short name>`

**Aim:** Describe the user-visible behavior to verify.

**Inputs:**
```text
<each line entered into the console, or `None`>
```

**Command:**
```powershell
<complete command to run from the repository root>
```

**Expected output:**
```text
<complete expected standard output>
```
