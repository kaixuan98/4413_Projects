package microservices.modal;



import java.io.Serializable;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="products")
public class ProductCollection implements Serializable {

  private List<ProductBean> products;
  public ProductCollection() { }

  @XmlElement(name="product")
  public List<ProductBean> getProducts() {
    return products;
  }

  public void setProducts(List<ProductBean> products) {
    this.products = products;
  }
}
