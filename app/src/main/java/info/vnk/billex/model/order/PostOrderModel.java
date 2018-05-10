package info.vnk.billex.model.order;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import info.vnk.billex.model.product.PostProductModel;

/**
 * Created by Visak-Mac on 01/05/17.
 */
@JsonIgnoreProperties( ignoreUnknown = true )
public class PostOrderModel {

    @JsonProperty("order_id")
    String orderId;

    @JsonProperty("cust_id")
    int customerId;

    @JsonProperty("date_of_order")
    String dateOfOrder;

    @JsonProperty("delivery_date")
    String dateOfDelivery;

    @JsonProperty("bill_type")
    String billType;

    @JsonProperty("staff_id")
    String staffId;

    @JsonProperty("product_details")
    List<PostProductModel> listProduct;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getDateOfOrder() {
        return dateOfOrder;
    }

    public void setDateOfOrder(String dateOfOrder) {
        this.dateOfOrder = dateOfOrder;
    }

    public String getDateOfDelivery() {
        return dateOfDelivery;
    }

    public void setDateOfDelivery(String dateOfDelivery) {
        this.dateOfDelivery = dateOfDelivery;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public List<PostProductModel> getListProduct() {
        return listProduct;
    }

    public void setListProduct(List<PostProductModel> listProduct) {
        this.listProduct = listProduct;
    }

    public String getBillType() {
        return billType;
    }

    public void setBillType(String billType) {
        this.billType = billType;
    }
}
