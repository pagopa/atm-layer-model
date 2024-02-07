package it.gov.pagopa.atmlayer.service.model.model;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PageInfo<T> {
    private Integer page;
    private Integer limit;
    private Integer itemsFound;
    private Integer totalPages;
    private List<T> results;
}
