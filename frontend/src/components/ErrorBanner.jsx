// Small presentational component. Handles the special case of a 429 response
// by showing the retry-after hint the backend sent in the Retry-After header.
export default function ErrorBanner({ error }) {
  if (!error) return null;

  const is429 = error.status === 429;
  return (
    <div className={`banner ${is429 ? 'banner-warn' : 'banner-error'}`}>
      <strong>{is429 ? 'Slow down' : 'Error'}:</strong> {error.message}
      {is429 && error.retryAfter && (
        <div className="banner-sub">
          Try again in about {error.retryAfter} second{error.retryAfter === 1 ? '' : 's'}.
        </div>
      )}
    </div>
  );
}
