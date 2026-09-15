package ermactually.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/** Tests state and default chronological behavior shared by all tasks. */
public class TaskTest {
    @Test
    public void constructor_newTask_isIncompleteAndFormatsType() {
        Task task = new Task("read book", TaskType.TODO);

        assertFalse(task.isDone());
        assertEquals(" ", task.getStatusIcon());
        assertEquals("read book", task.getDescription());
        assertEquals("[T][ ] read book", task.toString());
        assertTrue(task.getRelevantDate().isEmpty());
        assertTrue(task.getRelevantTime().isEmpty());
        assertFalse(task.occursOn(LocalDate.of(2026, 9, 15)));
    }

    @Test
    public void markAndUnmark_updatesCompletionStateAndDisplay() {
        Task task = new Task("read book", TaskType.TODO);

        task.markAsDone();
        assertTrue(task.isDone());
        assertEquals("X", task.getStatusIcon());
        assertEquals("[T][X] read book", task.toString());

        task.unmarkAsDone();
        assertFalse(task.isDone());
        assertEquals("[T][ ] read book", task.toString());
    }

    @Test
    public void taskType_getSymbol_returnsEveryDisplaySymbol() {
        assertEquals("T", TaskType.TODO.getSymbol());
        assertEquals("D", TaskType.DEADLINE.getSymbol());
        assertEquals("E", TaskType.EVENT.getSymbol());
    }
}
