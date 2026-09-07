package ermactually.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import ermactually.ErmActuallyException;

/**
 * Tests task collection operations provided by {@link TaskList}.
 */
public class TaskListTest {
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
    public void findIndexesOn_tasksWithDifferentDateBehavior_returnsMatchingIndexes()
            throws ErmActuallyException {
        TaskList tasks = new TaskList(new ArrayList<>(List.of(
                new Todo("read book"),
                new Deadline("submit report", "2026-08-25"),
                new Event("conference", "2026-08-24", "2026-08-26"),
                new Deadline("return book", "2026-08-26"))));

        assertEquals(List.of(1, 2), tasks.findIndexesOn(LocalDate.of(2026, 8, 25)));
    }
}
