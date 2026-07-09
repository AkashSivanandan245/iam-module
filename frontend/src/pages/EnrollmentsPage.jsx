import { useState, useCallback } from 'react';
import PageHeader from '../components/PageHeader';
import Toast from '../components/Toast';

const MOCK_ENROLLMENTS = [
  { id: '1', course: 'Introduction to Java Spring Boot', instructor: 'Rishi Gupta', thumbnail: '☕', progress: 72, status: 'IN_PROGRESS', enrolledOn: '2026-06-01', lastAccessed: '2026-07-07', totalModules: 18, completedModules: 13, nextLesson: 'Module 14 — JWT Authentication' },
  { id: '2', course: 'React & Vite — Modern Frontend', instructor: 'Dron Garg', thumbnail: '⚛️', progress: 100, status: 'COMPLETED', enrolledOn: '2026-05-10', lastAccessed: '2026-06-28', totalModules: 12, completedModules: 12, nextLesson: null },
  { id: '3', course: 'Security & IAM Fundamentals', instructor: 'Akash Sivanandan', thumbnail: '🔐', progress: 15, status: 'IN_PROGRESS', enrolledOn: '2026-07-05', lastAccessed: '2026-07-06', totalModules: 8, completedModules: 1, nextLesson: 'Module 2 — OAuth2 Flows' },
];

const STATUS_LABEL = {
  IN_PROGRESS: { label: 'In progress', cls: 'status-invited' },
  COMPLETED:   { label: 'Completed',   cls: 'status-active'  },
  NOT_STARTED: { label: 'Not started', cls: 'status-suspended'},
};

export default function EnrollmentsPage() {
  const [filter, setFilter] = useState('ALL');
  const [toast, setToast] = useState(null);
  const show = useCallback((message, icon = '✓') => setToast({ message, icon }), []);

  const filtered = MOCK_ENROLLMENTS.filter((e) => filter === 'ALL' || e.status === filter);
  const total = MOCK_ENROLLMENTS.length;
  const completed = MOCK_ENROLLMENTS.filter((e) => e.status === 'COMPLETED').length;
  const inProgress = MOCK_ENROLLMENTS.filter((e) => e.status === 'IN_PROGRESS').length;

  return (
      <>
        <PageHeader title="My Enrollments" subtitle={`${total} course${total === 1 ? '' : 's'} enrolled`} />

        <div className="enrollment-stats">
          <div className="stat-card"><div className="stat-number">{total}</div><div className="stat-label muted">Total enrolled</div></div>
          <div className="stat-card"><div className="stat-number" style={{ color: 'var(--accent-light)' }}>{inProgress}</div><div className="stat-label muted">In progress</div></div>
          <div className="stat-card"><div className="stat-number" style={{ color: '#7be0a8' }}>{completed}</div><div className="stat-label muted">Completed</div></div>
          <div className="stat-card"><div className="stat-number">{Math.round(MOCK_ENROLLMENTS.reduce((a, e) => a + e.progress, 0) / total)}%</div><div className="stat-label muted">Avg progress</div></div>
        </div>

        <div className="tabs">
          {[['ALL', 'All'], ['IN_PROGRESS', 'In progress'], ['COMPLETED', 'Completed']].map(([val, label]) => (
              <button key={val} className={'tab' + (filter === val ? ' tab-active' : '')} onClick={() => setFilter(val)}>{label}</button>
          ))}
        </div>

        <div className="enrollment-list">
          {filtered.map((e) => {
            const st = STATUS_LABEL[e.status] || STATUS_LABEL.NOT_STARTED;
            return (
                <div key={e.id} className="enrollment-card">
                  <div className="enrollment-thumb">{e.thumbnail}</div>
                  <div className="enrollment-body">
                    <div className="enrollment-top">
                      <div>
                        <h3 className="enrollment-title">{e.course}</h3>
                        <p className="muted small">Instructor: {e.instructor}</p>
                      </div>
                      <span className={`badge ${st.cls}`}>{st.label}</span>
                    </div>
                    <div className="progress-row">
                      <div className="progress-bar-bg">
                        <div className="progress-bar-fill" style={{ width: `${e.progress}%` }} />
                      </div>
                      <span className="muted small">{e.progress}%</span>
                    </div>
                    <div className="enrollment-meta">
                      <span className="muted small">{e.completedModules}/{e.totalModules} modules</span>
                      <span className="muted small">Last accessed: {new Date(e.lastAccessed).toLocaleDateString()}</span>
                    </div>
                    {e.nextLesson && (
                        <div className="next-lesson">
                          <span className="muted small">Next up:</span>
                          <button className="ghost small-btn" onClick={() => show(`Resuming: ${e.nextLesson}`, '▶')}>
                            ▶ {e.nextLesson}
                          </button>
                        </div>
                    )}
                    {e.status === 'COMPLETED' && (
                        <div className="next-lesson">
                          <button className="ghost small-btn" onClick={() => show('Certificate downloaded successfully.', '🎓')}>
                            🎓 Download certificate
                          </button>
                        </div>
                    )}
                  </div>
                </div>
            );
          })}
          {filtered.length === 0 && (
              <div className="coming-soon">
                <div className="coming-soon-icon">📚</div>
                <h1>No enrollments here</h1>
                <p className="muted">Go to Courses to enroll in something new.</p>
              </div>
          )}
        </div>

        {toast && <Toast message={toast.message} icon={toast.icon} onClose={() => setToast(null)} />}
      </>
  );
}