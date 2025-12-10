export async function importPostmanCollection(shareLink) {
    try {
        // If it's a share link, it might be JSON or a webpage. 
        // Postman share links usually look like https://www.postman.com/collections/id
        // Or raw JSON links.
        // For this prototype, we assume the user provides a direct link to the JSON or we try to fetch it.

        const response = await fetch(shareLink);
        if (!response.ok) {
            throw new Error('Failed to fetch collection');
        }

        const data = await response.json();

        // Handle Postman API response structure (wrapped in 'collection' object)
        const collection = data.collection || data;

        // Basic parsing of Postman Collection v2.1
        if (collection.info && collection.item) {
            return parseCollectionItems(collection.item);
        }

        return { requests: [], mocks: [] };
    } catch (error) {
        console.error('Import failed:', error);
        throw error;
    }
}

function parseCollectionItems(items, parentPath = '') {
    let requests = [];
    let mocks = [];

    items.forEach(item => {
        if (item.request) {
            // It's a request
            const req = {
                name: item.name,
                method: item.request.method,
                url: item.request.url?.raw || item.request.url || '',
                headers: item.request.header || [],
                body: item.request.body ? item.request.body.raw : '',
                description: item.request.description
            };
            requests.push(req);

            // Check for examples (responses)
            if (item.response && item.response.length > 0) {
                item.response.forEach(resp => {
                    // Postman examples have a 'originalRequest' or just use the request info
                    // We'll use the example's body as the mock response
                    // And try to infer the path/method from the request

                    // Note: Postman examples might be complex. We'll do a best effort.
                    // We use the request's path.

                    // Parse the URL to get the path (remove host)
                    let path = req.url;
                    try {
                        // If it's a full URL, extract pathname
                        if (path.startsWith('http')) {
                            const urlObj = new URL(path);
                            path = urlObj.pathname;
                        } else if (path.startsWith('{{')) {
                            // Remove variable prefix like {{url}} or {{baseUrl}}
                            path = path.replace(/^{{[^}]+}}/, '');
                        }
                    } catch (e) {
                        // keep original path if parsing fails
                    }

                    mocks.push({
                        path: path,
                        method: req.method,
                        response: resp.body, // Postman stores response body in 'body'
                        status: resp.code || 200,
                        name: resp.name // Example name
                    });
                });
            }

        } else if (item.item) {
            // It's a folder
            const result = parseCollectionItems(item.item, parentPath + item.name + ' / ');
            requests = requests.concat(result.requests);
            mocks = mocks.concat(result.mocks);
        }
    });

    return { requests, mocks };
}
