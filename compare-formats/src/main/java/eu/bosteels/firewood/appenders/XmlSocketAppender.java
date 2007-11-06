package eu.bosteels.firewood.appenders;

import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.xml.XMLLayout;

import java.io.IOException;
import java.io.DataOutputStream;
import java.net.Socket;

/**
 * SocketAppender that sends xml over the wire
 */
public class XmlSocketAppender extends AbstractSocketAppender {

  private DataOutputStream dataOutputStream;

  private XMLLayout layout = new XMLLayout();

  protected void closeOutputStream() throws IOException {
    if (dataOutputStream != null) {
      dataOutputStream.close();
      dataOutputStream = null;
    }
  }

  protected void openOutputStream(Socket socket) throws IOException {
    dataOutputStream = new DataOutputStream(socket.getOutputStream());
  }


  @Override
  public void activateOptions() {
    super.activateOptions();
    this.layout.setLocationInfo(locationInfo);
    this.layout.setProperties(true);
  }

  protected void write(LoggingEvent event) throws IOException {
    String xml = layout.format(event);
    byte[] bytes = xml.getBytes("UTF-8");
    dataOutputStream.writeInt(bytes.length);
    dataOutputStream.write(bytes);
  }
}
