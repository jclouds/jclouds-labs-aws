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
package org.jclouds.elb;

import static org.jclouds.aws.reference.AWSConstants.PROPERTY_AUTH_TAG;
import static org.jclouds.aws.reference.AWSConstants.PROPERTY_HEADER_TAG;

import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.elb.config.ELBHttpApiModule;
import org.jclouds.elb.loadbalancer.config.ELBLoadBalancerContextModule;
import org.jclouds.loadbalancer.LoadBalancerServiceContext;
import org.jclouds.rest.internal.BaseHttpApiMetadata;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * Implementation of {@link ApiMetadata} for Amazon's Elastic Load Balancing api.
 */
@AutoService(ApiMetadata.class)
public class ELBApiMetadata extends BaseHttpApiMetadata<ELBApi> {

   @Override
   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
   }

   public ELBApiMetadata() {
      this(new Builder());
   }

   protected ELBApiMetadata(Builder builder) {
      super(Builder.class.cast(builder));
   }

   public static Properties defaultProperties() {
      Properties properties = BaseHttpApiMetadata.defaultProperties();
      properties.setProperty(PROPERTY_AUTH_TAG, "AWS");
      properties.setProperty(PROPERTY_HEADER_TAG, "amz");
      return properties;
   }

   public static class Builder extends BaseHttpApiMetadata.Builder<ELBApi, Builder> {

      protected Builder() {
         id("elb")
         .name("Amazon Elastic Load Balancing Api")
         .identityName("Access Key ID")
         .credentialName("Secret Access Key")
         .version("2012-06-01")
         .defaultProperties(ELBApiMetadata.defaultProperties())
         .defaultEndpoint("https://elasticloadbalancing.us-east-1.amazonaws.com")
         .documentation(URI.create("http://docs.amazonwebservices.com/ElasticLoadBalancing/latest/APIReference"))
         .view(LoadBalancerServiceContext.class)
         .defaultModules(ImmutableSet.<Class<? extends Module>>of(ELBHttpApiModule.class, ELBLoadBalancerContextModule.class));
      }

      @Override
      public ELBApiMetadata build() {
         return new ELBApiMetadata(this);
      }

      @Override
      protected Builder self() {
         return this;
      }
   }
}
