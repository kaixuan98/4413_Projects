package microservices.modal;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "product")
public class ProductBean implements Serializable {
	
	private String productID;
	private String name;
	private double price;
	
	public ProductBean () {}
	
	public String getID() { return productID; }
	public String getName() { return name; }
	public double getPrice() { return price; }
	
	public void setID(String productID) { this.productID = productID; }
	public void setName(String name) { this.name = name; }
	public void setPrice(double price) { this.price = price; }
}
