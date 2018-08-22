package ru.largusshop.internal_orders.utils;

import ru.largusshop.internal_orders.model.Meta;
import ru.largusshop.internal_orders.model.Uom;

public enum Uoms {
    KRUT_SHT("https://online.moysklad.ru/api/remap/1.1/entity/uom/19f1edc0-fc42-4001-94cb-c9ec9c62ec10"),
    KRUT_KOMPL("https://online.moysklad.ru/api/remap/1.1/entity/uom/9b8aa7bc-0fa2-11e5-90a2-8ecb001fb4e5"),
    MAK_SHT("https://online.moysklad.ru/api/remap/1.1/entity/uom/19f1edc0-fc42-4001-94cb-c9ec9c62ec10"),
    MAK_KOMPL("https://online.moysklad.ru/api/remap/1.1/entity/uom/0e1312ef-6029-11e8-9107-5048000a48fe");
    private Uom uom;

    Uoms(String href) {
        this.uom = Uom.builder()
                      .meta(Meta.builder()
                                .href(href)
                                .metadataHref("https://online.moysklad.ru/api/remap/1.1/entity/uom/metadata")
                                .type("uom")
                                .mediaType("application/json")
                                .build())
                      .build();
    }

    public Uom getUom() {
        return uom;
    }
}
