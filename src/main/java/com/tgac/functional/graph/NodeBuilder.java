package com.tgac.functional.graph;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
/**
 * @author TGa
 */
@SuppressWarnings("UnusedReturnValue")
@RequiredArgsConstructor(staticName = "of")
public class NodeBuilder<V, E> {
    private final V value;
    private final List<Tuple2<Predicate<E>, Function<E, NodeBuilder<V, E>>>> transitions = new ArrayList<>();
    private Function<E, NodeBuilder<V, E>> invalidArgumentHandler = e -> {
        throw new IllegalArgumentException("Invalid edge: " + e);
    };

    public NodeBuilder<V, E> transition(Predicate<E> condition, NodeBuilder<V, E> target) {
        transitions.add(Tuple.of(condition, ignored -> target));
        return this;
    }

    public NodeBuilder<V, E> transition(Predicate<E> condition, Function<E, NodeBuilder<V, E>> factory) {
        transitions.add(Tuple.of(condition, factory));
        return this;
    }

    public NodeBuilder<V, E> invalidArgumentHandler(Function<E, NodeBuilder<V, E>> handler) {
        invalidArgumentHandler = handler;
        return this;
    }

    public Graph.Node<V, E> build() {
        return Graph.node(value,
                edge -> transitions.stream()
                        .filter(t -> t._1.test(edge))
                        .findFirst()
                        .map(Tuple2::_2)
                        .orElse(invalidArgumentHandler)
                        .apply(edge)
                        .build());
    }


}
