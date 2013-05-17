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
package org.jclouds.rds.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;

import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.internal.Arg0ToPagedIterable;
import org.jclouds.rds.RDSApi;
import org.jclouds.rds.domain.SecurityGroup;
import org.jclouds.rds.features.SecurityGroupApi;
import org.jclouds.rds.options.ListSecurityGroupsOptions;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.base.Optional;

/**
 * @author Adrian Cole
 */
@Beta
public class SecurityGroupsToPagedIterable extends Arg0ToPagedIterable.FromCaller<SecurityGroup, SecurityGroupsToPagedIterable> {

   private final RDSApi api;

   @Inject
   protected SecurityGroupsToPagedIterable(RDSApi api) {
      this.api = checkNotNull(api, "api");
   }

   @Override
   protected Function<Object, IterableWithMarker<SecurityGroup>> markerToNextForArg0(Optional<Object> arg0) {
      final String region = arg0.isPresent() ? arg0.get().toString() : null;
      final SecurityGroupApi securityGroupApi = api.getSecurityGroupApiForRegion(region);
      return new Function<Object, IterableWithMarker<SecurityGroup>>() {

         @Override
         public IterableWithMarker<SecurityGroup> apply(Object input) {
            return securityGroupApi.list(ListSecurityGroupsOptions.Builder.afterMarker(input));
         }

         @Override
         public String toString() {
            return "listSecurityGroupsInRegion(" + region + ")";
         }
      };
   }
}
