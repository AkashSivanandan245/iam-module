import { useEffect } from 'react';

export default function Toast({ message, icon = '✓', onClose }) {
  useEffect(() => {
    const t = setTimeout(onClose, 2500);
    return () => clearTimeout(t);
  }, [onClose]);

  return (
    <div className="toast">
      <span className="toast-icon">{icon}</span>
      <span>{message}</span>
    </div>
  );
}
