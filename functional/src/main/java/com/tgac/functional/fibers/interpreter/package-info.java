// ABOUTME: The fiber interpreter: one step dispatch, billing by construction,
// ABOUTME: workforces sealed at quiescence, channels folded and awaited.

/**
 * The substrate under every scheduler: {@link com.tgac.functional.fibers.interpreter.Frame}
 * is the single step interpreter (movers continue, parkers yield);
 * {@link com.tgac.functional.fibers.interpreter.Scope} is a workforce — counted
 * work, blocked records, the seal at quiescence;
 * {@link com.tgac.functional.fibers.interpreter.Channel} is a channel closed
 * by a workforce; {@link com.tgac.functional.fibers.interpreter.ResumeHandle} and
 * {@link com.tgac.functional.fibers.interpreter.AwaitBoundary} carry suspended
 * frames back to their queues. Schedulers own queues and granularity, nothing
 * else — every semantic lives here (docs/design/emit.md, await.md, completion.md).
 */
package com.tgac.functional.fibers.interpreter;
