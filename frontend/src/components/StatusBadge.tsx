import { statusLabels } from "../format";
import type { PropertyStatus } from "../types";

type StatusBadgeProps = {
  status: PropertyStatus;
};

export function StatusBadge({ status }: StatusBadgeProps) {
  return <span className={`status-badge status-${status.toLowerCase()}`}>{statusLabels[status]}</span>;
}
