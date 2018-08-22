package ru.largusshop.internal_orders.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Meta {
    private String href;
    private String metadataHref;
    private String type;
    private String mediaType;
    private int size;
}
