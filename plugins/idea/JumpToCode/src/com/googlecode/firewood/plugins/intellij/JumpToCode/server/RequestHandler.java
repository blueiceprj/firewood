package com.googlecode.firewood.plugins.intellij.JumpToCode.server;

import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.common.IoSession;
import org.apache.log4j.Logger;
import com.thoughtworks.xstream.XStream;
import com.googlecode.firewood.plugins.intellij.JumpToCode.logic.FileUtils;
import com.googlecode.firewood.plugins.intellij.JumpToCode.model.Request;
import com.googlecode.firewood.plugins.intellij.JumpToCode.model.Response;
import com.googlecode.firewood.plugins.intellij.JumpToCode.model.SourceLocation;

/**
 */
public class RequestHandler extends IoHandlerAdapter {

  private XStream xstream;

  Logger logger = Logger.getLogger(getClass());

  public RequestHandler() {
    xstream = new XStream();
    xstream.setClassLoader(Request.class.getClassLoader());
    xstream.alias("sourceLocation", SourceLocation.class);
    xstream.alias("request", Request.class);
    xstream.alias("response", Response.class);
    xstream.useAttributeFor("type", String.class);
    xstream.useAttributeFor("id", int.class);
  }

  @Override
  public void messageReceived(IoSession session, Object message) throws Exception {
    try {
      logger.debug("incoming request: \n" + message.toString());
      Object req = xstream.fromXML(message.toString());
      if (req instanceof Request) {
        Response response = process((Request) req);
        String result = xstream.toXML(response);
        session.write(result);
      }
    } catch (Exception e) {
      Response response = new Response();
      response.setResultCode(2900);
      response.setResultMessage("Failure: " + e.toString());
      e.printStackTrace();
    }
  }

  @Override
  public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
    logger.warn("exceptionCaught", cause);
    session.close(true);
  }

  private Response process(Request request) {
    Response response = new Response(request);
    try {
      process(request, response);
    } catch (Exception e) {
      response.setResultCode(2900);
      response.setResultMessage("Failure: " + e.getMessage());
      e.printStackTrace();
    }
    return response;
  }

  private Response process(Request request, Response response) {
    if (request.is(Request.CheckLocation)) {
      if (logger.isDebugEnabled()) {
        logger.debug("checkLocation: " + request.getSourceLocation());
      }
      if (FileUtils.isReachable(request.getSourceLocation())) {
        response.setResultCode(1000);
        response.setResultMessage("Ok, location found");
      } else {
        response.setResultCode(2001);
        response.setResultMessage("location not found");
      }
      return response;
    }
    if (request.is(Request.JumpToLocation)) {
      if (logger.isDebugEnabled()) {
        logger.debug("jump to " + request.getSourceLocation());
      }
      if (FileUtils.jumpToLocation(request.getSourceLocation())) {
        response.setResultCode(1000);
        response.setResultMessage("Ok, jumped to location");
      } else {
        response.setResultCode(2001);
        response.setResultMessage("location not found");
      }
      return response;
    }
    String msg = "Unknown request type: " + request.getType();
    logger.info(msg);
    response.setResultCode(2800);
    response.setResultMessage(msg);
    return response;
  }

}
