package com.tgac.functional.monad;

import static com.tgac.functional.monad.Free.liftF;
import static com.tgac.functional.recursion.Recur.done;
import static com.tgac.functional.recursion.Recur.recur;

import com.tgac.functional.category.Functor;
import com.tgac.functional.category.Monad;
import com.tgac.functional.recursion.Recur;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;

class FreeTest {

	public interface Calculator<A> extends Functor<Calculator<?>, A> {

		@Override
		<B> Calculator<B> map(Function<? super A, B> f);

		@lombok.Value
		@RequiredArgsConstructor
		class Value<A, V> implements Calculator<A> {
			V value;
			Function<V, A> next;

			@Override
			public <B> Calculator<B> map(Function<? super A, B> f) {
				return new Value<>(value, v -> f.apply(next.apply(v)));
			}
		}

		enum OpType {
			ADD, SUB, MUL, DIV;
		}

		@lombok.Value
		@RequiredArgsConstructor
		class BinaryOp<A, V> implements Calculator<A> {
			OpType type;
			Free<Calculator<?>, V> lhs;
			Free<Calculator<?>, V> rhs;
			Function<V, Calculator<A>> next;

			@Override
			public <B> Calculator<B> map(Function<? super A, B> f) {
				return new BinaryOp<>(type, lhs, rhs, v -> next.apply(v).map(f));
			}
		}

		static <A> Free<Calculator<?>, A> value(A x) {
			Free<Calculator<?>, Free<Calculator<?>, A>> calculatorPureFree = liftF(new Value<>(x, Free.Pure::new));
			Monad<Free<Calculator<?>, ?>, A> freeDoubleMonad = calculatorPureFree.flatMap(v -> v);
			return freeDoubleMonad.cast();
		}

		static Free<Calculator<?>, Double> binaryOp(OpType op, Free<Calculator<?>, Double> x, Free<Calculator<?>, Double> y) {
			BinaryOp<Double, Double> binaryOp = new BinaryOp<>(op, x, y, v -> new Calculator.Value<>(v, Function.identity()));
			return liftF(binaryOp).cast();
		}

		static Free<Calculator<?>, Double> add(Free<Calculator<?>, Double> x, Free<Calculator<?>, Double> y) {
			return binaryOp(OpType.ADD, x, y);
		}

		static Free<Calculator<?>, Double> sub(Free<Calculator<?>, Double> x, Free<Calculator<?>, Double> y) {
			return binaryOp(OpType.SUB, x, y);
		}

		static Free<Calculator<?>, Double> mul(Free<Calculator<?>, Double> x, Free<Calculator<?>, Double> y) {
			return binaryOp(OpType.MUL, x, y);
		}

		static Free<Calculator<?>, Double> div(Free<Calculator<?>, Double> x, Free<Calculator<?>, Double> y) {
			return binaryOp(OpType.DIV, x, y);
		}

		static Recur<Double> run(Free<Calculator<?>, Double> program) {
			if (program instanceof Free.Pure) {
				return done(((Free.Pure<Calculator<?>, Double>) program).getValue());
			} else if (program instanceof Free.Suspend) {
				Free.Suspend<Calculator<?>, Double> suspend = (Free.Suspend<Calculator<?>, Double>) program;

				Calculator<Free<Calculator<?>, Double>> calc = suspend.getSuspended().cast();

				if (calc instanceof Value) {
					Value<Free<Calculator<?>, Double>, Double> value = (Value<Free<Calculator<?>, Double>, Double>) calc;
					return recur(() -> run(value.next.apply(value.value)));
				} else if (calc instanceof BinaryOp) {
					BinaryOp<Free<Calculator<?>, Double>, Double> op = (BinaryOp<Free<Calculator<?>, Double>, Double>) calc;

					Recur<Double> lhs = recur(() -> Calculator.run(op.getLhs()));
					Recur<Double> rhs = recur(() -> Calculator.run(op.getRhs()));

					Recur<Double> result;
					switch (op.getType()) {
						case ADD:
							result = lhs.flatMap(l -> rhs.map(r -> l + r));
							break;
						case SUB:
							result = lhs.flatMap(l -> rhs.map(r -> l - r));
							break;
						case MUL:
							result = lhs.flatMap(l -> rhs.map(r -> l * r));
							break;
						case DIV:
							result = lhs.flatMap(l -> rhs.map(r -> l / r));
							break;
						default:
							throw new UnsupportedOperationException("Unknown operator type: " + op.getType().name());
					}
					return result.flatMap(v -> recur(() -> run(
							liftF(op.next.apply(v))
									.flatMap(Function.identity())
									.cast())));
				} else {
					throw new UnsupportedOperationException("Unknown Calc type: " + calc.getClass().getSimpleName());
				}
			} else {
				throw new UnsupportedOperationException("Unkown Free type: " + program.getClass().getSimpleName());
			}

		}

		static Recur<String> describe2(Free<Calculator<?>, Double> program) {
			if (program instanceof Free.Pure) {
				return done(((Free.Pure<Calculator<?>, Double>) program).get().toString());
			} else if (program instanceof Free.Suspend) {
				Free.Suspend<Calculator<?>, Double> suspend = (Free.Suspend<Calculator<?>, Double>) program;
				Calculator<Free<Calculator<?>, Double>> instr = suspend.getSuspended().cast();

				if (instr instanceof Value) {
					Value<Free<Calculator<?>, Double>, Double> val = (Value<Free<Calculator<?>, Double>, Double>) instr;
					return describe(val.getNext().apply(val.getValue()));
				} else if (instr instanceof BinaryOp) {
					BinaryOp<Free<Calculator<?>, Double>, Double> op =
							(BinaryOp<Free<Calculator<?>, Double>, Double>) instr;

					Recur<String> lhs = recur(() -> describe2(op.getLhs()));
					Recur<String> rhs = recur(() -> describe2(op.getRhs()));
					String symbol;

					switch (op.getType()) {
						case ADD:
							symbol = "+";
							break;
						case SUB:
							symbol = "-";
							break;
						case MUL:
							symbol = "*";
							break;
						case DIV:
							symbol = "/";
							break;
						default:
							throw new IllegalStateException("Unknown op");
					}
					return Recur.zip(lhs, rhs)
							.map(t -> "(" + t._1 + " " + symbol + " " + t._2 + ")");
				}
			}
			throw new IllegalStateException("Unknown Free node: " + program);
		}

		/**
		 * Describes a Free Calculator program as a string representation of the expression.
		 * This method is designed for programs that form a direct expression tree,
		 * typically built by nested calls to smart constructors like Calculator.add(...).
		 *
		 * @param program The Free<Calculator<?>, Double> program to describe.
		 * @return A Recur<String> representing the suspended or completed description.
		 * 		Call runTrampoline() on the result to get the final String.
		 */
		public static Recur<String> describe(Free<Calculator<?>, Double> program) {
			if (program instanceof Free.Pure) {
				// Base case: the program is a pure value. Describe it as its string representation.
				return Recur.done(((Free.Pure<Calculator<?>, Double>) program).getValue().toString());
			} else if (program instanceof Free.Suspend) {
				Free.Suspend<Calculator<?>, Double> suspendNode = (Free.Suspend<Calculator<?>, Double>) program;

				// suspendNode.getSuspended() is Functor<Calculator<?>, Free<Calculator<?>, Double>>
				// because Free.Suspend<F,A> holds Functor<F, Free<F,A>>.
				// The .cast() is from TypeConstructor, which Functor extends.
				Calculator<Free<Calculator<?>, Double>> currentInstruction =
						suspendNode.getSuspended().cast();

				if (currentInstruction instanceof Calculator.Value) {
					// The instruction is Calculator.Value<A, V_val>
					// A (type param for Calculator) is Free<Calculator<?>, Double>
					// V_val (type param for Calculator.Value's raw value) is Double
					Calculator.Value<Free<Calculator<?>, Double>, Double> valueInstruction =
							(Calculator.Value<Free<Calculator<?>, Double>, Double>) currentInstruction;

					// valueInstruction.getNext() is Function<Double, Free<Calculator<?>, Double>>
					// Applying it to the raw value gives the next Free step.
					Free<Calculator<?>, Double> nextFreeStep =
							valueInstruction.getNext().apply(valueInstruction.getValue());

					// Recursively describe this next step.
					return Recur.recur(() -> describe(nextFreeStep));

				} else if (currentInstruction instanceof Calculator.BinaryOp) {
					// The instruction is Calculator.BinaryOp<A, V_op>
					// A (type param for Calculator) is Free<Calculator<?>, Double>
					// V_op (type param for BinaryOp's operands) is Double
					Calculator.BinaryOp<Free<Calculator<?>, Double>, Double> binaryOpInstruction =
							(Calculator.BinaryOp<Free<Calculator<?>, Double>, Double>) currentInstruction;

					// Recursively describe the left and right hand side programs.
					Recur<String> lhsDescRecur = Recur.recur(() -> describe(binaryOpInstruction.getLhs()));
					Recur<String> rhsDescRecur = Recur.recur(() -> describe(binaryOpInstruction.getRhs()));

					String symbol;
					switch (binaryOpInstruction.getType()) {
						case ADD:
							symbol = "+";
							break;
						case SUB:
							symbol = "-";
							break;
						case MUL:
							symbol = "*";
							break;
						case DIV:
							symbol = "/";
							break;
						default:
							throw new IllegalStateException("Unknown operator type: " + binaryOpInstruction.getType().name());
					}

					// Combine the descriptions of lhs and rhs with the operator symbol.
					// This produces the string representation for this binary operation.
					// The 'next' function of BinaryOp is not directly used here for string construction,
					// as this describe method focuses on the structure of the current operation
					// and its operands.
					return Recur.zip(lhsDescRecur, rhsDescRecur)
							.map(pair -> "(" + pair._1 + " " + symbol + " " + pair._2 + ")");
				} else {
					throw new IllegalStateException("Unknown Calculator instruction type: " + currentInstruction.getClass().getName());
				}
			} else {
				throw new IllegalStateException("Unknown Free node type: " + program.getClass().getName());
			}
		}

	}

	@Test
	public void shouldCalculate() {
		Free<Calculator<?>, Double> freeObjectMonad = Calculator.value(10.)
				.flatMap(x -> Calculator.add(Calculator.value(x), Calculator.value(1.)))
				.flatMap(x -> Calculator.div(Calculator.value(x), Calculator.value(2.)))
				.cast();
		System.out.println(Calculator.run(freeObjectMonad).get());
		System.out.println(Calculator.describe(freeObjectMonad).get());
	}

}