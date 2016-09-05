package controllers.oms.yunda;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Orders {
    private Order order;

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}
}
