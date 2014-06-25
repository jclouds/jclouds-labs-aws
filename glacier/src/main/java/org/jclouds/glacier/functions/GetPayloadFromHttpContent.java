/*
 * Licensed to the Apache Software Foimport org.jclouds.http.HttpException;
import org.jclouds.http.HttpResponse;

import com.google.common.base.Function;
 for additional information regarding copyright ownership.
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
package org.jclouds.glacier.functions;

import org.jclouds.http.HttpException;
import org.jclouds.http.HttpResponse;
import org.jclouds.io.Payload;

import com.google.common.base.Function;

/**
 * Gets the payload from the http response.
 */
public class GetPayloadFromHttpContent implements Function<HttpResponse, Payload> {

   @Override
   public Payload apply(HttpResponse from) {
      if (from.getPayload() == null)
         throw new HttpException("Did not receive payload");
      return from.getPayload();
   }
}
