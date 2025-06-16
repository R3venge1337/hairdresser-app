export interface PageableRequest {
  page: number;
  size: number;
  sortField: String;
  sortDirection: string;
}

export interface PageDto<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}
