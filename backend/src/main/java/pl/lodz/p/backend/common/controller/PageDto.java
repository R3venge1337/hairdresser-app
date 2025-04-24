package pl.lodz.p.backend.common.controller;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.util.List;

@Getter
@Setter
@FieldNameConstants
public class PageDto<T> {

  private int pageNumber;

  private int pageSize;

  private long totalElements;

  private List<T> content;
}