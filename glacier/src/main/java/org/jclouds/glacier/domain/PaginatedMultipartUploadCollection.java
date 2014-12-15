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

import java.util.Iterator;

import org.jclouds.collect.IterableWithMarker;
import org.jclouds.glacier.options.PaginationOptions;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;
import com.google.common.base.Optional;

/**
 * Paginated collection used to store multipart upload lists.
 */
@AutoValue
public abstract class PaginatedMultipartUploadCollection extends IterableWithMarker<MultipartUploadMetadata> {

   public abstract Iterable<MultipartUploadMetadata> getUploads();

   @Nullable
   public abstract String getMarker();

   @Override
   public Iterator<MultipartUploadMetadata> iterator() {
      return getUploads().iterator();
   }

   @Override
   public Optional<Object> nextMarker() {
      return Optional.<Object>fromNullable(getMarker());
   }

   public PaginationOptions nextPaginationOptions() {
      return PaginationOptions.class.cast(nextMarker().get());
   }

   @SerializedNames({"UploadsList", "Marker"})
   public static PaginatedMultipartUploadCollection create(Iterable<MultipartUploadMetadata> uploads, String marker) {
      return new AutoValue_PaginatedMultipartUploadCollection(uploads, marker);
   }
}
