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
import java.util.Date;

import com.google.gson.annotations.SerializedName;


public class ArchiveMetadataCollection {

   @SerializedName("ArchiveList")
   private final Iterable<ArchiveMetadata> archives;
   @SerializedName("VaultARN")
   private final String vaultARN;
   @SerializedName("InventoryDate")
   private final Date inventoryDate;

   @ConstructorProperties({ "ArchiveList", "VaultARN", "InventoryDate" })
   public ArchiveMetadataCollection(Iterable<ArchiveMetadata> archives, String vaultARN, Date inventoryDate) {
      this.archives = checkNotNull(archives, "archives");
      this.vaultARN = checkNotNull(vaultARN, "vaultARN");
      this.inventoryDate = (Date) checkNotNull(inventoryDate, "inventoryDate").clone();
   }

   public Iterable<ArchiveMetadata> getArchives() {
      return archives;
   }

   public String getVaultARN() {
      return vaultARN;
   }

   public Date getInventoryDate() {
      return (Date) inventoryDate.clone();
   }
}
