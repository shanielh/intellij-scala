package org.jetbrains.plugins.scala.lang.dfa.cfg.transformations

import org.jetbrains.plugins.scala.lang.dfa.cfg.ScalaDfaControlFlowBuilder

/**
 * Interface for abstractions over Scala PSI elements that can be transformed into the Intermediate Representation
 * (stack-based, bytecode-like control flow representation, compatible with
 * [[com.intellij.codeInspection.dataFlow.interpreter.DataFlowInterpreter]]).
 */
trait Transformable {

  /**
   * '''Preconditions:''' The method should not assume or depend on any knowledge
   * about the contents of the stack before or at the moment of its invocation.
   *
   * '''Postconditions''': The return value of this Transformable will appear on the top of the stack.
   * There will be exactly one new value on the top and the rest of the stack will remain unchanged.
   *
   * @param builder the control flow builder to which the generated IR instructions will be added
   * @throws TransformationFailedException when this or any recursively invoked transformation fails
   *                                       and building of the control flow should be aborted
   */
  @throws(classOf[TransformationFailedException])
  def transform(builder: ScalaDfaControlFlowBuilder): Unit
}
