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
package org.jclouds.glacier.blobstore.strategy.internal;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.Math.floor;
import static java.lang.Math.log;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

import org.jclouds.glacier.blobstore.strategy.PayloadSlice;
import org.jclouds.glacier.blobstore.strategy.SlicingStrategy;
import org.jclouds.glacier.util.ContentRange;
import org.jclouds.io.Payload;
import org.jclouds.io.PayloadSlicer;

import com.google.inject.Inject;

public class BaseSlicingStrategy implements SlicingStrategy {

   public static final double DEFAULT_RATIO = 0.32; // (part size/number of parts) ratio

   private final PayloadSlicer slicer;
   private Payload payload;
   private volatile long partSizeInMB;
   private volatile long total;
   private volatile long copied;
   private volatile int part;

   @Inject
   public BaseSlicingStrategy(PayloadSlicer slicer) {
      this.slicer = checkNotNull(slicer, "slicer");
      this.total = 0;
      this.copied = 0;
      this.partSizeInMB = 0;
      this.part = 0;
   }

   //TODO: Inject custom ratio
   protected long calculatePartSize(long length) {
      long lengthInMB = (long) (length / (1L << 20)) + 1;
      double fpPartSizeInMB = sqrt(DEFAULT_RATIO * lengthInMB); //Get the part size which matches the given ratio
      long partSizeInMB = (long) pow(2, floor(log(fpPartSizeInMB) / log(2)) + 1); //Get the next power of 2
      if (partSizeInMB < 1) return 1;
      else if (partSizeInMB > MAX_PART_SIZE) return MAX_PART_SIZE;
      return partSizeInMB;
   }

   public long getRemaining() {
      return total - copied;
   }

   @Override
   public void startSlicing(Payload payload) {
      this.payload = checkNotNull(payload, "payload");
      this.copied = 0;
      this.total = checkNotNull(payload.getContentMetadata().getContentLength(), "contentLength");
      this.partSizeInMB = calculatePartSize(total);
      this.part = 0;
   }

   @Override
   public PayloadSlice nextSlice() {
      checkNotNull(this.payload, "payload");
      long sliceLength = getRemaining() < (partSizeInMB << 20) ? getRemaining() : partSizeInMB << 20;
      Payload slicedPayload = slicer.slice(payload, copied, sliceLength);
      ContentRange range = ContentRange.build(copied, copied + sliceLength - 1);
      copied += sliceLength;
      part++;
      return new PayloadSlice(slicedPayload, range, part);
   }

   @Override
   public boolean hasNext() {
      return this.getRemaining() != 0;
   }

   @Override
   public long getPartSizeInMB() {
      return partSizeInMB;
   }
}
