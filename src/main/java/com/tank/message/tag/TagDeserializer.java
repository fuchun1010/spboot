package com.tank.message.tag;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author fuchun
 */
public class TagDeserializer extends StdDeserializer<Tag> {

  public TagDeserializer() {
    this(null);
  }

  public TagDeserializer(Class<?> vc) {
    super(vc);
  }

  @Override
  public Tag deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
    JsonNode node = jsonParser.getCodec().readTree(jsonParser);
    Tag tag = new Tag();
    String id = node.get("id").asText();
    String op = node.get("op").asText();
    String name = node.get("name").asText();
    tag.setId(id).setOp(op).setName(name);
    ArrayNode arrayNode = (ArrayNode) node.get("conditions");
    tag = this.parse(tag, arrayNode);
    return tag;
  }

  private Tag parse(Tag tag, ArrayNode arrayNode) {
    System.out.println("xxxx");
    Iterator<JsonNode> nodes = arrayNode.elements();
    while (nodes.hasNext()) {
      JsonNode node = nodes.next();
      boolean isGroup = node.path("conditions").isArray();
      if (isGroup) {
        Tag tmp = new Tag();
        String id = node.get("id").asText();
        String op = node.get("op").asText();
        String name = node.get("name").asText();
        tmp.setId(id).setOp(op).setName(name);
        tag.add(tmp);
        ArrayNode array = (ArrayNode) node.get("conditions");
        if (array.size() > 0) {
          parse(tmp, array);
        }
      } else {
        String field = node.get("field").asText();
        List<String> values = new LinkedList<>();
        ArrayNode tmpValues = (ArrayNode) node.get("values");
        for(JsonNode textNode:tmpValues){
          values.add(textNode.asText());
        }
        String compare = node.get("compare").asText();
        Condition condition = new Condition();
        condition.setCompare(compare).setField(field).setValues(values);
        tag.add(condition);
      }
    }
    return tag;
  }


}
