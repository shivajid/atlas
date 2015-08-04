package com.atlas.client;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.apache.atlas.AtlasClient;
import org.apache.atlas.AtlasServiceException;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class NewAtlasClient extends AtlasClient {

	 private WebResource service;
	
	public NewAtlasClient(String baseurl) {
		super(baseurl);

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
        WebResource resource = service.path(api.getPath());
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
        LIST_TRAIT_TYPES(BASE_URI + TYPES + "?type=trait", HttpMethod.GET),

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
    
   

 
    
}
