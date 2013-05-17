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
package org.jclouds.elb.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;

import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.internal.Arg0ToPagedIterable;
import org.jclouds.elb.ELBApi;
import org.jclouds.elb.domain.LoadBalancer;
import org.jclouds.elb.features.LoadBalancerApi;
import org.jclouds.elb.options.ListLoadBalancersOptions;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.base.Optional;

/**
 * @author Adrian Cole
 */
@Beta
public class LoadBalancersToPagedIterable extends Arg0ToPagedIterable.FromCaller<LoadBalancer, LoadBalancersToPagedIterable> {

   private final ELBApi api;

   @Inject
   protected LoadBalancersToPagedIterable(ELBApi api) {
      this.api = checkNotNull(api, "api");
   }

   @Override
   protected Function<Object, IterableWithMarker<LoadBalancer>> markerToNextForArg0(Optional<Object> arg0) {
      final String region = arg0.isPresent() ? arg0.get().toString() : null;
      final LoadBalancerApi loadBalancerApi = api.getLoadBalancerApiForRegion(region);
      return new Function<Object, IterableWithMarker<LoadBalancer>>() {

         @Override
         public IterableWithMarker<LoadBalancer> apply(Object input) {
            return loadBalancerApi.list(ListLoadBalancersOptions.Builder.afterMarker(input));
         }

         @Override
         public String toString() {
            return "listLoadBalancersInRegion(" + region + ")";
         }
      };
   }
}
