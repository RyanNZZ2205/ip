package ermactually.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.Test;

import ermactually.ErmActuallyException;

/**
 * Tests task collection operations provided by {@link TaskList}.
 */
public class TaskListTest {
    @Test
    public void addGetSizeAndIsEmpty_updatesCollection() throws ErmActuallyException {
        TaskList tasks = new TaskList();
        Todo todo = new Todo("read");

        assertTrue(tasks.isEmpty());
        tasks.add(todo);

        assertFalse(tasks.isEmpty());
        assertEquals(1, tasks.size());
        assertSame(todo, tasks.get(0));
    }

    @Test
    public void markUnmarkAndDelete_updatesRequestedTask() throws ErmActuallyException {
        Todo firstTask = new Todo("first");
        Todo secondTask = new Todo("second");
        TaskList tasks = new TaskList(new ArrayList<>(List.of(firstTask, secondTask)));

        tasks.mark(1);
        assertTrue(secondTask.isDone());
        assertFalse(firstTask.isDone());

        tasks.unmark(1);
        assertFalse(secondTask.isDone());
        assertSame(firstTask, tasks.delete(0));
        assertEquals(List.of("second"), getDescriptions(tasks));
    }

    @Test
    public void indexedOperations_invalidIndex_throwIndexOutOfBoundsException()
            throws ErmActuallyException {
        TaskList tasks = new TaskList(new ArrayList<>(List.of(new Todo("only"))));

        assertThrows(IndexOutOfBoundsException.class, () -> tasks.get(1));
        assertThrows(IndexOutOfBoundsException.class, () -> tasks.mark(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> tasks.unmark(2));
        assertThrows(IndexOutOfBoundsException.class, () -> tasks.delete(1));
    }

    @Test
    public void constructorAndIterator_copyCollectionStructure() throws ErmActuallyException {
        Todo todo = new Todo("read");
        ArrayList<Task> source = new ArrayList<>(List.of(todo));
        TaskList tasks = new TaskList(source);
        source.clear();

        Iterator<Task> iterator = tasks.iterator();
        assertSame(todo, iterator.next());
        iterator.remove();

        assertEquals(1, tasks.size());
    }
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
                new Event("holiday", "2026-09-09", "2026-09-09"),
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
    public void findIndexes_emptyKeyword_matchesEveryDescription() throws ErmActuallyException {
        TaskList tasks = new TaskList(new ArrayList<>(List.of(
                new Todo("read"), new Todo("shop"))));

        assertEquals(List.of(0, 1), tasks.findIndexes(""));
    }

    @Test
    public void findIndexesOn_rangeBoundaries_returnsDatedTaskIndexesOnly()
            throws ErmActuallyException {
        TaskList tasks = new TaskList(new ArrayList<>(List.of(
                new Todo("undated"),
                new Deadline("due", "2026-09-14"),
                new Event("conference", "2026-09-13", "2026-09-15"),
                new Deadline("later", "2026-09-16"))));

        assertEquals(List.of(1, 2), tasks.findIndexesOn(LocalDate.of(2026, 9, 14)));
        assertEquals(List.of(2), tasks.findIndexesOn(LocalDate.of(2026, 9, 15)));
        assertEquals(List.of(), tasks.findIndexesOn(LocalDate.of(2026, 9, 20)));
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
