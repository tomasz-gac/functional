package com.tgac.functional.recursion;

import com.tgac.functional.category.Nothing;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.Getter;

@SuppressWarnings("unchecked")
public final class TreeEngine<A> implements Engine<A> {
	private Node root;
	private final Function<Branch, Node> childSelector;

	public static <A> TreeEngine<A> branchAvoiding(Recur<A> recur) {
		return new TreeEngine<>(new Leaf(null, (Recur<Object>) recur),
				TreeEngine::branchAvoidingTraversal);
	}

	public static <A> TreeEngine<A> roundRobin(Recur<A> recur) {
		return new TreeEngine<>(new Leaf(null, (Recur<Object>) recur),
				TreeEngine::depthFirstTraversal);
	}

	private TreeEngine(Node root, Function<Branch, Node> childSelector) {
		this.root = root;
		this.childSelector = childSelector;
	}

	@Override
	public boolean run(int iterations, Consumer<? super A> sink) {
		for (int i = 0; i < iterations; i++) {
			if (step(sink))
				return true;
		}
		return root == null;
	}

	@Override
	public void run(Consumer<? super A> sink) {
		while (true) {
			if (run(Integer.MAX_VALUE, sink))
				break;
		}
	}

	@Override
	public A get() {
		AtomicReference<A> result = new AtomicReference<>();
		run(result::set);
		return result.get();
	}

	@Override
	public Optional<A> run(int iterations) {
		Object[] box = new Object[1];
		return run(iterations, v -> box[0] = v) ?
				Optional.of((A) box[0]) :
				Optional.empty();
	}

	@Override
	public boolean step(Consumer<? super A> sink) {
		if (root == null) {
			return true;
		} else if (root instanceof Leaf) {
			root = stepLeaf((Leaf) root, sink);
		} else if (root instanceof Branch) {
			root = stepBranch((Branch) root, childSelector);
		} else {
			throw new IllegalStateException("Unknown Node subclass: " + root.getClass());
		}
		return root == null;
	}

	@Override
	public void close() throws Exception {
		// empty by design
	}

	private static <A> Node stepLeaf(Leaf leaf, Consumer<? super A> sink) {
		Recur<Object> computation = leaf.computation;

		if (computation instanceof Recur.More) {
			leaf.computation = ((Recur.More<Object>) computation).getRec().get();
			return leaf;

		} else if (computation instanceof Recur.FlatMap) {
			Recur.FlatMap<Object, Object> flat = (Recur.FlatMap<Object, Object>) computation;
			leaf.fs.addLast(flat.getF());
			leaf.computation = flat.getArg();
			return leaf;

		} else if (computation instanceof Recur.Done) {
			Object value = ((Recur.Done<Object>) computation).getValue();
			if (leaf.fs.isEmpty()) {
				sink.accept((A) value);
				return null;
			}
			leaf.computation = leaf.fs.pollLast().apply(value);
			return leaf;

		} else if (computation instanceof Recur.ForEach) {
			return buildBranch(leaf);
		} else {
			throw new IllegalStateException("Unknown Recur subclass: " + computation.getClass());
		}
	}

	private static Branch buildBranch(Leaf leaf) {
		Recur.ForEach<Object> forEach = (Recur.ForEach<Object>) leaf.computation;

		Branch branch = new Branch(leaf.parent, forEach.getSink());
		branch.fs.addAll(leaf.fs);
		forEach.getOptions().stream()
				.map(recur -> new Leaf(branch, recur))
				.forEachOrdered(branch.children::add);

		if (leaf.parent == null) {
			return branch;
		} else {
			Branch parent = (Branch) leaf.parent;
			parent.replaceChild(leaf, branch);
			return branch;
		}
	}

	private static Node stepBranch(Branch branch, Function<Branch, Node> selector) {
		if (branch.children.isEmpty()) {
			return getEmptyBranchReplacement(branch);
		}
		Node current = branch;
		while (current instanceof Branch) {
			Branch b = (Branch) current;
			if (b.children.isEmpty()) {
				replaceInParent(b, getEmptyBranchReplacement(b));
				return branch;
			}
			current = selector.apply(b);
		}
		Node replacement = stepLeaf((Leaf) current, ((Branch) current.getParent()).onEach);
		replaceInParent(current, replacement);
		return branch;
	}

	private static Node branchAvoidingTraversal(Branch b) {
		return b.children.stream()
				.filter(c -> !Branch.class.isInstance(c))
				.findFirst()
				.orElseGet(() -> b.children.get(b.incrementIndexAndGet()));
	}

	private static Node depthFirstTraversal(Branch b) {
		return b.children.get(b.incrementIndexAndGet());
	}

	private static void replaceInParent(Node current, Node replacement) {
		Branch parent = (Branch) current.getParent();
		if (replacement == null) {
			parent.children.remove(current);
		} else {
			parent.replaceChild(current, replacement);
		}
	}

	private static Leaf getEmptyBranchReplacement(Branch branch) {
		return new Leaf(branch.parent, Recur.done(Nothing.nothing()), branch.fs);
	}

	private interface Node {
		Node getParent();
	}

	@AllArgsConstructor
	private static class Leaf implements Node {
		@Getter
		Node parent;
		Recur<Object> computation;
		Deque<Function<Object, Recur<Object>>> fs = new ArrayDeque<>();

		public Leaf(Node parent, Recur<Object> computation) {
			this.parent = parent;
			this.computation = computation;
		}
	}

	private static class Branch implements Node {
		@Getter
		Node parent;
		Consumer<Object> onEach;
		@Getter
		private int index = -1;
		final List<Node> children = new ArrayList<>();
		final Deque<Function<Object, Recur<Object>>> fs = new ArrayDeque<>();

		public Branch(Node parent, Consumer<Object> onEach) {
			this.parent = parent;
			this.onEach = onEach;
		}

		void replaceChild(Node oldChild, Node newChild) {
			int idx = children.indexOf(oldChild);
			if (idx != -1) {
				children.set(idx, newChild);
				if (newChild instanceof Leaf) {
					((Leaf) newChild).parent = this;
				} else if (newChild instanceof Branch) {
					((Branch) newChild).parent = this;
				}
			}
		}

		public int incrementIndexAndGet() {
			++index;
			index %= children.size();
			return index;
		}

	}
}

