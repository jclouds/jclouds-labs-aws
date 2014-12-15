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

import org.jclouds.glacier.util.ContentRange;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class JobMetadata {

   public abstract String getAction();

   @Nullable public abstract String getArchiveId();

   @Nullable public abstract Long getArchiveSizeInBytes();

   @Nullable public abstract String getArchiveSHA256TreeHash();

   public abstract boolean isCompleted();

   @Nullable public abstract Date getCompletionDate();

   public abstract Date getCreationDate();

   @Nullable public abstract Long getInventorySizeInBytes();

   @Nullable public abstract String getJobDescription();

   public abstract String getJobId();

   @Nullable public abstract ContentRange getRetrievalByteRange();

   @Nullable public abstract String getSha256TreeHash();

   @Nullable public abstract String getSnsTopic();

   public abstract JobStatus getStatusCode();

   @Nullable public abstract String getStatusMessage();

   public abstract String getVaultArn();

   @Nullable public abstract InventoryRetrievalParameters getParameters();

   @SerializedNames({ "Action", "ArchiveId", "ArchiveSizeInBytes", "ArchiveSHA256TreeHash", "Completed",
         "CompletionDate", "CreationDate", "InventorySizeInBytes", "JobDescription", "JobId", "RetrievalByteRange",
         "SHA256TreeHash", "SNSTopic", "StatusCode", "StatusMessage", "VaultARN", "InventoryRetrievalParameters" })
   public static JobMetadata create(String action, String archiveId, Long archiveSizeInBytes,
         String archiveSHA256TreeHash, boolean completed, Date completionDate, Date creationDate,
         Long inventorySizeInBytes, String jobDescription, String jobId, String retrievalByteRange,
         String sha256TreeHash, String snsTopic, String statusCode, String statusMessage, String vaultArn,
         InventoryRetrievalParameters parameters) {
      return new AutoValue_JobMetadata(action, archiveId, archiveSizeInBytes, archiveSHA256TreeHash, completed,
            completionDate == null ? null : (Date) completionDate.clone(), (Date) creationDate.clone(), inventorySizeInBytes,
            jobDescription, jobId, retrievalByteRange == null ? null : ContentRange.fromString(retrievalByteRange),
            sha256TreeHash, snsTopic, JobStatus.fromString(statusCode), statusMessage,
            vaultArn, parameters);
   }
}

