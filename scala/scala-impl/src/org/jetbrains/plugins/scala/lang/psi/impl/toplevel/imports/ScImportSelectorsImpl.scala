package org.jetbrains.plugins.scala.lang.psi.impl.toplevel
package imports

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import org.jetbrains.plugins.scala.JavaArrayFactoryUtil
import org.jetbrains.plugins.scala.lang.parser.ScalaElementType
import org.jetbrains.plugins.scala.lang.psi.api.toplevel.imports._
import org.jetbrains.plugins.scala.lang.psi.impl.ScalaStubBasedElementImpl
import org.jetbrains.plugins.scala.lang.psi.stubs.ScImportSelectorsStub

class ScImportSelectorsImpl private (stub: ScImportSelectorsStub, node: ASTNode)
  extends ScalaStubBasedElementImpl(stub, ScalaElementType.IMPORT_SELECTORS, node) with ScImportSelectors {

  def this(node: ASTNode) = this(null, node)

  def this(stub: ScImportSelectorsStub) = this(stub, null)

  override def toString: String = "ImportSelectors"

  override def hasWildcard: Boolean = byStubOrPsi(_.hasWildcard)(wildcardElement.nonEmpty)

  override def wildcardElement: Option[PsiElement] =
    selectors.reverseIterator.flatMap(_.wildcardElement).nextOption()

  override def selectors: Seq[ScImportSelector] =
    getStubOrPsiChildren(ScalaElementType.IMPORT_SELECTOR, JavaArrayFactoryUtil.ScImportSelectorFactory).toSeq
}