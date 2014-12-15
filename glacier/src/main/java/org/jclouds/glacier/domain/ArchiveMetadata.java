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
import com.google.common.hash.HashCode;

@AutoValue
public abstract class ArchiveMetadata {

   public abstract String getArchiveId();

   @Nullable public abstract String getDescription();

   public abstract Date getCreationDate();

   public abstract long getSize();

   public abstract HashCode getTreeHash();

   @SerializedNames({ "ArchiveId", "ArchiveDescription", "CreationDate", "Size", "SHA256TreeHash" })
   public static ArchiveMetadata create(String archiveId, String description, Date creationDate, long size,
                                        String hashCode) {
      return new AutoValue_ArchiveMetadata(archiveId, description, creationDate, size, HashCode.fromString(hashCode));

   }

}
