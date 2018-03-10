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
package com.zhaow.restful.navigator;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.zhaow.restful.common.ServiceHelper;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@State(name = "RestServiceProjectsManager")
public class RestServiceProjectsManager extends AbstractProjectComponent
  implements PersistentStateComponent<RestServicesNavigatorState>, Disposable, ProjectComponent {
  protected final Project myProject;



  private RestServicesNavigatorState myState = new RestServicesNavigatorState();

  public static RestServiceProjectsManager getInstance(Project p) {
    return p.getComponent(RestServiceProjectsManager.class);
  }

  public RestServiceProjectsManager(Project project) {
      super(project);
      myProject = project;
//    System.out.println("RestServiceProjectsManager");
  }


  @Override
  public void dispose() {

  }

  @Nullable
  @Override
  public RestServicesNavigatorState getState() {
    return null;
  }

  @Override
  public void loadState(RestServicesNavigatorState state) {

  }

  public List<RestServiceProject> getServiceProjects() {
    List<RestServiceProject> list = DumbService.getInstance(myProject).runReadActionInSmartMode(() -> ServiceHelper.buildRestServiceProjectList(myProject));
    return list;
  }

  public boolean hasProjects() {
    if (! myProject.isInitialized()) {
      return false;
    }
    System.out.println("======hasProjects=====");
    return getServiceProjects().size() > 0;
  }

  public void forceUpdateAllProjects() {

  }
}
