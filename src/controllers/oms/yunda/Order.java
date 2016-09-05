package controllers.oms.yunda;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Order {
    private String order_serial_no;
	private String khddh;
	private String nbckh;
    private String order_type;
    private Double weight;
    private String size;
    private Double value;
    private Double collection_value;
    private String remark;
    private String cus_area1;
    private String cus_area2;
    private String callback_id;
    private String wave_no;
    private Integer special;
    private Integer receiver_force;
    
    private Items items;
    private Sender sender;
    private Receiver receiver;
    
    
    public String getOrder_serial_no() {
		return order_serial_no;
	}
	public void setOrder_serial_no(String order_serial_no) {
		this.order_serial_no = order_serial_no;
	}
	public String getKhddh() {
		return khddh;
	}
	public void setKhddh(String khddh) {
		this.khddh = khddh;
	}
	public String getOrder_type() {
		return order_type;
	}
	public void setOrder_type(String order_type) {
		this.order_type = order_type;
	}
	public Double getWeight() {
		return weight;
	}
	public void setWeight(Double weight) {
		this.weight = weight;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public Double getValue() {
		return value;
	}
	public void setValue(Double value) {
		this.value = value;
	}
	public Double getCollection_value() {
		return collection_value;
	}
	public void setCollection_value(Double collection_value) {
		this.collection_value = collection_value;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getCus_area1() {
		return cus_area1;
	}
	public void setCus_area1(String cus_area1) {
		this.cus_area1 = cus_area1;
	}
	public String getCus_area2() {
		return cus_area2;
	}
	public void setCus_area2(String cus_area2) {
		this.cus_area2 = cus_area2;
	}
	public String getCallback_id() {
		return callback_id;
	}
	public void setCallback_id(String callback_id) {
		this.callback_id = callback_id;
	}
	public String getWave_no() {
		return wave_no;
	}
	public void setWave_no(String wave_no) {
		this.wave_no = wave_no;
	}
    
	public Items getItems() {
		return items;
	}
	public void setItems(Items items2) {
		this.items = items2;
	}
	public Integer getSpecial() {
		return special;
	}
	public void setSpecial(Integer special) {
		this.special = special;
	}
	public Integer getReceiver_force() {
		return receiver_force;
	}
	public void setReceiver_force(Integer receiver_force) {
		this.receiver_force = receiver_force;
	}
	public String getNbckh() {
		return nbckh;
	}
	public void setNbckh(String nbckh) {
		this.nbckh = nbckh;
	}
	public Sender getSender() {
		return sender;
	}
	public void setSender(Sender sender) {
		this.sender = sender;
	}
	public Receiver getReceiver() {
		return receiver;
	}
	public void setReceiver(Receiver receiver) {
		this.receiver = receiver;
	}

}
