package org.jetbrains.plugins.scala.lang.transformation.calls

import com.intellij.psi.PsiElement
import org.jetbrains.plugins.scala.lang.psi.api.expr.ScMethodCall
import org.jetbrains.plugins.scala.lang.psi.impl.ScalaCode._
import org.jetbrains.plugins.scala.lang.transformation.{AbstractTransformer, RenamedReference}
import org.jetbrains.plugins.scala.project.ProjectContext

class ExpandApplyCall extends AbstractTransformer {
  override protected def transformation(implicit project: ProjectContext): PartialFunction[PsiElement, Unit] = {
    case ScMethodCall(e @ RenamedReference(_, "apply"), _) =>
      e.replace(code"$e.apply")
  }
}
