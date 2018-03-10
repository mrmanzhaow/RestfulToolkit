package com.zhaow.restful.statistics;

import com.intellij.featureStatistics.ApplicabilityFilter;
import com.intellij.featureStatistics.FeatureDescriptor;
import com.intellij.featureStatistics.GroupDescriptor;
import com.intellij.featureStatistics.ProductivityFeaturesProvider;
import com.intellij.remoteServer.util.CloudBundle;

import java.util.Collections;

public class RestfulToolkitFeaturesProvider extends ProductivityFeaturesProvider {
  public static final String CLOUDS_GROUP_ID = "clouds";
  public static final String UPLOAD_SSH_KEY_FEATURE_ID = "upload.ssh.key";

  @Override
  public FeatureDescriptor[] getFeatureDescriptors() {
    return new FeatureDescriptor[]{new FeatureDescriptor(UPLOAD_SSH_KEY_FEATURE_ID,
                                                         CLOUDS_GROUP_ID,
                                                         "UploadSshKey.html",
                                                         CloudBundle.getText("upload.ssh.key.display.name"),
                                                         0,
                                                         0,
                                                         Collections.<String>emptySet(),
                                                         0,
                                                         this)};
  }

  @Override
  public GroupDescriptor[] getGroupDescriptors()
  {
    return new GroupDescriptor[] {
      new GroupDescriptor(CLOUDS_GROUP_ID, CloudBundle.getText("group.display.name"))
    };
  }

  @Override
  public ApplicabilityFilter[] getApplicabilityFilters() {
    return new ApplicabilityFilter[0];
  }
}