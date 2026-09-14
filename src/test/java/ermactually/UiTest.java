package ermactually;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import ermactually.task.Deadline;
import ermactually.task.Event;
import ermactually.task.SortDirection;
import ermactually.task.TaskList;
import ermactually.task.Todo;

/** Tests text formatting performed by the console and graphical interfaces. */
public class UiTest {
    private final Ui ui = new Ui();

    @Test
    public void formatTaskList_emptyAndPopulatedLists_returnsNumberedOutput()
            throws ErmActuallyException {
        assertEquals(" Here are the tasks in your list:\nWoohoo! No tasks found!",
                ui.formatTaskList(new TaskList()));

        TaskList tasks = taskList(new Todo("read"), new Deadline("submit", "2026-09-14"));
        assertEquals(" Here are the tasks in your list:\n"
                + " 1. [T][ ] read\n"
                + " 2. [D][ ] submit (by: Sep 14 2026)", ui.formatTaskList(tasks));
    }

    @Test
    public void formatSortedTaskList_eachDirection_namesDirection() throws ErmActuallyException {
        TaskList tasks = taskList(new Todo("read"));

        assertEquals(" Here are the tasks in your list, sorted in ascending order:\n"
                + " 1. [T][ ] read", ui.formatSortedTaskList(tasks, SortDirection.ASCENDING));
        assertEquals(" Here are the tasks in your list, sorted in descending order:\n"
                + " 1. [T][ ] read", ui.formatSortedTaskList(tasks, SortDirection.DESCENDING));
        assertEquals(" No tasks to sort.", ui.formatNoTasksToSort());
    }

    @Test
    public void formatMatchingTasks_matchesAndNoMatches_preservesOriginalNumbers()
            throws ErmActuallyException {
        TaskList tasks = taskList(
                new Todo("read book"), new Todo("shop"), new Todo("return book"));

        assertEquals(" Here are the matching tasks in your list:\n"
                + " 1. [T][ ] read book\n"
                + " 3. [T][ ] return book", ui.formatMatchingTasks(tasks, "book"));
        assertEquals(" Here are the matching tasks in your list:\n No matching tasks found.",
                ui.formatMatchingTasks(tasks, "movie"));
    }

    @Test
    public void formatTasksOnDate_matchesDatedTasksAndSkipsTodo() throws ErmActuallyException {
        TaskList tasks = taskList(
                new Todo("undated"),
                new Deadline("submit", "2026-09-14"),
                new Event("conference", "2026-09-13", "2026-09-15"));

        assertEquals(" Here are the tasks occurring on 2026-09-14:\n"
                + " 2. [D][ ] submit (by: Sep 14 2026)\n"
                + " 3. [E][ ] conference (from: Sep 13 2026 to: Sep 15 2026)",
                ui.formatTasksOnDate(tasks, LocalDate.of(2026, 9, 14)));
        assertEquals(" Here are the tasks occurring on 2026-09-20:\n"
                + " No deadlines or events found.",
                ui.formatTasksOnDate(tasks, LocalDate.of(2026, 9, 20)));
    }

    @Test
    public void formatActionResponses_returnsExactUserFacingText() throws ErmActuallyException {
        Todo todo = new Todo("read");
        todo.markAsDone();

        assertEquals(" Alright! I've added this new task:\n"
                + "   [T][X] read\n Wow! you have 2 tasks in the list.",
                ui.formatTaskAdded(todo, 2));
        assertEquals("oh! good job you've actually finished this task:\n [T][X] read",
                ui.formatTaskMarked(todo));
        todo.unmarkAsDone();
        assertEquals("oh? okay then I'll unmark it for you:\n  [T][ ] read",
                ui.formatTaskUnmarked(todo));
        assertEquals(" Noted. I've removed this task:\n"
                + "   [T][ ] read\n Now you have 0 tasks in the list.",
                ui.formatTaskDeleted(todo, 0));
        assertEquals(" uhohhhh... problem", ui.formatError("problem"));
        assertEquals("Farewell! Hope you stop by again soon!", ui.formatFarewell());
    }

    /** Creates a task list from the supplied tasks. */
    private TaskList taskList(ermactually.task.Task... tasks) {
        return new TaskList(new ArrayList<>(List.of(tasks)));
    }
}
