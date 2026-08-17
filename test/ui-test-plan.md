# Console UI Test Plan

Record all console user-interface test cases here. Run them with the project-local `$test-ui` skill.

## Test environment

- Run commands from the repository root.
- Use Java 25 for Java build and run commands.
- Compare complete standard output. Line endings and one final trailing newline are normalized; all other whitespace is significant.

## Test cases

Add cases in the following format. The command must include any required input piping or redirection.

## Test case: add todo and list tasks

**Aim:** Verify that a todo is added, shown with its task type and unfinished status, and listed.

**Inputs:**
```text
todo borrow book
list
bye
```

**Command:**
```powershell
javac -d _temp\ui-test-classes src\main\java\*.java; @("todo borrow book", "list", "bye") | java -cp _temp\ui-test-classes ErmActually
```

**Expected output:**
```text
____________________________________________________________
+----------------+
|  Erm Actually  |
+----------------+
Greetings! I'm Erm Actually.
What can I actually do for you?
____________________________________________________________
____________________________________________________________
 Alright! I've added this new task:
   [T][ ] borrow book
 Wow! you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1. [T][ ] borrow book
____________________________________________________________
____________________________________________________________
Farewell! Hope you stop by again soon!
____________________________________________________________
```

## Test case: add deadline

**Aim:** Verify that a deadline preserves and displays its `by` value.

**Inputs:**
```text
deadline return book /by Sunday
bye
```

**Command:**
```powershell
javac -d _temp\ui-test-classes src\main\java\*.java; @("deadline return book /by Sunday", "bye") | java -cp _temp\ui-test-classes ErmActually
```

**Expected output:**
```text
____________________________________________________________
+----------------+
|  Erm Actually  |
+----------------+
Greetings! I'm Erm Actually.
What can I actually do for you?
____________________________________________________________
____________________________________________________________
 Alright! I've added this new task:
   [D][ ] return book (by: Sunday)
 Wow! you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Farewell! Hope you stop by again soon!
____________________________________________________________
```

## Test case: add event

**Aim:** Verify that an event preserves and displays its start and end strings.

**Inputs:**
```text
event project meeting /from Mon 2pm /to 4pm
bye
```

**Command:**
```powershell
javac -d _temp\ui-test-classes src\main\java\*.java; @("event project meeting /from Mon 2pm /to 4pm", "bye") | java -cp _temp\ui-test-classes ErmActually
```

**Expected output:**
```text
____________________________________________________________
+----------------+
|  Erm Actually  |
+----------------+
Greetings! I'm Erm Actually.
What can I actually do for you?
____________________________________________________________
____________________________________________________________
 Alright! I've added this new task:
   [E][ ] project meeting (from: Mon 2pm to: 4pm)
 Wow! you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Farewell! Hope you stop by again soon!
____________________________________________________________
```

## Test case: mark and unmark a task

**Aim:** Verify that marking changes a todo to complete and unmarking restores it to incomplete.

**Inputs:**
```text
todo borrow book
mark 1
unmark 1
bye
```

**Command:**
```powershell
javac -d _temp\ui-test-classes src\main\java\*.java; @("todo borrow book", "mark 1", "unmark 1", "bye") | java -cp _temp\ui-test-classes ErmActually
```

**Expected output:**
```text
____________________________________________________________
+----------------+
|  Erm Actually  |
+----------------+
Greetings! I'm Erm Actually.
What can I actually do for you?
____________________________________________________________
____________________________________________________________
 Alright! I've added this new task:
   [T][ ] borrow book
 Wow! you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
oh! good job you've actually finished this task:
 [T][X] borrow book
____________________________________________________________
____________________________________________________________
oh? okay then I'll unmark it for you:
  [T][ ] borrow book
____________________________________________________________
____________________________________________________________
Farewell! Hope you stop by again soon!
____________________________________________________________
```
