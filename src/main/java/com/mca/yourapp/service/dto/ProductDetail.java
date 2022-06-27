package com.mca.yourapp.service.dto;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@JsonIdentityReference
public class ProductDetail implements Serializable {
    @Size(min = 1)
    private String id;

    @Size(min = 1)
    private String name;

    @NotNull
    private Number price;

    private boolean availability;

    @Override
    public String toString() {
        return "{"
                + "\"id\":\"" + id + "\""
                + ",\"name\":\"" + name + "\""
                + ",\"price\":" + price
                + ",\"availability\":" + availability
                + "}";
    }
}
