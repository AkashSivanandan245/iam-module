import { Fragment, useEffect, useState } from 'react';
import { auditApi } from '../api/audit';
import PageHeader from '../components/PageHeader';
import ErrorBanner from '../components/ErrorBanner';

// Simple paginated audit trail. Read-only — the backend is the source of truth
// and there's no reason to expose mutation from the UI.
export default function AuditPage() {
  const [pageData, setPageData] = useState(null);
  const [pageNumber, setPageNumber] = useState(0);
  const [pageSize] = useState(20);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [expandedId, setExpandedId] = useState(null);

  useEffect(() => {
    setLoading(true);
    auditApi.list(pageNumber, pageSize)
      .then(setPageData)
      .catch(setError)
      .finally(() => setLoading(false));
  }, [pageNumber, pageSize]);

  return (
    <>
      <PageHeader
        title="Audit log"
        subtitle={pageData ? `${pageData.totalElements} events` : 'Loading…'}
      />

      <ErrorBanner error={error} />

      <div className="card no-pad">
        <table className="data-table">
          <thead>
            <tr>
              <th>Time</th>
              <th>Action</th>
              <th>Entity</th>
              <th>User</th>
              <th>IP</th>
              <th className="col-actions">Details</th>
            </tr>
          </thead>
          <tbody>
            {loading && <tr><td colSpan="6" className="muted center">Loading…</td></tr>}
            {!loading && pageData?.content?.length === 0 && (
              <tr><td colSpan="6" className="muted center">No audit events.</td></tr>
            )}
            {!loading && pageData?.content?.map((row) => (
              <Fragment key={row.id}>
                <tr>
                  <td className="muted small mono">
                    {row.timestamp ? new Date(row.timestamp).toLocaleString() : '—'}
                  </td>
                  <td><span className="badge action-badge">{row.action}</span></td>
                  <td className="mono small">
                    {row.entityType}{row.entityId ? `#${row.entityId.slice(0, 8)}` : ''}
                  </td>
                  <td className="mono small">
                    {row.userId ? row.userId.slice(0, 8) + '…' : '—'}
                  </td>
                  <td className="mono small">{row.ipAddress || '—'}</td>
                  <td>
                    {row.details && (
                      <button
                        className="ghost small-btn"
                        onClick={() => setExpandedId(expandedId === row.id ? null : row.id)}
                      >
                        {expandedId === row.id ? 'Hide' : 'View'}
                      </button>
                    )}
                  </td>
                </tr>
                {expandedId === row.id && (
                  <tr>
                    <td colSpan="6" className="expanded">
                      <pre>{row.details}</pre>
                    </td>
                  </tr>
                )}
              </Fragment>
            ))}
          </tbody>
        </table>
      </div>

      {pageData && pageData.totalPages > 1 && (
        <div className="pagination">
          <button
            className="ghost small-btn"
            disabled={pageNumber === 0}
            onClick={() => setPageNumber((p) => Math.max(0, p - 1))}
          >
            ← Prev
          </button>
          <span className="muted">
            Page {pageData.pageNumber + 1} of {pageData.totalPages}
          </span>
          <button
            className="ghost small-btn"
            disabled={pageData.isLast}
            onClick={() => setPageNumber((p) => p + 1)}
          >
            Next →
          </button>
        </div>
      )}
    </>
  );
}
