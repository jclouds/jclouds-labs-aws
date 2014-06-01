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

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;

import org.jclouds.glacier.util.ContentRange;

import com.google.gson.annotations.SerializedName;

/**
 * Defines the attributes needed for a multipart upload part.
 */
public class PartMetadata {

   @SerializedName("SHA256TreeHash")
   private final String treeHash;
   @SerializedName("RangeInBytes")
   private final ContentRange range;

   @ConstructorProperties({ "SHA256TreeHash", "RangeInBytes" })
   public PartMetadata(String treeHash, String range) {
      super();
      this.treeHash = checkNotNull(treeHash, "treeHash");
      this.range = ContentRange.fromString(checkNotNull(range, "range"));
   }

   public ContentRange getRange() {
      return range;
   }

   public String getTreeHash() {
      return treeHash;
   }

}
