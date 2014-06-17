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
package org.jclouds.glacier.predicates.validators;

import org.jclouds.io.Payload;
import org.jclouds.predicates.Validator;

import com.google.inject.Singleton;

/**
 * Validates the Glacier archive payload being uploaded.
 */
@Singleton
public final class PayloadValidator extends Validator<Payload> {

   private static final long MAX_CONTENT_SIZE = 1L << 32; // 4GiB

   @Override
   public void validate(Payload payload) {
      if (payload == null)
         throw exception(payload, "Archive must have a payload.");
      if (payload.getContentMetadata().getContentLength() == null)
         throw exception(payload, "Content length must be set.");
      if (payload.getContentMetadata().getContentLength() > MAX_CONTENT_SIZE)
         throw exception(payload, "Max content size is 4gb" + " but was " + payload.getContentMetadata().getContentLength());
   }

   protected static IllegalArgumentException exception(Payload payload, String reason) {
      return new IllegalArgumentException(
            String.format(
                  "Payload '%s' doesn't match Glacier archive upload rules. "
                        + "Reason: %s. For more info, please refer to http://docs.aws.amazon.com/amazonglacier/latest/dev/api-archive-post.html.",
                  payload, reason));
   }
}
