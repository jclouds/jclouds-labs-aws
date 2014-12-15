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

import org.jclouds.collect.IterableWithMarker;
import org.jclouds.glacier.options.PaginationOptions;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;
import com.google.common.base.Optional;

/**
 * Defines the attributes needed for Multipart uploads. Extends IterableWithMarker to support requesting paginated
 * multipart upload parts.
 */
@AutoValue
public abstract class MultipartUploadMetadata extends IterableWithMarker<PartMetadata> {

   @Nullable public abstract String getArchiveDescription();

   public abstract Date getCreationDate();

   public abstract String getMultipartUploadId();

   public abstract long getPartSizeInBytes();

   public abstract String getVaultARN();

   @Nullable public abstract Iterable<PartMetadata> getParts();

   @Nullable public abstract String getMarker();

   public long getPartSizeInMB() {
      return this.getPartSizeInBytes() >> 20;
   }

   @Override
   public Iterator<PartMetadata> iterator() {
      return getParts() == null ? null : getParts().iterator();
   }

   @Override
   public Optional<Object> nextMarker() {
      return Optional.<Object>fromNullable(getMarker());
   }

   public PaginationOptions nextPaginationOptions() {
      return PaginationOptions.class.cast(nextMarker().get());
   }

   @SerializedNames({ "ArchiveDescription", "CreationDate", "MultipartUploadId", "PartSizeInBytes", "VaultARN",
         "Parts", "Marker" })
   public static MultipartUploadMetadata create(String archiveDescription, Date creationDate, String multipartUploadId,
         long partSizeInBytes, String vaultARN, Iterable<PartMetadata> parts, String marker) {
      return new AutoValue_MultipartUploadMetadata(archiveDescription, creationDate, multipartUploadId, partSizeInBytes,
            vaultARN, parts, marker);
   }
}
