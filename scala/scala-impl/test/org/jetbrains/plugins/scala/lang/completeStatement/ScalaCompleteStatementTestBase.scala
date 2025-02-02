package org.jetbrains.plugins.scala
package lang
package completeStatement

import com.intellij.ide.highlighter.JavaFileType
import com.intellij.openapi.actionSystem.IdeActions.ACTION_EDITOR_COMPLETE_STATEMENT
import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.psi.statistics.StatisticsManager
import com.intellij.psi.statistics.impl.StatisticsManagerImpl
import org.jetbrains.plugins.scala.base.ScalaLightCodeInsightFixtureTestCase
import org.jetbrains.plugins.scala.extensions.StringExt

abstract class ScalaCompleteStatementTestBase extends ScalaLightCodeInsightFixtureTestCase {

  protected val fileType: LanguageFileType = ScalaFileType.INSTANCE

  protected override def setUp(): Unit = {
    super.setUp()

    StatisticsManager.getInstance match {
      case impl: StatisticsManagerImpl => impl.enableStatistics(getTestRootDisposable)
    }

    //We should change this setting in order to be sure EnterProcessor works without 'swap-settings-hack'
    //it was in org.jetbrains.plugins.scala.editor.smartEnter.ScalaSmartEnterProcessor#moveCaretInsideBracesIfAny
    getCommonCodeStyleSettings.KEEP_SIMPLE_BLOCKS_IN_ONE_LINE = true
  }

  override def tearDown(): Unit = {
    getCommonCodeStyleSettings.KEEP_SIMPLE_BLOCKS_IN_ONE_LINE = false

    super.tearDown()
  }

  def doCompletionTest(fileText: String, resultText: String): Unit = {
    myFixture.configureByText(fileType, fileText.withNormalizedSeparator.trim)
    myFixture.performEditorAction(ACTION_EDITOR_COMPLETE_STATEMENT)
    myFixture.checkResult(resultText.withNormalizedSeparator.trim, true)
  }
}

class JavaCompleteStatementTest extends ScalaCompleteStatementTestBase {

  override protected val fileType: LanguageFileType = JavaFileType.INSTANCE

  def testFormatJava(): Unit = doCompletionTest(
    fileText =
      s"""
         |class B {
         |    int d=7+7+7+77;$CARET
         |}
      """.stripMargin,
    resultText =
      s"""
         |class B {
         |    int d = 7 + 7 + 7 + 77;$CARET
         |}
      """.stripMargin
  )

  def testIfConditionJava(): Unit = doCompletionTest( //WHAT THE _?!
    fileText =
      s"""
         |class B {
         |    public static void main(String[] args) {
         |        if $CARET
         |    }
         |}
      """.stripMargin,
    resultText =
      s"""
         |class B {
         |    public static void main(String[] args) {
         |        if ($CARET) {
         |        }
         |    }
         |}
      """.stripMargin
  )

  def testIfCondition2Java(): Unit = doCompletionTest(
    fileText =
      s"""
         |class B {
         |    public static void main(String[] args) {
         |        if ()$CARET
         |    }
         |}
      """.stripMargin,
    resultText =
      s"""
         |class B {
         |    public static void main(String[] args) {
         |        if ($CARET) {
         |        }
         |    }
         |}
      """.stripMargin
  )
}
