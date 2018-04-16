package com.zhaow.utils;

import com.intellij.openapi.actionSystem.DataKey;
import com.zhaow.restful.navigation.action.RestServiceItem;

import java.util.Collection;
import java.util.List;

public class RestServiceDataKeys {

  public static final DataKey<List<RestServiceItem>> SERVICE_ITEMS = DataKey.create("SERVICE_ITEMS");

  private RestServiceDataKeys() {
  }
}
