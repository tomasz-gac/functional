package com.tgac.functional.fibers;

// ABOUTME: The opaque token fibers carry to name the scope their work belongs to.
// ABOUTME: Only the runtime's Scope implements it; foreign implementations are refused.

/**
 * The scope a fiber's work belongs to, as an opaque token: {@link Fiber#detachTo}
 * accepts one, {@link Source#scope} returns one, frames carry one. The methods
 * that record work against a scope live on the runtime's own implementation,
 * package-private in {@code fibers.schedulers} — exactly-once recording holds
 * because nothing else can call them. A foreign implementation of this
 * interface is refused at frame construction.
 */
public interface WorkScope {
}
