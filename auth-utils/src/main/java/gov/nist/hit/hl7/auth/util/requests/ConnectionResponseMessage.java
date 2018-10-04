package gov.nist.hit.hl7.auth.util.requests;

import java.util.Date;

public class ConnectionResponseMessage<T> {

  private Status status;
  private String type;
  private String text;
  private String resourceId;
  private boolean hide;
  private Date date;
  private T data;

  public ConnectionResponseMessage(Status status, String type, String text, String resourceId,
      boolean hide, Date date, T data) {
    super();
    this.status = status;
    this.type = type;
    this.text = text;
    this.resourceId = resourceId;
    this.hide = hide;
    this.date = date;
    this.data = data;
  }

  public ConnectionResponseMessage() {
    super();
    // TODO Auto-generated constructor stub
  }

  /**
   * @param failed
   * @param localizedMessage
   */
  public ConnectionResponseMessage(Status status, String localizedMessage) {
    super();
    this.status = status;
    this.text = localizedMessage;
    // TODO Auto-generated constructor stub
  }

  /**
   * @param success
   * @param tableOfContentUpdated
   * @param id
   * @param date2
   */
  public ConnectionResponseMessage(Status status, String message, String id, Date date) {
    // TODO Auto-generated constructor stub
    this.status = status;
    this.text = message;
    this.resourceId = id;
    this.date = date;

  }

  public enum Status {
    SUCCESS, WARNING, INFO, FAILED;
  }



  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public String getResourceId() {
    return resourceId;
  }

  public void setResourceId(String resourceId) {
    this.resourceId = resourceId;
  }

  public boolean isHide() {
    return hide;
  }

  public void setHide(boolean hide) {
    this.hide = hide;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public Object getData() {
    return data;
  }

  public void setData(T data) {
    this.data = data;
  }



}
