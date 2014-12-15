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

import java.util.Date;
import java.util.Iterator;

import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;
import com.google.common.collect.FluentIterable;

@AutoValue
public abstract class ArchiveMetadataCollection extends FluentIterable<ArchiveMetadata>{

   public abstract Iterable<ArchiveMetadata> getArchives();

   public abstract String getVaultARN();

   public abstract Date getInventoryDate();

   @Override
   public Iterator<ArchiveMetadata> iterator() {
      return getArchives().iterator();
   }

   @SerializedNames({ "ArchiveList", "VaultARN", "InventoryDate" })
   public static ArchiveMetadataCollection create(Iterable<ArchiveMetadata> archives, String vaultARN, Date inventoryDate) {
      return new AutoValue_ArchiveMetadataCollection(archives, vaultARN, inventoryDate);
   }

}
