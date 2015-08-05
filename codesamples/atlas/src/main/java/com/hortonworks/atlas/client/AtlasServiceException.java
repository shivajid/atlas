package com.hortonworks.atlas.client;

import com.sun.jersey.api.client.ClientResponse;
import org.codehaus.jettison.json.JSONException;

public class AtlasServiceException extends Exception {
    private ClientResponse.Status status;

    public AtlasServiceException(NewAtlasClient.API api, Exception e) {
        super("Metadata service API " + api + " failed", e);
    }

    public AtlasServiceException(NewAtlasClient.API api, ClientResponse response) {
        super("Metadata service API " + api + " failed with status " +
                response.getClientResponseStatus().getStatusCode() + "(" +
                response.getClientResponseStatus().getReasonPhrase() + ") Response Body (" +
                response.getEntity(String.class) + ")");
        this.status = response.getClientResponseStatus();
    }

    public AtlasServiceException(Exception e) {
        super(e);
    }

    public AtlasServiceException(String message, Exception e) {
        super(message, e);
    }

    public ClientResponse.Status getStatus() {
        return status;
    }
}