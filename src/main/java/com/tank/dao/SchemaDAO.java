package com.tank.dao;

import com.mashape.unirest.http.HttpClientHelper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequest;
import com.tank.common.JacksonObjectMapper;
import com.tank.message.schema.SchemaRes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author fuchun
 */
@Service
public class SchemaDAO {


  public Optional<SchemaRes> fetchSchemaResponse(String schemaId) throws UnirestException, IOException {

    Unirest.setObjectMapper(new JacksonObjectMapper());
    HttpRequest request = Unirest.get(schemaIdUrl).header("accept", "application/json").routeParam("schemaId", schemaId);
    SchemaRes response = HttpClientHelper.request(request, SchemaRes.class).getBody();
    if (Objects.isNull(response)) {
      this.logger.log(Level.WARNING, "restful request url:" + schemaIdUrl + " no data response");
      throw new UnirestException(schemaIdUrl + " no data response");
    }
    return Optional.of(response);
  }

  @Value("${esAgent.schemaIdUrl}")
  private String schemaIdUrl;

  private Logger logger = Logger.getLogger(SchemaDAO.class.getName());
}
