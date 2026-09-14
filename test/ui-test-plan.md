# Console UI Test Plan

Record all console user-interface test cases here. Run them with the project-local `$test-ui` skill.

## Test environment

- Run commands from the repository root.
- Use Java 25 for Java build and run commands.
- Compare complete standard output. Line endings and one final trailing newline are normalized; all other whitespace is significant.

## Manual GUI test matrix

The JavaFX interface should be checked manually because its rendering depends on the operating system,
display server, fonts, and scaling settings. For each available environment, launch the application,
add each task type, mark and unmark a task, delete a task, search, sort, and restart to verify persistence.

- Operating systems: Windows 11, macOS, and a common Linux desktop distribution.
- Display sizes: 1024×768 and 1920×1080 or larger.
- Display scaling: 100%, 150%, and 200% where supported.
- OS display languages: English and Simplified Chinese.
- Visual checks: startup messages, speaker images, response colors, text wrapping, scrollbar behavior,
  keyboard submission, cleared input, minimum window size, and readable task dates and times.
- Error checks: malformed commands and a malformed save file should produce a visible chatbot error without
  freezing or closing the window.

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
$testData = '_temp\ui-test-data\01.txt'; if (Test-Path -LiteralPath $testData) { Remove-Item -LiteralPath $testData -Force }; $cliSources = Get-ChildItem src\main\java -Recurse -Filter *.java | Where-Object { $_.Name -notin @('DialogBox.java', 'Launcher.java', 'Main.java', 'MainWindow.java') }; javac -d _temp\ui-test-classes $cliSources.FullName; @("todo borrow book", "list", "bye") | java -cp _temp\ui-test-classes ermactually.ErmActually $testData; Get-Content -LiteralPath $testData
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
 here you go! your task list:
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
$testData = '_temp\ui-test-data\02.txt'; if (Test-Path -LiteralPath $testData) { Remove-Item -LiteralPath $testData -Force }; $cliSources = Get-ChildItem src\main\java -Recurse -Filter *.java | Where-Object { $_.Name -notin @('DialogBox.java', 'Launcher.java', 'Main.java', 'MainWindow.java') }; javac -d _temp\ui-test-classes $cliSources.FullName; @("deadline return book /by 2019-12-02", "bye") | java -cp _temp\ui-test-classes ermactually.ErmActually $testData; Get-Content -LiteralPath $testData
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
$testData = '_temp\ui-test-data\03.txt'; if (Test-Path -LiteralPath $testData) { Remove-Item -LiteralPath $testData -Force }; $cliSources = Get-ChildItem src\main\java -Recurse -Filter *.java | Where-Object { $_.Name -notin @('DialogBox.java', 'Launcher.java', 'Main.java', 'MainWindow.java') }; javac -d _temp\ui-test-classes $cliSources.FullName; @("deadline submit report /by 2026-08-25 1900", "bye") | java -cp _temp\ui-test-classes ermactually.ErmActually $testData; Get-Content -LiteralPath $testData
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
$testData = '_temp\ui-test-data\04.txt'; if (Test-Path -LiteralPath $testData) { Remove-Item -LiteralPath $testData -Force }; $cliSources = Get-ChildItem src\main\java -Recurse -Filter *.java | Where-Object { $_.Name -notin @('DialogBox.java', 'Launcher.java', 'Main.java', 'MainWindow.java') }; javac -d _temp\ui-test-classes $cliSources.FullName; @("deadline return book /by 2/12/2019", "bye") | java -cp _temp\ui-test-classes ermactually.ErmActually $testData
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
 uhohhhh... the deadline's format is actually in yyyy-MM-dd or yyyy-MM-dd HHmm!
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
$testData = '_temp\ui-test-data\05.txt'; if (Test-Path -LiteralPath $testData) { Remove-Item -LiteralPath $testData -Force }; $cliSources = Get-ChildItem src\main\java -Recurse -Filter *.java | Where-Object { $_.Name -notin @('DialogBox.java', 'Launcher.java', 'Main.java', 'MainWindow.java') }; javac -d _temp\ui-test-classes $cliSources.FullName; @("event project meeting /from 2019-12-02 1400 /to 2019-12-02 1600", "bye") | java -cp _temp\ui-test-classes ermactually.ErmActually $testData
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
event holiday /from 2026-08-25 /to 2026-08-26
bye
```

**Command:**
```powershell
$testData = '_temp\ui-test-data\06.txt'; if (Test-Path -LiteralPath $testData) { Remove-Item -LiteralPath $testData -Force }; $cliSources = Get-ChildItem src\main\java -Recurse -Filter *.java | Where-Object { $_.Name -notin @('DialogBox.java', 'Launcher.java', 'Main.java', 'MainWindow.java') }; javac -d _temp\ui-test-classes $cliSources.FullName; @("event holiday /from 2026-08-25 /to 2026-08-26", "bye") | java -cp _temp\ui-test-classes ermactually.ErmActually $testData; Get-Content -LiteralPath $testData
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
   [E][ ] holiday (from: Aug 25 2026 to: Aug 26 2026)
 Wow! you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Farewell! Hope you stop by again soon!
____________________________________________________________
V2 | E | 0 | aG9saWRheQ== | MjAyNi0wOC0yNQ== | MjAyNi0wOC0yNg==
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
$testData = '_temp\ui-test-data\07.txt'; if (Test-Path -LiteralPath $testData) { Remove-Item -LiteralPath $testData -Force }; $cliSources = Get-ChildItem src\main\java -Recurse -Filter *.java | Where-Object { $_.Name -notin @('DialogBox.java', 'Launcher.java', 'Main.java', 'MainWindow.java') }; javac -d _temp\ui-test-classes $cliSources.FullName; @("todo borrow book", "deadline submit report /by 2019-12-03", "event conference /from 2019-12-02 0900 /to 2019-12-04 1700", "on 2019-12-03", "bye") | java -cp _temp\ui-test-classes ermactually.ErmActually $testData
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

## Test case: find tasks by description keyword

**Aim:** Verify that keyword search is case-insensitive, includes every task type, preserves original task numbers, reports no matches, and rejects a missing keyword.

**Inputs:**
```text
todo Read Book
todo buy groceries
deadline return book /by 2026-06-06
find book
find movie
find
bye
```

**Command:**
```powershell
$testData = '_temp\ui-test-data\08.txt'; if (Test-Path -LiteralPath $testData) { Remove-Item -LiteralPath $testData -Force }; $cliSources = Get-ChildItem src\main\java -Recurse -Filter *.java | Where-Object { $_.Name -notin @('DialogBox.java', 'Launcher.java', 'Main.java', 'MainWindow.java') }; javac -d _temp\ui-test-classes $cliSources.FullName; @("todo Read Book", "todo buy groceries", "deadline return book /by 2026-06-06", "find book", "find movie", "find", "bye") | java -cp _temp\ui-test-classes ermactually.ErmActually $testData
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
   [T][ ] Read Book
 Wow! you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Alright! I've added this new task:
   [T][ ] buy groceries
 Wow! you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Alright! I've added this new task:
   [D][ ] return book (by: Jun 06 2026)
 Wow! you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the matching tasks in your list:
 1. [T][ ] Read Book
 3. [D][ ] return book (by: Jun 06 2026)
____________________________________________________________
____________________________________________________________
 Here are the matching tasks in your list:
 No matching tasks found.
____________________________________________________________
____________________________________________________________
 uhohhhh... actually you have to give me a keyword to find!
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
$testData = '_temp\ui-test-data\09.txt'; if (Test-Path -LiteralPath $testData) { Remove-Item -LiteralPath $testData -Force }; $cliSources = Get-ChildItem src\main\java -Recurse -Filter *.java | Where-Object { $_.Name -notin @('DialogBox.java', 'Launcher.java', 'Main.java', 'MainWindow.java') }; javac -d _temp\ui-test-classes $cliSources.FullName; @("on 2019-12-03", "on", "on Tuesday", "bye") | java -cp _temp\ui-test-classes ermactually.ErmActually $testData
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
 I didn't find any deadlines or events!
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
$testData = '_temp\ui-test-data\10.txt'; if (Test-Path -LiteralPath $testData) { Remove-Item -LiteralPath $testData -Force }; $cliSources = Get-ChildItem src\main\java -Recurse -Filter *.java | Where-Object { $_.Name -notin @('DialogBox.java', 'Launcher.java', 'Main.java', 'MainWindow.java') }; javac -d _temp\ui-test-classes $cliSources.FullName; @("todo borrow book", "mark 1", "unmark 1", "bye") | java -cp _temp\ui-test-classes ermactually.ErmActually $testData; Get-Content -LiteralPath $testData
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

**Aim:** Verify that empty fields and missing task delimiters display specific chatbot errors without adding a task.

**Inputs:**
```text
todo
deadline /by 2026-08-25
deadline submit report
deadline submit report /by
event  /from Mon /to Tue
event meeting /to 2026-08-26
event meeting /from Mon
event meeting /from /to 4pm
event meeting /from 2026-08-25 /to
bye
```

**Command:**
```powershell
$testData = '_temp\ui-test-data\11.txt'; if (Test-Path -LiteralPath $testData) { Remove-Item -LiteralPath $testData -Force }; $cliSources = Get-ChildItem src\main\java -Recurse -Filter *.java | Where-Object { $_.Name -notin @('DialogBox.java', 'Launcher.java', 'Main.java', 'MainWindow.java') }; javac -d _temp\ui-test-classes $cliSources.FullName; @("todo", "deadline /by 2026-08-25", "deadline submit report", "deadline submit report /by", "event  /from Mon /to Tue", "event meeting /to 2026-08-26", "event meeting /from Mon", "event meeting /from /to 4pm", "event meeting /from 2026-08-25 /to", "bye") | java -cp _temp\ui-test-classes ermactually.ErmActually $testData
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
 uhohhhh... Please add a description of the todo!
____________________________________________________________
____________________________________________________________
 uhohhhh... please add in a description for the deadline!
____________________________________________________________
____________________________________________________________
 uhohhhh... please add in a deadline! its actually using /by
____________________________________________________________
____________________________________________________________
 uhohhhh... please add in a deadline! its actually using /by
____________________________________________________________
____________________________________________________________
 uhohhhh... Please add in a description of the event!
____________________________________________________________
____________________________________________________________
 uhohhhh... please add in a starting date/time! its actually using /from
____________________________________________________________
____________________________________________________________
 uhohhhh... please add in an ending date/time! its actually using /to
____________________________________________________________
____________________________________________________________
 uhohhhh... The event start cannot be empty.
____________________________________________________________
____________________________________________________________
 uhohhhh... The event end cannot be empty.
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
$testData = '_temp\ui-test-data\12.txt'; New-Item -ItemType Directory -Force (Split-Path $testData) | Out-Null; @("T | 1 | borrow book", "D | 0 | return book | 2019-12-02", "E | 0 | project meeting | 2019-12-02T14:00 | 2019-12-02T16:00") | Set-Content -LiteralPath $testData; $cliSources = Get-ChildItem src\main\java -Recurse -Filter *.java | Where-Object { $_.Name -notin @('DialogBox.java', 'Launcher.java', 'Main.java', 'MainWindow.java') }; javac -d _temp\ui-test-classes $cliSources.FullName; @("list", "bye") | java -cp _temp\ui-test-classes ermactually.ErmActually $testData
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
 here you go! your task list:
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
$testData = '_temp\ui-test-data\13.txt'; if (Test-Path -LiteralPath $testData) { Remove-Item -LiteralPath $testData -Force }; $cliSources = Get-ChildItem src\main\java -Recurse -Filter *.java | Where-Object { $_.Name -notin @('DialogBox.java', 'Launcher.java', 'Main.java', 'MainWindow.java') }; javac -d _temp\ui-test-classes $cliSources.FullName; @("todo read | review", "bye") | java -cp _temp\ui-test-classes ermactually.ErmActually $testData; Get-Content -LiteralPath $testData
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
$testData = '_temp\ui-test-data\14.txt'; New-Item -ItemType Directory -Force (Split-Path $testData) | Out-Null; "X | 4 | invalid" | Set-Content -LiteralPath $testData; $cliSources = Get-ChildItem src\main\java -Recurse -Filter *.java | Where-Object { $_.Name -notin @('DialogBox.java', 'Launcher.java', 'Main.java', 'MainWindow.java') }; javac -d _temp\ui-test-classes $cliSources.FullName; @("list", "bye") | java -cp _temp\ui-test-classes ermactually.ErmActually $testData
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
 here you go! your task list:
Woohoo! No tasks found!
____________________________________________________________
____________________________________________________________
Farewell! Hope you stop by again soon!
____________________________________________________________
```

## Test case: reject missing task numbers

**Aim:** Verify that missing and nonexistent task numbers display their specific errors and input ending without `bye`
exits cleanly.

**Inputs:**
```text
mark
unmark
delete
delete 1
```

**Command:**
```powershell
$testData = '_temp\ui-test-data\15.txt'; if (Test-Path -LiteralPath $testData) { Remove-Item -LiteralPath $testData -Force }; $cliSources = Get-ChildItem src\main\java -Recurse -Filter *.java | Where-Object { $_.Name -notin @('DialogBox.java', 'Launcher.java', 'Main.java', 'MainWindow.java') }; javac -d _temp\ui-test-classes $cliSources.FullName; @("mark", "unmark", "delete", "delete 1") | java -cp _temp\ui-test-classes ermactually.ErmActually $testData
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
 uhohhhh... actually you need to give me a valid task number!
____________________________________________________________
____________________________________________________________
 uhohhhh... actually you need to give me a valid task number!
____________________________________________________________
____________________________________________________________
 uhohhhh... actually you need to give me a valid task number!
____________________________________________________________
____________________________________________________________
 uhohhhh... actually that task number doesn't exist!
____________________________________________________________
```

## Test case: sort mixed tasks ascending

**Aim:** Verify that the default ascending sort interleaves dated tasks, keeps date-only tasks before timed tasks, places todos last, updates `list`, and saves V2 lines in sorted order.

**Inputs:**
```text
sort
list
bye
```

**Command:**
```powershell
$testData = '_temp\ui-test-data\16.txt'; New-Item -ItemType Directory -Force (Split-Path $testData) | Out-Null; @('T | 0 | buy milk', 'E | 0 | conference | 2026-09-10T09:00 | 2026-09-10T17:00', 'D | 1 | submit report | 2026-09-09T17:00', 'D | 0 | pay bill | 2026-09-09', 'E | 0 | holiday | 2026-09-09 | 2026-09-10') | Set-Content -LiteralPath $testData; $cliSources = Get-ChildItem src\main\java -Recurse -Filter *.java | Where-Object { $_.Name -notin @('DialogBox.java', 'Launcher.java', 'Main.java', 'MainWindow.java') }; javac -d _temp\ui-test-classes $cliSources.FullName; @('sort', 'list', 'bye') | java -cp _temp\ui-test-classes ermactually.ErmActually $testData; Get-Content -LiteralPath $testData
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
 Here are the tasks in your list, sorted in ascending order:
 1. [D][ ] pay bill (by: Sep 09 2026)
 2. [E][ ] holiday (from: Sep 09 2026 to: Sep 10 2026)
 3. [D][X] submit report (by: Sep 09 2026 5:00 PM)
 4. [E][ ] conference (from: Sep 10 2026 9:00 AM to: Sep 10 2026 5:00 PM)
 5. [T][ ] buy milk
____________________________________________________________
____________________________________________________________
 here you go! your task list:
 1. [D][ ] pay bill (by: Sep 09 2026)
 2. [E][ ] holiday (from: Sep 09 2026 to: Sep 10 2026)
 3. [D][X] submit report (by: Sep 09 2026 5:00 PM)
 4. [E][ ] conference (from: Sep 10 2026 9:00 AM to: Sep 10 2026 5:00 PM)
 5. [T][ ] buy milk
____________________________________________________________
____________________________________________________________
Farewell! Hope you stop by again soon!
____________________________________________________________
V2 | D | 0 | cGF5IGJpbGw= | MjAyNi0wOS0wOQ==
V2 | E | 0 | aG9saWRheQ== | MjAyNi0wOS0wOQ== | MjAyNi0wOS0xMA==
V2 | D | 1 | c3VibWl0IHJlcG9ydA== | MjAyNi0wOS0wOVQxNzowMA==
V2 | E | 0 | Y29uZmVyZW5jZQ== | MjAyNi0wOS0xMFQwOTowMA== | MjAyNi0wOS0xMFQxNzowMA==
V2 | T | 0 | YnV5IG1pbGs=
```

## Test case: sort mixed tasks descending

**Aim:** Verify that descending sort reverses dates and timed values while keeping date-only tasks before timed tasks and todos last.

**Inputs:**
```text
sort desc
bye
```

**Command:**
```powershell
$testData = '_temp\ui-test-data\17.txt'; New-Item -ItemType Directory -Force (Split-Path $testData) | Out-Null; @('T | 0 | buy milk', 'E | 0 | conference | 2026-09-10T09:00 | 2026-09-10T17:00', 'D | 1 | submit report | 2026-09-09T17:00', 'D | 0 | pay bill | 2026-09-09', 'E | 0 | holiday | 2026-09-09 | 2026-09-10') | Set-Content -LiteralPath $testData; $cliSources = Get-ChildItem src\main\java -Recurse -Filter *.java | Where-Object { $_.Name -notin @('DialogBox.java', 'Launcher.java', 'Main.java', 'MainWindow.java') }; javac -d _temp\ui-test-classes $cliSources.FullName; @('sort desc', 'bye') | java -cp _temp\ui-test-classes ermactually.ErmActually $testData
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
 Here are the tasks in your list, sorted in descending order:
 1. [E][ ] conference (from: Sep 10 2026 9:00 AM to: Sep 10 2026 5:00 PM)
 2. [D][ ] pay bill (by: Sep 09 2026)
 3. [E][ ] holiday (from: Sep 09 2026 to: Sep 10 2026)
 4. [D][X] submit report (by: Sep 09 2026 5:00 PM)
 5. [T][ ] buy milk
____________________________________________________________
____________________________________________________________
Farewell! Hope you stop by again soon!
____________________________________________________________
```

## Test case: sort an empty list

**Aim:** Verify that sorting an empty list returns its dedicated successful response without creating a save file.

**Inputs:**
```text
sort
bye
```

**Command:**
```powershell
$testData = '_temp\ui-test-data\18.txt'; if (Test-Path -LiteralPath $testData) { Remove-Item -LiteralPath $testData -Force }; $cliSources = Get-ChildItem src\main\java -Recurse -Filter *.java | Where-Object { $_.Name -notin @('DialogBox.java', 'Launcher.java', 'Main.java', 'MainWindow.java') }; javac -d _temp\ui-test-classes $cliSources.FullName; @('sort', 'bye') | java -cp _temp\ui-test-classes ermactually.ErmActually $testData
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
 actually, there is nothing to sort!
____________________________________________________________
____________________________________________________________
Farewell! Hope you stop by again soon!
____________________________________________________________
```

## Test case: reject invalid sort syntax

**Aim:** Verify that extra sort arguments return the usage error without changing the in-memory order or legacy save file.

**Inputs:**
```text
sort asc desc
list
bye
```

**Command:**
```powershell
$testData = '_temp\ui-test-data\19.txt'; New-Item -ItemType Directory -Force (Split-Path $testData) | Out-Null; @('T | 0 | buy milk', 'D | 0 | submit report | 2026-09-09') | Set-Content -LiteralPath $testData; $cliSources = Get-ChildItem src\main\java -Recurse -Filter *.java | Where-Object { $_.Name -notin @('DialogBox.java', 'Launcher.java', 'Main.java', 'MainWindow.java') }; javac -d _temp\ui-test-classes $cliSources.FullName; @('sort asc desc', 'list', 'bye') | java -cp _temp\ui-test-classes ermactually.ErmActually $testData; Get-Content -LiteralPath $testData
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
 uhohhhh... Please use: sort [asc|desc].
____________________________________________________________
____________________________________________________________
 here you go! your task list:
 1. [T][ ] buy milk
 2. [D][ ] submit report (by: Sep 09 2026)
____________________________________________________________
____________________________________________________________
Farewell! Hope you stop by again soon!
____________________________________________________________
T | 0 | buy milk
D | 0 | submit report | 2026-09-09
```

## Test case: reject malformed and contradictory input

**Aim:** Verify flexible command whitespace, duplicate-task rejection, duplicate parameter rejection,
invalid event ordering, nonexistent dates, and tab-separated task numbers.

**Inputs:**
```text
<tab> todo<tab>borrow book <tab>
todo   borrow book
deadline report /by 2026-09-15 /by 2026-09-16
event meeting /from 2026-09-15 0900 /to 2026-09-15 0900
event trip /from 2026-02-30 /to 2026-03-01
mark<tab>1
bye
```

**Command:**
```powershell
$testData = '_temp\ui-test-data\20.txt'; if (Test-Path -LiteralPath $testData) { Remove-Item -LiteralPath $testData -Force }; $cliSources = Get-ChildItem src\main\java -Recurse -Filter *.java | Where-Object { $_.Name -notin @('DialogBox.java', 'Launcher.java', 'Main.java', 'MainWindow.java') }; javac -d _temp\ui-test-classes $cliSources.FullName; @("`t todo`tborrow book `t", "todo   borrow book", "deadline report /by 2026-09-15 /by 2026-09-16", "event meeting /from 2026-09-15 0900 /to 2026-09-15 0900", "event trip /from 2026-02-30 /to 2026-03-01", "mark`t1", "bye") | java -cp _temp\ui-test-classes ermactually.ErmActually $testData
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
 uhohhhh... That task already exists.
____________________________________________________________
____________________________________________________________
 uhohhhh... Please use exactly one /by for the deadline.
____________________________________________________________
____________________________________________________________
 uhohhhh... how can the event end before it starts?
____________________________________________________________
____________________________________________________________
 uhohhhh... actually the format of start is in yyyy-MM-dd or yyyy-MM-dd HHmm!
____________________________________________________________
____________________________________________________________
oh! good job you've actually finished this task:
 [T][X] borrow book
____________________________________________________________
____________________________________________________________
Farewell! Hope you stop by again soon!
____________________________________________________________
```

## Test case: reject duplicate saved tasks

**Aim:** Verify that duplicate task details in the data file are treated as corrupted data and are not partially loaded.

**Inputs:**
```text
list
bye
```

**Command:**
```powershell
$testData = '_temp\ui-test-data\21.txt'; New-Item -ItemType Directory -Force (Split-Path $testData) | Out-Null; @('T | 0 | borrow book', 'T | 1 | borrow book') | Set-Content -LiteralPath $testData; $cliSources = Get-ChildItem src\main\java -Recurse -Filter *.java | Where-Object { $_.Name -notin @('DialogBox.java', 'Launcher.java', 'Main.java', 'MainWindow.java') }; javac -d _temp\ui-test-classes $cliSources.FullName; @('list', 'bye') | java -cp _temp\ui-test-classes ermactually.ErmActually $testData
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
 here you go! your task list:
Woohoo! No tasks found!
____________________________________________________________
____________________________________________________________
Farewell! Hope you stop by again soon!
____________________________________________________________
```
