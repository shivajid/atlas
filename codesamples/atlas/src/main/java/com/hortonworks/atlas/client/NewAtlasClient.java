/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hortonworks.atlas.client;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.apache.atlas.AtlasClient;
import org.apache.atlas.AtlasException;
import org.apache.atlas.AtlasServiceException;
import org.apache.atlas.security.SecureClientUtils;
import org.apache.atlas.typesystem.types.DataTypes;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.hadoop.conf.Configuration;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.client.urlconnection.URLConnectionClientHandler;

import static org.apache.atlas.security.SecurityProperties.TLS_ENABLED;
public class NewAtlasClient extends AtlasClient {
	{
		System.setProperty("atlas.conf", "conf");
	}
	 private WebResource service;
	
	public NewAtlasClient(String baseurl) {
		super(baseurl);
		DefaultClientConfig config = new DefaultClientConfig();
        PropertiesConfiguration clientConfig = null;
        try {
            clientConfig = getClientProperties();
            if (clientConfig.getBoolean(TLS_ENABLED, false)) {
                // create an SSL properties configuration if one doesn't exist.  SSLFactory expects a file, so forced
                // to create a
                // configuration object, persist it, then subsequently pass in an empty configuration to SSLFactory
                SecureClientUtils.persistSSLClientConfiguration(clientConfig);
            }
        } catch (Exception e) {
           e.printStackTrace();
        }

        URLConnectionClientHandler handler = SecureClientUtils.getClientConnectionHandler(config, clientConfig);

        Client client = new Client(handler, config);
       // System.out.println(baseurl);
        client.resource(UriBuilder.fromUri(baseurl).build());

        service = client.resource(UriBuilder.fromUri(baseurl).build());

	}
	
	

	
	/**
     * Return all trait names for the given entity id
     * @param guid
     * @return
	 * @throws Exception 
     */
    public List<String> getTraitNames(String guid) throws Exception {
        WebResource resource = getResource(API.LIST_TRAITS, guid, URI_TRAITS);
        JSONObject response = callAPIWithResource(API.LIST_TRAITS, resource);
        return extractResults(response);
    }

    private WebResource getResource(API api, String... pathParams) {
    	
    	//System.out.println(api.getPath());
        WebResource resource = service.path(api.getPath());
        //System.out.println( resource.getURI().toString());
       
        if (pathParams != null) {
            for (String pathParam : pathParams) {
                resource = resource.path(pathParam);
            }
        }
        return resource;
    }
    
    private List<String> extractResults(JSONObject response) throws AtlasServiceException {
        try {
            JSONArray results = response.getJSONArray(AtlasClient.RESULTS);
            List<String> list = new ArrayList<String>();
            for (int index = 0; index < results.length(); index++) {
                list.add(results.getString(index));
            }
            return list;
        } catch (JSONException e) {
          throw new AtlasServiceException(e);
        }
    }
    
    enum API {

        //Type operations
        CREATE_TYPE(BASE_URI + TYPES, HttpMethod.POST),
        GET_TYPE(BASE_URI + TYPES, HttpMethod.GET),
        LIST_TYPES(BASE_URI + TYPES, HttpMethod.GET),
        LIST_TRAIT_TYPES(BASE_URI + TYPES + "?type=TRAIT", HttpMethod.GET),

        //Entity operations
        CREATE_ENTITY(BASE_URI + URI_ENTITIES, HttpMethod.POST),
        GET_ENTITY(BASE_URI + URI_ENTITIES, HttpMethod.GET),
        UPDATE_ENTITY(BASE_URI + URI_ENTITIES, HttpMethod.PUT),
        LIST_ENTITY(BASE_URI + URI_ENTITIES, HttpMethod.GET),

        //Trait operations
        ADD_TRAITS(BASE_URI + URI_ENTITIES, HttpMethod.POST),
        DELETE_TRAITS(BASE_URI + URI_ENTITIES, HttpMethod.DELETE),
        LIST_TRAITS(BASE_URI + URI_ENTITIES, HttpMethod.GET),

        //Search operations
        SEARCH(BASE_URI + URI_SEARCH, HttpMethod.GET),
        SEARCH_DSL(BASE_URI + URI_SEARCH + "/dsl", HttpMethod.GET),
        SEARCH_GREMLIN(BASE_URI + URI_SEARCH + "/gremlin", HttpMethod.GET),
        SEARCH_FULL_TEXT(BASE_URI + URI_SEARCH + "/fulltext", HttpMethod.GET),

        //Lineage operations
        LINEAGE_INPUTS_GRAPH(BASE_URI + URI_LINEAGE, HttpMethod.GET),
        LINEAGE_OUTPUTS_GRAPH(BASE_URI + URI_LINEAGE, HttpMethod.GET),
        LINEAGE_SCHEMA(BASE_URI + URI_LINEAGE, HttpMethod.GET);

        private final String method;
        private final String path;

        API(String path, String method) {
            this.path = path;
            this.method = method;
        }

        public String getMethod() {
            return method;
        }

        public String getPath() {
            return path;
        }
    }
    
    private JSONObject callAPIWithResource(API api, WebResource resource) throws Exception {
        return callAPIWithResource(api, resource, null);
    }

    private JSONObject callAPIWithResource(API api, WebResource resource, Object requestObject)
    throws Exception {
    	
        ClientResponse clientResponse = resource.accept(JSON_MEDIA_TYPE).type(JSON_MEDIA_TYPE)
                .method(api.getMethod(), ClientResponse.class, requestObject);

        Response.Status expectedStatus =
                HttpMethod.POST.equals(api.getMethod()) ? Response.Status.CREATED : Response.Status.OK;
        
        if (clientResponse.getStatus() == expectedStatus.getStatusCode()) {
            String responseAsString = clientResponse.getEntity(String.class);
            
            try {
                return new JSONObject(responseAsString);
            } catch (JSONException e) {
                throw new AtlasServiceException(e);
            }
        }

        throw new Exception("Metadata service API " + api + " failed");
    }
    
    /**
     * Adds trait to the give entity
     * @param guid
     * @param traitDefinition
     * @throws Exception 
     */
    public void addTrait(String guid, String traitDefinition) throws Exception {
        callAPI(API.ADD_TRAITS, traitDefinition, guid, URI_TRAITS);
    }
    
    private JSONObject callAPI(API api, Object requestObject, String... pathParams) throws Exception {
        WebResource resource = getResource(api, pathParams);
        return callAPIWithResource(api, resource, requestObject);
    }
    
    

    public List<String> listTypes(DataTypes.TypeCategory category) throws Exception {
        final JSONObject jsonObject = callAPI(API.LIST_TRAIT_TYPES, null);
        return extractResults(jsonObject);
    }
   

 
    
}
