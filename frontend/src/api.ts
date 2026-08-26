import type { PageResponse, Property, SearchFilters } from "./types";

const API_URL = import.meta.env.VITE_API_URL ?? "";

async function request<T>(path: string, options?: RequestInit): Promise<T> {
  const response = await fetch(`${API_URL}/api/v1${path}`, {
    headers: {
      "Content-Type": "application/json",
      ...options?.headers,
    },
    ...options,
  });

  if (!response.ok) {
    let message = "No pudimos completar la solicitud.";
    try {
      const problem = await response.json();
      message = problem.detail ?? problem.title ?? message;
    } catch {
      message = response.statusText || message;
    }
    throw new Error(message);
  }

  if (response.status === 204) {
    return undefined as T;
  }

  return response.json() as Promise<T>;
}

function cleanFilters(filters: SearchFilters) {
  const params = new URLSearchParams();
  params.set("page", "0");
  params.set("size", "12");

  if (filters.city.trim()) params.set("city", filters.city.trim());
  if (filters.type) params.set("type", filters.type);
  if (filters.operation) params.set("operation", filters.operation);
  if (filters.minPrice) params.set("minPrice", filters.minPrice);
  if (filters.maxPrice) params.set("maxPrice", filters.maxPrice);

  return params.toString();
}

export function getProperties(filters: SearchFilters) {
  return request<PageResponse<Property>>(`/properties?${cleanFilters(filters)}`);
}

export function getProperty(id: number) {
  return request<Property>(`/properties/${id}`);
}

export function getMyProperties() {
  return request<Property[]>("/me/properties", {
    headers: {
      "X-User-Id": "1",
    },
  });
}

export function resolveImageUrl(url?: string | null) {
  if (!url) return "";
  if (url.startsWith("http")) return url;
  return url;
}
