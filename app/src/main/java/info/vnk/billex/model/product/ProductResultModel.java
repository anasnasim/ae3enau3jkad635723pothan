package info.vnk.billex.model.product;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by priyesh on 25/04/17.
 */

@JsonIgnoreProperties( ignoreUnknown = true )
public class ProductResultModel {
    @JsonProperty("result")
    private List<ProductModel> result;

    @JsonProperty("message")
    private String message;

    @JsonProperty("status")
    private int status;

    public List<ProductModel> getResult() {
        return result;
    }

    public void setResult(List<ProductModel> result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}