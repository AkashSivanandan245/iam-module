import { useAuth } from '../context/AuthContext';
import PageHeader from '../components/PageHeader';

// Mock analytics data — replace with real API calls when M06 (Analytics) is built.
// Two views: personal (ANALYTICS:READ) and platform-wide (ANALYTICS:MANAGE).

const PERSONAL_STATS = {
  coursesEnrolled: 3,
  coursesCompleted: 1,
  avgScore: 91,
  totalHours: 42,
  streak: 7,
  rank: 12,
};

const PERSONAL_PROGRESS = [
  { course: 'Introduction to Java Spring Boot', progress: 72, score: null, hours: 24 },
  { course: 'React & Vite — Modern Frontend',   progress: 100, score: 88, hours: 14 },
  { course: 'Security & IAM Fundamentals',       progress: 15, score: null, hours: 4  },
];

const PLATFORM_STATS = {
  totalUsers: 4,
  activeThisMonth: 3,
  totalCourses: 6,
  totalEnrollments: 18,
  completionRate: 34,
  avgEngagement: 68,
};

const PLATFORM_COURSES = [
  { course: 'Introduction to Java Spring Boot', enrolled: 124, completed: 44, avgScore: 82 },
  { course: 'React & Vite — Modern Frontend',   enrolled: 89,  completed: 67, avgScore: 79 },
  { course: 'Security & IAM Fundamentals',       enrolled: 78,  completed: 12, avgScore: 91 },
  { course: 'PostgreSQL & Database Design',       enrolled: 67,  completed: 31, avgScore: 85 },
  { course: 'Cloud Deployment with Azure',        enrolled: 45,  completed: 8,  avgScore: 77 },
  { course: 'System Design & Architecture',       enrolled: 32,  completed: 3,  avgScore: 88 },
];

function StatCard({ number, label, color, icon }) {
  return (
    <div className="stat-card">
      <div className="stat-icon">{icon}</div>
      <div className="stat-number" style={color ? { color } : {}}>{number}</div>
      <div className="stat-label muted">{label}</div>
    </div>
  );
}

export default function AnalyticsPage() {
  const { can } = useAuth();
  const showPlatform = can('ANALYTICS:MANAGE');

  return (
    <>
      <PageHeader
        title="Analytics"
        subtitle={showPlatform ? 'Platform-wide overview' : 'Your learning progress'}
      />

      {/* ---- Personal stats (everyone with ANALYTICS:READ) ---- */}
      <section>
        <h2 className="section-heading">Your stats</h2>
        <div className="enrollment-stats">
          <StatCard icon="📚" number={PERSONAL_STATS.coursesEnrolled} label="Enrolled" />
          <StatCard icon="🎓" number={PERSONAL_STATS.coursesCompleted} label="Completed" color="#7be0a8" />
          <StatCard icon="⭐" number={`${PERSONAL_STATS.avgScore}%`} label="Avg score" color="var(--accent-light)" />
          <StatCard icon="⏱" number={`${PERSONAL_STATS.totalHours}h`} label="Time spent" />
          <StatCard icon="🔥" number={`${PERSONAL_STATS.streak} days`} label="Streak" color="#ffd68a" />
        </div>
      </section>

      {/* ---- Course progress ---- */}
      <section>
        <h2 className="section-heading">Course progress</h2>
        <div className="card no-pad">
          <table className="data-table">
            <thead>
              <tr>
                <th>Course</th>
                <th>Progress</th>
                <th>Score</th>
                <th>Hours</th>
              </tr>
            </thead>
            <tbody>
              {PERSONAL_PROGRESS.map((row) => (
                <tr key={row.course}>
                  <td>{row.course}</td>
                  <td>
                    <div className="progress-row" style={{ gap: '0.5rem' }}>
                      <div className="progress-bar-bg" style={{ width: '100px' }}>
                        <div
                          className="progress-bar-fill"
                          style={{ width: `${row.progress}%` }}
                        />
                      </div>
                      <span className="muted small">{row.progress}%</span>
                    </div>
                  </td>
                  <td>
                    {row.score !== null ? (
                      <span style={{ color: row.score >= 80 ? '#7be0a8' : '#ffd68a', fontWeight: 600 }}>
                        {row.score}%
                      </span>
                    ) : (
                      <span className="muted">—</span>
                    )}
                  </td>
                  <td className="muted">{row.hours}h</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </section>

      {/* ---- Platform stats (ANALYTICS:MANAGE only) ---- */}
      {showPlatform && (
        <>
          <section style={{ marginTop: '2rem' }}>
            <h2 className="section-heading">
              Platform overview
              <span className="badge coming-badge" style={{ marginLeft: '0.75rem', fontSize: '0.7rem' }}>
                Admin only
              </span>
            </h2>
            <div className="enrollment-stats">
              <StatCard icon="👥" number={PLATFORM_STATS.totalUsers}       label="Total users" />
              <StatCard icon="✅" number={PLATFORM_STATS.activeThisMonth}  label="Active this month" color="#7be0a8" />
              <StatCard icon="📚" number={PLATFORM_STATS.totalCourses}     label="Total courses" />
              <StatCard icon="📋" number={PLATFORM_STATS.totalEnrollments} label="Enrollments" />
              <StatCard icon="🎯" number={`${PLATFORM_STATS.completionRate}%`} label="Completion rate" color="var(--accent-light)" />
              <StatCard icon="📈" number={`${PLATFORM_STATS.avgEngagement}%`} label="Avg engagement" color="#ffd68a" />
            </div>
          </section>

          <section>
            <h2 className="section-heading">Course performance</h2>
            <div className="card no-pad">
              <table className="data-table">
                <thead>
                  <tr>
                    <th>Course</th>
                    <th>Enrolled</th>
                    <th>Completed</th>
                    <th>Completion rate</th>
                    <th>Avg score</th>
                  </tr>
                </thead>
                <tbody>
                  {PLATFORM_COURSES.map((row) => {
                    const rate = Math.round((row.completed / row.enrolled) * 100);
                    return (
                      <tr key={row.course}>
                        <td>{row.course}</td>
                        <td>{row.enrolled}</td>
                        <td>{row.completed}</td>
                        <td>
                          <div className="progress-row" style={{ gap: '0.5rem' }}>
                            <div className="progress-bar-bg" style={{ width: '80px' }}>
                              <div className="progress-bar-fill" style={{ width: `${rate}%` }} />
                            </div>
                            <span className="muted small">{rate}%</span>
                          </div>
                        </td>
                        <td>
                          <span style={{ color: row.avgScore >= 80 ? '#7be0a8' : '#ffd68a', fontWeight: 600 }}>
                            {row.avgScore}%
                          </span>
                        </td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>
          </section>
        </>
      )}
    </>
  );
}
