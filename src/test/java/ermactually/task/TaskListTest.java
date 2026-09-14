package ermactually.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import ermactually.ErmActuallyException;

/**
 * Tests task collection operations provided by {@link TaskList}.
 */
public class TaskListTest {
    @Test
    public void createChronologicallySorted_mixedTasksAscending_ordersByDateAndTimeStably()
            throws ErmActuallyException {
        Todo firstTodo = new Todo("buy milk");
        Deadline equalTimedDeadline = new Deadline("submit report", "2026-09-09 1700");
        equalTimedDeadline.markAsDone();
        TaskList tasks = new TaskList(new ArrayList<>(List.of(
                firstTodo,
                new Event("conference", "2026-09-10 0900", "2026-09-10 1700"),
                equalTimedDeadline,
                new Deadline("pay bill", "2026-09-09"),
                new Event("holiday", "2026-09-09", "2026-09-10"),
                new Event("same-time event", "2026-09-09 1700", "2026-09-12"),
                new Deadline("morning task", "2026-09-09 0900"),
                new Todo("call Alex"))));

        TaskList sortedTasks = tasks.createChronologicallySorted(SortDirection.ASCENDING);

        assertEquals(List.of(
                "pay bill", "holiday", "morning task", "submit report",
                "same-time event", "conference", "buy milk", "call Alex"),
                getDescriptions(sortedTasks));
        assertSame(equalTimedDeadline, sortedTasks.get(3));
    }

    @Test
    public void createChronologicallySorted_mixedTasksDescending_keepsDateOnlyAndTodosInFixedGroups()
            throws ErmActuallyException {
        TaskList tasks = new TaskList(new ArrayList<>(List.of(
                new Todo("first todo"),
                new Deadline("morning task", "2026-09-09 0900"),
                new Event("date-only event", "2026-09-09", "2026-09-10 1200"),
                new Deadline("evening task", "2026-09-09 1700"),
                new Deadline("date-only deadline", "2026-09-09"),
                new Event("later event", "2026-09-10", "2026-09-30"),
                new Todo("second todo"))));

        TaskList sortedTasks = tasks.createChronologicallySorted(SortDirection.DESCENDING);

        assertEquals(List.of(
                "later event", "date-only event", "date-only deadline", "evening task",
                "morning task", "first todo", "second todo"), getDescriptions(sortedTasks));
    }

    @Test
    public void createChronologicallySorted_unsortedTasks_doesNotMutateOriginalList()
            throws ErmActuallyException {
        TaskList tasks = new TaskList(new ArrayList<>(List.of(
                new Todo("buy milk"),
                new Deadline("submit report", "2026-09-09"))));

        TaskList sortedTasks = tasks.createChronologicallySorted(SortDirection.ASCENDING);

        assertEquals(List.of("buy milk", "submit report"), getDescriptions(tasks));
        assertEquals(List.of("submit report", "buy milk"), getDescriptions(sortedTasks));
    }

    @Test
    public void createChronologicallySorted_emptyAndOneTaskLists_preservesContents()
            throws ErmActuallyException {
        TaskList emptyTasks = new TaskList();
        TaskList oneTask = new TaskList(new ArrayList<>(List.of(new Todo("buy milk"))));

        assertEquals(List.of(), getDescriptions(
                emptyTasks.createChronologicallySorted(SortDirection.ASCENDING)));
        assertEquals(List.of("buy milk"), getDescriptions(
                oneTask.createChronologicallySorted(SortDirection.DESCENDING)));
    }

    @Test
    public void findIndexes_matchingDescriptions_returnsOriginalIndexesInOrder()
            throws ErmActuallyException {
        TaskList tasks = new TaskList(new ArrayList<>(List.of(
                new Todo("read book"),
                new Todo("buy groceries"),
                new Deadline("return book", "2026-06-06"))));

        assertEquals(List.of(0, 2), tasks.findIndexes("book"));
    }

    @Test
    public void findIndexes_differentLetterCase_returnsMatch() throws ErmActuallyException {
        TaskList tasks = new TaskList(new ArrayList<>(List.of(new Todo("Read Book"))));

        assertEquals(List.of(0), tasks.findIndexes("book"));
    }

    @Test
    public void findIndexes_noMatchingDescriptions_returnsEmptyList() throws ErmActuallyException {
        TaskList tasks = new TaskList(new ArrayList<>(List.of(new Todo("buy groceries"))));

        assertEquals(List.of(), tasks.findIndexes("book"));
    }

    @Test
    public void add_duplicateTask_exceptionThrownAndListUnchanged() throws ErmActuallyException {
        TaskList tasks = new TaskList();
        tasks.add(new Deadline("submit report", "2026-09-15 1700"));

        ErmActuallyException exception = assertThrows(
                ErmActuallyException.class, () ->
                        tasks.add(new Deadline("submit report", "2026-09-15T17:00")));

        assertEquals("That task already exists.", exception.getMessage());
        assertEquals(1, tasks.size());
    }

    /** Returns task descriptions in their current list order. */
    private List<String> getDescriptions(TaskList tasks) {
        ArrayList<String> descriptions = new ArrayList<>();
        for (Task task : tasks) {
            descriptions.add(task.getDescription());
        }
        return descriptions;
    }
}
