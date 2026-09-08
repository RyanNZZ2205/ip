# ErmActually User Guide

// Update the title above to match the actual product name

// Product screenshot goes here

// Product intro goes here

## Sorting tasks chronologically

Use `sort [asc|desc]` to reorder and immediately save the current task list. `sort` defaults to
ascending order and is equivalent to `sort asc`. Spaces and tabs may separate `sort` from its
optional direction.

- Deadlines are ordered by their due date and optional due time.
- Events are ordered by their start date and optional start time. Their end values do not affect sorting,
  even when the start and end use different date/time precision.
- Date-only tasks appear before timed tasks on the same date in both directions.
- Todos appear after every dated task in both directions.
- Tasks with equal sort values retain their existing relative order. Completion status does not affect sorting.

For example, `sort desc` places later dates before earlier dates and displays the complete, newly numbered list:

```text
 Here are the tasks in your list, sorted in descending order:
 1. [E][ ] conference (from: Sep 10 2026 9:00 AM to: Sep 10 2026 5:00 PM)
 2. [D][ ] pay bill (by: Sep 09 2026)
 3. [T][ ] buy milk
```

Sorting changes the task numbers used by commands such as `mark`, `unmark`, and `delete`, and the new order is
restored after restarting the application. Sorting is a one-time operation: tasks added later are appended until
you run `sort` again. A successful sort of any non-empty list is saved, even when its order does not change; this
also converts a loaded legacy save file to the current V2 format.

An empty list returns ` No tasks to sort.`. Unsupported values and extra arguments, such as `sort ASC`,
`sort date`, or `sort asc desc`, return ` uhohhhh... Please use: sort [asc|desc].` and do not change the list.

## Adding deadlines

// Describe the action and its outcome.

// Give examples of usage

Example: `keyword (optional arguments)`

// A description of the expected outcome goes here

```
expected output
```

## Feature ABC

// Feature details


## Feature XYZ

// Feature details
