# ErmActually User Guide

ErmActually is a friendly desktop chatbot that helps you record and manage todos, deadlines, and events using short text commands.

![ErmActually graphical interface showing task commands and responses](Ui.png)

## Quick start

1. Install [Java 25](https://www.oracle.com/java/technologies/downloads/) on your computer.
2. Download `ermactually.jar` from the [latest release](https://github.com/RyanNZZ2205/ip/releases/latest).
3. Put the JAR file in the folder where you want ErmActually to keep its data.
4. Open a terminal in that folder and run:

   ```shell
   java -jar ermactually.jar
   ```

5. Enter a command in the text box, then press <kbd>Enter</kbd> or select **Send**.

ErmActually saves your tasks automatically in `data/ErmActually.txt`. Your tasks will be restored the next time you start the application from the same folder.

> [!TIP]
> Commands and their keywords, such as `todo` and `/by`, should be typed in lowercase.

## Command format

- Words in `UPPER_CASE` are values you provide. Do not type the surrounding angle brackets.
- Items in square brackets are optional.
- Dates use `yyyy-MM-dd`, for example `2026-09-15`.
- Times use the 24-hour `HHmm` format, for example `0900` or `1730`.
- Task numbers come from `list` and can change after deleting or sorting tasks.

## Features

### Add a todo: `todo`

Use a todo for a task without a date or time.

```text
todo <DESCRIPTION>
```

Example: `todo borrow a book`

### Add a deadline: `deadline`

Use a deadline for a task that must be completed by a date. Adding a time is optional.

```text
deadline <DESCRIPTION> /by <DATE> [TIME]
```

Examples:

- `deadline submit report /by 2026-09-15`
- `deadline submit report /by 2026-09-15 1730`

### Add an event: `event`

Use an event for something with a start and an end. You can enter dates only or include times for both endpoints.

```text
event <DESCRIPTION> /from <DATE> [TIME] /to <DATE> [TIME]
```

Examples:

- `event holiday /from 2026-09-15 /to 2026-09-18`
- `event project meeting /from 2026-09-15 1400 /to 2026-09-15 1600`

The end must be after the start. For an event that starts and ends on the same date, provide times for both endpoints or for neither endpoint.

### View all tasks: `list`

```text
list
```

Each task is displayed with a number and status:

- `[T]`, `[D]`, and `[E]` mean todo, deadline, and event.
- `[X]` means completed; `[ ]` means incomplete.

### Mark or unmark a task

Use the task number shown by `list`.

```text
mark <TASK_NUMBER>
unmark <TASK_NUMBER>
```

Examples: `mark 2` and `unmark 2`

### Delete a task: `delete`

```text
delete <TASK_NUMBER>
```

Example: `delete 3`

### Find tasks by description: `find`

Searches are case-insensitive and match the keyword anywhere in a description.

```text
find <KEYWORD>
```

Example: `find book`

### Find tasks on a date: `on`

This finds deadlines on the given date and events that include the date. Event start and end dates are included.

```text
on <DATE>
```

Example: `on 2026-09-15`

### Sort tasks chronologically: `sort`

```text
sort [asc|desc]
```

- `sort` and `sort asc` put earlier dated tasks first.
- `sort desc` puts later dated tasks first.
- Date-only tasks come before timed tasks on the same date.
- Todos remain after dated tasks.
- Tasks with the same date and time keep their relative order.

Sorting immediately saves the new order and changes the task numbers used by `mark`, `unmark`, and `delete`.

### Exit ErmActually: `bye`

```text
bye
```

ErmActually displays a farewell message. In the desktop interface, close the window when you are finished.

## Command summary

| Purpose | Command |
| --- | --- |
| Add a todo | `todo <DESCRIPTION>` |
| Add a deadline | `deadline <DESCRIPTION> /by <DATE> [TIME]` |
| Add an event | `event <DESCRIPTION> /from <DATE> [TIME] /to <DATE> [TIME]` |
| View all tasks | `list` |
| Mark a task complete | `mark <TASK_NUMBER>` |
| Mark a task incomplete | `unmark <TASK_NUMBER>` |
| Delete a task | `delete <TASK_NUMBER>` |
| Find by description | `find <KEYWORD>` |
| Find by date | `on <DATE>` |
| Sort tasks | `sort [asc|desc]` |
| Say goodbye | `bye` |

If ErmActually rejects a command, check its spelling, required separators, date format, and task number before trying again.
