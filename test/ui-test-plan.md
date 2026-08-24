# Console UI Test Plan

Record all console user-interface test cases here. Run them with the project-local `$test-ui` skill.

## Test environment

- Run commands from the repository root.
- Use Java 25 for Java build and run commands.
- Compare complete standard output. Line endings and one final trailing newline are normalized; all other whitespace is significant.

## Test cases

Add cases in the following format. The command must include any required input piping or redirection.

## Test case: add todo and list tasks

**Aim:** Verify that a todo is added, saved to disk, shown with its task type and unfinished status, and listed.

**Inputs:**
```text
todo borrow book
list
bye
```

**Command:**
```powershell
if (Test-Path data) { Remove-Item -Recurse -Force data }; javac -d _temp\ui-test-classes src\main\java\*.java; @("todo borrow book", "list", "bye") | java -cp _temp\ui-test-classes ErmActually; Get-Content data\ErmActually.txt
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
V2 | T | 0 | Ym9ycm93IGJvb2s=
```

## Test case: add deadline

**Aim:** Verify that an ISO-format deadline date is parsed, displayed in a friendly format, and saved in ISO format.

**Inputs:**
```text
deadline return book /by 2019-12-02
bye
```

**Command:**
```powershell
if (Test-Path data) { Remove-Item -Recurse -Force data }; javac -d _temp\ui-test-classes src\main\java\*.java; @("deadline return book /by 2019-12-02", "bye") | java -cp _temp\ui-test-classes ErmActually; Get-Content data\ErmActually.txt
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
   [D][ ] return book (by: Dec 02 2019)
 Wow! you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Farewell! Hope you stop by again soon!
____________________________________________________________
V2 | D | 0 | cmV0dXJuIGJvb2s= | MjAxOS0xMi0wMg==
```

## Test case: add deadline with optional time

**Aim:** Verify that a deadline accepts, displays, and saves an optional time.

**Inputs:**
```text
deadline submit report /by 2026-08-25 1900
bye
```

**Command:**
```powershell
if (Test-Path data) { Remove-Item -Recurse -Force data }; javac -d _temp\ui-test-classes src\main\java\*.java; @("deadline submit report /by 2026-08-25 1900", "bye") | java -cp _temp\ui-test-classes ErmActually; Get-Content data\ErmActually.txt
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
   [D][ ] submit report (by: Aug 25 2026 7:00 PM)
 Wow! you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Farewell! Hope you stop by again soon!
____________________________________________________________
V2 | D | 0 | c3VibWl0IHJlcG9ydA== | MjAyNi0wOC0yNVQxOTowMA==
```

## Test case: reject invalid deadline date

**Aim:** Verify that a deadline not using a valid `yyyy-MM-dd` date is rejected without adding a task.

**Inputs:**
```text
deadline return book /by 2/12/2019
bye
```

**Command:**
```powershell
if (Test-Path data) { Remove-Item -Recurse -Force data }; javac -d _temp\ui-test-classes src\main\java\*.java; @("deadline return book /by 2/12/2019", "bye") | java -cp _temp\ui-test-classes ErmActually
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
 uhohhhh... Please enter the deadline in yyyy-MM-dd or yyyy-MM-dd HHmm format.
____________________________________________________________
____________________________________________________________
Farewell! Hope you stop by again soon!
____________________________________________________________
```

## Test case: add event

**Aim:** Verify that an event preserves and displays its start and end strings.

**Inputs:**
```text
event project meeting /from 2019-12-02 1400 /to 2019-12-02 1600
bye
```

**Command:**
```powershell
if (Test-Path data) { Remove-Item -Recurse -Force data }; javac -d _temp\ui-test-classes src\main\java\*.java; @("event project meeting /from 2019-12-02 1400 /to 2019-12-02 1600", "bye") | java -cp _temp\ui-test-classes ErmActually
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
   [E][ ] project meeting (from: Dec 02 2019 2:00 PM to: Dec 02 2019 4:00 PM)
 Wow! you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Farewell! Hope you stop by again soon!
____________________________________________________________
```

## Test case: add event without times

**Aim:** Verify that an event accepts date-only endpoints and does not display invented times.

**Inputs:**
```text
event holiday /from 2026-08-25 /to 2026-08-25
bye
```

**Command:**
```powershell
if (Test-Path data) { Remove-Item -Recurse -Force data }; javac -d _temp\ui-test-classes src\main\java\*.java; @("event holiday /from 2026-08-25 /to 2026-08-25", "bye") | java -cp _temp\ui-test-classes ErmActually; Get-Content data\ErmActually.txt
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
   [E][ ] holiday (from: Aug 25 2026 to: Aug 25 2026)
 Wow! you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Farewell! Hope you stop by again soon!
____________________________________________________________
V2 | E | 0 | aG9saWRheQ== | MjAyNi0wOC0yNQ== | MjAyNi0wOC0yNQ==
```

## Test case: find tasks occurring on a date

**Aim:** Verify that date search finds deadlines and multi-day events, skips todos, and preserves original task numbers.

**Inputs:**
```text
todo borrow book
deadline submit report /by 2019-12-03
event conference /from 2019-12-02 0900 /to 2019-12-04 1700
on 2019-12-03
bye
```

**Command:**
```powershell
if (Test-Path data) { Remove-Item -Recurse -Force data }; javac -d _temp\ui-test-classes src\main\java\*.java; @("todo borrow book", "deadline submit report /by 2019-12-03", "event conference /from 2019-12-02 0900 /to 2019-12-04 1700", "on 2019-12-03", "bye") | java -cp _temp\ui-test-classes ErmActually
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
 Alright! I've added this new task:
   [D][ ] submit report (by: Dec 03 2019)
 Wow! you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Alright! I've added this new task:
   [E][ ] conference (from: Dec 02 2019 9:00 AM to: Dec 04 2019 5:00 PM)
 Wow! you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks occurring on 2019-12-03:
 2. [D][ ] submit report (by: Dec 03 2019)
 3. [E][ ] conference (from: Dec 02 2019 9:00 AM to: Dec 04 2019 5:00 PM)
____________________________________________________________
____________________________________________________________
Farewell! Hope you stop by again soon!
____________________________________________________________
```

## Test case: handle date search without matches and invalid dates

**Aim:** Verify that date search reports no matches and rejects missing or invalid dates.

**Inputs:**
```text
on 2019-12-03
on
on Tuesday
bye
```

**Command:**
```powershell
if (Test-Path data) { Remove-Item -Recurse -Force data }; javac -d _temp\ui-test-classes src\main\java\*.java; @("on 2019-12-03", "on", "on Tuesday", "bye") | java -cp _temp\ui-test-classes ErmActually
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
 Here are the tasks occurring on 2019-12-03:
 No deadlines or events found.
____________________________________________________________
____________________________________________________________
 uhohhhh... Please provide a date in yyyy-MM-dd format.
____________________________________________________________
____________________________________________________________
 uhohhhh... Please provide a valid date in yyyy-MM-dd format.
____________________________________________________________
____________________________________________________________
Farewell! Hope you stop by again soon!
____________________________________________________________
```

## Test case: mark and unmark a task

**Aim:** Verify that marking changes a todo to complete, unmarking restores it to incomplete, and the final state is saved.

**Inputs:**
```text
todo borrow book
mark 1
unmark 1
bye
```

**Command:**
```powershell
if (Test-Path data) { Remove-Item -Recurse -Force data }; javac -d _temp\ui-test-classes src\main\java\*.java; @("todo borrow book", "mark 1", "unmark 1", "bye") | java -cp _temp\ui-test-classes ErmActually; Get-Content data\ErmActually.txt
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
V2 | T | 0 | Ym9ycm93IGJvb2s=
```

## Test case: reject empty task fields

**Aim:** Verify that empty todo, deadline, and event fields display specific chatbot errors without adding a task.

**Inputs:**
```text
todo
deadline  /by Friday
deadline submit report /by
event  /from Mon /to Tue
event meeting /from /to 4pm
event meeting /from 2pm /to
bye
```

**Command:**
```powershell
if (Test-Path data) { Remove-Item -Recurse -Force data }; javac -d _temp\ui-test-classes src\main\java\*.java; @("todo", "deadline  /by Friday", "deadline submit report /by", "event  /from Mon /to Tue", "event meeting /from /to 4pm", "event meeting /from 2pm /to", "bye") | java -cp _temp\ui-test-classes ErmActually
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
 uhohhhh... Please add a description for todo!
____________________________________________________________
____________________________________________________________
 uhohhhh... Please /by for the deadline.
____________________________________________________________
____________________________________________________________
 uhohhhh... Please add a deadline using /by.
____________________________________________________________
____________________________________________________________
 uhohhhh... Please add a description for this event!
____________________________________________________________
____________________________________________________________
 uhohhhh... Event time details cannot be empty.
____________________________________________________________
____________________________________________________________
 uhohhhh... Event time details cannot be empty.
____________________________________________________________
____________________________________________________________
Farewell! Hope you stop by again soon!
____________________________________________________________
```

## Test case: load saved tasks

**Aim:** Verify that legacy todo, deadline, and event tasks, including a valid ISO deadline and completion state, are restored when the chatbot starts.

**Inputs:**
```text
list
bye
```

**Command:**
```powershell
New-Item -ItemType Directory -Force data | Out-Null; @("T | 1 | borrow book", "D | 0 | return book | 2019-12-02", "E | 0 | project meeting | 2019-12-02T14:00 | 2019-12-02T16:00") | Set-Content data\ErmActually.txt; javac -d _temp\ui-test-classes src\main\java\*.java; @("list", "bye") | java -cp _temp\ui-test-classes ErmActually
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
 Here are the tasks in your list:
 1. [T][X] borrow book
 2. [D][ ] return book (by: Dec 02 2019)
 3. [E][ ] project meeting (from: Dec 02 2019 2:00 PM to: Dec 02 2019 4:00 PM)
____________________________________________________________
____________________________________________________________
Farewell! Hope you stop by again soon!
____________________________________________________________
```

## Test case: save special characters

**Aim:** Verify that a description containing the save-file delimiter is preserved safely.

**Inputs:**
```text
todo read | review
bye
```

**Command:**
```powershell
if (Test-Path data) { Remove-Item -Recurse -Force data }; javac -d _temp\ui-test-classes src\main\java\*.java; @("todo read | review", "bye") | java -cp _temp\ui-test-classes ErmActually; Get-Content data\ErmActually.txt
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
   [T][ ] read | review
 Wow! you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Farewell! Hope you stop by again soon!
____________________________________________________________
V2 | T | 0 | cmVhZCB8IHJldmlldw==
```

## Test case: reject invalid saved tasks

**Aim:** Verify that malformed saved data shows an error and does not partially load tasks.

**Inputs:**
```text
list
bye
```

**Command:**
```powershell
if (Test-Path data) { Remove-Item -Recurse -Force data }; New-Item -ItemType Directory -Force data | Out-Null; "X | 4 | invalid" | Set-Content data\ErmActually.txt; javac -d _temp\ui-test-classes src\main\java\*.java; @("list", "bye") | java -cp _temp\ui-test-classes ErmActually
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
 uhohhhh... I couldn't load your tasks.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
Woohoo! No tasks found!
____________________________________________________________
____________________________________________________________
Farewell! Hope you stop by again soon!
____________________________________________________________
```

## Test case: reject missing task numbers

**Aim:** Verify that task-changing commands without a number display errors and input ending without `bye` exits cleanly.

**Inputs:**
```text
mark
unmark
delete
```

**Command:**
```powershell
if (Test-Path data) { Remove-Item -Recurse -Force data }; javac -d _temp\ui-test-classes src\main\java\*.java; @("mark", "unmark", "delete") | java -cp _temp\ui-test-classes ErmActually
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
 uhohhhh... Please provide a valid task number.
____________________________________________________________
____________________________________________________________
 uhohhhh... Please provide a valid task number.
____________________________________________________________
____________________________________________________________
 uhohhhh... Please provide a valid task number.
____________________________________________________________
```
