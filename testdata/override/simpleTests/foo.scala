implement foo
package test

class a extends b {
  <caret>
}
abstract class b {
  def foo(x: b): b
}<end>
package test

class a extends b {
  def foo(x: b): b = null
}
abstract class b {
  def foo(x: b): b
}