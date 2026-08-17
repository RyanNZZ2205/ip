---
name: test-ui
description: Run and verify planned console user-interface tests for this Java project. Use when asked to execute, add, update, or report command-line UI test cases recorded in test/ui-test-plan.md, including exact expected-output comparisons and test-session transcripts.
---

# Console UI Testing

Read `test/ui-test-plan.md` before testing. Treat every `## Test case:` section, in file order, as one test.

## Test-plan format

Keep the plan in Markdown and give every test case all four fields below:

````markdown
## Test case: `<short name>`

**Aim:** What behavior this test verifies.

**Inputs:**
```text
<each line entered into the console, or `None`>
```

**Command:**
```powershell
<a complete command run from the repository root; include input redirection or piping>
```

**Expected output:**
```text
<the complete expected console output>
```
````

Keep commands self-contained. Run them from the repository root. Use Java 25 for Java build and run commands. Update the plan whenever its cases, commands, assumptions, or expected output change.

## Run the tests

1. Check that the plan has at least one complete test case. If it does not, report that testing cannot start and identify the missing fields.
2. Run test cases in order without modifying source files or the plan. Capture standard output and standard error separately; use standard output for the expected-output comparison.
3. Compare the complete actual standard output with `Expected output`. Preserve spaces and blank lines; normalize only line endings and one final trailing newline so Windows and Unix consoles compare consistently.
4. After each passing test, retain its supplied input and captured output for the session transcript.
5. On the first failed test, stop immediately. Report its aim, command, supplied inputs, expected output, actual output, and any standard error. Do not run later cases.

## Report the session

Always show a `Console test session` record in the response. For each test that ran, include its name, input, and output in fenced `text` blocks, then give the pass/fail result. On success, state how many cases passed. On failure, clearly label the expected and actual outputs and state that the session was terminated.

Do not silently trim prompts, whitespace, or unexpected diagnostics to make a test pass. If the command exits unsuccessfully, treat the case as failed even when its standard output matches.
