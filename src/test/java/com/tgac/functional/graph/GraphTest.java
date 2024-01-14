package com.tgac.functional.graph;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;
public class GraphTest {

	enum Vertices {
		A, B,
		C, D
	}

	enum Edges {
		forward, across, backwards
	}

	Graph.Node<Vertices, Edges> vertexA() {
		return Graph.node(
				Vertices.A,
				e -> {
					switch (e) {
						case forward:
							return vertexB();
						case backwards:
							return vertexC();
						case across:
							return vertexD();
						default:
							throw new IllegalArgumentException();
					}
				});
	}

	private Graph.Node<Vertices, Edges> vertexB() {
		return Graph.node(
				Vertices.B,
				e -> {
					switch (e) {
						case forward:
							return vertexD();
						case backwards:
							return vertexA();
						case across:
							return vertexC();
						default:
							throw new IllegalArgumentException();
					}
				});
	}

	private Graph.Node<Vertices, Edges> vertexC() {
		return Graph.node(
				Vertices.C,
				e -> {
					switch (e) {
						case forward:
							return vertexA();
						case backwards:
							return vertexD();
						case across:
							return vertexB();
						default:
							throw new IllegalArgumentException();
					}
				});
	}

	private Graph.Node<Vertices, Edges> vertexD() {
		return Graph.node(
				Vertices.D,
				e -> {
					switch (e) {
						case forward:
							return vertexC();
						case backwards:
							return vertexB();
						case across:
							return vertexA();
						default:
							throw new IllegalArgumentException();
					}
				});
	}

	@Test
	public void shouldTraverseGraph() {
		Assertions.assertThat(
						vertexA().traverse(
										Stream.of(
												Edges.forward,
												Edges.forward,
												Edges.forward,
												Edges.forward),
										(l, r) -> r)
								.getValue())
				.isEqualTo(Vertices.A);
	}

	@Test
	public void shouldTraverseGraph2() {
		Assertions.assertThat(
						vertexA().traverse(
										Stream.of(
												Edges.forward,
												Edges.across,
												Edges.backwards),
										(l, r) -> r)
								.getValue())
				.isEqualTo(Vertices.D);
	}

	public static Graph.Transition<String, String> concat(String last) {
		return next -> {
			String value = last.concat(next);
			return Graph.node(value, concat(value));
		};
	}

	public static Graph.Node<String, String> concatGraph() {
		return Graph.node("", concat(""));
	}

	@Test
	public void shouldConcatStrings() {
		Assertions.assertThat(
						concatGraph()
								.traverse(
										Stream.of("A", "B", "C", "D", "abcd"),
										(l, r) -> r).getValue())
				.isEqualTo("ABCDabcd");
	}
}