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

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

/**
 * Defines the attributes needed to describe a vault.
 */
@AutoValue
public abstract class VaultMetadata {
   public abstract String getVaultName();

   public abstract String getVaultARN();

   public abstract Date getCreationDate();

   @Nullable public abstract Date getLastInventoryDate();

   public abstract long getNumberOfArchives();

   public abstract long getSizeInBytes();

   @SerializedNames({ "VaultName", "VaultARN", "CreationDate", "LastInventoryDate", "NumberOfArchives", "SizeInBytes"})
   public static VaultMetadata create(String vaultName, String vaultARN, Date creationDate, Date lastInventoryDate,
                                      long numberOfArchives, long sizeInBytes) {
      return new AutoValue_VaultMetadata(vaultName, vaultARN, creationDate, lastInventoryDate, numberOfArchives, sizeInBytes);
   }
}
