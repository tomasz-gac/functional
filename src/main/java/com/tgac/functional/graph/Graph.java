package com.tgac.functional.graph;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * This class contains nodes and transitions that are used to build graphs.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Graph {

    /**
     * Node is a value of type V that is connected to other nodes by values of type E. Vertex transition is a function that accepts an edge and returns a node connected with it.
     *
     * @param <V>
     *         node value type
     * @param <E>
     *         edge type
     */
    @Value
    @RequiredArgsConstructor(staticName = "of")
    public static class Node<V, E> {
        V value;
        Transition<V, E> transition;

        public Node<V, E> transition(E edge) {
            return transition.apply(edge);
        }
        public Node<V, E> traverse(Stream<E> edges, BinaryOperator<Node<V, E>> merger) {
            return edges
                    .reduce(this,
                            (current, message) -> merger.apply(current, current.transition(message)),
                            (l, r) -> {
                                throw new UnsupportedOperationException();
                            });
        }
    }

    public interface Transition<V, E> extends Function<E, Node<V, E>> {

        default <E2, V2> Transition<V2, E2> map(
                Function<V, V2> nodeMapper,
                Function<E2, E> edgeMapper) {
            return e2 -> {
                Node<V, E> resolved = apply(edgeMapper.apply(e2));
                return node(
                        nodeMapper.apply(resolved.getValue()),
                        resolved.getTransition().map(nodeMapper, edgeMapper));
            };
        }

        default <E2> Transition<V, E2> mapEdge(Function<E2, E> mapper) {
            return map(Function.identity(), mapper);
        }

        default <V2> Transition<V2, E> mapNode(Function<V, V2> mapper) {
            return map(mapper, Function.identity());
        }
    }

    public static <V, E> Node<V, E> node(V value, Transition<V, E> transition) {
        return Node.of(value, transition);
    }
}