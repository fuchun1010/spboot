package com.tank.dao;

import com.mashape.unirest.http.HttpClientHelper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequest;
import com.tank.common.JacksonObjectMapper;
import com.tank.message.schema.SchemaRes;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

/**
 * @author fuchun
 */
@Service
public class SchemaDAO {


  public Optional<SchemaRes> fetchSchemaResponse(String schemaId) throws UnirestException, IOException {

    Unirest.setObjectMapper(new JacksonObjectMapper());
    HttpRequest request = Unirest.get(schemaIdUrl).header("accept", "application/json").routeParam("schemaId", schemaId);
    SchemaRes response = HttpClientHelper.request(request,SchemaRes.class).getBody();
    SchemaRes schemaRes = new SchemaRes();
    schemaRes.setTypes(response.getTypes()).setTable(response.getTable());
    return Optional.of(schemaRes);
  }

  @Value("${esAgent.schemaIdUrl}")
  private String schemaIdUrl;
}
