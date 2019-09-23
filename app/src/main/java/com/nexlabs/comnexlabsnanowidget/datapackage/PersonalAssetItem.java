package com.nexlabs.comnexlabsnanowidget.datapackage;

import java.math.BigDecimal;

public class PersonalAssetItem extends Crypto {

    BigDecimal quantity;
    public PersonalAssetItem(String id, String symbol, String name, BigDecimal quantity) {
        super(id, symbol, name);
        this.quantity = quantity;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }
}
