import { useState, useCallback } from 'react';
import { useAuth } from '../context/AuthContext';
import PageHeader from '../components/PageHeader';
import Toast from '../components/Toast';

const MOCK_ASSESSMENTS = [
  { id: '1', title: 'Spring Boot — Mid-term Quiz', course: 'Introduction to Java Spring Boot', type: 'QUIZ', questions: 20, duration: '30 mins', dueDate: '2026-07-15', status: 'PENDING', score: null, maxScore: 100, attempts: 0, maxAttempts: 2 },
  { id: '2', title: 'React Hooks — Practice Assessment', course: 'React & Vite — Modern Frontend', type: 'QUIZ', questions: 15, duration: '20 mins', dueDate: '2026-06-25', status: 'SUBMITTED', score: 88, maxScore: 100, attempts: 1, maxAttempts: 2 },
  { id: '3', title: 'Build a REST API — Final Project', course: 'Introduction to Java Spring Boot', type: 'PROJECT', questions: null, duration: '2 weeks', dueDate: '2026-07-30', status: 'PENDING', score: null, maxScore: 100, attempts: 0, maxAttempts: 1 },
  { id: '4', title: 'IAM Concepts — Knowledge Check', course: 'Security & IAM Fundamentals', type: 'QUIZ', questions: 10, duration: '15 mins', dueDate: '2026-07-20', status: 'GRADED', score: 95, maxScore: 100, attempts: 1, maxAttempts: 3 },
];

const STATUS_CONFIG = {
  PENDING:   { label: 'Pending',   cls: 'status-suspended' },
  SUBMITTED: { label: 'Submitted', cls: 'status-invited'   },
  GRADED:    { label: 'Graded',    cls: 'status-active'    },
};

const TYPE_ICON = { QUIZ: '📝', PROJECT: '🛠️', ASSIGNMENT: '📄' };

export default function AssessmentsPage() {
  const { can } = useAuth();
  const [filter, setFilter] = useState('ALL');
  const [toast, setToast] = useState(null);
  const show = useCallback((message, icon = '✓') => setToast({ message, icon }), []);

  const filtered = MOCK_ASSESSMENTS.filter((a) => filter === 'ALL' || a.status === filter);
  const pending = MOCK_ASSESSMENTS.filter((a) => a.status === 'PENDING').length;
  const graded  = MOCK_ASSESSMENTS.filter((a) => a.status === 'GRADED').length;
  const avgScore = MOCK_ASSESSMENTS.filter((a) => a.score !== null)
          .reduce((acc, a) => acc + (a.score / a.maxScore) * 100, 0) /
      (MOCK_ASSESSMENTS.filter((a) => a.score !== null).length || 1);

  return (
      <>
        <PageHeader
            title="Assessments"
            subtitle={`${MOCK_ASSESSMENTS.length} assessments`}
            actions={can('ASSESSMENT:MANAGE') && (
                <button onClick={() => show('Assessment created successfully.', '📝')}>New assessment</button>
            )}
        />

        <div className="enrollment-stats">
          <div className="stat-card"><div className="stat-number" style={{ color: '#ffd68a' }}>{pending}</div><div className="stat-label muted">Pending</div></div>
          <div className="stat-card"><div className="stat-number" style={{ color: '#7be0a8' }}>{graded}</div><div className="stat-label muted">Graded</div></div>
          <div className="stat-card"><div className="stat-number" style={{ color: 'var(--accent-light)' }}>{Math.round(avgScore)}%</div><div className="stat-label muted">Avg score</div></div>
        </div>

        <div className="tabs">
          {[['ALL', 'All'], ['PENDING', 'Pending'], ['SUBMITTED', 'Submitted'], ['GRADED', 'Graded']].map(([val, label]) => (
              <button key={val} className={'tab' + (filter === val ? ' tab-active' : '')} onClick={() => setFilter(val)}>{label}</button>
          ))}
        </div>

        <div className="assessment-list">
          {filtered.map((a) => {
            const st = STATUS_CONFIG[a.status] || STATUS_CONFIG.PENDING;
            const isOverdue = new Date(a.dueDate) < new Date() && a.status === 'PENDING';
            return (
                <div key={a.id} className="assessment-card">
                  <div className="assessment-icon">{TYPE_ICON[a.type] || '📝'}</div>
                  <div className="assessment-body">
                    <div className="assessment-top">
                      <div>
                        <h3 className="assessment-title">{a.title}</h3>
                        <p className="muted small">{a.course}</p>
                      </div>
                      <span className={`badge ${isOverdue ? 'status-deactivated' : st.cls}`}>
                    {isOverdue ? 'Overdue' : st.label}
                  </span>
                    </div>
                    <div className="assessment-meta">
                      <span className="muted small">📋 {a.type}</span>
                      {a.questions && <span className="muted small">❓ {a.questions} questions</span>}
                      <span className="muted small">⏱ {a.duration}</span>
                      <span className="muted small">📅 Due: {new Date(a.dueDate).toLocaleDateString()}</span>
                      <span className="muted small">🔄 {a.attempts}/{a.maxAttempts} attempts used</span>
                    </div>
                    {a.score !== null && (
                        <div className="assessment-score">
                          <span className="score-label muted small">Score:</span>
                          <span className="score-value" style={{ color: a.score >= 80 ? '#7be0a8' : '#ffd68a' }}>
                      {a.score}/{a.maxScore}
                    </span>
                          <div className="progress-bar-bg" style={{ width: '120px' }}>
                            <div className="progress-bar-fill" style={{ width: `${(a.score / a.maxScore) * 100}%`, background: a.score >= 80 ? '#7be0a8' : '#ffd68a' }} />
                          </div>
                        </div>
                    )}
                    <div className="assessment-actions">
                      {can('ASSESSMENT:SUBMIT') && a.status === 'PENDING' && a.attempts < a.maxAttempts && (
                          <button onClick={() => show(a.type === 'QUIZ' ? 'Quiz submitted successfully. Results will be available shortly.' : 'Project submitted successfully. Your instructor will review it.', '📤')}>
                            {a.type === 'QUIZ' ? 'Start quiz' : 'Submit project'}
                          </button>
                      )}
                      {can('ASSESSMENT:MANAGE') && (
                          <button className="ghost small-btn" onClick={() => show('Submissions loaded.', '📋')}>
                            View submissions
                          </button>
                      )}
                      {a.status === 'GRADED' && (
                          <button className="ghost small-btn" onClick={() => show('Feedback loaded successfully.', '💬')}>
                            View feedback
                          </button>
                      )}
                    </div>
                  </div>
                </div>
            );
          })}
          {filtered.length === 0 && (
              <div className="coming-soon">
                <div className="coming-soon-icon">📝</div>
                <h1>No assessments here</h1>
                <p className="muted">Nothing to show for this filter.</p>
              </div>
          )}
        </div>

        {toast && <Toast message={toast.message} icon={toast.icon} onClose={() => setToast(null)} />}
      </>
  );
}