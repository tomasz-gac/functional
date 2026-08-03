package com.tgac.functional.fibers.interpreter;

// ABOUTME: The resume handle completes its frame exactly once: a duplicate
// ABOUTME: completion refuses loudly instead of re-queuing the frame twice.

import static com.tgac.functional.category.Nothing.nothing;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.tgac.functional.fibers.Fiber;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

public class ResumeHandleTest {

	@Test
	public void refusesDuplicateCompletion() {
		Frame frame = new Frame(Fiber.done(nothing()));
		List<String> requeues = new ArrayList<>();
		ResumeHandle handle = new ResumeHandle(frame, null, () -> requeues.add("requeue"));

		handle.resume(nothing());
		assertThat(requeues).hasSize(1);

		// a second completion would step the same frame twice concurrently —
		// the handle must refuse, loudly and by name
		assertThatThrownBy(() -> handle.resume(nothing()))
				.isInstanceOf(IllegalStateException.class);
		assertThat(requeues).hasSize(1);
	}
}
