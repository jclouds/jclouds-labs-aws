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
package org.jclouds.iam.features;

import javax.inject.Named;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.aws.filters.FormSigner;
import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.PagedIterable;
import org.jclouds.iam.domain.User;
import org.jclouds.iam.functions.UsersToPagedIterable;
import org.jclouds.iam.xml.ListUsersResultHandler;
import org.jclouds.iam.xml.UserHandler;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.FormParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.Transform;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.annotations.XMLResponseParser;

/**
 * Provides access to Amazon IAM via the Query API
 * <p/>
 * 
 * @see <a href="http://docs.amazonwebservices.com/IAM/latest/APIReference" />
 * @author Adrian Cole
 */
@RequestFilters(FormSigner.class)
@VirtualHost
public interface UserApi {

   /**
    * returns all users in order.
    */
   @Named("ListUsers")
   @POST
   @Path("/")
   @FormParams(keys = "Action", values = "ListUsers")
   @XMLResponseParser(ListUsersResultHandler.class)
   @Transform(UsersToPagedIterable.class)
   PagedIterable<User> list();

   /**
    * retrieves up to 100 users in order.
    */
   @Named("ListUsers")
   @POST
   @Path("/")
   @FormParams(keys = "Action", values = "ListUsers")
   @XMLResponseParser(ListUsersResultHandler.class)
   IterableWithMarker<User> listFirstPage();

   /**
    * retrieves up to 100 users in order, starting at {@code marker}
    * 
    * @param marker
    *           starting point to resume the list
    */
   @Named("ListUsers")
   @POST
   @Path("/")
   @FormParams(keys = "Action", values = "ListUsers")
   @XMLResponseParser(ListUsersResultHandler.class)
   IterableWithMarker<User> listAt(@FormParam("Marker") String marker);

   /**
    * returns all users in order at the specified {@code pathPrefix}.
    * 
    * @param pathPrefix
    *           ex. {@code /division_abc/subdivision_xyz/}
    */
   @Named("ListUsers")
   @POST
   @Path("/")
   @FormParams(keys = "Action", values = "ListUsers")
   @XMLResponseParser(ListUsersResultHandler.class)
   @Transform(UsersToPagedIterable.class)
   PagedIterable<User> listPathPrefix(@FormParam("PathPrefix") String pathPrefix);

   /**
    * retrieves up to 100 users in order at the specified {@code pathPrefix}.
    * 
    * @param pathPrefix
    *           ex. {@code /division_abc/subdivision_xyz/}
    */
   @Named("ListUsers")
   @POST
   @Path("/")
   @FormParams(keys = "Action", values = "ListUsers")
   @XMLResponseParser(ListUsersResultHandler.class)
   IterableWithMarker<User> listPathPrefixFirstPage(@FormParam("PathPrefix") String pathPrefix);

   /**
    * retrieves up to 100 users in order at the specified {@code pathPrefix}, starting at {@code marker}.
    * 
    * @param pathPrefix
    *           ex. {@code /division_abc/subdivision_xyz/}
    * @param marker
    *           starting point to resume the list
    */
   @Named("ListUsers")
   @POST
   @Path("/")
   @FormParams(keys = "Action", values = "ListUsers")
   @XMLResponseParser(ListUsersResultHandler.class)
   IterableWithMarker<User> listPathPrefixAt(@FormParam("PathPrefix") String pathPrefix,
         @FormParam("Marker") String marker);

   /**
    * Retrieves information about the current user, including the user's path, GUID, and ARN.
    */
   @Named("GetUser")
   @POST
   @Path("/")
   @XMLResponseParser(UserHandler.class)
   @FormParams(keys = "Action", values = "GetUser")
   User getCurrent();

   /**
    * Retrieves information about the specified user, including the user's path, GUID, and ARN.
    * 
    * @param name
    *           Name of the user to get information about.
    * @return null if not found
    */
   @Named("GetUser")
   @POST
   @Path("/")
   @XMLResponseParser(UserHandler.class)
   @FormParams(keys = "Action", values = "GetUser")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   User get(@FormParam("UserName") String name);
}
