/*
 * Copyright 2000-2017 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zhaow.restful.navigation.action;

import com.intellij.ide.util.gotoByName.ChooseByNameItemProvider;
import com.intellij.ide.util.gotoByName.ChooseByNameModel;
import com.intellij.ide.util.gotoByName.ChooseByNamePopup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.text.StringUtil;
import com.zhaow.utils.ToolkitUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RestServiceChooseByNamePopup extends ChooseByNamePopup {
  public static final Key<RestServiceChooseByNamePopup> CHOOSE_BY_NAME_POPUP_IN_PROJECT_KEY = new Key<>("ChooseByNamePopup");

  protected RestServiceChooseByNamePopup(@Nullable Project project, @NotNull ChooseByNameModel model, @NotNull ChooseByNameItemProvider provider, @Nullable ChooseByNamePopup oldPopup, @Nullable String predefinedText, boolean mayRequestOpenInCurrentWindow, int initialIndex) {
    super(project, model, provider, oldPopup, predefinedText, mayRequestOpenInCurrentWindow, initialIndex);
  }

  public static RestServiceChooseByNamePopup createPopup(final Project project,
                                                         @NotNull final ChooseByNameModel model,
                                                         @NotNull ChooseByNameItemProvider provider,
                                                         @Nullable final String predefinedText,
                                                         boolean mayRequestOpenInCurrentWindow,
                                                         final int initialIndex) {
    final RestServiceChooseByNamePopup oldPopup = project == null ? null : project.getUserData(CHOOSE_BY_NAME_POPUP_IN_PROJECT_KEY);
    if (oldPopup != null) {
      oldPopup.close(false);
    }
    RestServiceChooseByNamePopup newPopup = new RestServiceChooseByNamePopup(project, model, provider, oldPopup, predefinedText, mayRequestOpenInCurrentWindow, initialIndex);

    if (project != null) {
      project.putUserData(CHOOSE_BY_NAME_POPUP_IN_PROJECT_KEY, newPopup);
    }
    return newPopup;
  }

  @Override
  public String transformPattern(String pattern) {
    final ChooseByNameModel model = getModel();
    return getTransformedPattern(pattern, model);
  }

//TODO: resolve PathVariable
  public static String getTransformedPattern(String pattern, ChooseByNameModel model) {
    if (! (model instanceof GotoRequestMappingModel) ) {
      return pattern;
    }

    pattern = ToolkitUtil.removeRedundancyMarkup(pattern);;
    return pattern;
  }


  @Nullable
  public String getMemberPattern() {
    final String enteredText = getTrimmedText();
    final int index = enteredText.lastIndexOf('#');
    if (index == -1) {
      return null;
    }

    String name = enteredText.substring(index + 1).trim();
    return StringUtil.isEmptyOrSpaces(name) ? null : name;
  }

/*  public void repaintList() {
    myRepaintQueue.cancelAllUpdates();
    myRepaintQueue.queue(new Update(this) {
      @Override
      public void run() {
        RestServiceChooseByNamePopup.this.repaintListImmediate();
      }
    });
  }*/

}