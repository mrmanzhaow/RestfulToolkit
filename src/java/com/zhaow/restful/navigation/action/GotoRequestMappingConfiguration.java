package com.zhaow.restful.navigation.action;

import com.intellij.ide.util.gotoByName.ChooseByNameFilterConfiguration;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.StoragePathMacros;
import com.intellij.openapi.project.Project;
import com.zhaow.restful.method.HttpMethod;

/**
 * Configuration for file type filtering popup in "Go to | Service" action.
 *
 * @author zhaow
 */
@State(name = "GotoRequestMappingConfiguration", storages = @Storage(StoragePathMacros.WORKSPACE_FILE))
class GotoRequestMappingConfiguration extends ChooseByNameFilterConfiguration<HttpMethod> {
  /**
   * Get configuration instance
   *
   * @param project a project instance
   * @return a configuration instance
   */
  public static GotoRequestMappingConfiguration getInstance(Project project) {
    return ServiceManager.getService(project, GotoRequestMappingConfiguration.class);
  }

  @Override
  protected String nameForElement(HttpMethod type) {
    return type.name();
  }
}
