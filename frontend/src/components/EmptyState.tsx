import type { ReactNode } from "react";

type EmptyStateProps = {
  title: string;
  children: ReactNode;
  action?: string;
  onAction?: () => void;
};

export function EmptyState({ title, children, action, onAction }: EmptyStateProps) {
  return (
    <div className="empty-state">
      <div className="empty-mark" aria-hidden="true" />
      <h3>{title}</h3>
      <p>{children}</p>
      {action && (
        <button type="button" onClick={onAction}>
          {action}
        </button>
      )}
    </div>
  );
}
