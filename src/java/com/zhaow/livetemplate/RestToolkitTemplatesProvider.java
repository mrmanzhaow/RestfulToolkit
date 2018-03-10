package com.zhaow.livetemplate;

import com.intellij.codeInsight.template.impl.DefaultLiveTemplatesProvider;

public class RestToolkitTemplatesProvider implements DefaultLiveTemplatesProvider {
  @Override
  public String[] getDefaultLiveTemplateFiles() {
//    return new String[]{"/liveTemplates/RestToolkit"};
    return null;
  }

  @Override
  public String[] getHiddenLiveTemplateFiles() {
    return null;
  }
}