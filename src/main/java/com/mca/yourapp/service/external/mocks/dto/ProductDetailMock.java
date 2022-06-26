package com.mca.yourapp.service.external.mocks.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
public class ProductDetailMock implements Serializable {
    @Size(min = 1)
    private String id;

    @Size(min = 1)
    private String name;

    @NotNull
    private Number price;

    private boolean availability;
}
