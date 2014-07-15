/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.glacier.domain;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

public enum JobStatus {
   SUCCEEDED("Succeeded"),
   FAILED("Failed"),
   IN_PROGRESS("InProgress");

   private final String value;

   private static final Map<String, JobStatus> stringToEnum = ImmutableMap.of("Succeeded", SUCCEEDED,
         "Failed", FAILED, "InProgress", IN_PROGRESS);

   public static JobStatus fromString(String symbol) {
      return stringToEnum.get(symbol);
   }

   public String getValue() {
      return this.value;
   }

   public String toString() {
      return this.value;
   }

   JobStatus(String value) {
      this.value = value;
   }
}
