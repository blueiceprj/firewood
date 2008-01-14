package com.googlecode.firewood.plugins.intellij.JumpToCode.model;

import com.thoughtworks.xstream.XStream;

/**
 */
public class TestXstream {

  public static void main(String[] args) {
//    ReflectionProvider reflectionProvider = new PureJavaReflectionProvider();
//    XStream xstream = new XStream(reflectionProvider);

    XStream xstream;

    xstream = new XStream();
    xstream.setClassLoader(Request.class.getClassLoader());
    
    xstream.alias("sourceLocation", SourceLocation.class);
    xstream.alias("request", Request.class);
    xstream.alias("response", Response.class);
    xstream.useAttributeFor("type", String.class);
    //xstream.useAttributeFor("type", RequestType.class);
    //xstream.registerConverter(new RequestTypeConverter());
    xstream.useAttributeFor("id", int.class);

    SourceLocation sourceLocation = new SourceLocation();
    sourceLocation.setClassName("xx.yy.Méàèçy\"'<>&Class");
    sourceLocation.setLineNumber(105);

    Request request = new Request();
    request.setSourceLocation(sourceLocation);
    request.setType(Request.JumpToLocation);

    String s = xstream.toXML(request);
    System.out.println(s);

    String xml =
      "<request type=\"JumpToLocation\">\n" +
        " <id>1008</id>" +
        "  <sourceLocation>\n" +
        "    <className>xx.yy.MyClass</className>\n" +
        "    <lineNumber>105</lineNumber>\n" +
        "    <module>xyz</module>\n" +
        "  </sourceLocation>\n" +
        "</request>";

    xstream.omitField(String.class, "module");

    //xstream.setMarshallingStrategy(new );
    request = (Request) xstream.fromXML(xml);
    System.out.println("request.type = " + request.getType());
    System.out.println("class: " + request.getSourceLocation().getClassName());
    System.out.println("req.id=" +  request.getId());
    System.out.println("module:" + request.getSourceLocation().getModule());

    Response response = new Response();
    response.setId(1408);
    response.setResultCode(1002);
    response.setResultMessage("location is reachable");
    s = xstream.toXML(response);
    System.out.println(s);

    String resp =
      "<response id=\"1408\">\n" +
      "  <resultCode>1002</resultCode>\n" +
      "  <resultMessage>location is reachable</resultMessage>\n" +
      "</response>";
    Object o = xstream.fromXML(resp);
    System.out.println("o = " + o);

  }

}
