import React from 'react';

const ResponsePanel = ({ response, loading, error }) => {
    if (loading) {
        return <div className="panel response-panel">Loading...</div>;
    }

    if (error) {
        return <div className="panel response-panel" style={{ color: 'var(--error-color)' }}>Error: {error}</div>;
    }

    if (!response) {
        return <div className="panel response-panel">Enter a URL and click Send to get a response</div>;
    }

    const isSuccess = response.status >= 200 && response.status < 300;
    const statusClass = isSuccess ? 'status-2xx' : 'status-4xx';

    return (
        <div className="panel response-panel">
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '1rem' }}>
                <div>
                    Status: <span className={`status-badge ${statusClass}`}>{response.status} {response.statusText}</span>
                </div>
                <div>
                    Time: {response.time}ms
                </div>
                <div>
                    Size: {response.size} B
                </div>
            </div>

            <div className="tabs">
                <div className="tab active">Body</div>
                <div className="tab">Headers</div>
            </div>

            <div className="response-viewer">
                <pre>{JSON.stringify(response.data, null, 2)}</pre>
            </div>
        </div>
    );
};

export default ResponsePanel;
