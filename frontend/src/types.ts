export type PropertyType = "APARTMENT" | "HOUSE" | "LAND" | "COMMERCIAL" | "OTHER";
export type OperationType = "SALE" | "RENT" | "TEMPORARY_RENT";
export type PropertyStatus = "DRAFT" | "PUBLISHED" | "PAUSED";

export type PropertyImage = {
  id: number;
  propertyId?: number;
  url: string;
  sortOrder?: number;
  createdAt?: string;
};

export type Property = {
  id: number;
  title: string | null;
  description: string | null;
  type: PropertyType | null;
  operation: OperationType | null;
  price: number | null;
  currency: string | null;
  address: string | null;
  city: string | null;
  province: string | null;
  bedrooms: number | null;
  bathrooms: number | null;
  coveredArea: number | null;
  totalArea: number | null;
  status: PropertyStatus;
  images?: PropertyImage[];
  createdAt: string;
  updatedAt: string;
};

export type PageResponse<T> = {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
};

export type SearchFilters = {
  city: string;
  type: "" | PropertyType;
  operation: "" | OperationType;
  minPrice: string;
  maxPrice: string;
};
