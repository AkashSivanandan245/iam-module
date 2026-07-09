import { useEffect, useState } from 'react';
import { masterDataApi } from '../api/masterData';
import { useAuth } from '../context/AuthContext';
import PageHeader from '../components/PageHeader';
import Modal from '../components/Modal';
import ErrorBanner from '../components/ErrorBanner';

// Each tab shares the same shape: list, create, delete. Only difference is which
// fields the create form asks for and which parent-reference is required.
const TABS = [
  {
    key: 'orgs',
    label: 'Organisations',
    api: masterDataApi.orgs,
    createFields: [{ name: 'name', label: 'Name', type: 'text' }],
  },
  {
    key: 'universities',
    label: 'Universities',
    api: masterDataApi.universities,
    createFields: [
      { name: 'name', label: 'Name', type: 'text' },
      { name: 'organisationId', label: 'Organisation', type: 'ref', refTab: 'orgs' },
    ],
  },
  {
    key: 'branches',
    label: 'Branches',
    api: masterDataApi.branches,
    createFields: [
      { name: 'name', label: 'Name', type: 'text' },
      { name: 'universityId', label: 'University', type: 'ref', refTab: 'universities' },
    ],
  },
  {
    key: 'domains',
    label: 'Domains',
    api: masterDataApi.domains,
    createFields: [{ name: 'name', label: 'Name', type: 'text' }],
  },
];

export default function MasterDataPage() {
  const [activeKey, setActiveKey] = useState('orgs');
  const [items, setItems] = useState([]);
  const [refs, setRefs] = useState({}); // {tabKey: items[]} for foreign key dropdowns
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showCreate, setShowCreate] = useState(false);
  const { can } = useAuth();

  const active = TABS.find((t) => t.key === activeKey);

  const load = () => {
    setLoading(true);
    active.api.list()
        .then(setItems)
        .catch(setError)
        .finally(() => setLoading(false));
  };

  // Pre-load referenced tabs so the create form's dropdowns are populated.
  useEffect(() => {
    load();
    const refTabs = (active.createFields || [])
        .filter((f) => f.type === 'ref')
        .map((f) => f.refTab);
    refTabs.forEach((k) => {
      TABS.find((t) => t.key === k).api.list()
          .then((data) => setRefs((prev) => ({ ...prev, [k]: data })))
          .catch(() => {});
    });
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [activeKey]);

  const remove = async (item) => {
    if (!confirm(`Delete "${item.name}"?`)) return;
    try {
      await active.api.remove(item.id);
      load();
    } catch (err) {
      setError(err);
    }
  };

  return (
      <>
        <PageHeader
            title="Master data"
            subtitle="Organisations, universities, branches, and domains."
            actions={can('MASTERDATA:CREATE') && (
                <button onClick={() => setShowCreate(true)}>
                  New {active.label.slice(0, -1).toLowerCase()}
                </button>
            )}
        />

        <ErrorBanner error={error} />

        <div className="tabs">
          {TABS.map((t) => (
              <button
                  key={t.key}
                  className={'tab' + (t.key === activeKey ? ' tab-active' : '')}
                  onClick={() => setActiveKey(t.key)}
              >
                {t.label}
              </button>
          ))}
        </div>

        <div className="card no-pad">
          <table className="data-table">
            <thead>
            <tr>
              <th>Name</th>
              <th>ID</th>
              <th>Created</th>
              <th className="col-actions">Actions</th>
            </tr>
            </thead>
            <tbody>
            {loading && <tr><td colSpan="4" className="muted center">Loading…</td></tr>}
            {!loading && items.length === 0 && (
                <tr><td colSpan="4" className="muted center">Nothing here yet.</td></tr>
            )}
            {!loading && items.map((it) => (
                <tr key={it.id}>
                  <td><strong>{it.name}</strong></td>
                  <td className="muted small mono">{it.id}</td>
                  <td className="muted small">
                    {it.createdAt ? new Date(it.createdAt).toLocaleDateString() : '—'}
                  </td>
                  <td>
                    {can('MASTERDATA:DELETE') && (
                        <button className="ghost small-btn danger-btn" onClick={() => remove(it)}>
                          Delete
                        </button>
                    )}
                  </td>
                </tr>
            ))}
            </tbody>
          </table>
        </div>

        {showCreate && (
            <CreateEntityModal
                tab={active}
                refs={refs}
                onClose={() => setShowCreate(false)}
                onCreated={() => {
                  setShowCreate(false);
                  load();
                }}
            />
        )}
      </>
  );
}

/* -------------------- Create modal -------------------- */

function CreateEntityModal({ tab, refs, onClose, onCreated }) {
  const [form, setForm] = useState(() =>
      Object.fromEntries((tab.createFields || []).map((f) => [f.name, '']))
  );
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState(null);

  const submit = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    setError(null);
    try {
      await tab.api.create(form);
      onCreated();
    } catch (err) {
      setError(err);
      setSubmitting(false);
    }
  };

  return (
      <Modal title={`New ${tab.label.slice(0, -1).toLowerCase()}`} onClose={onClose}>
        <ErrorBanner error={error} />
        <form onSubmit={submit} className="stack">
          {tab.createFields.map((field) => (
              <label key={field.name}>
                {field.label}
                {field.type === 'ref' ? (
                    <select
                        value={form[field.name]}
                        onChange={(e) => setForm({ ...form, [field.name]: e.target.value })}
                        required
                    >
                      <option value="">Select…</option>
                      {(refs[field.refTab] || []).map((r) => (
                          <option key={r.id} value={r.id}>{r.name}</option>
                      ))}
                    </select>
                ) : (
                    <input
                        type={field.type}
                        value={form[field.name]}
                        onChange={(e) => setForm({ ...form, [field.name]: e.target.value })}
                        required
                    />
                )}
              </label>
          ))}
          <div className="modal-actions">
            <button type="button" className="ghost" onClick={onClose}>Cancel</button>
            <button type="submit" disabled={submitting}>
              {submitting ? 'Creating…' : 'Create'}
            </button>
          </div>
        </form>
      </Modal>
  );
}