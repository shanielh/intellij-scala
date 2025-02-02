package org.jetbrains.plugins.scala.macroAnnotations;

import com.intellij.DynamicBundle;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

public final class MacrosBundle extends DynamicBundle {
  @NonNls
  private static final String BUNDLE = "messages.ScalaMacrosBundle";

  private static final MacrosBundle INSTANCE = new MacrosBundle();

  private MacrosBundle() {
    super(BUNDLE);
  }

  @Nls
  public static String message(@NotNull @PropertyKey(resourceBundle = BUNDLE) String key, @NotNull Object... params) {
    return INSTANCE.getMessage(key, params);
  }
}
